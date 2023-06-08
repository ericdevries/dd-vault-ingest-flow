package nl.knaw.dans.vaultingest.core.rdabag;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.dans.vaultingest.core.rdabag.converter.DataciteConverter;
import nl.knaw.dans.vaultingest.core.rdabag.converter.OaiOreConverter;
import nl.knaw.dans.vaultingest.core.rdabag.converter.PidMappingConverter;
import nl.knaw.dans.vaultingest.core.rdabag.serializer.DataciteSerializer;
import nl.knaw.dans.vaultingest.core.rdabag.serializer.OaiOreSerializer;
import nl.knaw.dans.vaultingest.core.rdabag.serializer.OriginalMetadataSerializer;
import nl.knaw.dans.vaultingest.core.rdabag.serializer.PidMappingSerializer;

public class DefaultRdaBagWriterFactory implements RdaBagWriterFactory {

    private final DataciteSerializer dataciteSerializer;
    private final PidMappingSerializer pidMappingSerializer;
    private final OaiOreSerializer oaiOreSerializer;
    private final OriginalMetadataSerializer originalMetadataSerializer;

    private final DataciteConverter dataciteConverter;
    private final PidMappingConverter pidMappingConverter;
    private final OaiOreConverter oaiOreConverter;

    public DefaultRdaBagWriterFactory(ObjectMapper objectMapper) {
        this.dataciteSerializer = new DataciteSerializer();
        this.pidMappingSerializer = new PidMappingSerializer();
        this.oaiOreSerializer = new OaiOreSerializer(objectMapper);
        this.originalMetadataSerializer = new OriginalMetadataSerializer();
        this.dataciteConverter = new DataciteConverter();
        this.pidMappingConverter = new PidMappingConverter();
        this.oaiOreConverter = new OaiOreConverter();
    }

    @Override
    public RdaBagWriter createRdaBagWriter() {
        return new RdaBagWriter(
            dataciteSerializer,
            pidMappingSerializer,
            oaiOreSerializer,
            originalMetadataSerializer,
            dataciteConverter,
            pidMappingConverter,
            oaiOreConverter
        );
    }
}
