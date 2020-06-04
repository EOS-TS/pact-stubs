# PactStubs
 
 This project aims to create configurable WireMock Stubs (http://wiremock.org/) and JSON messages from Pacts (https://docs.pact.io/) for leaner integration testing:
 * Fully support all regex based rules in Pact, i.e. allow regex based url path
 * Allow for *pact verified* manipulation of responses/messages, i.e. to inject test data expectations
    *  *currently only json payloads are supported  
 * Allow for *pact verified* concrete request pattern (wiremock only), i.e. for parallel test execution, for otherwise regex based pact rules of url path, query params and request body
    + +currently only json payloads are supported

 In addition, this library provides `String` messages from MessagePacts ().
 
 
 ## Getting Started
 
 ### Prerequisites
 
 [Pact2Wiremock](src/main/java/de/eosts/pactstubs/Pact2Wiremock.java) uses the WireMock Stubbing API to configure a running WireMock instance.
 PactStubs can load pacts from PactBroker (https://github.com/pact-foundation/pact_broker), from folder or urls. 
 
 ### Dependency
 
 Add to your java project as dependency (maven), since there's a dependency to pact-jvm, scala variants 2.11 and 2.12 are supported:
 
 ```
 <dependency>
   <groupId>de.eosts</groupId>
   <artifactId>pact-stubs</artifactId>
   <version>1.0.0</version>
 </dependency>
 ```

 PactStubs itself requires at least (and will include transitively):
 ```
     com.github.tomakehurst:wiremock-jre8:2.25.1    
     au.com.dius:pact-jvm-provider-junit:4.0.4
     au.com.dius:pact-jvm-consumer-junit:4.0.4
 ```
 ### Usage
 
 #### Pact2WireMock
 
 Pact2WireMock needs to be configured to load Pacts (PactInteractionLoader) and requires a WireMock instance.
 
 ```java
class Example {
    Pact2WireMock pact2WireMock;
    public void setup() {
        PactInteractionLoader pactInteractionLoader = PactInteractionLoader.folderBuilder().path(pathToPacts).build();
//        PactInteractionLoader pactInteractionLoader =   PactInteractionLoader.pactBrokerBuilder().pactBrokerHost("pactBrokerHost").pactBrokerPort("pactBrokerPort").build();
//        PactInteractionLoader pactInteractionLoader = PactInteractionLoader.urlsBuilder().urls(new String[]{"url"}).build();

        pact2WireMock = new Pact2WireMock(pactInteractionLoader, new WireMock(mockServerHost, mockserverPort));
    }
    
    public void stubFromPact() {
        // creates a stub from an interaction defined by description and (optional) provider state 
        pact2WireMock.stubFor(InteractionIdentifier.builder()
              .consumer("consumer").provider("provider").description("description").providerState("providerState"));
     
        // creates stub with additional response customization: sets the value 'testId' to jsonPath '$.example.id'
        pact2WireMock.stubFor("consumer", "provider", "pact description", new JsonPathSetCommand("$.example.id", "testId"));

        // creates stub with specific request url, i.e. where url path in pact is a regex expression (e.g. /api/resource/(.+)/property)
        pact2WireMock.stubFor("consumer", "provider", "pact description", SpecificRequestSpec.builder().urlPath("/api/resource/12345/property").build());

    }

}
 
 ```

#### Pact2Message

...

#### Manipulating (json) responses via JsonPath

Currently setting values and renaming keys via JsonPath is supported. These are wrapped by [JsonPathCommands](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathCommand.java).

| JsonPathCommand  | Description  | Comments  |
|---|---|---|
| [JsonPathSetCommand](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathSetCommand.java)  | overwrite existing property's value |  (* throws com.jayway.jsonpath.PathNotFoundException if property doesn't exist) |
| [JsonPathPutCommand](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathPutCommand.java)  | add new property  | (* this is only useful for *optional* fields, which are not represented in pact contracts. Usage might indicate a missing pact for the optional field or provider interface is too generic)  | 
| [JsonPathRenameKeyCommand](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathRenameKeyCommand.java)  | rename existing key  | (* throws com.jayway.jsonpath.PathNotFoundException if property doesn't exist)  |
| [JsonPathAddToArrayCommand](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathAddToArrayCommand.java)  | add object to existing array  | (* throws com.jayway.jsonpath.PathNotFoundException if property doesn't exist)  |
| [JsonPathDeleteCommand](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathDeleteCommand.java)  | delete key or element in array  | (* throws com.jayway.jsonpath.PathNotFoundException if property doesn't exist)  |
| [JsonPathMapCommand](src/main/java/de/eosts/pactstubs/jsonpath/JsonPathMapCommand.java)  | set a value based on current value and given map function  | (* throws com.jayway.jsonpath.PathNotFoundException if property doesn't exist)  |
|   |   |   |


Since manipulation of responses/message could violate the respective pact, a [ResponseComparisonResultException](src/main/java/de/eosts/pactstubs/exception/ResponseComparisonResultException.java) is thrown if the underlying pact comparison finds differences.
In rare cases it might be necessary to create responses, that do not pass pact comparison (i.e. if json keys have dynamic, value-like semantics). For those cases, the thrown ResponseComparisonResultException can be caught and provides access to found diffs for further analysis.

 ## Running the tests
 
   ```
   ./gradlew test
   ```
 
 ## Built With
 
 * [WireMock](http://wiremock.org/)
 * [Pact](https://docs.pact.io/)
 * [Jayway JsonPath](https://github.com/json-path/JsonPath)
 * [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
 
 ## Versioning
 
 We use [SemVer](http://semver.org/) for versioning. 
 
 