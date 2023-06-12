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
package nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class DansTS {
    public static final String NS = "https://dar.dans.knaw.nl/schema/dansTemporalSpatial#";
    private static final Model m = ModelFactory.createDefaultModel();
    public static final Property dansTemporalCoverage = m.createProperty(NS, "dansTemporalCoverage");
    public static final Property dansSpatialCoverageControlled = m.createProperty(NS, "dansSpatialCoverageControlled");
    public static final Property dansSpatialCoverageText = m.createProperty(NS, "dansSpatialCoverageText");
    public static final Property dansSpatialPoint = m.createProperty(NS, "dansSpatialPoint");
    public static final Property dansSpatialPointX = m.createProperty(NS, "dansSpatialPointX");
    public static final Property dansSpatialPointY = m.createProperty(NS, "dansSpatialPointY");
    public static final Property dansSpatialPointScheme = m.createProperty(NS, "dansSpatialPointScheme");
    public static final Property dansSpatialBox = m.createProperty(NS, "dansSpatialBox");
    public static final Property dansSpatialBoxNorth = m.createProperty(NS, "dansSpatialBoxNorth");
    public static final Property dansSpatialBoxWest = m.createProperty(NS, "dansSpatialBoxWest");
    public static final Property dansSpatialBoxSouth = m.createProperty(NS, "dansSpatialBoxSouth");
    public static final Property dansSpatialBoxEast = m.createProperty(NS, "dansSpatialBoxEast");
    public static final Property dansSpatialBoxScheme = m.createProperty(NS, "dansSpatialBoxScheme");
}
