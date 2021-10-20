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
package de.eosts.pactstubs;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.core.model.messaging.MessagePact;
import de.eosts.pactstubs.exception.MessageNotVerifiedException;
import de.eosts.pactstubs.jsonpath.JsonPathSetCommand;
import de.eosts.pactstubs.loader.PactInteractionLoader;
import org.junit.Assert;
import org.junit.Test;

public class PactMessageTest {

    private static final String CONSUMER = "consumer";
    private static final String PROVIDER = "provider";

    @Test
    public void shouldReturnMessageFromPact() {
        String description = "message description";

        MessagePact messagePact = MessagePactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).expectsToReceive(description)
                .withContent(new PactDslJsonBody().stringValue("key", "value"))
                .toPact();

        Pact2Message pact2Message = new Pact2Message(PactInteractionLoader.pactInstanceBuilder().pact(messagePact).build());
        Assert.assertEquals("{\"key\":\"value\"}", pact2Message.message(CONSUMER, PROVIDER, description));
    }

    @Test
    public void shouldReturnManipulatedMessageFromPact() {
        String description = "message description";

        MessagePact messagePact = MessagePactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).expectsToReceive(description)
                .withContent(new PactDslJsonBody().stringType("key", "value"))
                .toPact();

        Pact2Message pact2Message = new Pact2Message(PactInteractionLoader.pactInstanceBuilder().pact(messagePact).build());
        Assert.assertEquals("{\"key\":\"newValue\"}", pact2Message.message(CONSUMER, PROVIDER, description, new JsonPathSetCommand("$.key", "newValue")));
    }

    @Test(expected = MessageNotVerifiedException.class)
    public void shouldThrowNotVerifiedException() {
        String description = "message description";

        MessagePact messagePact = MessagePactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).expectsToReceive(description)
                .withContent(new PactDslJsonBody().stringValue("key", "value"))
                .toPact();

        Pact2Message pact2Message = new Pact2Message(PactInteractionLoader.pactInstanceBuilder().pact(messagePact).build());
        pact2Message.message(CONSUMER, PROVIDER, description, new JsonPathSetCommand("$.key", "non confirming change"));
    }
}