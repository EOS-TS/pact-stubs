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

import au.com.dius.pact.core.model.matchingrules.NumberTypeMatcher;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import org.slf4j.Logger;

public class NumberTypePattern implements RequestBodyPattern<NumberTypeMatcher> {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(NumberTypePattern.class);

    @Override
    public MatchesJsonPathPattern from(String jsonPath, NumberTypeMatcher matchingRule) {
        log.info("NumberType (matched via MatchesJsonPathPattern + RegexPattern) cannot verify type, i.e. 1 and \"1\" will match as integer");
        String regex = null;
        switch (matchingRule.getNumberType()) {
        case NUMBER:
            regex = "^(\\d+(\\.\\d+)?)$";
            break;
        case INTEGER:
            regex = "^(\\d)$";
            break;
        case DECIMAL:
            regex = "^(\\d+\\.\\d+)$";
        }
        return new MatchesJsonPathPattern(jsonPath, new RegexPattern(regex));
    }
}
