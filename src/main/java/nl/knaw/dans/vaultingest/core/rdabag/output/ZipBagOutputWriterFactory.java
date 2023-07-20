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

import nl.knaw.dans.vaultingest.core.deposit.Deposit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class ZipBagOutputWriterFactory implements BagOutputWriterFactory {
    private final Path outputDir;

    public ZipBagOutputWriterFactory(Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public BagOutputWriter createBagOutputWriter(Deposit deposit) throws IOException {
        var output = outputDir.resolve(outputFilename(deposit.getBagId(), deposit.getObjectVersion()));
        return new ZipBagOutputWriter(output);
    }

    String outputFilename(String bagId, Long objectVersion) {
        Objects.requireNonNull(bagId);
        Objects.requireNonNull(objectVersion);

        // strip anything before all colons (if present), and also the colon itself
        bagId = bagId.toLowerCase().replaceAll(".*:", "");

        return String.format("vaas-%s-v%s.zip", bagId, objectVersion);
    }
}
