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
import nl.knaw.dans.vaultingest.core.mappings.vocabulary.DansDVMetadata;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class VaultMetadata extends Base {

    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        var result = new ArrayList<Statement>();

        // VLT003
        toBasicTerm(resource, DansDVMetadata.dansBagId, deposit.getBagId())
            .ifPresent(result::add);

        // VLT004 and also VLT004(A), nbn is set during the loading
        toBasicTerm(resource, DansDVMetadata.dansNbn, deposit.getNbn())
            .ifPresent(result::add);

        // VLT005A
        toBasicTerm(resource, DansDVMetadata.dansOtherId, deposit.getDoi())
            .ifPresent(result::add);

        // VLT007
        toBasicTerm(resource, DansDVMetadata.dansSwordToken, deposit.getSwordToken())
            .ifPresent(result::add);

        // VLT008
        toBasicTerm(resource, DansDVMetadata.dansDataSupplier, deposit.getDepositorId())
            .ifPresent(result::add);

        return result;
    }

}
