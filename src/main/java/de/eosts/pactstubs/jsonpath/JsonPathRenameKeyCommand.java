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

/**
 * Wrapper for jsonPath to rename key in response body
 */
public class JsonPathRenameKeyCommand extends JsonPathCommand {
    private final String oldKeyName;
    private final String newKeyName;

    public JsonPathRenameKeyCommand(String jsonPath, String oldKeyName, String newKeyName) {
        super(jsonPath);
        this.oldKeyName = oldKeyName;
        this.newKeyName = newKeyName;
    }

    public JsonPathRenameKeyCommand(String jsonPath, Predicate[] filters, String oldKeyName, String newKeyName) {
        super(jsonPath, filters);
        this.oldKeyName = oldKeyName;
        this.newKeyName = newKeyName;
    }

    @Override
    public WriteContext apply(WriteContext documentContext) {
        return documentContext.renameKey(getJsonPath(), this.oldKeyName, this.newKeyName, getFilters());
    }
}
