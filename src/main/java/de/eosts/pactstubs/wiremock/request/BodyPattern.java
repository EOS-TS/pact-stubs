/**
 * Copyright 2020 EOS Technology Solutions GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.jayway.jsonpath.PathNotFoundException;
import de.eosts.pactstubs.wiremock.request.body.RequestBodyPatternFromMatchingRuleFactory;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BodyPattern implements Pact2WireMockRequestPattern {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BodyPattern.class);

    @Override
    public void accept(Request pactRequest, RequestPatternBuilder requestPatternBuilder) {
        OptionalBody body = pactRequest.getBody();
        if (body.isPresent()) {
            Map<String, MatchingRuleGroup> bodyRules = pactRequest.getMatchingRules().rulesForCategory("body").getMatchingRules();
            if (!bodyRules.isEmpty()) {
                DocumentContext documentContext = JsonPath.parse(new String(body.unwrap()));
                bodyRules.forEach((jsonPath, matchingRuleGroup) -> {
                    List<MatchingRule> rules = matchingRuleGroup.getRules();
                    rules.forEach(matchingRule -> {
                        requestPatternBuilder.withRequestBody(RequestBodyPatternFromMatchingRuleFactory.getInstance(jsonPath, matchingRule));
                        if (!(matchingRule instanceof MinTypeMatcher || matchingRule instanceof MaxTypeMatcher || matchingRule instanceof MinMaxTypeMatcher)) {
                            //delete static body elements that are replaced by pattern (skip minmax matcher)
                            try {
                                documentContext.delete(jsonPath);
                            } catch (PathNotFoundException exception) {
                                // unfortunately, in cases where nested array types are handled, the eachArrayLike rule as jsonPath
                                // will delete all nested paths. Ignoring the resulting PathNotException should be safe to do
                                // but will log just to give a hint in case this is another exception
                                log.info("Path not found for json path {}, should be ok assuming this is part of a nested array rule", jsonPath);
                            }
                        }
                    });
                });
                addStaticElementsToRequestPattern(requestPatternBuilder, documentContext);
            } else {
                requestPatternBuilder.withRequestBody(new EqualToJsonPattern(new String(body.unwrap()), true, true));
            }
        }
    }

    private void addStaticElementsToRequestPattern(RequestPatternBuilder requestPatternBuilder, DocumentContext documentContext) {
        //add all static fields from pact request after removing all empty elements (as a result of matching rule replacement)
        Object json = documentContext.json();
        if (json instanceof Collection) {
            Collection<Object> asCollection = (Collection<Object>) json;
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
