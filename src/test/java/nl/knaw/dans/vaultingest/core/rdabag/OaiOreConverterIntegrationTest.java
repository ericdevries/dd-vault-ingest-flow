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
package nl.knaw.dans.vaultingest.core.rdabag;

import lombok.Builder;
import lombok.Value;
import nl.knaw.dans.vaultingest.core.deposit.SimpleCommonDepositManager;
import nl.knaw.dans.vaultingest.core.domain.Deposit;
import nl.knaw.dans.vaultingest.core.rdabag.converter.OaiOreConverter;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SchemaDO;
import org.assertj.core.api.iterable.ThrowingExtractor;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


// Test all the mappings end-to-end
public class OaiOreConverterIntegrationTest {


    // CIT001
    @Test
    void title() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DCTerms.title, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("A bag containing examples for each mapping rule");
    }

    // CIT002
    @Test
    void alternativeTitles() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DCTerms.alternative, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("DCTERMS title 2",
                "DCTERMS alt title 1",
                "DCTERMS alt title 2",
                "DCTERMS title 1");
    }

    // CIT003, CIT004
    @Test
    void otherIds() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.otherId, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.otherIdValue))
            .containsOnly("DCTERMS_ID001",
                "DCTERMS_ID002",
                "DCTERMS_ID003");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.otherIdAgency))
            .containsOnlyNulls();
    }

    // CIT005, CIT006, CIT007
    @Test
    void authors() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.author, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.authorName))
            .containsOnly("Unformatted Creator", "I Lastname", "Creator Organization");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.authorAffiliation))
            .containsOnly("Example Org", null);

        assertThat(statements)
            .map(getPropertyAsString(Datacite.agentIdentifier))
            .containsOnly(null, "123456789", "0000-1111-2222-3333");

        assertThat(statements)
            .map(getPropertyAsString(Datacite.agentIdentifierScheme))
            .containsOnly(null, "ORCID", "VIAF");
    }

    // CIT008
    @Test
    void datasetContact() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.datasetContact, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.datasetContactName))
            .containsOnly("user001");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.datasetContactEmail))
            .containsOnly("user001@dans.knaw.nl");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.datasetContactAffiliation))
            .containsOnly("user001 university");
    }

    // CIT009, CIT011, CIT012
    @Test
    void descriptions() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.dsDescription, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.dsDescriptionValue))
            .containsOnly("This bags contains one or more examples of each mapping rule.",
                "Even more descriptions",
                "some issuing date",
                "some validation date",
                "A second description",
                "some submission date",
                "some copyright date",
                "some modified date",
                "some date",
                "some acceptance date",
                "some coverage description");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.dsDescriptionDate))
            .containsOnlyNulls();
    }

    // CIT013
    @Test
    void subjects() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DCTerms.subject, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Chemistry",
                "Computer and Information Science");
    }

    // CIT014, CIT015, CIT016
    @Test
    void keywords() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.keyword, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.keywordValue))
            .containsOnly("Broader Match: buttons (fasteners)",
                "Old School Latin",
                "keyword1",
                "non-military uniform button",
                "keyword2");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.keywordVocabulary))
            .containsOnly(null, "PAN thesaurus ideaaltypes", "Art and Architecture Thesaurus");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.keywordVocabularyURI))
            .containsOnly(null, "https://data.cultureelerfgoed.nl/term/id/pan/PAN", "http://vocab.getty.edu/aat/");
    }

    // CIT017
    @Test
    void publications() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DCTerms.isReferencedBy, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(Datacite.resourceIdentifier))
            .containsOnly("0317-8471");

        assertThat(statements)
            .map(getPropertyAsString(Datacite.resourceIdentifierScheme))
            .containsOnly("ISSN");
    }

    // CIT018
    @Test
    void languages() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DCTerms.language, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Basque", "Kalaallisut, Greenlandic", "Western Frisian");
    }

    // CIT019
    @Test
    void productionDates() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.productionDate, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("2015-09-09");
    }

    // CIT020, CIT021
    @Test
    void contributors() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DCTerms.contributor, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.contributorName))
            .containsOnly("CON van Tributor (Contributing Org)", "Contributing Org");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.contributorType))
            .containsOnly("ProjectMember", "Sponsor");
    }

    // TODO CIT022 is still under revision, fix later
    // CIT023
    @Test
    void grantNumbers() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, SchemaDO.sponsor, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.grantNumberAgency))
            .containsOnly("NWO");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.grantNumberValue))
            .containsOnly("54321");
    }

    // CIT024
    @Test
    void distributor() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.distributor, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.distributorName))
            .containsOnly("D. I. Stributor");
    }

    // CIT025
    @Test
    void distributionDate() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.distributionDate, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("2015-09-09");
    }

    // CIT026
    @Test
    void dateOfCollection() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.dateOfCollection, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.dateOfCollectionStart))
            .containsOnly("2015-06-01");

        assertThat(statements)
            .map(getPropertyAsString(DVCitation.dateOfCollectionEnd))
            .containsOnly("2016-12-31");
    }

    // CIT027
    @Test
    void series() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DVCitation.series, (RDFNode) null)
        ).toList();

        // TODO docs state "Separate multiple occurrences with an empty line", but that is not how it is implemented
        assertThat(statements)
            .map(getPropertyAsString(DVCitation.seriesInformation))
            .containsOnly("Information about a series: first", "Information about a series: second");
    }

    // CIT028
    @Test
    void wasDerivedFrom() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, PROV.wasDerivedFrom, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Sous an ayisyen", "Source 3", "Source 2");
    }


    // RIG001
    @Test
    void dansRightHolder() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansRights.dansRightsHolder, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("I Lastname");
    }

    // RIG002
    @Test
    void dansPersonalDataPresent() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansRights.dansPersonalDataPresent, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Yes");
    }

    // RIG003
    @Test
    void dansMetadataLanguage() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansRights.dansMetadataLanguage, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Georgian", "Haitian, Haitian Creole", "English");
    }

    // REL001
    @Test
    void dansAudience() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansRel.dansAudience, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("D16500", "D16300", "D16200", "D16400", "D16100", "E16000", "D13400");
    }


    // REL002
    @Test
    void dansCollection() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansRel.dansCollection, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("https://vocabularies.dans.knaw.nl/collections/ssh/ce21b6fb-4283-4194-9369-b3ff4c3d76e7");
    }

    // REL003
    @Test
    void dansRelation() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansRel.dansRelation, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DansRel.dansRelationType))
            .map(Object::toString)
            .containsOnly(
                "is_required_by",
                "has_version",
                "requires",
                "references",
                "is_format_of",
                "is_version_of",
                "is_referenced_by",
                "relation",
                "replaces",
                "has_part",
                "conforms_to",
                "is_part_of",
                "has_format"
            );

        assertThat(statements)
            .map(getPropertyAsString(DansRel.dansRelationText))
            .map(Object::toString)
            .containsOnly(
                "Test requires",
                "Test is required by",
                "Test has version",
                "Test conforms to",
                "Test has format",
                "Test is part of",
                "Test references",
                "Test is referenced by",
                "Test replaces",
                "Test relation",
                "Test has part",
                "Test is format of",
                "Test is version of"
            );

        assertThat(statements)
            .map(getPropertyAsString(DansRel.dansRelationURI))
            .map(Object::toString)
            .containsOnly(
                "https://example.com/isReferencedBy",
                "https://example.com/replaces",
                "https://example.com/isRequiredBy",
                "https://example.com/isVersionOf",
                "https://example.com/hasVersion",
                "https://example.com/hasFormat",
                "https://example.com/conformsTo",
                "https://example.com/requires",
                "https://example.com/relation",
                "https://example.com/isPartOf",
                "https://example.com/hasPart",
                "https://example.com/references",
                "https://example.com/isFormatOf"
            );
    }


    // TS001
    @Test
    void temporalCoverage() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansTS.dansTemporalCoverage, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Het Romeinse Rijk", "De Oudheid");
    }

    // TS002
    @Test
    void spatialPoint() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansTS.dansSpatialPoint, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialPointX))
            .map(Object::toString)
            .containsOnly("126466", "4.288788");

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialPointY))
            .map(Object::toString)
            .containsOnly("52.078663", "529006");

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialPointScheme))
            .containsOnly(null, "http://www.opengis.net/def/crs/EPSG/0/28992");
    }

    // TS003
    @Test
    void spatialBox() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansTS.dansSpatialBox, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialBoxNorth))
            .containsOnly("628000", "53.23074335194507");

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialBoxEast))
            .containsOnly("140000", "6.563118076315912");

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialBoxSouth))
            .containsOnly("335000", "51.46343658020442");

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialBoxWest))
            .containsOnly("102000", "3.5621054065986075");

        assertThat(statements)
            .map(getPropertyAsString(DansTS.dansSpatialBoxScheme))
            .containsOnly("http://www.opengis.net/def/crs/EPSG/0/4326", "http://www.opengis.net/def/crs/EPSG/0/28992");

    }

    // TS006
    @Test
    void spatialCoverageControlled() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansTS.dansSpatialCoverageControlled, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("South Africa", "Japan");
    }

    // TS007
    @Test
    void spatialCoverageText() throws Exception {
        var obj = loadModel();
        var statements = obj.model.listStatements(
            new SimpleSelector(obj.resource, DansTS.dansSpatialCoverageText, (RDFNode) null)
        ).toList();

        assertThat(statements)
            .extracting("object")
            .map(Object::toString)
            .containsOnly("Roman Empire");
    }

    private ModelObject loadModel() throws Exception {
        var depositManager = new SimpleCommonDepositManager();
        var deposit = depositManager.loadDeposit(Path.of("/input/integration-test-complete-bag/c169676f-5315-4d86-bde0-a62dbc915228/"));
        deposit.setNbn("urn:nbn:nl:ui:13-4c-1a2b");

        var model = new OaiOreConverter().convert(deposit).getModel();

        return ModelObject.builder()
            .deposit(deposit)
            .resource(model.getResource(deposit.getNbn()))
            .model(model)
            .build();
    }

    private ThrowingExtractor<Statement, String, RuntimeException> getPropertyAsString(Property property) {
        return s -> {
            var prop = s.getObject().asResource().getProperty(property);

            if (prop == null) {
                return null;
            }

            return prop.getObject().toString();
        };
    }

    @Builder
    @Value
    private static class ModelObject {
        Deposit deposit;
        Model model;
        Resource resource;
        List<Resource> files;
    }
}
