/*
 * MIT License
 *
 * Copyright (c) 2024 Yvan Mazy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.darkkraft.minecraftremapper;

import be.darkkraft.minecraftremapper.http.RequestHttpClient;
import be.darkkraft.minecraftremapper.process.RemapperProcessor;
import be.darkkraft.minecraftremapper.process.exception.ProcessingException;
import be.darkkraft.minecraftremapper.setting.PreparationSettings;
import be.darkkraft.minecraftremapper.version.Version;
import be.darkkraft.minecraftremapper.version.fetcher.VersionFetcher;
import be.darkkraft.minecraftremapper.version.fetcher.exception.VersionFetchingException;
import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws ProcessingException {
        final Gson gson = new Gson();
        final RequestHttpClient httpClient = RequestHttpClient.newDefault();
        final VersionFetcher versionFetcher = VersionFetcher.newMojangFetcher(httpClient, gson);

        final List<Version> versions;
        try {
            versions = versionFetcher.fetchVersions();
        } catch (final VersionFetchingException e) {
            LOGGER.error("Failed to fetch Minecraft versions", e);
            System.exit(-1);
            return;
        }

        final Configuration config = new Configuration();
        final JCommander commander = JCommander.newBuilder().addObject(config).build();
        commander.parse(args);
        if (args.length == 0 || config.isHelp()) {
            commander.usage();
            return;
        }
        if (config.isList()) {
            int total = 0;
            for (final Version version : versions) {
                if (version.type().isOld()) {
                    continue;
                }
                total++;
                LOGGER.info("{} ({})", version.id(), version.type());
                if (version.id().equals("1.14.4")) { // Last version with mapping
                    break;
                }
            }
            LOGGER.info("Versions found: {}/{}", total, versions.size());
            return;
        }

        final DirectionType type = config.getType();
        if (type == null) {
            LOGGER.error("Please specify type between 'client' and 'server'.");
            System.exit(-1);
            return;
        }

        final String selectedVersion = config.getVersion();
        final Version version = versions.stream().filter(v -> v.id().equals(selectedVersion)).findFirst().orElse(null);

        if (version == null) {
            LOGGER.error("Version '{}' is not found!", selectedVersion);
            System.exit(-1);
            return;
        }

        LOGGER.info("Selected version: {} ({})", version.id(), type);
        LOGGER.info("Decompiling: {}", config.isDecompile());
        LOGGER.info("Output directory: {}", config.getOutputDirectory());
        LOGGER.info("----------------");

        final PreparationSettings settings =
                new PreparationSettings(httpClient, gson, type, version, config.getOutputDirectory(), config.isDecompile());

        final long start = System.currentTimeMillis();
        new RemapperProcessor(settings).process();
        LOGGER.info("Finished in {} seconds", (System.currentTimeMillis() - start) / 1_000);
    }

}