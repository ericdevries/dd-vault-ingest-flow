package nl.knaw.dans.vaultingest.core.mappings.metadata;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class Funder implements GrantNumber {
    String funderName;
    String fundingProgramme;
    String awardNumber;
    String awardTitle;

    @Override
    public String getAgency() {
        return funderName;
    }

    @Override
    public String getValue() {
        return Stream.of(fundingProgramme, awardNumber)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }
}
