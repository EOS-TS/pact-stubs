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
package de.eosts.pactstubs.loader;

import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.core.model.PactSource;
import au.com.dius.pact.core.model.UnknownPactSource;
import au.com.dius.pact.provider.junitsupport.loader.PactLoader;

import java.util.Collections;
import java.util.List;

/**
 * Builder for PactInteractionLoader from pact instance
 */
public class PactInstanceInteractionLoaderBuilder {
    private Pact pact;

    PactInstanceInteractionLoaderBuilder() {
    }

    public PactInstanceInteractionLoaderBuilder pact(Pact pact) {
        this.pact = pact;
        return this;
    }

    public PactInteractionLoader build() {
        return new PactInteractionLoader(new PactLoader() {
            @Override
            public List<Pact> load(String providerName) {
                if (pact.getProvider().getName().equals(providerName)) {
                    return Collections.singletonList(pact);
                }

                return Collections.emptyList();
            }

            @Override
            public PactSource getPactSource() {
                return UnknownPactSource.INSTANCE;
            }
        });
    }
}
