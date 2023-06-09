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

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.dans.vaultingest.core.deposit.SimpleCommonDepositManager;
import nl.knaw.dans.vaultingest.core.utilities.InMemoryOutputWriter;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RdaBagWriterIntegrationTest {

    @Test
    void bagWriter_should_reference_originalmetadatazip_in_all_files() throws Exception {
        var depositManager = new SimpleCommonDepositManager();
        var deposit = depositManager.loadDeposit(Path.of("/input/integration-test-complete-bag/c169676f-5315-4d86-bde0-a62dbc915228"));

        var factory = new DefaultRdaBagWriterFactory(new ObjectMapper());
        var writer = factory.createRdaBagWriter();

        try (var output = new InMemoryOutputWriter()) {
            writer.write(deposit, output);

            var data = output.getData();
            assertThat(data.keySet())
                .map(Path::toString)
                .containsOnly(
                    "bag-info.txt",
                    "bagit.txt",
                    "manifest-sha1.txt",
                    "manifest-md5.txt",
                    "tagmanifest-sha1.txt",
                    "tagmanifest-md5.txt",
                    "data/random images/image02.jpeg",
                    "data/random images/image03.jpeg",
                    "data/random images/image01.png",
                    "data/a/deeper/path/With some file.txt",
                    "metadata/dataset.xml",
                    "metadata/files.xml",
                    "metadata/oai-ore.rdf",
                    "metadata/oai-ore.jsonld",
                    "original-metadata.zip",
                    "metadata/pid-mapping.txt",
                    "metadata/datacite.xml"
                );

            // pid-mapping should contain a reference
            var pidMappingsPaths = Arrays.stream(data.get(Path.of("metadata/pid-mapping.txt")).split("\n"))
                .map(line -> line.split("\\s+"))
                .filter(line -> line.length == 2)
                .map(line -> line[1])
                .collect(Collectors.toList());

            assertTrue(pidMappingsPaths.contains("original-metadata.zip"));

            // manifests should contain a reference
            var manifestPaths = Arrays.stream(data.get(Path.of("manifest-md5.txt")).split("\n"))
                .map(line -> line.split("\\s+"))
                .filter(line -> line.length == 2)
                .map(line -> line[1])
                .collect(Collectors.toList());

            assertTrue(manifestPaths.contains("original-metadata.zip"));

            // the oai-ore rdf file should have a reference to this file too
            var oaiOreRdf = data.get(Path.of("metadata/oai-ore.rdf"));
            assertTrue(oaiOreRdf.contains("<schema:name>original-metadata.zip</schema:name>"));

            // the tagmanifest files should NOT contain a reference
            var tagmanifestPaths = Arrays.stream(data.get(Path.of("tagmanifest-md5.txt")).split("\n"))
                .map(line -> line.split("\\s+"))
                .filter(line -> line.length == 2)
                .map(line -> line[1])
                .collect(Collectors.toList());

            assertFalse(tagmanifestPaths.contains("original-metadata.zip"));
        }
    }
}
