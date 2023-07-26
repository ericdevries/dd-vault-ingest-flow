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
package nl.knaw.dans.vaultingest.client;

import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.validatedansbag.api.ValidateCommandDto;
import nl.knaw.dans.validatedansbag.api.ValidateOkDto;
import nl.knaw.dans.vaultingest.core.validator.BagValidator;
import nl.knaw.dans.vaultingest.core.validator.InvalidDepositException;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractBagValidator implements BagValidator {
    private final Client httpClient;
    private final URI serviceUri;

    public AbstractBagValidator(Client httpClient, URI serviceUri) {
        this.httpClient = httpClient;
        this.serviceUri = serviceUri;
    }

    @Override
    public void validate(Path bagDir) throws InvalidDepositException {
        if (bagDir == null) {
            throw new InvalidDepositException("Bag directory cannot be null");
        }

        var command = new ValidateCommandDto()
            .bagLocation(bagDir.toString())
            .packageType(getPackageType());

        log.debug("Validating bag {} with command {}", bagDir, command);

        try (var multipart = new FormDataMultiPart()
            .field("command", command, MediaType.APPLICATION_JSON_TYPE)) {

            try (var response = httpClient.target(serviceUri)
                .request()
                .post(Entity.entity(multipart, multipart.getMediaType()))) {

                log.debug("Validate bag response: {}", response);
                if (response.getStatus() == 200) {
                    var entity = response.readEntity(ValidateOkDto.class);

                    if (Boolean.FALSE.equals(entity.getIsCompliant())) {
                        throw formatValidationError(entity);
                    }
                }
            }
        }
        catch (IOException e) {
            log.error("Unable to create multipart form data object", e);
        }
    }

    private InvalidDepositException formatValidationError(ValidateOkDto result) {
        var violations = result.getRuleViolations().stream()
            .map(r -> String.format("- [%s] %s", r.getRule(), r.getViolation()))
            .collect(Collectors.joining("\n"));

        return new InvalidDepositException(String.format(
            "Bag was not valid according to Profile Version %s. Violations: %s",
            result.getProfileVersion(), violations)
        );
    }

    protected abstract ValidateCommandDto.PackageTypeEnum getPackageType();

}
