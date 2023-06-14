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

import nl.knaw.dans.vaultingest.core.domain.Deposit;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DVCore;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.SchemaDO;

import java.util.Optional;

public class Terms {


    public static Optional<Statement> toLicense(Resource resource, String license) {
        if (license == null) {
            return Optional.empty();
        }

        var model = resource.getModel();

        return Optional.ofNullable(model.createStatement(
            resource,
            SchemaDO.license,
            license
        ));
    }

    public static Statement toFileTermsOfAccess(Resource resource, Deposit deposit) {

        var model = resource.getModel();
        var termsOfAccess = model.createResource();

        // TRM002, TRM003, TRM004
        termsOfAccess.addProperty(DVCore.fileRequestAccess,
            String.valueOf(deposit.isRequestAccess()));

        // TRM005 and TRM006
        var terms = deposit.getTermsOfAccess();

        if (terms != null) {
            for (var term : terms) {
                termsOfAccess.addProperty(DVCore.termsOfAccess, term);
            }
        }

        return model.createStatement(
            resource,
            DVCore.fileTermsOfAccess,
            termsOfAccess
        );
    }
}
