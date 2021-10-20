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
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;

public class DatePattern implements RequestBodyPattern<DateMatcher> {
    @Override
    public MatchesJsonPathPattern from(String jsonPath, DateMatcher matchingRule) {
        String format = matchingRule.getFormat();
        if (!format.equals("yyyy-MM-dd")) {
            throw new UnsupportedOperationException("currently only date format \"yyyy-MM-dd\" is supported. Found : " + format);
        }
        String regex = "^([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])$";
        return new MatchesJsonPathPattern(jsonPath, new RegexPattern(regex));
    }
}
