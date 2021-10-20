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

import au.com.dius.pact.provider.ComparisonResult;

public class ResponseComparisonResult<T> {

    private final T response;
    private final ComparisonResult comparisonResult;

    public ResponseComparisonResult(T response, ComparisonResult comparisonResult) {
        this.response = response;
        this.comparisonResult = comparisonResult;
    }

    public T getResponse() {
        return this.response;
    }

    public ComparisonResult getComparisonResult() {
        return this.comparisonResult;
    }
}
