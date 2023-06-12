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
package nl.knaw.dans.vaultingest.core.deposit.mapping;

import nl.knaw.dans.vaultingest.core.deposit.CountryResolver;
import nl.knaw.dans.vaultingest.core.domain.metadata.SpatialBox;
import nl.knaw.dans.vaultingest.core.domain.metadata.SpatialPoint;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TemporalSpatial {

    public static List<String> getTemporalCoverages(Document ddm) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:temporal")
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
    }

    public static List<SpatialPoint> getSpatialPoints(Document ddm) {
        return XPathEvaluator.nodes(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcx-gml:spatial[count(gml:Point) = 1]")
            .map(item -> {
                var scheme = Optional.ofNullable(item.getAttributes().getNamedItem("srsName"))
                    .map(Node::getTextContent)
                    .orElse(null);

                var pos = XPathEvaluator.strings(item, "gml:Point/gml:pos")
                    .findFirst()
                    .map(p -> p.split("\\s+"))
                    .orElseThrow(() -> new RuntimeException("No pos found in spatial point"));

                if (pos.length != 2) {
                    throw new IllegalStateException("pos should have two values, but has " + pos.length);
                }

                return SpatialPoint.builder()
                    .point1(pos[0])
                    .point2(pos[1])
                    .scheme(scheme)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public static List<SpatialBox> getSpatialBoxes(Document ddm) {
        var filters = "@srsName = 'http://www.opengis.net/def/crs/EPSG/0/28992' or @srsName = 'http://www.opengis.net/def/crs/EPSG/0/4326'";
        return XPathEvaluator.nodes(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcx-gml:spatial/gml:boundedBy/gml:Envelope[" + filters + "]")
            .map(item -> {
                var scheme = Optional.ofNullable(item.getAttributes().getNamedItem("srsName"))
                    .map(Node::getTextContent)
                    .orElse(null);

                var lowerCorner = XPathEvaluator.strings(item, "gml:lowerCorner")
                    .findFirst()
                    .map(p -> p.split("\\s+"))
                    .orElseThrow(() -> new RuntimeException("No lowerCorner found in spatial box"));

                var upperCorner = XPathEvaluator.strings(item, "gml:upperCorner")
                    .findFirst()
                    .map(p -> p.split("\\s+"))
                    .orElseThrow(() -> new RuntimeException("No upperCorner found in spatial box"));

                if (lowerCorner.length != 2 || upperCorner.length != 2) {
                    throw new IllegalStateException("lowerCorner and upperCorner should have two values, but have " + lowerCorner.length + " and " + upperCorner.length);
                }

                return SpatialBox.builder()
                    .lower1(lowerCorner[0])
                    .lower2(lowerCorner[1])
                    .upper1(upperCorner[0])
                    .upper2(upperCorner[1])
                    .scheme(scheme)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public static List<String> getSpatialCoveragesControlled(Document ddm, CountryResolver countryResolver) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:spatial")
            .map(String::trim)
            .distinct()
            .filter(countryResolver::isControlledValue)
            .collect(Collectors.toList());
    }

    public static List<String> getSpatialCoveragesText(Document ddm, CountryResolver countryResolver) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:spatial")
            .map(String::trim)
            .distinct()
            .filter(c -> !countryResolver.isControlledValue(c))
            .collect(Collectors.toList());
    }
}
