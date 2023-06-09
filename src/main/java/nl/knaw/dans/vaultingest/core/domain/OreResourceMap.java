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
package nl.knaw.dans.vaultingest.core.domain;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Value
public class OreResourceMap {
    Model model;

    public Set<String> getUsedNamespaces() {
        var predicateNamespaces = this.model.listStatements()
            .toList().stream()
            .map(Statement::getPredicate)
            .map(Property::getNameSpace)
            .collect(Collectors.toSet());

        log.trace("predicateNamespaces: {}", predicateNamespaces);

        return predicateNamespaces;
    }
}
