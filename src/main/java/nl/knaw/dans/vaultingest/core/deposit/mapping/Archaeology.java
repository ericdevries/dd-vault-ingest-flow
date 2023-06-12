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
package nl.knaw.dans.vaultingest.core.deposit.mapping;

import nl.knaw.dans.vaultingest.core.domain.metadata.ArchisNumber;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import nl.knaw.dans.vaultingest.core.xml.XmlNamespaces;
import org.w3c.dom.Document;

import java.util.List;
import java.util.stream.Collectors;

public class Archaeology {
    // AR001
    public static List<String> getArchisZaakIds(Document document) {
        var namespace = Base.getIdTypeNamespace(document);

        return XPathEvaluator.strings(document,
                String.format(
                    "/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier[@xsi:type='%s:ARCHIS-ZAAK-IDENTIFICATIE']",
                    namespace
                )
            )
            .map(String::trim)
            .collect(Collectors.toList());
    }

    // AR002
    public static List<ArchisNumber> getArchisNumbers(Document document) {
        var filters = List.of(
            "ARCHIS-ONDERZOEK", "ARCHIS-VONDSTMELDING", "ARCHIS-MONUMENT", "ARCHIS-WAARNEMING"
        );

        var namespace = Base.getIdTypeNamespace(document);
        var xpathFilters = filters.stream().map(item -> String.format("@xsi:type = '%s:%s'", namespace, item))
            .collect(Collectors.joining(" or "));

        return XPathEvaluator.nodes(document, "/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier" + "[" + xpathFilters + "]")
            .map(node -> ArchisNumber.builder()
                .id(node.getTextContent().trim())
                .type(node.getAttributes().getNamedItemNS(XmlNamespaces.NAMESPACE_XSI, "type").getTextContent().substring(namespace.length() + 1))
                .build())
            .collect(Collectors.toList());
    }

    // AR003
    public static List<String> getAbrRapportTypes(Document document) {
        return getValueUriBySubjectSchemeAndUri(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:reportNumber",
            "ABR Rapporten",
            "https://data.cultureelerfgoed.nl/term/id/abr/7a99aaba-c1e7-49a4-9dd8-d295dbcc870e"
        );
    }

    // AR004
    public static List<String> getAbrRapportNumbers(Document document) {
        return getTextBySubjectSchemeAndUri(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:reportNumber",
            "ABR Rapporten",
            "https://data.cultureelerfgoed.nl/term/id/abr/7a99aaba-c1e7-49a4-9dd8-d295dbcc870e");
    }

    // AR005
    public static List<String> getAbrVerwervingswijzes(Document document) {
        return getValueUriBySubjectSchemeAndUri(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:acquisitionMethod",
            "ABR verwervingswijzen",
            "https://data.cultureelerfgoed.nl/term/id/abr/554ca1ec-3ed8-42d3-ae4b-47bcb848b238");
    }

    // AR006
    public static List<String> getAbrComplex(Document document) {
        return getValueUriBySubjectSchemeAndUri(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:subject",
            "ABR Complextypen",
            "https://data.cultureelerfgoed.nl/term/id/abr/e9546020-4b28-4819-b0c2-29e7c864c5c0");
    }

    // AR007
    public static List<String> getAbrArtifact(Document document) {
        return getValueUriBySubjectSchemeAndUri(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:subject",
            "ABR Artefacten",
            "https://data.cultureelerfgoed.nl/term/id/abr/22cbb070-6542-48f0-8afe-7d98d398cc0b");
    }

    // AR008
    public static List<String> getAbrPeriod(Document document) {
        return getValueUriBySubjectSchemeAndUri(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:temporal",
            "ABR Periodes",
            "https://data.cultureelerfgoed.nl/term/id/abr/9b688754-1315-484b-9c89-8817e87c1e84");
    }


    private static List<String> getTextBySubjectSchemeAndUri(Document document, String query, String subjectScheme, String uri) {
        return XPathEvaluator.strings(document,
                query +
                    "[@subjectScheme = '" + subjectScheme + "' " +
                    "and @schemeURI = '" + uri + "']")
            .map(String::trim)
            .collect(Collectors.toList());
    }

    private static List<String> getValueUriBySubjectSchemeAndUri(Document document, String query, String subjectScheme, String uri) {
        return XPathEvaluator.strings(document,
                query +
                    "[@subjectScheme = '" + subjectScheme + "' " +
                    "and @schemeURI = '" + uri + "']/@valueURI")
            .map(String::trim)
            .collect(Collectors.toList());
    }
}