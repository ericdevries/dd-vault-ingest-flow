package nl.knaw.dans.vaultingest.core.rdabag.converter.mappers;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Generic {
    public static List<Statement> toBasicTerms(Resource resource, Property property, Collection<String> values) {
        if (values == null) {
            return List.of();
        }

        var model = resource.getModel();
        var result = new ArrayList<Statement>();

        for (var audience : values) {
            result.add(model.createStatement(
                resource,
                property,
                audience
            ));
        }

        return result;
    }
}
