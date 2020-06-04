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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.WriteContext;

import java.util.Arrays;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;

public class JsonPathWriter {

    private final Configuration configuration;

    public JsonPathWriter() {
        this.configuration = defaultConfiguration().setOptions(Option.REQUIRE_PROPERTIES);
    }

    public JsonPathWriter(Configuration configuration) {
        this.configuration = configuration;
    }

    public String write(String content, WriteContextOperator... operators) {
        WriteContext writeContext = JsonPath.parse(content, configuration);
        Arrays.stream(operators).forEach(writeContextOperator -> writeContextOperator.apply(writeContext));
        return writeContext.jsonString();
    }
}
