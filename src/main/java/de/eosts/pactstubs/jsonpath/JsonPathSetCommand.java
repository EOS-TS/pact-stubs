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
package de.eosts.pactstubs.jsonpath;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.WriteContext;

/**
 * Wrapper for jsonPath + value to overwrite values in response body
 * <p>
 * value can be of arbitrary types, which will be converted by json provider to match json types (String -&gt; String, Map -&gt; Object, ...)
 */
public class JsonPathSetCommand extends JsonPathCommand {
    private final Object newValue;

    public JsonPathSetCommand(String jsonPath, Object newValue) {
        super(jsonPath);
        this.newValue = newValue;
    }

    public JsonPathSetCommand(String jsonPath, Predicate[] filters, Object newValue) {
        super(jsonPath, filters);
        this.newValue = newValue;
    }

    @Override
    public WriteContext apply(WriteContext documentContext) {
        return documentContext.set(getJsonPath(), this.newValue, getFilters());
    }
}
