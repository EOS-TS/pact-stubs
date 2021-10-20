/*
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

import java.util.List;
import java.util.Map;

public class HeaderPattern implements Pact2WireMockRequestPattern {

    @Override
    public void accept(Request pactRequest, RequestPatternBuilder requestPatternBuilder) {
        Map<String, List<String>> headers = pactRequest.getHeaders();
        if (!headers.isEmpty()) {
            Map<String, MatchingRuleGroup> headerRules = pactRequest.getMatchingRules().rulesForCategory("header").getMatchingRules();
            headers.forEach((headerKey, headerValues) -> {
                if (headerRules.containsKey(headerKey)) {
                    MatchingRule rule = headerRules.get(headerKey).getRules().get(0);
                    requestPatternBuilder.withHeader(headerKey, new RegexPattern(((RegexMatcher) rule).getRegex()));
                } else {
                    requestPatternBuilder.withHeader(headerKey, new EqualToPattern(String.join(",", headerValues)));
                }
            });
        }
    }
}
