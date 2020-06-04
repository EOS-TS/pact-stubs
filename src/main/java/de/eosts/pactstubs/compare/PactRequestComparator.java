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
package de.eosts.pactstubs.compare;

import au.com.dius.pact.core.model.Request;
import au.com.dius.pact.core.model.matchingrules.MatchingRule;
import au.com.dius.pact.core.model.matchingrules.MatchingRuleGroup;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import de.eosts.pactstubs.spec.SpecificRequestSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PactRequestComparator implements RequestComparator {

    @Override
    public Map<String, String> compare(Request pactRequest, SpecificRequestSpec specificRequestSpec) {
        Map<String, String> mismatches = new HashMap<>();

        specificRequestSpec.getUrlPath().ifPresent(urlPath -> mismatches.putAll(getMismatchesForUrlPath(pactRequest, urlPath)));
        specificRequestSpec.getQueryParams().ifPresent(queryParams -> mismatches.putAll(getMismatchesForQueryParams(queryParams, pactRequest)));
        //TODO verify specific jsonBody
        return mismatches;
    }

    private Map<String, String> getMismatchesForQueryParams(Map<String, String> queryParams, Request pactRequest) {
        Map<String, String> mismatches = new HashMap<>();

        Map<String, MatchingRuleGroup> queryRules = pactRequest.getMatchingRules().rulesForCategory("query").getMatchingRules();
        Map<String, List<String>> query = pactRequest.getQuery();
        if (!query.isEmpty()) {
            query.forEach((queryKey, queryValue) -> {
                String specificParamValue = queryParams.get(queryKey);

                if (queryRules.containsKey(queryKey)) {
                    MatchingRule rule = queryRules.get(queryKey).getRules().get(0);
                    String regex = ((RegexMatcher) rule).getRegex();

                    if (specificParamValue == null || !specificParamValue.matches(regex)) {
                        mismatches.put(queryKey, specificParamValue + " !~ " + regex);
                    }
                } else {
                    if (!queryValue.get(0).equals(specificParamValue)) {
                        mismatches.put(queryKey, specificParamValue + " != " + queryValue.get(0));
                    }
                }
            });
        }

        return mismatches;
    }

    private Map<String, String> getMismatchesForUrlPath(Request pactRequest, String urlPath) {
        Map<String, String> mismatches = new HashMap<>();
        List<MatchingRule> pathRules = pactRequest.getMatchingRules().rulesForCategory("path").allMatchingRules();
        if (!pathRules.isEmpty()) {
            String pathRegex = ((RegexMatcher) pathRules.get(0)).getRegex();
            if (!urlPath.matches(pathRegex)) {
                mismatches.put(pathRegex, urlPath);
            }
        } else {
            if (!urlPath.equals(pactRequest.getPath())) {
                mismatches.put(pactRequest.getPath(), urlPath);
            }
        }
        return mismatches;
    }
}
