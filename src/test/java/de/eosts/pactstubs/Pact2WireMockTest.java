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

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.core.model.RequestResponsePact;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.eosts.pactstubs.exception.ResponseNotVerifiedException;
import de.eosts.pactstubs.jsonpath.JsonPathSetCommand;
import de.eosts.pactstubs.loader.PactInteractionLoader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Pact2WireMockTest {

    private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";
    private static final String CONSUMER = "consumer";
    private static final String PROVIDER = "provider";

    private CloseableHttpClient httpclient = HttpClients.createDefault();
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void shouldMatchCompleteGETRequestAndRespond() throws IOException {
        String requestWithQueryParams = "request with query params";
        String thisIsTheResponse = "this is the response";

        RequestResponsePact requestResponsePact = ConsumerPactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).uponReceiving(requestWithQueryParams)
                .method("GET")
                .matchPath("/segment/.+")
                .matchHeader("Accept", "application/.+")
                .matchQuery("param", "value.+")
                .willRespondWith()
                .status(200)
                .matchHeader("Content-Type", "application/.+", "application/json")
                .body(thisIsTheResponse)
                .toPact();

        Pact2WireMock pact2WireMock = new Pact2WireMock(PactInteractionLoader.pactInstanceBuilder().pact(requestResponsePact).build(), new WireMock());
        pact2WireMock.stubFor(CONSUMER, PROVIDER, requestWithQueryParams);

        HttpGet httpGet = new HttpGet(HTTP_LOCALHOST_8080 + "/segment/1?param=valueX");
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(byteArrayOutputStream);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals("application/json", response.getHeaders("Content-Type")[0].getValue());
        Assert.assertEquals(thisIsTheResponse, byteArrayOutputStream.toString());
    }

    @Test
    public void shouldMatchBody() throws IOException {
        String requestWithBody = "request with body";
        RequestResponsePact requestResponsePact = ConsumerPactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).uponReceiving("request with body")
                .method("POST")
                .path("/segment/")
                .body(
                        new PactDslJsonBody()
                                //expressions
                                .stringType("string")
                                .date("date")
                                .numberType("number")
                                .integerType("integer")
                                .decimalType("decimal")
                                .uuid("uuid")
                                .booleanType("boolean")
                                .time("time")
                                .datetime("datetime")
                                .array("array").stringType("arrayString").closeArray()
                                .object("object").stringType("object.string").object("level2").stringType("level2.object.string").closeObject().closeObject()
                                .eachArrayLike("arrayOfArrays").stringType("arrayOfArraysString").closeArray().closeArray().asBody()
                                //values
                                .stringValue("stringValue", "string")
                                .numberValue("numberValue", 12.1)
                                .booleanValue("booleanValue", true)
                                .array("arrayValue").stringValue("arrayString").closeArray()
                                .eachArrayLike("arrayOfArraysValue").stringValue("arrayOfArraysString").closeArray().closeArray()

                )
                .willRespondWith()
                .status(201)
                .toPact();
        String requestBody = "{\n"
                + "  \"string\": \"string\",\n"
                + "  \"date\": \"2019-01-01\",\n"
                + "  \"number\": 22.1,\n"
                + "  \"integer\": 1,\n"
                + "  \"decimal\": 1.0,\n"
                + "  \"uuid\": \"52667026-1993-4b5b-924f-0ade3120d28c\",\n"
                + "  \"boolean\": true,\n"
                + "  \"time\": \"T14:00:00\",\n"
                + "  \"datetime\": \"2019-01-01T14:00:00\",\n"
                + "  \"array\": [\n"
                + "    \"arrayString\"\n"
                + "  ],\n"
                + "  \"object\": {\n"
                + "    \"object.string\": \"string\",\n"
                + "    \"level2\": {\n"
                + "      \"level2.object.string\": \"string\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"arrayOfArrays\": [\n"
                + "    [\n"
                + "      \"arrayOfArraysString\"\n"
                + "    ]\n"
                + "  ],\n"
                + "  \"stringValue\": \"string\",\n"
                + "  \"numberValue\": 12.1,\n"
                + "  \"booleanValue\": true,\n"
                + "  \"arrayValue\": [\n"
                + "    \"arrayString\"\n"
                + "  ],\n"
                + "  \"arrayOfArraysValue\": [\n"
                + "    [\n"
                + "      \"arrayOfArraysString\"\n"
                + "    ]\n"
                + "  ]\n"
                + "}";
        Pact2WireMock pact2WireMock = new Pact2WireMock(PactInteractionLoader.pactInstanceBuilder().pact(requestResponsePact).build(), new WireMock());
        pact2WireMock.stubFor(CONSUMER, PROVIDER, requestWithBody);

        HttpPost httpPost = new HttpPost(HTTP_LOCALHOST_8080 + "/segment/");
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

        httpclient.execute(httpPost);
        // Avoid asserting status code, since WireMockRule will print unmatched requests in case of failure
        // Assert.assertEquals(201, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnManipulatedResponse() throws IOException {
        String request = "request";

        RequestResponsePact requestResponsePact = ConsumerPactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).uponReceiving(request)
                .method("GET")
                .path("/")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody().stringType("key", "value"))
                .toPact();

        Pact2WireMock pact2WireMock = new Pact2WireMock(PactInteractionLoader.pactInstanceBuilder().pact(requestResponsePact).build(), new WireMock());
        pact2WireMock.stubFor(CONSUMER, PROVIDER, request, new JsonPathSetCommand("$.key", "confirming change"));
        HttpGet httpGet = new HttpGet(HTTP_LOCALHOST_8080 + "/");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(byteArrayOutputStream);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals("{\"key\":\"confirming change\"}", byteArrayOutputStream.toString());
    }

    @Test(expected = ResponseNotVerifiedException.class)
    public void shouldThrowNotVerifiedException() {
        String request = "request";

        RequestResponsePact requestResponsePact = ConsumerPactBuilder.consumer(CONSUMER).hasPactWith(PROVIDER).uponReceiving(request)
                .method("GET")
                .path("/")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody().stringValue("key", "value"))
                .toPact();

        Pact2WireMock pact2WireMock = new Pact2WireMock(PactInteractionLoader.pactInstanceBuilder().pact(requestResponsePact).build(), new WireMock());
        pact2WireMock.stubFor(CONSUMER, PROVIDER, request, new JsonPathSetCommand("$.key", "non confirming change"));
    }

}