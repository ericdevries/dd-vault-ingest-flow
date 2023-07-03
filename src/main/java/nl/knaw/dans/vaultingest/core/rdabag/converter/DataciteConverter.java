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
package nl.knaw.dans.vaultingest.core.rdabag.converter;

import nl.knaw.dans.vaultingest.core.deposit.Deposit;
import nl.knaw.dans.vaultingest.domain.*;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class DataciteConverter {

    public Resource convert(Deposit deposit) {
        return getResource(deposit);
    }

    private Resource getResource(Deposit deposit) {
        var resource = new Resource();
//        resource.setTitles(geTitles(deposit));
//        resource.setResourceType(getResourceType());
//        resource.setCreators(getCreators(deposit));
//        resource.setIdentifier(getIdentifier(deposit));
//        resource.setPublisher(getPublisher());
//        resource.setDescriptions(getDescriptions(deposit));
//        resource.setPublicationYear(getPublicationYear(deposit));
//        resource.setContributors(getContributors(deposit));

        return resource;
    }
//
//    private String getPublicationYear(Deposit deposit) {
//        // TODO verify what date to use; I assumed distribution date
//        try {
//            var date = new SimpleDateFormat("yyyy-MM-dd").parse(deposit.getDistributionDate());
//            return new SimpleDateFormat("yyyy").format(date);
//        }
//        catch (Exception e) {
//            return null;
//        }
//    }
//
//    private Resource.Titles geTitles(Deposit deposit) {
//        var titles = new Resource.Titles();
//        var title = new Resource.Titles.Title();
//        title.setValue(deposit.getTitle());
//        titles.getTitle().add(title);
//        return titles;
//    }
//
//    private Resource.ResourceType getResourceType() {
//        var resourceType = new Resource.ResourceType();
//        resourceType.setResourceTypeGeneral(ResourceType.DATASET);
//        return resourceType;
//    }
//
//    private Resource.Creators getCreators(Deposit deposit) {
//        var creators = new Resource.Creators();
//
//        if (deposit.getAuthors() != null) {
//            var authors = deposit.getAuthors()
//                .stream()
//                .map(a -> {
//                    var creator = new Resource.Creators.Creator();
//                    var name = new Resource.Creators.Creator.CreatorName();
//                    name.setValue(a.getDisplayName());
//                    creator.setCreatorName(name);
//
//                    if (a.getAffiliation() != null) {
//                        var affiliation = new Affiliation();
//                        affiliation.setValue("(" + a.getAffiliation() + ")");
//                        creator.getAffiliation().add(affiliation);
//                    }
//
//                    var nameIdentifier = a.getIdentifier();
//
//                    if (nameIdentifier != null) {
//                        var identifier = new NameIdentifier();
//                        identifier.setValue(nameIdentifier.getValue());
//                        identifier.setNameIdentifierScheme(nameIdentifier.getScheme());
//                        identifier.setSchemeURI(nameIdentifier.getSchemeURI());
//
//                        creator.getNameIdentifier().add(identifier);
//                    }
//
//                    return creator;
//                })
//                .collect(Collectors.toList());
//
//            creators.getCreator().addAll(authors);
//        }
//
//        return creators;
//    }
//
//    private Resource.Identifier getIdentifier(Deposit deposit) {
//        var id = deposit.getDoi().substring(deposit.getDoi().indexOf(':') + 1);
//        var identifier = new Resource.Identifier();
//        identifier.setIdentifierType("DOI");
//        identifier.setValue(id);
//
//        return identifier;
//    }
//
//    private Resource.Publisher getPublisher() {
//        var publisher = new Resource.Publisher();
//        // TODO get from configuration
//        publisher.setValue("DANS");
//        return publisher;
//    }
//
//    private Resource.Descriptions getDescriptions(Deposit deposit) {
//        var descriptions = new Resource.Descriptions();
//
//        deposit.getDescription().ifPresent(d -> {
//            var description = new Resource.Descriptions.Description();
//            description.setDescriptionType(DescriptionType.ABSTRACT);
//            description.getContent().add(d.getValue());
//            descriptions.getDescription().add(description);
//        });
//
//        return descriptions;
//    }
//
//    private Resource.Contributors getContributors(Deposit deposit) {
//        var contributors = new Resource.Contributors();
//
//        if (deposit.getContributors() != null) {
//            for (var item : deposit.getContributors()) {
//
//                var contributor = new Resource.Contributors.Contributor();
//                var name = new Resource.Contributors.Contributor.ContributorName();
//                name.setValue(item.getName());
//                contributor.setContributorName(name);
//                contributor.setContributorType(getContributorType(item.getType()));
//                contributors.getContributor().add(contributor);
//            }
//        }
//
//        return contributors;
//    }
//
//    private ContributorType getContributorType(String type) {
//        try {
//            return ContributorType.fromValue(type);
//        }
//        catch (Exception e) {
//            return ContributorType.OTHER;
//        }
//    }

}
