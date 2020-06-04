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

import java.util.Objects;
import java.util.Optional;

public class InteractionIdentifier {

    private final String consumer;
    private final String provider;
    private final String description;
    private final String providerState;

    InteractionIdentifier(String consumer, String provider, String description, String providerState) {
        this.consumer = Objects.requireNonNull(consumer);
        this.provider = Objects.requireNonNull(provider);
        this.description = Objects.requireNonNull(description);
        this.providerState = providerState;
    }

    public static InteractionIdentifierBuilder builder() {
        return new InteractionIdentifierBuilder();
    }

    public String getConsumer() {
        return consumer;
    }

    public String getProvider() {
        return provider;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getProviderState() {
        return Optional.ofNullable(providerState);
    }

    public static class InteractionIdentifierBuilder {
        private String consumer;
        private String provider;
        private String description;
        private String providerState;

        InteractionIdentifierBuilder() {
        }

        public InteractionIdentifierBuilder consumer(String consumer) {
            this.consumer = consumer;
            return this;
        }

        public InteractionIdentifierBuilder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public InteractionIdentifierBuilder description(String description) {
            this.description = description;
            return this;
        }

        public InteractionIdentifierBuilder providerState(String providerState) {
            this.providerState = providerState;
            return this;
        }

        public InteractionIdentifier build() {
            return new InteractionIdentifier(consumer, provider, description, providerState);
        }
    }

}
