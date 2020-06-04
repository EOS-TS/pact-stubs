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

import au.com.dius.pact.core.model.Response;
import au.com.dius.pact.provider.ComparisonResult;
import au.com.dius.pact.provider.ResponseComparison;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class PactResponseComparator implements ResponseComparator<Response, String> {

    @Override
    public ComparisonResult compare(Response pactResponse, String convertedBody) {
        Map<String, Object> dummyActualResponse = new HashMap<>();
        dummyActualResponse.put("contentType", APPLICATION_JSON);
        return ResponseComparison.compareResponse(pactResponse, dummyActualResponse, pactResponse.getStatus(), pactResponse.getHeaders(), convertedBody);
    }
}
