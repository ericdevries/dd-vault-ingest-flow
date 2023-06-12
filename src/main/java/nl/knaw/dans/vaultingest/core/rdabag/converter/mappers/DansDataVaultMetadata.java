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
package nl.knaw.dans.vaultingest.core.rdabag.converter.mappers;

import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DansDVMetadata;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DansTS;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.Generic.toBasicTerms;

public class DansDataVaultMetadata {

    // VLT001
    public static Optional<Statement> toDataversePid(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansDataversePid, value);
    }


    // VLT002
    public static Optional<Statement> toDataversePidVersion(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansDataversePidVersion, value);
    }

    // VLT003
    public static Optional<Statement> toBagId(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansBagId, value);
    }

    // VLT004
    public static Optional<Statement> toNbn(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansNbn, value);
    }


    // VLT005
    public static Optional<Statement> toOtherId(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansOtherId, value);
    }

    // VLT006
    public static Optional<Statement> toOtherIdVersion(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansOtherIdVersion, value);
    }

    // VLT007
    public static Optional<Statement> toSwordToken(Resource resource, String value) {
        return toStatement(resource, DansDVMetadata.dansSwordToken, value);
    }

    private static Optional<Statement> toStatement(Resource resource, Property property, String dataversePid) {
        if (dataversePid == null) {
            return Optional.empty();
        }

        return Optional.of(resource.getModel().createStatement(
            resource,
            property,
            dataversePid
        ));
    }
}
