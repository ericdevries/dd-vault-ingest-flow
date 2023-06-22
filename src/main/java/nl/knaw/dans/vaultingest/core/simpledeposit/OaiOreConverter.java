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
package nl.knaw.dans.vaultingest.core.simpledeposit;

import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DVCore;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.ORE;
import nl.knaw.dans.vaultingest.core.simpledeposit.mapping.AlternativeTitles;
import nl.knaw.dans.vaultingest.core.simpledeposit.mapping.Authors;
import nl.knaw.dans.vaultingest.core.simpledeposit.mapping.OtherIds;
import nl.knaw.dans.vaultingest.core.simpledeposit.mapping.Titles;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SchemaDO;

import java.time.OffsetDateTime;

public class OaiOreConverter {

    public Model convert(SimpleDeposit deposit) {
        var model = ModelFactory.createDefaultModel();

        var resourceMap = createResourceMap(deposit, model);
        var resource = createAggregation(deposit, model);

        model.add(Titles.toRDF(resource, deposit));
        AlternativeTitles.toRDF(resource, deposit)
            .ifPresent(model::add);

        model.add(OtherIds.toRDF(resource, deposit));
        model.add(Authors.toRDF(resource, deposit));

        model.add(model.createStatement(
            resourceMap,
            ORE.describes,
            resource
        ));

        return model;
    }

    Resource createResourceMap(SimpleDeposit deposit, Model model) {
        var resourceMap = model.createResource("urn:uuid:" + deposit.getId());
        var resourceMapType = model.createStatement(resourceMap, RDF.type, ORE.ResourceMap);

        model.add(resourceMapType);
        model.add(model.createStatement(
            resourceMap,
            DCTerms.modified,
            OffsetDateTime.now().toString()
        ));

        var creator = model.createResource();
        model.add(model.createStatement(
            creator,
            FOAF.name,
            "DANS Vault Service"
        ));

        model.add(model.createStatement(
            resourceMap,
            DCTerms.creator,
            creator
        ));

        return resourceMap;
    }

    Resource createAggregatedResource(Model model, DepositFile depositFile) {
        var resource = model.createResource("urn:uuid:" + depositFile.getId());

        // TODO add access rights and checksum
        model.add(model.createStatement(resource, RDF.type, ORE.AggregatedResource));
        model.add(model.createStatement(resource, SchemaDO.name, depositFile.getPath().toString()));

        // FIL002A, FIL002B, FIL003, FIL004
        var descriptionText = depositFile.getDescription();

        if (descriptionText != null) {
            var description = model.createStatement(resource, SchemaDO.description, depositFile.getDescription());
            model.add(description);
        }

        // FIL002
        var directoryLabel = depositFile.getDirectoryLabel();

        if (directoryLabel != null) {
            model.add(model.createStatement(resource, DVCore.directoryLabel, directoryLabel.toString()));
        }

        // FIL005, FIL006, FIL007
        model.add(model.createStatement(resource, DVCore.restricted, Boolean.toString(depositFile.isRestricted())));

        return resource;
    }

    Resource createAggregation(SimpleDeposit deposit, Model model) {
        var resource = model.createResource(deposit.getNbn());
        var type = model.createStatement(resource, RDF.type, ORE.Aggregation);

        model.add(type);

        if (deposit.getPayloadFiles() != null) {
            for (var file : deposit.getPayloadFiles()) {
                var fileResource = createAggregatedResource(model, file);

                model.add(model.createStatement(
                    resource,
                    ORE.aggregates,
                    fileResource
                ));
            }
        }

        return resource;
    }
}
