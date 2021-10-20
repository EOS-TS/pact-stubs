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
package de.eosts.pactstubs.wiremock;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.core.model.RequestResponsePact;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import de.eosts.pactstubs.loader.PactInteractionLoader;
import de.eosts.pactstubs.Pact2WireMock;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class Pact2WireMockExceptionTest {

    private static final String CONSUMER = "consumer";
    private static final String PROVIDER = "provider";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options(), false);

    @Test(expected = VerificationException.class)
    public void requestNotMatchFromPactToBodyAndHeader() throws IOException {

        RequestResponsePact requestResponsePact = ConsumerPactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).uponReceiving("request")
                .method("GET")
                .matchPath("/segment/.+")
                .body("{}")
                .willRespondWith()
                .status(200)
                .toPact();

        Pact2WireMock pact2WireMock = new Pact2WireMock(PactInteractionLoader.pactInstanceBuilder().pact(requestResponsePact).build(), new WireMock());
        pact2WireMock.stubFor(CONSUMER, PROVIDER, "request");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/segment/1");
        httpclient.execute(httpGet);

        List<LoggedRequest> unmatchedRequests = wireMockRule.findAllUnmatchedRequests();
        if (!unmatchedRequests.isEmpty()) {
            List<NearMiss> nearMisses = wireMockRule.findNearMissesForAllUnmatchedRequests();
            if (nearMisses.isEmpty()) {
                throw VerificationException.forUnmatchedRequests(unmatchedRequests);
            } else {
                throw VerificationException.forUnmatchedNearMisses(nearMisses);
            }
        }
    }
}
