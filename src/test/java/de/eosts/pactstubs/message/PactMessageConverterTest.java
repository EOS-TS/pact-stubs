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
package de.eosts.pactstubs.message;

import au.com.dius.pact.core.model.ContentType;
import au.com.dius.pact.core.model.OptionalBody;
import au.com.dius.pact.core.model.ProviderState;
import au.com.dius.pact.core.model.matchingrules.MatchingRulesImpl;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import au.com.dius.pact.core.model.messaging.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import de.eosts.pactstubs.compare.ResponseComparisonResult;
import de.eosts.pactstubs.jsonpath.JsonPathSetCommand;
import de.eosts.pactstubs.jsonpath.JsonPathWriter;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;

public class PactMessageConverterTest {

    @Test
    public void shouldEmitMessageExactly() {
        String simpleJson = "{\"key\":\"value\"}";
        Message message = new Message("", Arrays.asList(new ProviderState("")), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        Assert.assertEquals("{\"key\":\"value\"}", new PactMessageConverter().convert(message).getResponse());
        Assert.assertNull(new PactMessageConverter().convert(message).getComparisonResult());
    }

    @Test
    public void shouldEmitManipulatedButValidMessages() {
        String simpleJson = "{\"key\":\"value\"}";
        Message message = new Message("", Arrays.asList(new ProviderState("")), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        MatchingRulesImpl matchingRules = new MatchingRulesImpl();

        matchingRules.addCategory("body").addRule("$.key", new RegexMatcher("i.+"));
        message.setMatchingRules(matchingRules);
        ResponseComparisonResult<String> comparisonResult = new PactMessageConverter().convert(message, new JsonPathSetCommand("$.key", "incorrect"));
        Assert.assertEquals("{\"key\":\"incorrect\"}", comparisonResult.getResponse());
        Assert.assertTrue(comparisonResult.getComparisonResult().getBodyMismatches().component1().getMismatches().isEmpty());
    }

    @Test
    public void shouldHaveComparisonResultWithNonPactConformConversions() {
        String simpleJson = "{\"key\":\"value\"}";
        Message message = new Message("", Arrays.asList(new ProviderState("")), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        ResponseComparisonResult<String> incorrect = new PactMessageConverter().convert(message, new JsonPathSetCommand("$.key", "incorrect"));
        Assert.assertTrue(incorrect.getComparisonResult().getBodyMismatches().component1().getMismatches().containsKey("$.key"));

    }

    @Test
    public void shouldConvertWithJackson() {
        String simpleJson = "{\"key\":\"value\"}";
        Message message = new Message("", Arrays.asList(new ProviderState("")), new OptionalBody(OptionalBody.State.PRESENT, simpleJson.getBytes(), ContentType.Companion.getJSON()));
        int epochSecond = 1619079376;
        int nanoAdjustment = 33154000; //will be serialized by Jackson with left filled zeros, e.g. 1 ns will be "000000001"
        String nanoAdjustmentPadded = ("000000000" + nanoAdjustment).substring(String.valueOf(nanoAdjustment).length());

        Instant sampleInstant = Instant.ofEpochSecond(epochSecond, nanoAdjustment);
        String response = new PactMessageConverter(
                new JsonPathWriter(defaultConfiguration()
                        .jsonProvider(new JacksonJsonProvider(new ObjectMapper().registerModule(new JavaTimeModule())))))
                .convert(message, new JsonPathSetCommand("$.key", sampleInstant)).getResponse();

        Assert.assertEquals("{\"key\":" + epochSecond + "." + nanoAdjustmentPadded + "}", response);
    }

}