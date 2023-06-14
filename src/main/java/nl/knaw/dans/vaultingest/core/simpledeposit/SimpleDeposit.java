package nl.knaw.dans.vaultingest.core.simpledeposit;

import lombok.ToString;
import lombok.experimental.SuperBuilder;
import nl.knaw.dans.vaultingest.core.deposit.CountryResolver;
import nl.knaw.dans.vaultingest.core.deposit.LanguageResolver;
import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.simpledeposit.mapping.Citation;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SuperBuilder
@ToString
public class SimpleDeposit {
    private final Document ddm;
    private final Document filesXml;
    private final String id;
    private final LanguageResolver languageResolver;
    private final CountryResolver countryResolver;
    private final List<DepositFile> depositFiles;
    private final Path path;
    private final SimpleDepositProperties properties;
    private String nbn;

    // CIT001
    public List<Statement> mapTitles(Resource resource) {
        return Citation.mapTitles(resource, ddm);
    }

    // CIT002
    public Optional<Statement> mapAlternativeTitle(Resource resource) {
        return Citation.mapAlternativeTitles(resource, ddm);
    }

    // CIT003 and CIT004
    public List<Statement> mapOtherIds(Resource resource) {
        return Citation.mapOtherIds(resource, ddm, properties);
    }

    // CIT005, CIT006 and CIT007
    public List<Statement> mapAuthors(Resource resource) {
        return Citation.mapAuthors(resource, ddm);
    }

    // etc
    public List<DepositFile> getPayloadFiles() {
        return depositFiles;
    }

    public List<Path> getMetadataFiles() throws IOException {
        return List.of();
    }

    public InputStream inputStreamForMetadataFile(Path path) {
        return new ByteArrayInputStream(("Data for path " + path).getBytes());
    }


    public enum State {
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