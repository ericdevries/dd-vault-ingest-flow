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

import nl.knaw.dans.vaultingest.core.domain.metadata.SpatialBox;
import nl.knaw.dans.vaultingest.core.domain.metadata.SpatialPoint;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DansRights;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DansTS;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.Generic.toBasicTerms;

public class DansTemporalSpatial {

    public static List<Statement> toLanguages(Resource resource, Collection<String> metadataLanguages) {
        if (metadataLanguages == null) {
            return List.of();
        }

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var language : metadataLanguages) {
            result.add(model.createStatement(
                resource,
                DansRights.dansMetadataLanguage,
                language
            ));
        }

        return result;
    }

    public static List<Statement> toTemporalCoverages(Resource resource, Collection<String> temporalCoverages) {
        return toBasicTerms(resource, DansTS.dansTemporalCoverage, temporalCoverages);
    }

    public static List<Statement> toSpatialPoints(Resource resource, Collection<SpatialPoint> spatialPoints) {
        if (spatialPoints == null) {
            return List.of();
        }

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var spatialPoint : spatialPoints) {
            var point = spatialPoint.getPoint();
            var element = model.createResource();
            element.addProperty(DansTS.dansSpatialPointX, point.getX());
            element.addProperty(DansTS.dansSpatialPointY, point.getY());

            if (spatialPoint.getScheme() != null) {
                element.addProperty(DansTS.dansSpatialPointScheme, spatialPoint.getScheme());
            }

            result.add(model.createStatement(
                resource,
                DansTS.dansSpatialPoint,
                element
            ));
        }

        return result;
    }

    public static List<Statement> toSpatialBoxes(Resource resource, Collection<SpatialBox> spatialBoxes) {
        if (spatialBoxes == null) {
            return List.of();
        }

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var spatialBox : spatialBoxes) {
            var box = spatialBox.getBox();
            var element = model.createResource();
            element.addProperty(DansTS.dansSpatialBoxEast, box.getEast());
            element.addProperty(DansTS.dansSpatialBoxWest, box.getWest());
            element.addProperty(DansTS.dansSpatialBoxNorth, box.getNorth());
            element.addProperty(DansTS.dansSpatialBoxSouth, box.getSouth());

            if (spatialBox.getScheme() != null) {
                element.addProperty(DansTS.dansSpatialBoxScheme, spatialBox.getScheme());
            }

            result.add(model.createStatement(
                resource,
                DansTS.dansSpatialBox,
                element
            ));
        }

        return result;
    }

    public static List<Statement> toSpatialCoverageControlled(Resource resource, Collection<String> spatialCoveragesControlled) {
        return toBasicTerms(resource, DansTS.dansSpatialCoverageControlled, spatialCoveragesControlled);
    }

    public static List<Statement> toSpatialCoverageText(Resource resource, Collection<String> spatialCoveragesText) {
        return toBasicTerms(resource, DansTS.dansSpatialCoverageText, spatialCoveragesText);
    }
}
