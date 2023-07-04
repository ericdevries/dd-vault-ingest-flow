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
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Languages extends Base {

    // TODO probably extract language resolving from deposit, make it a first class thing
    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        return toLanguages(resource, getLanguages(deposit.getDdm(), deposit));
    }

    static List<String> getLanguages(Document document, Deposit deposit) {
        // CIT018, ddm:language / @code
        return XPathEvaluator.strings(document,
                        "/ddm:DDM/ddm:dcmiMetadata/ddm:language[" +
                                "@encodingScheme='ISO639-1' or " +
                                "@encodingScheme='ISO639-2']/@code"
                )
                .map(deposit::resolveLanguage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    static List<Statement> toLanguages(Resource resource, Collection<String> languages) {
        return toBasicTerms(resource, DCTerms.language, languages);
    }
}
