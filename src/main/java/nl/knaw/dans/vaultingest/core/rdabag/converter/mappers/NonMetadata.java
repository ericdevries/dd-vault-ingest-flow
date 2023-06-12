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
package nl.knaw.dans.vaultingest.core.rdabag.converter.mappers;

import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.PROV;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class NonMetadata {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // DSET001
    public static Optional<Statement> toDoi(Resource resource, String doi) {
        if (doi == null) {
            return Optional.empty();
        }

        var model = resource.getModel();

        return Optional.of(model.createStatement(
            resource,
            PROV.alternateOf,
            doi
        ));
    }

    // DFILE001
    public static Optional<Statement> toAvailable(Resource resource, LocalDate available) {
        if (available == null) {
            return Optional.empty();
        }

        if (available.isBefore(LocalDate.now())) {
            return Optional.empty();
        }

        var model = resource.getModel();

        return Optional.of(model.createStatement(
            resource,
            DCTerms.available,
            available.format(formatter)
        ));
    }
}
