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
package de.eosts.pactstubs.exception;

import au.com.dius.pact.core.model.Response;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import de.eosts.pactstubs.compare.ResponseComparisonResult;

/**
 * RuntimeException to signal a converted response that does not fulfill pact
 */
public class ResponseNotVerifiedException extends RuntimeException implements ResponseComparisonResultException<ResponseDefinition> {

    private final transient Response pactResponse;
    private final transient ResponseComparisonResult<ResponseDefinition> responseComparisonResult;

    public ResponseNotVerifiedException(Response pactResponse, ResponseComparisonResult<ResponseDefinition> responseComparisonResult) {
        this.pactResponse = pactResponse;
        this.responseComparisonResult = responseComparisonResult;
    }

    public ResponseNotVerifiedException(String s, Response pactResponse, ResponseComparisonResult<ResponseDefinition> responseComparisonResult) {
        super(s);
        this.pactResponse = pactResponse;
        this.responseComparisonResult = responseComparisonResult;
    }

    public ResponseNotVerifiedException(String s, Throwable throwable, Response pactResponse, ResponseComparisonResult<ResponseDefinition> responseComparisonResult) {
        super(s, throwable);
        this.pactResponse = pactResponse;
        this.responseComparisonResult = responseComparisonResult;
    }

    public ResponseNotVerifiedException(Throwable throwable, Response pactResponse, ResponseComparisonResult<ResponseDefinition> responseComparisonResult) {
        super(throwable);
        this.pactResponse = pactResponse;
        this.responseComparisonResult = responseComparisonResult;
    }

    public ResponseNotVerifiedException(String s, Throwable throwable, boolean b, boolean b1, Response pactResponse, ResponseComparisonResult<ResponseDefinition> responseComparisonResult) {
        super(s, throwable, b, b1);
        this.pactResponse = pactResponse;
        this.responseComparisonResult = responseComparisonResult;
    }

    public Response getPactResponse() {
        return pactResponse;
    }

    public ResponseComparisonResult<ResponseDefinition> getResponseComparisonResult() {
        return responseComparisonResult;
    }
}
