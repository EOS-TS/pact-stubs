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
package de.eosts.pactstubs.message;

import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.provider.ComparisonResult;
import de.eosts.pactstubs.compare.PactMessageComparator;
import de.eosts.pactstubs.jsonpath.JsonPathWriter;
import de.eosts.pactstubs.jsonpath.WriteContextOperator;
import de.eosts.pactstubs.compare.ResponseComparator;
import de.eosts.pactstubs.compare.ResponseComparisonResult;
import de.eosts.pactstubs.jsonpath.JsonPathResponseConverter;

public class PactMessageConverter implements JsonPathResponseConverter<Message, String> {

    private final ResponseComparator<Message, String> messageResponseComparator;
    private final JsonPathWriter jsonPathWriter;

    public PactMessageConverter() {
        this.messageResponseComparator = new PactMessageComparator();
        this.jsonPathWriter = new JsonPathWriter();
    }

    public PactMessageConverter(ResponseComparator<Message, String> messageResponseComparator) {
        this.messageResponseComparator = messageResponseComparator;
        this.jsonPathWriter = new JsonPathWriter();
    }

    public PactMessageConverter(JsonPathWriter jsonPathWriter) {
        this.messageResponseComparator = new PactMessageComparator();
        this.jsonPathWriter = jsonPathWriter;
    }

    public PactMessageConverter(ResponseComparator<Message, String> messageResponseComparator, JsonPathWriter jsonPathWriter) {
        this.messageResponseComparator = messageResponseComparator;
        this.jsonPathWriter = jsonPathWriter;
    }

    @Override
    public ResponseComparisonResult<String> convert(Message interaction, WriteContextOperator... operators) {
        String message;
        ComparisonResult validationResult = null;
        String pactContents = new String(interaction.getContents().getValue());
        if (operators.length > 0) {
            message = jsonPathWriter.write(pactContents, operators);
            validationResult = messageResponseComparator.compare(interaction, message);
        } else {
            message = pactContents;
        }

        return new ResponseComparisonResult<>(message, validationResult);
    }
}
