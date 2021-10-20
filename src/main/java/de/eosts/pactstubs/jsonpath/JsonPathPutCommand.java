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
package de.eosts.pactstubs.jsonpath;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.WriteContext;
import org.slf4j.Logger;

/**
 * Wrapper for jsonPath + key/value to add new property in response body
 * <p>
 * value can be of arbitrary types, which will be converted by json provider to match json types (String -&gt; String, Map -&gt; Object, ...)
 */
public class JsonPathPutCommand extends JsonPathCommand {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JsonPathPutCommand.class);

    private final String key;
    private final Object value;

    public JsonPathPutCommand(String jsonPath, String key, Object value) {
        super(jsonPath);
        this.key = key;
        this.value = value;
    }

    public JsonPathPutCommand(String jsonPath, Predicate[] filters, String key, Object value) {
        super(jsonPath, filters);
        this.key = key;
        this.value = value;
    }

    @Override
    public WriteContext apply(WriteContext documentContext) {
        log.warn("JsonPathPutCommand potentially indicates missing contract (for optional fields e.g. as additional provider state). Adding the contract should be the preferred solution");
        return documentContext.put(getJsonPath(), this.key, this.value, getFilters());
    }
}
