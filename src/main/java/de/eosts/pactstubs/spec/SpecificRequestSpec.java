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
package de.eosts.pactstubs.spec;

import java.util.Map;
import java.util.Optional;

/**
 * This class provides a specific urlPath, query params and json requestBody for an otherwise regex based pact definition
 */
public class SpecificRequestSpec {
    private final String urlPath;
    private final Map<String, String> queryParams;
    private final String jsonBody;

    SpecificRequestSpec(String urlPath, Map<String, String> queryParams, String jsonBody) {
        this.urlPath = urlPath;
        this.queryParams = queryParams;
        this.jsonBody = jsonBody;
    }

    public static SpecificRequestSpecBuilder builder() {
        return new SpecificRequestSpecBuilder();
    }

    public Optional<String> getUrlPath() {
        return Optional.ofNullable(urlPath);
    }

    public Optional<Map<String, String>> getQueryParams() {
        return Optional.ofNullable(queryParams);
    }

    public Optional<String> getJsonBody() {
        return Optional.ofNullable(jsonBody);
    }

    public static class SpecificRequestSpecBuilder {
        private String urlPath;
        private Map<String, String> queryParams;
        private String jsonBody;

        SpecificRequestSpecBuilder() {
        }

        public SpecificRequestSpecBuilder urlPath(String urlPath) {
            this.urlPath = urlPath;
            return this;
        }

        public SpecificRequestSpecBuilder queryParams(Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public SpecificRequestSpecBuilder jsonBody(String jsonBody) {
            this.jsonBody = jsonBody;
            return this;
        }

        public SpecificRequestSpec build() {
            return new SpecificRequestSpec(urlPath, queryParams, jsonBody);
        }

    }

}
