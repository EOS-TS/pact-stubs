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
package de.eosts.pactstubs.compare;

import au.com.dius.pact.core.model.Response;
import au.com.dius.pact.provider.ComparisonResult;
import au.com.dius.pact.provider.ProviderResponse;
import au.com.dius.pact.provider.ResponseComparison;

public class PactResponseComparator implements ResponseComparator<Response, String> {

    @Override
    public ComparisonResult compare(Response pactResponse, String convertedBody) {
        ProviderResponse wrappedResponse = new ProviderResponse(pactResponse.getStatus(), pactResponse.getHeaders(), pactResponse.determineContentType(), convertedBody);
        return ResponseComparison.compareResponse(pactResponse, wrappedResponse);
    }
}
