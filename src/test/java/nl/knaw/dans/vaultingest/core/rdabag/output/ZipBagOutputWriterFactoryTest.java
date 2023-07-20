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
package nl.knaw.dans.vaultingest.core.rdabag.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ZipBagOutputWriterFactoryTest {

    @Test
    void outputFilename() {
        var bagId = "A129CD20-01F7-49E2-B6A1-B9CECF625471";
        var objectVersion = 15L;

        var result = new ZipBagOutputWriterFactory(null).outputFilename(bagId, objectVersion);

        assertThat(result).isEqualTo("vaas-a129cd20-01f7-49e2-b6a1-b9cecf625471-v15.zip");
    }

    @Test
    void outputFilename_should_strip_urn_uuid_prefix() {
        var bagId = "urn:uuid:A129CD20-01F7-49E2-B6A1-B9CECF625471";
        var objectVersion = 15L;

        var result = new ZipBagOutputWriterFactory(null).outputFilename(bagId, objectVersion);

        assertThat(result).isEqualTo("vaas-a129cd20-01f7-49e2-b6a1-b9cecf625471-v15.zip");
    }
}