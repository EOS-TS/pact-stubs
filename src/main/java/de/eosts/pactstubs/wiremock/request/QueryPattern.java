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

import au.com.dius.pact.core.model.Request;
import au.com.dius.pact.core.model.matchingrules.MatchingRule;
import au.com.dius.pact.core.model.matchingrules.MatchingRuleGroup;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class QueryPattern implements Pact2WireMockRequestPattern {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(QueryPattern.class);

    @Override
    public void accept(Request pactRequest, RequestPatternBuilder requestPatternBuilder) {
        Map<String, List<String>> query = pactRequest.getQuery();
        if (!query.isEmpty()) {
            Map<String, MatchingRuleGroup> queryRules = pactRequest.getMatchingRules().rulesForCategory("query").getMatchingRules();
            query.forEach((queryKey, queryValue) -> {
                if (queryRules.containsKey(queryKey)) {
                    MatchingRule rule = queryRules.get(queryKey).getRules().get(0);
                    requestPatternBuilder.withQueryParam(queryKey, new RegexPattern(((RegexMatcher) rule).getRegex()));
                } else {
                    requestPatternBuilder.withQueryParam(queryKey, new EqualToPattern(queryValue.get(0)));
                    if (queryValue.size() > 1) {
                        log.warn("WireMock only supports a single value for the same query param. Found additional value: {}", queryValue.get(1));
                    }
                }
            });
        }
    }
}
