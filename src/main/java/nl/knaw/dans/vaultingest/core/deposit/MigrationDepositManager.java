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
package nl.knaw.dans.vaultingest.core.deposit;

import gov.loc.repository.bagit.reader.BagReader;
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.vaultingest.core.domain.Deposit;
import nl.knaw.dans.vaultingest.core.validator.DepositValidator;
import nl.knaw.dans.vaultingest.core.validator.InvalidDepositException;
import nl.knaw.dans.vaultingest.core.xml.XmlReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class MigrationDepositManager extends AbstractDepositManager {
    private final LanguageResolver languageResolver;
    private final DepositValidator depositValidator;

    public MigrationDepositManager(XmlReader xmlReader, LanguageResolver languageResolver, DepositValidator depositValidator) {
        super(xmlReader);
        this.languageResolver = languageResolver;
        this.depositValidator = depositValidator;
    }

    public Deposit loadDeposit(Path path) throws InvalidDepositException {
        try {
            var bagDir = getBagDir(path);

            depositValidator.validate(bagDir);

            var bag = new BagReader().read(bagDir);
            var ddm = readXmlFile(bagDir.resolve(Path.of("metadata", "dataset.xml")));
            var filesXml = readXmlFile(bagDir.resolve(Path.of("metadata", "files.xml")));
            var agreements = readOptionalXmlFile(bagDir.resolve(Path.of("metadata", "amd.xml")));
            var amd = readOptionalXmlFile(bagDir.resolve(Path.of("metadata", "agreements.xml")));
            var originalFilePaths = getOriginalFilepaths(bagDir);

            var depositProperties = getDepositProperties(path);
            var depositFiles = getDepositFiles(bagDir, bag, ddm, filesXml, originalFilePaths);

            return MigrationDeposit.builder()
                .id(path.getFileName().toString())
                .ddm(ddm)
                .filesXml(filesXml)
                .agreementsXml(agreements)
                .amdXml(amd)
                .bag(new CommonDepositBag(bag))
                .depositFiles(depositFiles)
                .properties(depositProperties)
                .languageResolver(languageResolver)
                .build();

        }
        catch (InvalidDepositException e) {
            log.error("Invalid deposit: path={}", path, e);
            throw e;
        }
        catch (Exception e) {
            log.error("Error loading deposit from disk: path={}", path, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveDeposit(Deposit deposit) {

    }

    @Override
    public void updateDepositState(Path path, Deposit.State state, String message) {

    }

    Document readOptionalXmlFile(Path path) throws IOException, SAXException, ParserConfigurationException {
        try {
            return readXmlFile(path);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }
}
