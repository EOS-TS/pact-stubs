/**
 *
 *    Copyright 2020 EOS Technology Solutions GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.eosts.pactstubs.wiremock.request;

import au.com.dius.pact.core.model.OptionalBody;
import au.com.dius.pact.core.model.Request;
import au.com.dius.pact.core.model.matchingrules.MatchingRule;
import au.com.dius.pact.core.model.matchingrules.MatchingRuleGroup;
import au.com.dius.pact.core.model.matchingrules.MaxTypeMatcher;
import au.com.dius.pact.core.model.matchingrules.MinMaxTypeMatcher;
import au.com.dius.pact.core.model.matchingrules.MinTypeMatcher;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.eosts.pactstubs.wiremock.request.body.RequestBodyPatternFromMatchingRuleFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BodyPattern implements Pact2WireMockRequestPattern {

    @Override
    public void accept(Request pactRequest, RequestPatternBuilder requestPatternBuilder) {
        OptionalBody body = pactRequest.getBody();
        if (body.isPresent()) {
            Map<String, MatchingRuleGroup> bodyRules = pactRequest.getMatchingRules().rulesForCategory("body").getMatchingRules();
            if (!bodyRules.isEmpty()) {
                DocumentContext documentContext = JsonPath.parse(new String(body.getValue()));
                bodyRules.forEach((jsonPath, matchingRuleGroup) -> {
                    List<MatchingRule> rules = matchingRuleGroup.getRules();
                    rules.forEach(matchingRule -> {
                        requestPatternBuilder.withRequestBody(RequestBodyPatternFromMatchingRuleFactory.getInstance(jsonPath, matchingRule));
                        if (!(matchingRule instanceof MinTypeMatcher || matchingRule instanceof MaxTypeMatcher || matchingRule instanceof MinMaxTypeMatcher)) {
                            //delete static body elements that are replaced by pattern (skip minmax matcher)
                            documentContext.delete(jsonPath);
                        }
                    });
                });
                addStaticElementsToRequestPattern(requestPatternBuilder, documentContext);
            } else {
                requestPatternBuilder.withRequestBody(new EqualToJsonPattern(new String(body.getValue()), true, true));
            }
        }
    }

    private void addStaticElementsToRequestPattern(RequestPatternBuilder requestPatternBuilder, DocumentContext documentContext) {
        //add all static fields from pact request after removing all empty elements (as a result of matching rule replacement)
        Object json = documentContext.json();
        if (json instanceof Collection) {
            Collection asCollection = (Collection) json;
            deepRemoveEmptyCollections(asCollection);
            if (!asCollection.isEmpty()) {
                requestPatternBuilder.withRequestBody(new EqualToJsonPattern(documentContext.jsonString(), true, true));
            }
        } else if (json instanceof Map) {
            Map asMap = (Map) json;
            deepRemoveEmptyCollections(asMap.entrySet());
            if (!asMap.isEmpty()) {
                requestPatternBuilder.withRequestBody(new EqualToJsonPattern(documentContext.jsonString(), true, true));
            }
        } else {
            requestPatternBuilder.withRequestBody(new EqualToJsonPattern(documentContext.jsonString(), true, true));
        }
    }

    private static void deepRemoveEmptyCollections(Collection collection) {
        collection.forEach(o -> {
            if (o instanceof Map.Entry) {
                o = ((Map.Entry) o).getValue();
            }
            if (o instanceof Map) {
                deepRemoveEmptyCollections(((Map) o).entrySet());
            } else if (o instanceof Collection) {
                deepRemoveEmptyCollections((Collection) o);
            }
        });
        collection.removeIf(o -> (o instanceof Map.Entry && (((Map.Entry) o).getValue() instanceof Collection && ((Collection) ((Map.Entry) o).getValue()).isEmpty()
                || ((Map.Entry) o).getValue() instanceof Map && ((Map) ((Map.Entry) o).getValue()).isEmpty()))
                || o instanceof Collection && ((Collection) o).isEmpty()
                || o instanceof Map && ((Map) o).isEmpty());
    }

}
