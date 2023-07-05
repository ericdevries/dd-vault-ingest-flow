/*
 * Copyright (C) 2023 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.vaultingest.core.utilities;

import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.vaultingest.core.deposit.CountryResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class TestCountryResolver implements CountryResolver {
    private final Set<String> countries;

    public TestCountryResolver() throws IOException {
        var path = Path.of(getClass().getResource("/debug-etc/spatial-coverage-country-terms.txt").getPath());

        this.countries = Files.readAllLines(path, StandardCharsets.UTF_8)
            .stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isControlledValue(String country) {
        return country != null && countries.contains(country.toLowerCase());
    }
}
