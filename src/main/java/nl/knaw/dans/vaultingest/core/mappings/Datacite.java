package nl.knaw.dans.vaultingest.core.mappings;

import nl.knaw.dans.vaultingest.core.deposit.Deposit;
import nl.knaw.dans.vaultingest.core.mappings.metadata.Contributor;
import nl.knaw.dans.vaultingest.core.mappings.metadata.DatasetRelation;
import nl.knaw.dans.vaultingest.core.mappings.metadata.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Datacite {

    public static String getTitle(Deposit deposit) {
        return Titles.getTitle(deposit.getDdm());
    }

    public static List<DatasetRelation> getAuthors(Deposit deposit) {
        var items = new ArrayList<DatasetRelation>();
        items.addAll(Authors.getAuthors(deposit.getDdm()));
        items.addAll(Authors.getCreators(deposit.getDdm()));
        items.addAll(Authors.getOrganizations(deposit.getDdm()));

        return items;
    }

    public static Optional<Description> getDescription(Deposit deposit) {
        return Descriptions.getDescription(deposit.getDdm());
    }

    public static List<Contributor> getContributors(Deposit deposit) {
        return Contributors.getContributors(deposit.getDdm());
    }

    public static String getDistributionDate(Deposit deposit) {
        return DistributionDate.getDistributionDate(deposit.getDdm());
    }
}
