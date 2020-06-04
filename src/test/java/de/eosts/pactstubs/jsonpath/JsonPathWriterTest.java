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
package de.eosts.pactstubs.jsonpath;

import com.jayway.jsonpath.Filter;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class JsonPathWriterTest {

    private JsonPathWriter jsonPathWriter = new JsonPathWriter();

    @Test
    public void shouldSetValue() {
        String json = "{\"key\":\"value\"}";
        Map<String, Object> map = new HashMap<>();
        map.put("a", "b");
        String convertedJson = jsonPathWriter.write(json, new JsonPathSetCommand("$.key", map));
        Assert.assertEquals("{\"key\":{\"a\":\"b\"}}", convertedJson);
    }

    @Test
    public void shouldMapValue() {
        String json = "{\"key\":\"value\"}";
        String convertedJson = jsonPathWriter.write(json, new JsonPathMapCommand("$.key", (currentValue, configuration) -> currentValue + "1"));
        Assert.assertEquals("{\"key\":\"value1\"}", convertedJson);
    }

    @Test
    public void shouldRenameKey() {
        String json = "{\"key\":\"value\"}";
        String convertedJson = jsonPathWriter.write(json, new JsonPathRenameKeyCommand("$", "key", "key2"));
        Assert.assertEquals("{\"key2\":\"value\"}", convertedJson);
    }

    @Test
    public void shouldSetLocalDateValueAsString() {
        String json = "{\"datum\":\"2018-12-31\"}";
        String convertedJson = jsonPathWriter.write(json, new JsonPathSetCommand("$.datum", LocalDate.now().toString()));
        Assert.assertTrue(convertedJson.contains(LocalDate.now().toString()));
    }

    @Test
    public void shouldAddNewProperty() {
        String json = "{\"key\":\"value\"}";
        String convertedJson = jsonPathWriter.write(json, new JsonPathPutCommand("$", "key2", "value2"));
        Assert.assertEquals("{\"key\":\"value\",\"key2\":\"value2\"}", convertedJson);
    }

    @Test
    public void shouldAddItemToArray() {
        String json = "{\"key\": [value] }";
        String convertedJson = jsonPathWriter.write(json, new JsonPathAddToArrayCommand("$.key", "value2"));
        Assert.assertEquals("{\"key\":[\"value\",\"value2\"]}", convertedJson);
    }

    @Test
    public void shouldDeleteKeyValue() {
        String json = "{\"key\": \"value\" }";
        String convertedJson = jsonPathWriter.write(json, new JsonPathDeleteCommand("$.key"));
        Assert.assertEquals("{}", convertedJson);
    }

    @Test
    public void shouldDeleteElementInArray() {
        String json = "{\"key\":[\"value\",\"value2\"] }";
        String convertedJson = jsonPathWriter.write(json, new JsonPathDeleteCommand("$.key[1]"));
        Assert.assertEquals("{\"key\":[\"value\"]}", convertedJson);
    }

    @Test
    public void shouldDeleteObjectInArrayViaFilterExpression() {
        String json = "{\"key\":[\"value\",{\"filterKey\": \"filterValue\" }] }";
        String convertedJson = jsonPathWriter.write(json, new JsonPathDeleteCommand("$.key[?(@.filterKey == 'filterValue')]"));
        Assert.assertEquals("{\"key\":[\"value\"]}", convertedJson);
    }

    @Test
    public void shouldDeleteObjectInArrayViaPredicate() {
        String json = "{\"key\":[\"value\",{\"filterKey\": \"filterValue\" }] }";
        String convertedJson = jsonPathWriter.write(json, new JsonPathDeleteCommand("$.key[?]", new Filter[] { Filter.parse("[?(@.filterKey == 'filterValue')]") }));
        Assert.assertEquals("{\"key\":[\"value\"]}", convertedJson);
    }

    @Test
    public void shouldApplyGeneralModifications() {
        String json = "{\"key\":\"value\",\"kez2\":\"value\",\"key4\":[],\"key5\":[\"value\",{\"filterKey\": \"filterValue\" }]}";
        GeneralPurposeWriteContextOperator generalPurposeWriteContextOperator = new GeneralPurposeWriteContextOperator(writeContext -> writeContext
                .set("$.key", "modifiedValue")
                .map("$.key", (currentValue, configuration) -> currentValue + "+1")
                .renameKey("$", "kez2", "key2")
                .put("$", "key3", "value")
                .add("$.key4", "value")
                .delete("$.key5[?(@.filterKey == 'filterValue')]"));

        String convertedJson = jsonPathWriter.write(json, generalPurposeWriteContextOperator);
        Assert.assertEquals("{\"key\":\"modifiedValue+1\",\"key4\":[\"value\"],\"key5\":[\"value\"],\"key2\":\"value\",\"key3\":\"value\"}", convertedJson);
    }
}