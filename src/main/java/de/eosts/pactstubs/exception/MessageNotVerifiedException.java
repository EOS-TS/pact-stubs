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
package de.eosts.pactstubs.exception;

import au.com.dius.pact.core.model.OptionalBody;
import de.eosts.pactstubs.compare.ResponseComparisonResult;

/**
 * RuntimeException to signal a converted message that does not fulfill pact
 */
public class MessageNotVerifiedException extends RuntimeException implements ResponseComparisonResultException<String> {

    private final transient OptionalBody optionalBody;
    private final transient ResponseComparisonResult<String> responseComparisonResult;

    public MessageNotVerifiedException(OptionalBody optionalBody, ResponseComparisonResult<String> responseComparisonResult) {
        this.optionalBody = optionalBody;
        this.responseComparisonResult = responseComparisonResult;
    }

    public MessageNotVerifiedException(String s, OptionalBody optionalBody, ResponseComparisonResult<String> responseComparisonResult) {
        super(s);
        this.optionalBody = optionalBody;
        this.responseComparisonResult = responseComparisonResult;
    }

    public MessageNotVerifiedException(String s, Throwable throwable, OptionalBody optionalBody, ResponseComparisonResult<String> responseComparisonResult) {
        super(s, throwable);
        this.optionalBody = optionalBody;
        this.responseComparisonResult = responseComparisonResult;
    }

    public MessageNotVerifiedException(Throwable throwable, OptionalBody optionalBody, ResponseComparisonResult<String> responseComparisonResult) {
        super(throwable);
        this.optionalBody = optionalBody;
        this.responseComparisonResult = responseComparisonResult;
    }

    public MessageNotVerifiedException(String s, Throwable throwable, boolean b, boolean b1, OptionalBody optionalBody, ResponseComparisonResult<String> responseComparisonResult) {
        super(s, throwable, b, b1);
        this.optionalBody = optionalBody;
        this.responseComparisonResult = responseComparisonResult;
    }

    public OptionalBody getOptionalBody() {
        return optionalBody;
    }

    public ResponseComparisonResult<String> getResponseComparisonResult() {
        return responseComparisonResult;
    }
}
