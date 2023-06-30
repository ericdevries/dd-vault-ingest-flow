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

import nl.knaw.dans.vaultingest.core.domain.metadata.GrantNumber;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DVCitation;
import nl.knaw.dans.vaultingest.core.simpledeposit.SimpleDeposit;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.SchemaDO;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GrantNumbers extends Base {
    // CIT024
    public static List<Statement> toRDF(Resource resource, SimpleDeposit deposit) {
        return toGrantNumbers(resource, getGrantNumbers(deposit.getDdm()));
    }

    static List<GrantNumber> getGrantNumbers(Document document) {
        // TODO CIT023 is under revision
        var idType = getIdTypeNamespace(document);

        return XPathEvaluator.strings(document,
                String.format(
                    "/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier[@xsi:type = '%s:NWO-PROJECTNR']", idType)
            )
            .map(value -> GrantNumber.builder()
                .agency("NWO")
                .value(value)
                .build())
            .collect(Collectors.toList());
    }

    static List<Statement> toGrantNumbers(Resource resource, Collection<GrantNumber> grantNumbers) {
        return toComplexTerms(resource, SchemaDO.sponsor, grantNumbers, (element, grantNumber) -> {
            if (grantNumber.getAgency() != null) {
                element.addProperty(DVCitation.grantNumberAgency, grantNumber.getAgency());
            }
            if (grantNumber.getValue() != null) {
                element.addProperty(DVCitation.grantNumberValue, grantNumber.getValue());
            }
        });
    }
}
