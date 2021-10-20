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

import au.com.dius.pact.core.model.RequestResponseInteraction;
import au.com.dius.pact.core.model.Response;
import com.github.michaelbull.result.Err;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import de.eosts.pactstubs.compare.RequestComparisonResult;
import de.eosts.pactstubs.compare.ResponseComparisonResult;
import de.eosts.pactstubs.exception.RequestNotVerifiedException;
import de.eosts.pactstubs.exception.ResponseNotVerifiedException;
import de.eosts.pactstubs.jsonpath.JsonPathResponseConverter;
import de.eosts.pactstubs.jsonpath.WriteContextOperator;
import de.eosts.pactstubs.loader.PactInteractionLoader;
import de.eosts.pactstubs.spec.InteractionIdentifier;
import de.eosts.pactstubs.spec.SpecificRequestSpec;
import de.eosts.pactstubs.wiremock.request.Pact2WireMockRequestConverter;
import de.eosts.pactstubs.wiremock.response.PactResponseConverter;
import org.slf4j.Logger;

/**
 * Entry point for WireMock Stubs based on Pacts
 * <p>
 * Get pacts via {@link PactInteractionLoader}, provide {@link WireMock} instance and manipulate responses via {@link WriteContextOperator}s
 * in addition to the {@link WriteContextOperator}s, a {@link PactResponseConverter} can be provided to customize jsonPath based serialization
 */
