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
package de.eosts.pactstubs.loader;

import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Builder for PactInteractionLoader via PactBroker
 */
public class PactBrokerInteractionLoaderBuilder {
    private String pactBrokerHost;
    private String pactBrokerPort;
    private String pactBrokerProtocol;
    private List<String> pactBrokerTags;
    private List<String> pactBrokerConsumers = new ArrayList<>();
    private PactBrokerAuth pactBrokerAuth;

    PactBrokerInteractionLoaderBuilder() {
    }

    public PactBrokerInteractionLoaderBuilder pactBrokerHost(String pactBrokerHost) {
        this.pactBrokerHost = pactBrokerHost;
        return this;
    }

    public PactBrokerInteractionLoaderBuilder pactBrokerPort(String pactBrokerPort) {
        this.pactBrokerPort = pactBrokerPort;
        return this;
    }

    public PactBrokerInteractionLoaderBuilder pactBrokerProtocol(String pactBrokerProtocol) {
        this.pactBrokerProtocol = pactBrokerProtocol;
        return this;
    }

    public PactBrokerInteractionLoaderBuilder pactBrokerTags(List<String> pactBrokerTags) {
        this.pactBrokerTags = pactBrokerTags;
        return this;
    }

    public PactBrokerInteractionLoaderBuilder pactBrokerConsumers(List<String> pactBrokerConsumers) {
        this.pactBrokerConsumers = pactBrokerConsumers;
        return this;
    }

    public PactBrokerInteractionLoaderBuilder pactBrokerAuth(PactBrokerAuth pactBrokerAuth) {
        this.pactBrokerAuth = pactBrokerAuth;
        return this;
    }

    public PactInteractionLoader build() {
        return new PactInteractionLoader(new PactBrokerLoader(
                pactBrokerHost,
                pactBrokerPort,
                pactBrokerProtocol,
                pactBrokerTags != null ? pactBrokerTags : Collections.singletonList("latest"),
                emptyList(),
                pactBrokerConsumers,
                true,
                pactBrokerAuth,
                null,
                null,
                "false",
                emptyList(),
                "",
                null));

    }
}
