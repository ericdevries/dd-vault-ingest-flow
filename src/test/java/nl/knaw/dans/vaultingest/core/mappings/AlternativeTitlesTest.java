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
package nl.knaw.dans.vaultingest.core.mappings;

import nl.knaw.dans.vaultingest.core.deposit.Deposit;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlternativeTitlesTest {

    @Test
    void toRDF_should_read_title_first() throws Exception {
        var model = ModelFactory.createDefaultModel();
        var resource = model.createResource();

        var deposit = Deposit.builder()
            .ddm(Util.readXml("<ddm:DDM\n"
                + "        xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
                + "        xmlns:ddm=\"http://schemas.dans.knaw.nl/dataset/ddm-v2/\"\n"
                + "        xmlns:dcterms=\"http://purl.org/dc/terms/\"\n"
                + ">\n"
                + "    <ddm:dcmiMetadata>\n"
                + "        <!-- CIT002 -->\n"
                + "        <dcterms:title>DCTERMS title 1</dcterms:title>\n"
                + "        <dc:title>DC title 1</dc:title>\n"
                + "        <dcterms:alternative>DCTERMS alt title 1</dcterms:alternative>\n"
                + "    </ddm:dcmiMetadata>\n"
                + "</ddm:DDM>"
            ))
            .build();

        var result = AlternativeTitles.toRDF(resource, deposit);

        var expected = model.createStatement(
            resource,
            DCTerms.alternative,
            "DCTERMS title 1"
        );

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expected);
    }

    @Test
    void toRDF_should_also_handle_different_namespace_for_title() throws Exception {
        var model = ModelFactory.createDefaultModel();
        var resource = model.createResource();

        var deposit = Deposit.builder()
            .ddm(Util.readXml("<ddm:DDM\n"
                + "        xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
                + "        xmlns:ddm=\"http://schemas.dans.knaw.nl/dataset/ddm-v2/\"\n"
                + "        xmlns:dcterms=\"http://purl.org/dc/terms/\"\n"
                + ">\n"
                + "    <ddm:dcmiMetadata>\n"
                + "        <!-- CIT002 -->\n"
                + "        <dc:title>DC title 1</dc:title>\n"
                + "        <dcterms:title>DCTERMS title 1</dcterms:title>\n"
                + "        <dcterms:alternative>DCTERMS alt title 1</dcterms:alternative>\n"
                + "    </ddm:dcmiMetadata>\n"
                + "</ddm:DDM>"
            ))
            .build();

        var result = AlternativeTitles.toRDF(resource, deposit);
        var expected = model.createStatement(
            resource,
            DCTerms.alternative,
            "DC title 1"
        );
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expected);
    }

    @Test
    void toRDF_should_use_alternative_title_if_title_is_not_found() throws Exception {
        var model = ModelFactory.createDefaultModel();
        var resource = model.createResource();

        var deposit = Deposit.builder()
            .ddm(Util.readXml("<ddm:DDM\n"
                + "        xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
                + "        xmlns:ddm=\"http://schemas.dans.knaw.nl/dataset/ddm-v2/\"\n"
                + "        xmlns:dcterms=\"http://purl.org/dc/terms/\"\n"
                + ">\n"
                + "    <ddm:dcmiMetadata>\n"
                + "        <!-- CIT002 -->\n"
                + "        <dcterms:alternative>DCTERMS alt title 1</dcterms:alternative>\n"
                + "    </ddm:dcmiMetadata>\n"
                + "</ddm:DDM>"
            ))
            .build();

        var result = AlternativeTitles.toRDF(resource, deposit);

        assertThat(result).isPresent();

        var expected = model.createStatement(
            resource,
            DCTerms.alternative,
            "DCTERMS alt title 1"
        );

        assertThat(expected).isEqualTo(result.get());
    }
}