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

package be.darkkraft.minecraftremapper.process;

import be.darkkraft.minecraftremapper.DirectionType;
import be.darkkraft.minecraftremapper.http.exception.RequestHttpException;
import be.darkkraft.minecraftremapper.process.exception.ProcessingException;
import be.darkkraft.minecraftremapper.setting.PreparationSettings;
import be.darkkraft.minecraftremapper.util.FileUtil;
import com.google.gson.JsonObject;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.decompiler.api.Decompiler;
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class RemapperProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemapperProcessor.class);

    private final PreparationSettings config;
    private final Path root;

    private JsonObject downloadJson;

    public RemapperProcessor(final @NotNull PreparationSettings config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.root = Path.of(config.outputDirectory(), this.config.version().id() + config.target().name().toLowerCase());
    }

    public void process() throws ProcessingException {
        this.createOutputDirectory();
        this.downloadJson = this.fetchDownloadJson();

        final DownloadResult jarResult = this.downloadJar();
        // Unpack server version jar
        if (this.config.target() == DirectionType.SERVER) {
            if (jarResult.skipped()) {
                LOGGER.info("SKIP --> Unpack server is already done.");
            } else {
                this.unpackServerJar(jarResult.path());
            }
        }
        final Path mappingPath = this.downloadMapping();
        if (this.config.remap()) {
            final Path remapPath = this.remapJar(jarResult, mappingPath);
            if (this.config.decompile()) {
                LOGGER.info("Decompiling...");
                final Path path = remapPath.resolveSibling("decompiled");
                try {
                    FileUtil.recursiveDelete(path);
                } catch (final IOException e) {
                    LOGGER.error("Failed to delete directory with decompiled files, continue to decompile...", e);
                }
                Decompiler.builder().inputs(remapPath.toFile()).output(new DirectoryResultSaver(path.toFile())).build().decompile();
            }
        }
    }

    public @NotNull Path getVersionJarPath() {
        return this.root.resolve(this.config.version().id() + ".jar");
    }

    public @NotNull Path getMappingPath() {
        return this.root.resolve(this.config.version().id() + ".map");
    }

    private void createOutputDirectory() throws ProcessingException {
        if (!Files.isDirectory(this.root)) {
            try {
                Files.createDirectories(this.root);
            } catch (final IOException e) {
                throw new ProcessingException("Failed to create output directory", e);
            }
        }
    }

    private JsonObject fetchDownloadJson() throws ProcessingException {
        final String url = this.config.version().url();
        final String json;
        try {
            json = this.config.httpClient().getString(url);
        } catch (final RequestHttpException e) {
            throw new ProcessingException("Failed to download version metadata", e);
        }
        return this.config.gson().fromJson(json, JsonObject.class).getAsJsonObject("downloads");
    }

    private DownloadResult downloadJar() throws ProcessingException {
        return this.download("Version jar", this.config.getTargetKey(), "jar");
    }

    private Path downloadMapping() throws ProcessingException {
        return this.download("Version mapping", this.config.getTargetKey() + "_mappings", "map").path();
    }

    private void unpackServerJar(final Path path) throws ProcessingException {
        LOGGER.info("Unpack server jar...");
        final String id = this.config.version().id();
        try (final FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
            final Path source = fs.getPath("META-INF/versions/" + id + "/server-" + id + ".jar");
            if (Files.notExists(source)) {
                return;
            }
            final Path destination = path.getParent().resolve(id + ".jar");
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new ProcessingException("Failed to unpack server jar", e);
        }
    }

    private Path remapJar(final DownloadResult jarResult, final Path mappingPath) throws ProcessingException {
        final Path path = this.root.resolve("remapped-" + this.config.version().id() + ".jar");
        if (jarResult.skipped() && FileUtil.isValidJar(path)) {
            LOGGER.info("SKIP --> Remapping is already done.");
            return path;
        }
        LOGGER.info("Load mappings...");
        final JarMapping jarMapping = new JarMapping();
        try {
            jarMapping.loadMappings(mappingPath.toFile());
        } catch (final IOException e) {
            throw new ProcessingException("Failed to load mapping", e);
        }
        final JarRemapper jarRemapper = new JarRemapper(jarMapping);
        LOGGER.info("Remapping...");
        try {
            jarRemapper.remapJar(Jar.init(jarResult.path().toFile()), path.toFile());
        } catch (final IOException e) {
            throw new ProcessingException("Failed to remap jar", e);
        }
        return path;
    }

    private DownloadResult download(final String display, final String jsonKey, final String extension) throws ProcessingException {
        final JsonObject base = this.downloadJson.getAsJsonObject(jsonKey);
        final Path path = this.root.resolve(this.config.version().id() + '.' + extension);
        final String sha1 = base.get("sha1").getAsString();

        try {
            if (this.isAlreadyDownloaded(path, sha1)) {
                LOGGER.info("SKIP --> {} is already downloaded.", display);
                return new DownloadResult(path, true);
            }
        } catch (final IOException e) {
            throw new ProcessingException("Failed to check sha1 file", e);
        }

        LOGGER.info("Downloading {}...", display);
        final long start = System.currentTimeMillis();

        final String fileUrl = base.get("url").getAsString();
        final byte[] fileContent;
        try {
            fileContent = this.config.httpClient().getBytes(fileUrl);
        } catch (final RequestHttpException e) {
            throw new ProcessingException("Failed to download file data", e);
        }
        try {
            Files.write(path, fileContent);
        } catch (final IOException e) {
            throw new ProcessingException("Failed to write file", e);
        }
        try {
            Files.writeString(this.toHashPath(path), sha1);
        } catch (final IOException e) {
            throw new ProcessingException("Failed to write sha1 file", e);
        }

        LOGGER.info("{} is downloaded in {}ms", display, System.currentTimeMillis() - start);
        return new DownloadResult(path, false);
    }

    private boolean isAlreadyDownloaded(final Path path, final String sha1) throws IOException {
        if (Files.notExists(path)) {
            return false;
        }
        if (path.getFileName().toString().endsWith(".jar") && !FileUtil.isValidJar(path)) {
            return false;
        }
        final Path hashFile = this.toHashPath(path);
        if (Files.exists(hashFile)) {
            return Files.readString(hashFile).equals(sha1);
        }
        return false;
    }

    private Path toHashPath(final @NotNull Path path) {
        return path.toAbsolutePath().resolveSibling(path.getFileName().toString() + ".sha1");
    }

}