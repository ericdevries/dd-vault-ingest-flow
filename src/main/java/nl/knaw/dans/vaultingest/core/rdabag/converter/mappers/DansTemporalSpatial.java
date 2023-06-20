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

import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DansTS;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.Collection;
import java.util.List;

import static nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.Generic.toBasicTerms;

public class DansTemporalSpatial {

    public static List<Statement> toTemporalCoverages(Resource resource, Collection<String> temporalCoverages) {
        return toBasicTerms(resource, DansTS.dansTemporalCoverage, temporalCoverages);
    }

    public static List<Statement> toSpatialCoverageControlled(Resource resource, Collection<String> spatialCoveragesControlled) {
        return toBasicTerms(resource, DansTS.dansSpatialCoverageControlled, spatialCoveragesControlled);
    }

    public static List<Statement> toSpatialCoverageText(Resource resource, Collection<String> spatialCoveragesText) {
        return toBasicTerms(resource, DansTS.dansSpatialCoverageText, spatialCoveragesText);
    }
}
