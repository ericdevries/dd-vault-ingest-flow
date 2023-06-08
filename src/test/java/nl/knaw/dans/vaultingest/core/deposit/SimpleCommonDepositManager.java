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

import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.reader.BagReader;
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.vaultingest.core.domain.Deposit;
import nl.knaw.dans.vaultingest.core.domain.DepositFile;
import nl.knaw.dans.vaultingest.core.domain.ManifestAlgorithm;
import nl.knaw.dans.vaultingest.core.domain.OriginalFilepaths;
import nl.knaw.dans.vaultingest.core.utilities.TestDatasetContactResolver;
import nl.knaw.dans.vaultingest.core.utilities.TestLanguageResolver;
import nl.knaw.dans.vaultingest.core.validator.InvalidDepositException;
import nl.knaw.dans.vaultingest.core.xml.XPathEvaluator;
import nl.knaw.dans.vaultingest.core.xml.XmlReader;
import nl.knaw.dans.vaultingest.core.xml.XmlReaderImpl;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class SimpleCommonDepositManager implements DepositManager {
    private final XmlReader xmlReader = new XmlReaderImpl();

    @Override
    public Deposit loadDeposit(Path inputPath) throws InvalidDepositException {
        try {
            var resource = getClass().getResource(inputPath.toString());
            assert resource != null;

            var path = Path.of(resource.getPath());
            var bagDir = getBagDir(path);

            log.info("Reading bag from path {}", bagDir);
            var bag = new BagReader().read(bagDir);

            log.info("Reading metadata/dataset.xml from path {}", bagDir);
            var ddm = readXmlFile(bagDir.resolve(Path.of("metadata", "dataset.xml")));

            log.info("Reading metadata/files.xml from path {}", bagDir);
            var filesXml = readXmlFile(bagDir.resolve(Path.of("metadata", "files.xml")));

            log.info("Generating original file paths if file exists");
            var originalFilePaths = getOriginalFilepaths(bagDir);

            log.info("Reading deposit.properties on path {}", path);
            var depositProperties = getDepositProperties(path);

            log.info("Generating payload file list on path {}", path);
            var depositFiles = getDepositFiles(bagDir, bag, ddm, filesXml, originalFilePaths);

            return CommonDeposit.builder()
                .id(UUID.randomUUID().toString())
                .path(path)
                .ddm(ddm)
                .bag(new CommonDepositBag(bag))
                .filesXml(filesXml)
                .depositFiles(depositFiles)
                .properties(depositProperties)
                .datasetContactResolver(new TestDatasetContactResolver())
                .languageResolver(new TestLanguageResolver())
                .build();
        }
        catch (Exception e) {
            throw new InvalidDepositException("Error loading deposit", e);
        }
    }

    @Override
    public void saveDeposit(Deposit deposit) {
        log.info("Ignoring saveDeposit");
    }

    @Override
    public void updateDepositState(Path path, Deposit.State state, String message) {
        log.info("Ignoring updateDepositState");
    }

    protected Path getBagDir(Path path) throws IOException {
        try (var list = Files.list(path)) {
            return list.filter(Files::isDirectory)
                .findFirst()
                .orElseThrow();
        }
    }

    protected Document readXmlFile(Path path) throws IOException, SAXException, ParserConfigurationException {
        return xmlReader.readXmlFile(path);
    }

    protected CommonDepositProperties getDepositProperties(Path path) throws ConfigurationException {
        var propertiesFile = path.resolve("deposit.properties");
        var params = new Parameters();
        var paramConfig = params.properties()
            .setFileName(propertiesFile.toString());

        var builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>
            (PropertiesConfiguration.class, null, true)
            .configure(paramConfig);

        return new CommonDepositProperties(builder);
    }

    protected OriginalFilepaths getOriginalFilepaths(Path bagDir) throws IOException {
        var originalFilepathsFile = bagDir.resolve("original-filepaths.txt");
        var result = new OriginalFilepaths();

        if (Files.exists(originalFilepathsFile)) {
            try (var lines = Files.lines(originalFilepathsFile)) {
                lines.filter(StringUtils::isNotBlank)
                    .map(line -> line.split("\\s+", 2))
                    .forEach(line -> result.addMapping(
                        Path.of(line[1]), Path.of(line[0]))
                    );
            }
        }

        return result;
    }

    List<DepositFile> getDepositFiles(Path bagDir, Bag bag, Document ddm, Document filesXml, OriginalFilepaths originalFilepaths) {
        var manifests = getPrecomputedChecksums(bagDir, bag);

        return XPathEvaluator.nodes(filesXml, "/files:files/files:file")
            .map(node -> {
                var filePath = node.getAttributes().getNamedItem("filepath").getTextContent();
                var physicalPath = bagDir.resolve(originalFilepaths.getPhysicalPath(Path.of(filePath)));
                var checksums = manifests.get(bagDir.relativize(physicalPath));

                return CommonDepositFile.builder()
                    .id(UUID.randomUUID().toString())
                    .physicalPath(physicalPath)
                    .filesXmlNode(node)
                    .ddmNode(ddm)
                    .checksums(checksums)
                    .build();
            })
            .collect(Collectors.toList());
    }

    protected Map<Path, Map<ManifestAlgorithm, String>> getPrecomputedChecksums(Path bagDir, Bag bag) {
        var manifests = new HashMap<Path, Map<ManifestAlgorithm, String>>();

        for (var manifest : bag.getPayLoadManifests()) {
            try {
                var alg = ManifestAlgorithm.from(manifest.getAlgorithm().getMessageDigestName());

                for (var entry : manifest.getFileToChecksumMap().entrySet()) {
                    var relativePath = bagDir.relativize(entry.getKey());
                    var checksum = entry.getValue();

                    manifests.computeIfAbsent(relativePath, k -> new HashMap<>())
                        .put(alg, checksum);
                }
            }
            catch (NoSuchAlgorithmException e) {
                log.warn("Bag contains a checksum algorithm that is not supported: algorithm={}",
                    manifest.getAlgorithm().getMessageDigestName(), e);
            }
        }

        return manifests;
    }
}
