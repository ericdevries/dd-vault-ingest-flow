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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.knaw.dans.vaultingest.core.domain.ManifestAlgorithm;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import org.w3c.dom.Node;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Builder
public class DepositFile {
    private final String id;
    private final Node filesXmlNode;
    private final Node ddmNode;

    private final Path physicalPath;
    private final Map<ManifestAlgorithm, String> checksums;
    // TODO embargoes

    public String getId() {
        return id;
    }

    public boolean isRestricted() {
        var accessibleToRights = getAccessibleToRights();
        var accessRights = getAccessRights();

        if (accessibleToRights != null) {
            // if ANONYMOUS then false else true
            return !"ANONYMOUS".equals(accessibleToRights);
        }

        if (accessRights != null) {
            // if OPEN_ACCESS then false else true
            return !"OPEN_ACCESS".equals(accessRights);
        }

        return false;
    }

    public Path getDirectoryLabel() {
        return getFilePath().getParent();
    }

    public Path getFilename() {
        return getFilePath().getFileName();
    }

    public Path getPath() {
        var directoryLabel = getDirectoryLabel();

        if (directoryLabel != null) {
            return getDirectoryLabel().resolve(getFilename());
        }

        return getFilename();
    }

    public String getDescription() {
        return XPathEvaluator.strings(filesXmlNode, "dcterms:description")
            .findFirst().orElse(null);
    }

    public InputStream openInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(physicalPath.toFile()));
    }

    private String getFilePathAttribute() {
        return filesXmlNode.getAttributes().getNamedItem("filepath").getTextContent();
    }

    private Path getFilePath() {
        return Path.of(getFilePathAttribute());//.substring("data/".length()));
    }

    public String getAccessibleToRights() {
        return XPathEvaluator.strings(filesXmlNode, "files:accessibleToRights")
            .findFirst()
            .orElse(null);
    }

    private String getAccessRights() {
        return XPathEvaluator.strings(ddmNode, "/ddm:DDM/ddm:profile/ddm:accessRights")
            .findFirst()
            .orElse(null);
    }

    public Map<ManifestAlgorithm, String> getChecksums() {
        return Collections.unmodifiableMap(checksums);
    }
}
