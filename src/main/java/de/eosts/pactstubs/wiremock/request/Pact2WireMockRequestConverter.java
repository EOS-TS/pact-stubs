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
package de.eosts.pactstubs.wiremock.request;

import au.com.dius.pact.core.model.Request;
import au.com.dius.pact.core.model.matchingrules.MatchingRule;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import de.eosts.pactstubs.compare.PactRequestComparator;
import de.eosts.pactstubs.compare.RequestComparator;
import de.eosts.pactstubs.compare.RequestComparisonResult;
import de.eosts.pactstubs.spec.SpecificRequestSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that assembles WireMock RequestPattern from Pact request.
 * <p>
 * The resulting RequestPattern will be built with request method, urlPathPattern and {@link #requestPatternBuilder()}
 * , i.e. query, header and request body
 * Regex based path and query pattern from pact can be
 */
public class Pact2WireMockRequestConverter {

    private final List<Pact2WireMockRequestPattern> pact2WireMockRequestPatterns = new ArrayList<>();
    private final RequestPatternBuilder requestPatternBuilder;
    private final RequestComparator requestComparator;
    private final Request request;
    private final SpecificRequestSpec specificRequestSpec;

    public Pact2WireMockRequestConverter(Request request) {
        this(request, SpecificRequestSpec.builder().build());
    }

    public Pact2WireMockRequestConverter(Request request, SpecificRequestSpec specificRequestSpec) {
        this(request, specificRequestSpec, new PactRequestComparator());
    }

    public Pact2WireMockRequestConverter(Request request, SpecificRequestSpec specificRequestSpec, RequestComparator requestComparator) {
        this(request, specificRequestSpec, requestComparator, new RequestPatternBuilder(RequestMethod.fromString(request.getMethod()), getUrlPathPattern(request, specificRequestSpec)));
    }

    public Pact2WireMockRequestConverter(Request request, SpecificRequestSpec specificRequestSpec, RequestComparator requestComparator, RequestPatternBuilder requestPatternBuilder) {
        this.request = request;
        this.requestPatternBuilder = requestPatternBuilder;
        this.specificRequestSpec = specificRequestSpec;
        this.requestComparator = requestComparator;
        addDefaultPattern();
    }

    public Pact2WireMockRequestConverter(Request request, Pact2WireMockRequestPattern... pact2WireMockRequestPatterns) {
        this(request, SpecificRequestSpec.builder().build(), new PactRequestComparator(), pact2WireMockRequestPatterns);
    }

    public Pact2WireMockRequestConverter(Request request, SpecificRequestSpec specificRequestSpec, RequestComparator requestComparator, Pact2WireMockRequestPattern... pact2WireMockRequestPatterns) {
        this.request = request;
        this.specificRequestSpec = specificRequestSpec;
        this.requestPatternBuilder = new RequestPatternBuilder(RequestMethod.fromString(request.getMethod()), getUrlPathPattern(request, specificRequestSpec));
        this.requestComparator = requestComparator;
        this.pact2WireMockRequestPatterns.addAll(Arrays.asList(pact2WireMockRequestPatterns));
    }

    public Pact2WireMockRequestConverter(Request request, SpecificRequestSpec specificRequestSpec, RequestComparator requestComparator, RequestPatternBuilder requestPatternBuilder, Pact2WireMockRequestPattern... pact2WireMockRequestPatterns) {
        this.request = request;
        this.specificRequestSpec = specificRequestSpec;
        this.requestPatternBuilder = requestPatternBuilder;
        this.requestComparator = requestComparator;
        this.pact2WireMockRequestPatterns.addAll(Arrays.asList(pact2WireMockRequestPatterns));
    }

    private void addDefaultPattern() {
        pact2WireMockRequestPatterns.add(
                specificRequestSpec.getQueryParams()
                        .map(stringStringMap -> (Pact2WireMockRequestPattern) new SpecificQueryPattern(stringStringMap))
                        .orElseGet(QueryPattern::new));

        pact2WireMockRequestPatterns.add(
                specificRequestSpec.getJsonBody()
                        .map(jsonBody -> (Pact2WireMockRequestPattern) new SpecificJsonBodyPattern(jsonBody))
                        .orElseGet(BodyPattern::new));

        pact2WireMockRequestPatterns.add(new HeaderPattern());
    }

    public RequestComparisonResult convert() {
        return new RequestComparisonResult(requestPatternBuilder().build(), requestComparator.compare(request, specificRequestSpec));
    }

    /**
     * Creates a RequestPatternBuilder that is build with the existing {@link #pact2WireMockRequestPatterns}
     *
     * @return RequestPatternBuilder
     */
    public RequestPatternBuilder requestPatternBuilder() {
        pact2WireMockRequestPatterns.forEach(pact2WireMockRequestPattern -> pact2WireMockRequestPattern.accept(request, requestPatternBuilder));
        return requestPatternBuilder;
    }

    private static UrlPathPattern getUrlPathPattern(Request pactRequest, SpecificRequestSpec specificRequestSpec) {

        return specificRequestSpec.getUrlPath()
                .map(WireMock::urlPathEqualTo)
                .orElseGet(() -> {
                    List<MatchingRule> pathRules = pactRequest.getMatchingRules().rulesForCategory("path").allMatchingRules();
                    if (!pathRules.isEmpty()) {
                        String pathRegex = ((RegexMatcher) pathRules.get(0)).getRegex();
                        return WireMock.urlPathMatching(pathRegex);
                    } else {
                        return WireMock.urlPathEqualTo(pactRequest.getPath());
                    }
                });
    }

}
