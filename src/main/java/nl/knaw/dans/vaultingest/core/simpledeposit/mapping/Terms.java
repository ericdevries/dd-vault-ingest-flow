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

import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import nl.knaw.dans.vaultingest.core.xml.XmlNamespaces;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Terms {
    // TRM001
    public static String getLicense(Document document) {
        var dctermsNamespace = document.lookupPrefix(XmlNamespaces.NAMESPACE_DCTERMS);

        return XPathEvaluator.strings(document,
                String.format("/ddm:DDM/ddm:dcmiMetadata/dcterms:license[@xsi:type='%s:URI']", dctermsNamespace)
            )
            .map(String::trim)
            .findFirst()
            .orElse(null);
    }

    public static boolean isRequestAccess(Document ddm, Collection<DepositFile> files) {
        // TRM002
        var containsNone = files.stream().anyMatch(f -> "NONE".equals(f.getAccessibleToRights()));

        if (containsNone) {
            return false;
        }

        // TRM003
        var accessRights = XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:profile/ddm:accessRights")
            .map(String::trim)
            .collect(Collectors.toSet());

        if (accessRights.contains("NO_ACCESS")) {
            return false;
        }

        // TRM004
        return accessRights.size() == 0;
    }

    public static List<String> getTermsOfAccess(Document ddm, Collection<DepositFile> files) {
        var result = new ArrayList<String>();
        // TRM005
        var containsNone = files.stream().anyMatch(f -> "NONE".equals(f.getAccessibleToRights()));
        var accessRights = XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:accessRights")
            .map(String::trim)
            .findFirst()
            .orElse(null);

        if (containsNone) {
            result.add(accessRights != null ? accessRights : "N/a");
        }

        // TRM006
        var knownOrRestrictive = Set.of("RESTRICTED_REQUEST", "KNOWN");

        var accessibleToRights = files.stream()
            .map(DepositFile::getAccessibleToRights)
            .filter(Objects::nonNull)
            .anyMatch(knownOrRestrictive::contains);

        if (accessibleToRights) {
            result.add(accessRights != null ? accessRights : "");
        }

        return result;
    }
}