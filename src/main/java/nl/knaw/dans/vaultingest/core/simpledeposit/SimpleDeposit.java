package nl.knaw.dans.vaultingest.core.simpledeposit;

import lombok.ToString;
import lombok.experimental.SuperBuilder;
import nl.knaw.dans.vaultingest.core.deposit.CountryResolver;
import nl.knaw.dans.vaultingest.core.deposit.LanguageResolver;
import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

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

    public SimpleDepositProperties getProperties() {
        return properties;
    }

    public Document getDdm() {
        return ddm;
    }

    public Document getFilesXml() {
        return filesXml;
    }

    public String getNbn() {
        return nbn;
    }

    public String getId() {
        return id;
    }

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