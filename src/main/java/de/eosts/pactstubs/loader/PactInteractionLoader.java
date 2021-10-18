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
package de.eosts.pactstubs.loader;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.provider.junitsupport.loader.PactLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads interaction via folder, urls, pactbroker or from pact model instance
 */
public class PactInteractionLoader {
    private final PactLoader pactLoader;
    private final Map<PactPair, List<Interaction>> loadedInteractions = new HashMap<>();

    public PactInteractionLoader(PactLoader pactLoader) {
        this.pactLoader = pactLoader;
    }

    public static PactBrokerInteractionLoaderBuilder pactBrokerBuilder() {
        return new PactBrokerInteractionLoaderBuilder();
    }

    public static PactFolderInteractionLoaderBuilder folderBuilder() {
        return new PactFolderInteractionLoaderBuilder();
    }

    public static PactUrlInteractionLoaderBuilder urlsBuilder() {
        return new PactUrlInteractionLoaderBuilder();
    }

    public static PactInstanceInteractionLoaderBuilder pactInstanceBuilder() {
        return new PactInstanceInteractionLoaderBuilder();
    }

    public Interaction getInteraction(String consumer, String provider, String description) {
        List<Interaction> interactions = getOrCreateLoadedInteractions(consumer, provider);
        return interactions.stream()
                .filter(interaction -> interaction.getDescription().equals(description))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    public Interaction getInteraction(String consumer, String provider, String description, String providerState) {
        List<Interaction> interactions = getOrCreateLoadedInteractions(consumer, provider);
        return interactions.stream()
                .filter(interaction -> interaction.getDescription().equals(description)
                        && interaction.getProviderStates().stream().anyMatch(interactionState -> interactionState.getName().equals(providerState)))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private List<Interaction> getOrCreateLoadedInteractions(String consumer, String provider) {
        List<Interaction> interactions = loadedInteractions.get(new PactPair(consumer, provider));
        if (interactions == null) {
            try {
                interactions = pactLoader.load(provider).stream()
                        .filter(pact -> pact.getConsumer().getName().equals(consumer))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("No interactions found for consumer " + consumer + " and provider " + provider))
                        .getInteractions();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            loadedInteractions.put(new PactPair(consumer, provider), interactions);
        }
        return interactions;
    }

    private static class PactPair {
        private final String consumer;
        private final String provider;

        public PactPair(String consumer, String provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            PactPair pair = (PactPair) o;
            return Objects.equals(consumer, pair.consumer) &&
                    Objects.equals(provider, pair.provider);
        }

        @Override
        public int hashCode() {
            return Objects.hash(consumer, provider);
        }
    }

}
