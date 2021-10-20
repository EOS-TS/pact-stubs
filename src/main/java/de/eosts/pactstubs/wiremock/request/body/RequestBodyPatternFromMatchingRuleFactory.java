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
package de.eosts.pactstubs.wiremock.request.body;

import au.com.dius.pact.core.model.matchingrules.DateMatcher;
import au.com.dius.pact.core.model.matchingrules.MatchingRule;
import au.com.dius.pact.core.model.matchingrules.MaxTypeMatcher;
import au.com.dius.pact.core.model.matchingrules.MinMaxTypeMatcher;
import au.com.dius.pact.core.model.matchingrules.MinTypeMatcher;
import au.com.dius.pact.core.model.matchingrules.NumberTypeMatcher;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import au.com.dius.pact.core.model.matchingrules.TimeMatcher;
import au.com.dius.pact.core.model.matchingrules.TimestampMatcher;
import au.com.dius.pact.core.model.matchingrules.TypeMatcher;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import org.slf4j.Logger;

public class RequestBodyPatternFromMatchingRuleFactory {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RequestBodyPatternFromMatchingRuleFactory.class);

    private RequestBodyPatternFromMatchingRuleFactory() {
    }

    public static ContentPattern getInstance(String jsonPath, MatchingRule matchingRule) {
        if (matchingRule instanceof TypeMatcher) {
            return new TypePattern().from(jsonPath, (TypeMatcher) matchingRule);
        } else if (matchingRule instanceof MinTypeMatcher) {
            return new MinTypePattern().from(jsonPath, (MinTypeMatcher) matchingRule);
        } else if (matchingRule instanceof MaxTypeMatcher) {
            return new MaxTypePattern().from(jsonPath, (MaxTypeMatcher) matchingRule);
        } else if (matchingRule instanceof MinMaxTypeMatcher) {
            return new MinMaxTypePattern().from(jsonPath, (MinMaxTypeMatcher) matchingRule);
        } else if (matchingRule instanceof RegexMatcher) {
            return new RegexPattern().from(jsonPath, (RegexMatcher) matchingRule);
        } else if (matchingRule instanceof NumberTypeMatcher) {
            return new NumberTypePattern().from(jsonPath, (NumberTypeMatcher) matchingRule);
        } else if (matchingRule instanceof DateMatcher) {
            return new DatePattern().from(jsonPath, (DateMatcher) matchingRule);
        } else if (matchingRule instanceof TimeMatcher) {
            return new TimePattern().from(jsonPath, (TimeMatcher) matchingRule);
        } else if (matchingRule instanceof TimestampMatcher) {
            return new TimestampPattern().from(jsonPath, (TimestampMatcher) matchingRule);
        } else {
            log.warn("no support for matchingrule: {} Matching jsonPath only.", matchingRule);
            return new MatchesJsonPathPattern(jsonPath);
        }
    }
}
