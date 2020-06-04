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
package de.eosts.pactstubs.wiremock.response;

import au.com.dius.pact.core.model.Response;
import au.com.dius.pact.provider.ComparisonResult;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import de.eosts.pactstubs.compare.PactResponseComparator;
import de.eosts.pactstubs.compare.ResponseComparator;
import de.eosts.pactstubs.compare.ResponseComparisonResult;
import de.eosts.pactstubs.jsonpath.JsonPathWriter;
import de.eosts.pactstubs.jsonpath.WriteContextOperator;
import de.eosts.pactstubs.jsonpath.JsonPathResponseConverter;

import java.util.ArrayList;

public class PactResponseConverter implements JsonPathResponseConverter<Response, ResponseDefinition> {

    private final ResponseComparator<Response, String> responseComparator;
    private final JsonPathWriter jsonPathWriter;

    public PactResponseConverter() {
        this.responseComparator = new PactResponseComparator();
        this.jsonPathWriter = new JsonPathWriter();
    }

    public PactResponseConverter(ResponseComparator<Response, String> responseComparator) {
        this.responseComparator = responseComparator;
        this.jsonPathWriter = new JsonPathWriter();
    }

    public PactResponseConverter(JsonPathWriter jsonPathWriter) {
        this.responseComparator = new PactResponseComparator();
        this.jsonPathWriter = jsonPathWriter;
    }

    public PactResponseConverter(ResponseComparator<Response, String> responseComparator, JsonPathWriter jsonPathWriter) {
        this.responseComparator = responseComparator;
        this.jsonPathWriter = jsonPathWriter;
    }

    @Override
    public ResponseComparisonResult<ResponseDefinition> convert(Response response, WriteContextOperator... operators) {
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
        ComparisonResult validationResult = null;
        if (response.getBody().isPresent()) {
            String bodyAsString = new String(response.getBody().getValue());
            if (operators.length > 0) {
                String convertedBody = jsonPathWriter.write(bodyAsString, operators);
                validationResult = responseComparator.compare(response, convertedBody);
                responseDefinitionBuilder.withBody(convertedBody);
            } else {
                responseDefinitionBuilder.withBody(bodyAsString);
            }
        }
        ArrayList<HttpHeader> headerList = new ArrayList<>();
        response.getHeaders().forEach((headerName, headerValue) -> headerList.add(new HttpHeader(headerName, headerValue)));
        return new ResponseComparisonResult<>(
                responseDefinitionBuilder
                        .withStatus(response.getStatus())
                        .withHeaders(new HttpHeaders(headerList))
                        .build(),
                validationResult);
    }
}
