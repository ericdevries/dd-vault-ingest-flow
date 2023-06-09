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
package nl.knaw.dans.vaultingest.core.deposit;

import lombok.Builder;
import lombok.Getter;
import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.domain.ManifestAlgorithm;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Builder
@Getter
public class OriginalMetadataDepositFile implements DepositFile {
    private final String id;
    private final Path bagDir;

    @Override
    public boolean isRestricted() {
        return false;
    }

    @Override
    public Path getDirectoryLabel() {
        return null;
    }

    @Override
    public Path getFilename() {
        return Path.of("original-metadata.zip");
    }

    @Override
    public Path getPath() {
        return Path.of("original-metadata.zip");
    }

    @Override
    public String getDescription() {
        return "Original metadata";
    }

    @Override
    public InputStream openInputStream() throws IOException {
        var bytes = new ByteArrayOutputStream();
        var inputFiles = List.of(
            Path.of("metadata/dataset.xml"),
            Path.of("metadata/files.xml")
        );

        try (var output = new ZipOutputStream(bytes)) {
            for (var file : inputFiles) {
                var entry = new ZipEntry(file.getFileName().toString());
                output.putNextEntry(entry);

                try (var input = new FileInputStream(bagDir.resolve(file).toFile())) {
                    input.transferTo(output);
                }

                output.closeEntry();
            }
        }

        return new ByteArrayInputStream(bytes.toByteArray());
    }

    @Override
    public Map<ManifestAlgorithm, String> getChecksums() {
        // have the bag writer calculate the checksums for us
        return Map.of();
    }
}
