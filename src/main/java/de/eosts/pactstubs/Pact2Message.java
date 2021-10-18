/**
 * Copyright 2020 EOS Technology Solutions GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.eosts.pactstubs;

import au.com.dius.pact.core.model.messaging.Message;
import com.github.michaelbull.result.Err;
import de.eosts.pactstubs.compare.ResponseComparisonResult;
import de.eosts.pactstubs.exception.MessageNotVerifiedException;
import de.eosts.pactstubs.jsonpath.JsonPathResponseConverter;
import de.eosts.pactstubs.jsonpath.WriteContextOperator;
import de.eosts.pactstubs.loader.PactInteractionLoader;
import de.eosts.pactstubs.message.PactMessageConverter;

/**
 * Entry point to generate messages from Pacts
 * <p>
 * Get pacts via {@link PactInteractionLoader} and manipulate messages via {@link WriteContextOperator}s
 */
public class Pact2Message {

    private final PactInteractionLoader pactInteractionLoader;
    private final JsonPathResponseConverter<Message, String> pactMessageConverter;

    public Pact2Message(PactInteractionLoader pactInteractionLoader) {
        this.pactInteractionLoader = pactInteractionLoader;
        this.pactMessageConverter = new PactMessageConverter();
    }

    public Pact2Message(PactInteractionLoader pactInteractionLoader, JsonPathResponseConverter<Message, String> pactMessageConverter) {
        this.pactInteractionLoader = pactInteractionLoader;
        this.pactMessageConverter = pactMessageConverter;
    }

    /**
     * Returns the resulting message only.
     *
     * @param consumer             consumer
     * @param provider             provider
     * @param description          description
     * @param writeContextOperator writeContextOperator
     * @return the generated message based on pact, manipulated by writeContextOperator
     * @throws MessageNotVerifiedException if converted message differs from pact
     */
    public String message(String consumer, String provider, String description, WriteContextOperator... writeContextOperator) {
        return message((Message) pactInteractionLoader.getInteraction(consumer, provider, description), writeContextOperator);
    }

    /**
     * Returns the resulting message only.
     *
     * @param consumer             consumer
     * @param provider             provider
     * @param description          description
     * @param providerState        providerState
     * @param writeContextOperator writeContextOperator
     * @return the generated message based on pact, manipulated by writeContextOperator
     * @throws MessageNotVerifiedException if converted message differs from pact
     */
    public String message(String consumer, String provider, String description, String providerState, WriteContextOperator... writeContextOperator) {
        return message((Message) pactInteractionLoader.getInteraction(consumer, provider, description, providerState), writeContextOperator);
    }

    /**
     * Use interaction to generate message and manipulate it via writeContextOperator
     *
     * @param interaction          interaction
     * @param writeContextOperator writeContextOperator
     * @return the generated message based on pact, manipulated by writeContextOperator
     * @throws MessageNotVerifiedException if converted message differs from pact
     */
    public String message(Message interaction, WriteContextOperator... writeContextOperator) {
        ResponseComparisonResult<String> convertedMessage = pactMessageConverter.convert(interaction, writeContextOperator);
        if (convertedMessage.getComparisonResult() != null &&
                (convertedMessage.getComparisonResult().getBodyMismatches() instanceof Err || !convertedMessage.getComparisonResult().getBodyMismatches().component1().getMismatches().isEmpty())) {
            throw new MessageNotVerifiedException("Differences in \"" + interaction.getDescription() + "\": " + convertedMessage.getComparisonResult(), interaction.getContents(), convertedMessage);
        }
        return convertedMessage.getResponse();
    }

    public PactInteractionLoader getPactInteractionLoader() {
        return this.pactInteractionLoader;
    }
}