public class Pact2WireMock {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Pact2WireMock.class);

    private final PactInteractionLoader pactInteractionLoader;
    private final WireMock wireMock;
    private final JsonPathResponseConverter<Response, ResponseDefinition> pactResponseConverter;

    public Pact2WireMock(PactInteractionLoader pactInteractionLoader) {
        this.pactInteractionLoader = pactInteractionLoader;
        this.wireMock = new WireMock();
        this.pactResponseConverter = new PactResponseConverter();
    }

    public Pact2WireMock(PactInteractionLoader pactInteractionLoader, WireMock wireMock) {
        this.pactInteractionLoader = pactInteractionLoader;
        this.wireMock = wireMock;
        this.pactResponseConverter = new PactResponseConverter();
    }

    public Pact2WireMock(PactInteractionLoader pactInteractionLoader, WireMock wireMock, JsonPathResponseConverter<Response, ResponseDefinition> pactResponseConverter) {
        this.pactInteractionLoader = pactInteractionLoader;
        this.wireMock = wireMock;
        this.pactResponseConverter = pactResponseConverter;
    }

    /**
     * Create stub form interaction defined by consumer, provider, description and manipulate response via writeContextOperators
     *
     * @param consumer              consumer
     * @param provider              provider
     * @param description           pact description
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     */
    public void stubFor(String consumer, String provider, String description, WriteContextOperator... writeContextOperators) {
        stubFor(consumer, provider, description, SpecificRequestSpec.builder().build(), writeContextOperators);
    }

    /**
     * Create stub form interaction defined by consumer, provider, description and manipulate response via writeContextOperators
     *
     * @param consumer              consumer
     * @param provider              provider
     * @param description           pact description
     * @param specificRequestSpec   specifies a concrete request url or query params in regex based pacts
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     * @throws RequestNotVerifiedException  if comparator finds differences between the converted request and its pact
     */
    public void stubFor(String consumer, String provider, String description, SpecificRequestSpec specificRequestSpec, WriteContextOperator... writeContextOperators) {
        RequestResponseInteraction interaction = (RequestResponseInteraction) pactInteractionLoader.getInteraction(consumer, provider, description);
        stubFor(interaction, specificRequestSpec, writeContextOperators);
    }

    /**
     * Create stub form interaction defined by consumer, provider, description, providerState and manipulate response via writeContextOperators
     *
     * @param consumer              consumer
     * @param provider              provider
     * @param description           pact description
     * @param providerState         providerState
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     */
    public void stubFor(String consumer, String provider, String description, String providerState, WriteContextOperator... writeContextOperators) {
        stubFor(consumer, provider, description, providerState, SpecificRequestSpec.builder().build(), writeContextOperators);
    }

    /**
     * Create stub from interaction defined by consumer, provider, description and manipulate response via writeContextOperators
     *
     * @param consumer              consumer
     * @param provider              provider
     * @param description           pact description
     * @param providerState         providerState
     * @param specificRequestSpec   specifies a concrete request url or query params in regex based pacts
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     * @throws RequestNotVerifiedException  if comparator finds differences between the converted request and its pact
     */
    public void stubFor(String consumer, String provider, String description, String providerState, SpecificRequestSpec specificRequestSpec, WriteContextOperator... writeContextOperators) {
        RequestResponseInteraction interaction = (RequestResponseInteraction) pactInteractionLoader.getInteraction(consumer, provider, description, providerState);
        stubFor(interaction, specificRequestSpec, writeContextOperators);
    }

    /**
     * Create stub from interaction identifier and manipulate response via writeContextOperators
     *
     * @param interactionIdentifier interactionIdentifier
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     */
    public void stubFor(InteractionIdentifier interactionIdentifier, WriteContextOperator... writeContextOperators) {
        stubFor(interactionIdentifier, SpecificRequestSpec.builder().build(), writeContextOperators);
    }

    /**
     * Create stub from interaction identifier and manipulate response via writeContextOperators
     *
     * @param interactionIdentifier interactionIdentifier
     * @param specificRequestSpec   specifies a concrete request url or query params in regex based pacts
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     * @throws RequestNotVerifiedException  if comparator finds differences between the converted request and its pact
     */
    public void stubFor(InteractionIdentifier interactionIdentifier, SpecificRequestSpec specificRequestSpec, WriteContextOperator... writeContextOperators) {

        RequestResponseInteraction interaction = (RequestResponseInteraction) interactionIdentifier.getProviderState()
                .map(state -> pactInteractionLoader.getInteraction(
                        interactionIdentifier.getConsumer(), interactionIdentifier.getProvider(), interactionIdentifier.getDescription(), state))
                .orElseGet(() -> pactInteractionLoader.getInteraction(interactionIdentifier.getConsumer(), interactionIdentifier.getProvider(), interactionIdentifier.getDescription()));

        stubFor(interaction, specificRequestSpec, writeContextOperators);
    }

    /**
     * Use interaction for stub and manipulate response via writeContextOperators
     *
     * @param interaction           interaction
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     * @see au.com.dius.pact.core.model.Interaction
     */
    public void stubFor(RequestResponseInteraction interaction, WriteContextOperator... writeContextOperators) {
        stubFor(interaction, SpecificRequestSpec.builder().build(), writeContextOperators);
    }

    /**
     * Use interaction for stub and manipulate response via writeContextOperators
     *
     * @param interaction           interaction
     * @param specificRequestSpec   specifies a concrete request url or query params in regex based pacts
     * @param writeContextOperators writeContextOperators
     * @throws ResponseNotVerifiedException if comparator finds differences between the converted response and its pact
     * @throws RequestNotVerifiedException  if comparator finds differences between the converted request and its pact
     * @see au.com.dius.pact.core.model.Interaction
     */
    public void stubFor(RequestResponseInteraction interaction, SpecificRequestSpec specificRequestSpec, WriteContextOperator... writeContextOperators) {
        RequestComparisonResult requestComparisonResult = new Pact2WireMockRequestConverter(interaction.getRequest(), specificRequestSpec).convert();
        ResponseComparisonResult<ResponseDefinition> responseComparisonResult = pactResponseConverter.convert(interaction.getResponse(), writeContextOperators);

        ResponseDefinition responseDefinition = responseComparisonResult.getResponse();
        RequestPattern requestPattern = requestComparisonResult.getRequestPattern();

        log.debug("Configure WireMock with RequestPattern: {}", requestPattern);
        log.debug("Configure WireMock with ResponseDefinition: {}", responseDefinition);
        wireMock.register(new StubMapping(requestPattern, responseDefinition));
        if (responseComparisonResult.getComparisonResult() != null &&
                (responseComparisonResult.getComparisonResult().getBodyMismatches() instanceof Err || !responseComparisonResult.getComparisonResult().getBodyMismatches().component1().getMismatches().isEmpty())) {
            throw new ResponseNotVerifiedException("Differences in \"" + interaction.getDescription() + "\": " + responseComparisonResult.getComparisonResult(), interaction.getResponse(), responseComparisonResult);
        }
        if (requestComparisonResult.getComparisonResult() != null && !requestComparisonResult.getComparisonResult().isEmpty()) {
            throw new RequestNotVerifiedException("Specific path and/or query doesn't match pact: {}", interaction.getRequest(), requestComparisonResult);
        }
    }

    public PactInteractionLoader getPactInteractionLoader() {
        return this.pactInteractionLoader;
    }

    public WireMock getWireMock() {
        return this.wireMock;
    }
}
