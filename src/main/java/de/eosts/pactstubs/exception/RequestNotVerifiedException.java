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

import au.com.dius.pact.core.model.Request;
import de.eosts.pactstubs.compare.RequestComparisonResult;

/**
 * RuntimeException to signal a specific request does not fulfill pact
 */
public class RequestNotVerifiedException extends RuntimeException implements RequestComparisonResultException {

    private final transient Request pactRequest;
    private final transient RequestComparisonResult requestComparisonResult;

    public RequestNotVerifiedException(Request pactRequest, RequestComparisonResult requestComparisonResult) {
        this.pactRequest = pactRequest;
        this.requestComparisonResult = requestComparisonResult;
    }

    public RequestNotVerifiedException(String s, Request pactRequest, RequestComparisonResult requestComparisonResult) {
        super(s);
        this.pactRequest = pactRequest;
        this.requestComparisonResult = requestComparisonResult;
    }

    public RequestNotVerifiedException(String s, Throwable throwable, Request pactRequest, RequestComparisonResult requestComparisonResult) {
        super(s, throwable);
        this.pactRequest = pactRequest;
        this.requestComparisonResult = requestComparisonResult;
    }

    public RequestNotVerifiedException(Throwable throwable, Request pactRequest, RequestComparisonResult requestComparisonResult) {
        super(throwable);
        this.pactRequest = pactRequest;
        this.requestComparisonResult = requestComparisonResult;
    }

    public RequestNotVerifiedException(String s, Throwable throwable, boolean b, boolean b1, Request pactRequest, RequestComparisonResult requestComparisonResult) {
        super(s, throwable, b, b1);
        this.pactRequest = pactRequest;
        this.requestComparisonResult = requestComparisonResult;
    }

    public Request getPactRequest() {
        return pactRequest;
    }

    public RequestComparisonResult getRequestComparisonResult() {
        return requestComparisonResult;
    }
}
