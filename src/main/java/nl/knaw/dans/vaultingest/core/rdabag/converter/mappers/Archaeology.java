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

import nl.knaw.dans.vaultingest.core.domain.metadata.ArchisNumber;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DansArchaeology;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Archaeology {

    public static List<Statement> toArchisZaakIds(Resource resource, Collection<String> archisZaakIds) {
        return mapStrings(resource, archisZaakIds, DansArchaeology.dansArchisZaakId);
    }

    public static List<Statement> toArchisNumbers(Resource resource, Collection<ArchisNumber> archisNumbers) {
        if (archisNumbers == null) {
            return List.of();
        }

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var number : archisNumbers) {
            var element = model.createResource();

            if (number.getType() != null) {
                element.addProperty(DansArchaeology.dansArchisNumberType, number.getType());
            }
            if (number.getId() != null) {
                element.addProperty(DansArchaeology.dansArchisNumberId, number.getId());
            }

            result.add(model.createStatement(
                resource,
                DansArchaeology.dansArchisNumber,
                element
            ));
        }

        return result;
    }

    public static List<Statement> toAbrRapportTypes(Resource resource, Collection<String> abrRapportTypes) {
        return mapStrings(resource, abrRapportTypes, DansArchaeology.dansAbrRapportType);
    }

    public static List<Statement> toAbrRapportNummers(Resource resource, Collection<String> abrRapportNumbers) {
        return mapStrings(resource, abrRapportNumbers, DansArchaeology.dansAbrRapportNummer);
    }

    public static List<Statement> toAbrVerwervingswijzes(Resource
                                                             resource, Collection<String> abrVerwervingswijzes) {
        return mapStrings(resource, abrVerwervingswijzes, DansArchaeology.dansAbrVerwervingswijze);
    }

    public static List<Statement> toAbrComplex(Resource resource, Collection<String> abrComplex) {
        return mapStrings(resource, abrComplex, DansArchaeology.dansAbrComplex);
    }

    public static List<Statement> toAbrArtifacts(Resource resource, Collection<String> abrArtifact) {
        return mapStrings(resource, abrArtifact, DansArchaeology.dansAbrArtifact);
    }

    public static List<Statement> toAbrPeriods(Resource resource, Collection<String> abrPeriod) {
        return mapStrings(resource, abrPeriod, DansArchaeology.dansAbrPeriod);
    }

    private static List<Statement> mapStrings(Resource resource, Collection<String> values, Property property) {
        if (values == null) {
            return List.of();
        }

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var value : values) {
            result.add(model.createStatement(
                resource,
                property,
                value
            ));
        }

        return result;
    }
}
