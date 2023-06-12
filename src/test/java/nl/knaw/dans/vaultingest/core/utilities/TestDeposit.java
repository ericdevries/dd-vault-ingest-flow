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
package nl.knaw.dans.vaultingest.core.utilities;

import lombok.Builder;
import lombok.Data;
import nl.knaw.dans.vaultingest.core.domain.DansRelation;
import nl.knaw.dans.vaultingest.core.domain.Deposit;
import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.domain.metadata.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class TestDeposit implements Deposit {
    boolean update;
    private String id;
    private String title;
    private String nbn;
    private String doi;
    private List<Description> descriptions;
    private List<DatasetRelation> authors;
    private List<String> rightsHolder;
    private String subject;
    private List<String> alternativeTitles;
    private List<OtherId> otherIds;
    private List<DepositFile> payloadFiles;
    private String depositorId;
    private String swordToken;

    private List<String> subjects;
    private List<Keyword> keywords;
    private List<Publication> publications;
    private List<String> languages;
    private String productionDate;

    private List<Contributor> contributors;
    private List<GrantNumber> grantNumbers;
    private List<Distributor> distributors;
    private String distributionDate;
    private List<CollectionDate> collectionDates;
    private List<SeriesElement> series;
    private DatasetContact contact;
    private List<String> sources;
    private State state;
    private String stateDescription;
    private List<String> metadataLanguages;
    private List<String> audiences;
    private boolean personalDataPresent;

    private List<String> inCollection;
    private List<DansRelation> dansRelations;

    private List<String> temporalCoverages;
    private List<SpatialPoint> spatialPoints;
    private List<SpatialBox> spatialBoxes;
    private List<String> spatialCoveragesControlled;
    private List<String> spatialCoveragesText;

    @Override
    public void setState(State state, String message) {
        this.state = state;
        this.stateDescription = message;
    }

    @Override
    public Collection<Path> getMetadataFiles() {
        return List.of(
            Path.of("bag-info.txt"),
            Path.of("bagit.txt"),
            Path.of("metadata/files.xml"),
            Path.of("metadata/dataset.xml")
        );
    }

    @Override
    public InputStream inputStreamForMetadataFile(Path path) {
        return new ByteArrayInputStream(
            String.format(
                "This is a test metadata file with path %s", path
            ).getBytes()
        );
    }
}
