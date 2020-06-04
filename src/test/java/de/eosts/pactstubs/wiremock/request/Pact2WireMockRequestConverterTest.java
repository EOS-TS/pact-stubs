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
package de.eosts.pactstubs.wiremock.request;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.core.model.Request;
import de.eosts.pactstubs.compare.RequestComparisonResult;
import de.eosts.pactstubs.spec.SpecificRequestSpec;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Pact2WireMockRequestConverterTest {

    @Test
    public void shouldConvertMethod() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("PUT")
                .path("/segment/test")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        Assert.assertEquals("PUT", new Pact2WireMockRequestConverter(pactRequest).convert().getRequestPattern().getMethod().getName());

    }

    @Test
    public void shouldConvertUrlPath() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .path("/segment/test")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        Assert.assertEquals("/segment/test", new Pact2WireMockRequestConverter(pactRequest).convert().getRequestPattern().getUrlPath());
    }

    @Test
    public void shouldConvertUrlPathPattern() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .matchPath("/segment/.*")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        Assert.assertEquals("/segment/.*", new Pact2WireMockRequestConverter(pactRequest).convert().getRequestPattern().getUrlPathPattern());
    }

    @Test
    public void shouldConvertSpecificUrlPathNoMismatch() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .matchPath("/segment/.*")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        RequestComparisonResult comparisonResult = new Pact2WireMockRequestConverter(pactRequest, SpecificRequestSpec.builder().urlPath("/segment/").build()).convert();
        assertEquals("/segment/", comparisonResult.getRequestPattern().getUrlPath());
        assertTrue(comparisonResult.getComparisonResult().isEmpty());
    }

    @Test
    public void shouldConvertSpecificUrlPathWithMismatches() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .matchPath("/segment/.*")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();
        RequestComparisonResult comparisonResult = new Pact2WireMockRequestConverter(pactRequest, SpecificRequestSpec.builder().urlPath("/other/").build()).convert();

        assertEquals("/other/", comparisonResult.getRequestPattern().getUrlPath());

        assertEquals(new HashMap<String, String>() {{
            put("/segment/.*", "/other/");
        }}, comparisonResult.getComparisonResult());
    }

    @Test
    public void shouldConvertQueryParams() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .path("/segment/")
                .query("a=b&c=d")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        Assert.assertEquals("b", new Pact2WireMockRequestConverter(pactRequest).convert().getRequestPattern().getQueryParameters().get("a").getExpected());
        Assert.assertEquals("d", new Pact2WireMockRequestConverter(pactRequest).convert().getRequestPattern().getQueryParameters().get("c").getExpected());
    }

    @Test
    public void shouldConvertQueryParamPattern() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .path("/segment/")
                .matchQuery("a", ".*")
                .matchQuery("c", ".*")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        RequestComparisonResult comparisonResult = new Pact2WireMockRequestConverter(pactRequest).convert();

        assertEquals(".*", comparisonResult.getRequestPattern().getQueryParameters().get("a").getExpected());
        assertEquals(".*", comparisonResult.getRequestPattern().getQueryParameters().get("c").getExpected());
    }

    @Test
    public void shouldConvertSpecificQueryParamPatternNoMismatch() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .path("/segment/")
                .matchQuery("a", ".*")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        HashMap<String, String> specificQueryParams = new HashMap<>();
        specificQueryParams.put("a", "b");

        RequestComparisonResult comparisonResult = new Pact2WireMockRequestConverter(pactRequest, SpecificRequestSpec.builder().queryParams(specificQueryParams).build()).convert();
        assertEquals("b", comparisonResult.getRequestPattern().getQueryParameters().get("a").getExpected());
        assertTrue(comparisonResult.getComparisonResult().isEmpty());
    }

    @Test
    public void shouldConvertSpecificQueryParamPatternMismatches() {
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("GET")
                .path("/segment/")
                .query("a=11&b=12&c=any&d=match")
                .matchQuery("a", "1.*")
                .matchQuery("c", ".*")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        HashMap<String, String> specificQueryParams = new HashMap<>();
        specificQueryParams.put("a", "mismatch");
        specificQueryParams.put("d", "match");

        RequestComparisonResult convert = new Pact2WireMockRequestConverter(pactRequest, SpecificRequestSpec.builder().queryParams(specificQueryParams).build()).convert();
        assertEquals("mismatch", convert.getRequestPattern().getQueryParameters().get("a").getExpected());

        Map<String, String> comparisonResult = convert.getComparisonResult();
        assertFalse(comparisonResult.isEmpty());
        assertEquals("mismatch !~ 1.*", comparisonResult.get("a"));
        assertEquals("null != 12", comparisonResult.get("b"));
        assertEquals("null !~ .*", comparisonResult.get("c"));
        assertNull(comparisonResult.get("d"));
    }

    @Test
    public void shouldConvertSpecificBodyPattern(){
        Request pactRequest = ConsumerPactBuilder.consumer("consumer").hasPactWith("provider").uponReceiving("description")
                .method("POST")
                .path("/segment/")
                .body("{}")
                .willRespondWith()
                .toPact().getInteractions().get(0).getRequest();

        RequestComparisonResult convert = new Pact2WireMockRequestConverter(pactRequest, SpecificRequestSpec.builder().jsonBody("{\"key\": \"value\"}").build()).convert();

        assertEquals("{\"key\": \"value\"}", convert.getRequestPattern().getBodyPatterns().get(0).getValue());


    }

}