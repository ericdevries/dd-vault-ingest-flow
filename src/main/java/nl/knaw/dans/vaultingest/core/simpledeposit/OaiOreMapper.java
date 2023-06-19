package nl.knaw.dans.vaultingest.core.simpledeposit;

import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.DVCore;
import nl.knaw.dans.vaultingest.core.rdabag.converter.mappers.vocabulary.ORE;
import nl.knaw.dans.vaultingest.core.simpledeposit.mapping.Citation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SchemaDO;

import java.time.OffsetDateTime;

public class OaiOreMapper {

    public Model mapToOaiOreModel(SimpleDeposit deposit) {
        var model = ModelFactory.createDefaultModel();
        var resourceMap = createResourceMap(model, deposit.getId());
        var resource = createAggregation(model, deposit);

        var ddm = deposit.getDdm();
        var filesXml = deposit.getFilesXml();
        var properties = deposit.getProperties();

        model.add(Citation.mapTitles(resource, ddm));

        Citation.mapAlternativeTitles(resource, ddm)
            .ifPresent(model::add);

        model.add(Citation.mapOtherIds(resource, ddm, properties));
        model.add(Citation.mapAuthors(resource, ddm));

        model.add(model.createStatement(
            resourceMap,
            ORE.describes,
            resource
        ));

        return model;
    }

    private Resource createResourceMap(Model model, String id) {
        var resourceMap = model.createResource("urn:uuid:" + id);
        var resourceMapType = model.createStatement(resourceMap, RDF.type, ORE.ResourceMap);

        model.add(resourceMapType);
        model.add(model.createStatement(
            resourceMap,
            DCTerms.modified,
            OffsetDateTime.now().toString()
        ));

        var creator = model.createResource();
        model.add(model.createStatement(
            creator,
            FOAF.name,
            "DANS Vault Service"
        ));

        model.add(model.createStatement(
            resourceMap,
            DCTerms.creator,
            creator
        ));

        return resourceMap;
    }

    private Resource createAggregatedResource(Model model, DepositFile depositFile) {
        var resource = model.createResource("urn:uuid:" + depositFile.getId());

        // TODO add access rights and checksum
        model.add(model.createStatement(resource, RDF.type, ORE.AggregatedResource));
        model.add(model.createStatement(resource, SchemaDO.name, depositFile.getPath().toString()));

        // FIL002A, FIL002B, FIL003, FIL004
        var descriptionText = depositFile.getDescription();

        if (descriptionText != null) {
            var description = model.createStatement(resource, SchemaDO.description, depositFile.getDescription());
            model.add(description);
        }

        // FIL002
        var directoryLabel = depositFile.getDirectoryLabel();

        if (directoryLabel != null) {
            model.add(model.createStatement(resource, DVCore.directoryLabel, directoryLabel.toString()));
        }

        // FIL005, FIL006, FIL007
        model.add(model.createStatement(resource, DVCore.restricted, Boolean.toString(depositFile.isRestricted())));

        return resource;
    }

    private Resource createAggregation(Model model, SimpleDeposit deposit) {
        var resource = model.createResource(deposit.getNbn());
        var type = model.createStatement(resource, RDF.type, ORE.Aggregation);

        model.add(type);

        var files = deposit.getPayloadFiles();

        if (files != null) {
            for (var file : files) {
                var fileResource = createAggregatedResource(model, file);

                model.add(model.createStatement(
                    resource,
                    ORE.aggregates,
                    fileResource
                ));
            }
        }

        return resource;
    }

}
