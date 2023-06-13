package nl.knaw.dans.vaultingest.core.rdabag.converter.mappers;

import nl.knaw.dans.vaultingest.core.domain.Deposit;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DVCore;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.SchemaDO;

import java.util.Optional;

public class Terms {


    public static Optional<Statement> toLicense(Resource resource, String license) {
        System.out.println("Terms.toLicense " + license);
        if (license == null) {
            return Optional.empty();
        }

        var model = resource.getModel();

        return Optional.ofNullable(model.createStatement(
            resource,
            SchemaDO.license,
            license
        ));
    }

    public static Statement toFileTermsOfAccess(Resource resource, Deposit deposit) {

        var model = resource.getModel();
        var termsOfAccess = model.createResource();

        // TRM002, TRM003, TRM004
        termsOfAccess.addProperty(DVCore.fileRequestAccess,
            String.valueOf(deposit.isRequestAccess()));

        // TRM005 and TRM006
        for (var term : deposit.getTermsOfAccess()) {
            termsOfAccess.addProperty(DVCore.termsOfAccess, term);
        }

        return model.createStatement(
            resource,
            DVCore.fileTermsOfAccess,
            termsOfAccess
        );
    }
}
