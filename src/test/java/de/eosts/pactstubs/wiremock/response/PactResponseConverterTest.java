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
package de.eosts.pactstubs.wiremock.response;

import au.com.dius.pact.core.model.ContentType;
import au.com.dius.pact.core.model.OptionalBody;
import au.com.dius.pact.core.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import de.eosts.pactstubs.compare.ResponseComparisonResult;
import de.eosts.pactstubs.jsonpath.JsonPathSetCommand;
import de.eosts.pactstubs.jsonpath.JsonPathWriter;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;

public class PactResponseConverterTest {

    @Test
    public void shouldRespondExactly() {
        String simpleJson = "{\"key\":\"value\"}";
        Response response = new Response(200, new HashMap<>(), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        Assert.assertEquals("{\"key\":\"value\"}", new PactResponseConverter().convert(response).getResponse().getBody());
        Assert.assertNull(new PactResponseConverter().convert(response).getComparisonResult());
    }

    @Test
    public void shouldHaveComparisonResultWithNonPactConformConversions() {
        String simpleJson = "{\"key\":\"value\"}";
        Response response = new Response(200, new HashMap<>(), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        ResponseComparisonResult<ResponseDefinition> incorrect = new PactResponseConverter().convert(response, new JsonPathSetCommand("$.key", "incorrect"));
        Assert.assertTrue(incorrect.getComparisonResult().getBodyMismatches().component1().getMismatches().containsKey("$.key"));
    }

    @Test
    public void shouldConvertWithJackson() {
        String simpleJson = "{\"key\":\"value\"}";
        Response response = new Response(200, new HashMap<>(), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        Instant now = Instant.parse("2007-12-03T10:15:30.012345678Z");
        ResponseDefinition convertedResponse = new PactResponseConverter(
                new JsonPathWriter(defaultConfiguration()
                        .jsonProvider(new JacksonJsonProvider(new ObjectMapper().registerModule(new JavaTimeModule())))))
                .convert(response, new JsonPathSetCommand("$.key", now)).getResponse();

        Assert.assertEquals("{\"key\":1196676930.012345678}", convertedResponse.getBody());
    }

}
