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

import nl.knaw.dans.vaultingest.core.domain.metadata.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface Deposit {
    String getId();

    String getBagId();

    String getDoi();

    String getNbn();

    void setNbn(String nbn);

    String getTitle();

    boolean isUpdate();

    String getSwordToken();

    String getDepositorId();

    void setState(State state, String message);

    Collection<String> getAlternativeTitles();

    Collection<OtherId> getOtherIds();

    Optional<Description> getDescription();

    Collection<Description> getDescriptions();

    Collection<DatasetRelation> getAuthors();

    Collection<String> getSubjects();

    Collection<String> getRightsHolder();

    Collection<Keyword> getKeywords();

    Collection<Publication> getPublications();

    Collection<String> getLanguages();

    String getProductionDate();

    LocalDate getAvailableDate();

    Collection<Contributor> getContributors();

    Collection<GrantNumber> getGrantNumbers();

    Collection<Distributor> getDistributors();

    String getDistributionDate();

    Collection<CollectionDate> getCollectionDates();

    Collection<String> getSources();

    boolean isPersonalDataPresent();

    Collection<String> getMetadataLanguages();

    Collection<DepositFile> getPayloadFiles();

    Collection<Path> getMetadataFiles() throws IOException;

    InputStream inputStreamForMetadataFile(Path path);

    Collection<String> getAudiences();

    Collection<String> getInCollection();

    Collection<DansRelation> getDansRelations();

    Collection<String> getTemporalCoverages();

    Collection<String> getSpatialCoveragesControlled();

    Collection<String> getSpatialCoveragesText();

    String getLicense();

    boolean isRequestAccess();

    // TODO should this really be a collection?
    Collection<String> getTermsOfAccess();

    enum State {
        PUBLISHED,
        ACCEPTED,
        REJECTED,
        FAILED,
        DRAFT,
        FINALIZING,
        INVALID,
        SUBMITTED,
        UPLOADED
    }
}
