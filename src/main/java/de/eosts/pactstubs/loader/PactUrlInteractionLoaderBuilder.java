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

import au.com.dius.pact.core.support.Auth;
import au.com.dius.pact.provider.junitsupport.loader.PactUrlLoader;

/**
 * Builder for PactInterationLoader via urls
 */
public class PactUrlInteractionLoaderBuilder {
    private String[] urls;
    private Auth authentication = null;

    PactUrlInteractionLoaderBuilder() {
    }

    public PactUrlInteractionLoaderBuilder urls(String[] urls) {
        this.urls = urls;
        return this;
    }

    public PactUrlInteractionLoaderBuilder authentication(Auth authentication) {
        this.authentication = authentication;
        return this;
    }

    public PactInteractionLoader build() {
        return new PactInteractionLoader(new PactUrlLoader(urls, authentication));
    }

}
