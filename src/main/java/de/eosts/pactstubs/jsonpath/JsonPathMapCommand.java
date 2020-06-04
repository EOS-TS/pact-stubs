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

import com.jayway.jsonpath.MapFunction;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.WriteContext;

/**
 * Wrapper for jsonPath + mapFunction to overwrite values in response body based on existing value
 * <p>
 * value can be of arbitrary types, which will be converted by json provider to match json types (String -&gt; String, Map -&gt; Object, ...)
 */
public class JsonPathMapCommand extends JsonPathCommand {
    private final MapFunction mapFunction;

    public JsonPathMapCommand(String jsonPath, MapFunction mapFunction) {
        super(jsonPath);
        this.mapFunction = mapFunction;
    }

    public JsonPathMapCommand(String jsonPath, Predicate[] filters, MapFunction mapFunction) {
        super(jsonPath, filters);
        this.mapFunction = mapFunction;
    }

    @Override
    public WriteContext apply(WriteContext documentContext) {
        return documentContext.map(getJsonPath(), this.mapFunction, getFilters());
    }
}
