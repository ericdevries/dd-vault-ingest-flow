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
package nl.knaw.dans.vaultingest.core.simpledeposit.mapping;

import nl.knaw.dans.vaultingest.core.deposit.mapping.Author;
import nl.knaw.dans.vaultingest.core.deposit.mapping.Creator;
import nl.knaw.dans.vaultingest.core.deposit.mapping.Organizations;
import nl.knaw.dans.vaultingest.core.domain.metadata.DatasetRelation;
import nl.knaw.dans.vaultingest.core.domain.metadata.OtherId;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DVCitation;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.Datacite;
import nl.knaw.dans.vaultingest.core.simpledeposit.SimpleDepositProperties;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SchemaDO;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Citation {

    // CIT001
    public static List<Statement> mapTitles(Resource resource, Document ddm) {
        return XPathEvaluator.strings(ddm,
                "/ddm:DDM/ddm:profile/dc:title",
                "/ddm:DDM/ddm:profile/dcterms:title"
            )
            .map(String::trim)
            .findFirst()
            .map(title -> {
                    var model = resource.getModel();
                    var literal = model.createLiteral(title);

                    return List.of(
                        model.createStatement(resource, DCTerms.title, literal),
                        model.createStatement(resource, SchemaDO.name, literal)
                    );
                }
            )
            .orElse(List.of());

    }

    // CIT002
    public static Optional<Statement> mapAlternativeTitles(Resource resource, Document ddm) {
        return XPathEvaluator.strings(ddm,
                "/ddm:DDM/ddm:dcmiMetadata/dcterms:title",
                "/ddm:DDM/ddm:dcmiMetadata/dc:title",
                "/ddm:DDM/ddm:dcmiMetadata/dcterms:alternative")
            .map(String::trim)
            .findFirst()
            .map(title -> {
                var model = resource.getModel();

                return model.createStatement(
                    resource,
                    DCTerms.alternative,
                    model.createLiteral(title)
                );
            });
    }

    public static List<Statement> mapOtherIds(Resource resource, Document ddm, SimpleDepositProperties properties) {
        var otherIds = new ArrayList<OtherId>();
        var organizationIdentifiers = properties.getProperty("Has-Organizational-Identifier");

        // CIT003, data from bag
        organizationIdentifiers
            .stream()
            .filter(value -> {
                var parts = value.split(":", 2);
                return parts.length == 2 && StringUtils.isNotBlank(parts[0]) && StringUtils.isNotBlank(parts[1]);
            })
            .map(value -> {
                var parts = value.split(":", 2);
                return OtherId.builder()
                    .agency(parts[0])
                    .value(parts[1])
                    .build();
            })
            .findFirst()
            .ifPresent(otherIds::add);

        // CIT004, data from ddm
        XPathEvaluator.strings(ddm,
                "/ddm:DDM/ddm:dcmiMetadata/ddm:identifier[not(@xsi:type)]",
                "/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier[not(@xsi:type)]")
            .map(identifier -> OtherId.builder()
                .value(identifier)
                .build()
            )
            .forEach(otherIds::add);

        var model = resource.getModel();
        var statements = new ArrayList<Statement>();

        for (var otherId : otherIds) {
            var res = model.createResource();
            res.addProperty(DVCitation.otherIdValue, otherId.getValue());

            if (otherId.getAgency() != null) {
                res.addProperty(DVCitation.otherIdAgency, otherId.getAgency());
            }

            statements.add(model.createStatement(
                resource,
                DVCitation.otherId,
                res
            ));
        }

        return statements;

    }

    public static List<Statement> mapAuthors(Resource resource, Document ddm) {
        var authors = new ArrayList<DatasetRelation>();

        // CIT005
        authors.addAll(Creator.getCreators(ddm));

        // CIT006
        authors.addAll(Author.getAuthors(ddm));

        // CIT007
        authors.addAll(Organizations.getOrganizations(ddm));

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var author : authors) {
            var authorElement = model.createResource();
            authorElement.addProperty(DVCitation.authorName, author.getDisplayName());

            if (author.getAffiliation() != null) {
                authorElement.addProperty(DVCitation.authorAffiliation, author.getAffiliation());
            }

            var identifier = author.getIdentifier();

            if (identifier != null) {
                if (identifier.getScheme() != null) {
                    authorElement.addProperty(Datacite.agentIdentifierScheme, identifier.getScheme());
                }

                if (identifier.getValue() != null) {
                    authorElement.addProperty(Datacite.agentIdentifier, identifier.getValue());
                }
            }

            result.add(model.createStatement(
                resource,
                DVCitation.author,
                authorElement
            ));
        }

        return result;
    }
}
