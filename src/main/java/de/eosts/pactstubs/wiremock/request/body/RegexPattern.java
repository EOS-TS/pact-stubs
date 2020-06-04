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
package de.eosts.pactstubs.wiremock.request.body;

import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;

public class RegexPattern implements RequestBodyPattern<RegexMatcher> {
    @Override
    public MatchesJsonPathPattern from(String jsonPath, RegexMatcher matchingRule) {
        String regex = matchingRule.getRegex();
        return new MatchesJsonPathPattern(jsonPath, new com.github.tomakehurst.wiremock.matching.RegexPattern(regex));
    }
}
