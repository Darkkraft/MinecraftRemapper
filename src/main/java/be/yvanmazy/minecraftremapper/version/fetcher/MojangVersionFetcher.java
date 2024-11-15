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

package be.yvanmazy.minecraftremapper.version.fetcher;

import be.yvanmazy.minecraftremapper.http.RequestHttpClient;
import be.yvanmazy.minecraftremapper.version.Version;
import be.yvanmazy.minecraftremapper.version.VersionJsonAdapter;
import be.yvanmazy.minecraftremapper.version.fetcher.exception.VersionFetchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

final class MojangVersionFetcher implements VersionFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MojangVersionFetcher.class);
    private static final String URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    private final RequestHttpClient httpClient;
    private final Gson gson;

    public MojangVersionFetcher(final @NotNull RequestHttpClient httpClient, final @NotNull Gson gson) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.gson = Objects.requireNonNull(gson, "gson must not be null");
    }

    @Override
    public @NotNull List<Version> fetchVersions() throws VersionFetchingException {
        try {
            final String rawJson = this.httpClient.getString(URL);
            final JsonObject json = this.gson.fromJson(rawJson, JsonObject.class);

            return this.parseVersions(json.getAsJsonArray("versions"));
        } catch (final Exception e) {
            throw new VersionFetchingException(e);
        }
    }

    private List<Version> parseVersions(final JsonArray versions) {
        return versions.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).map(object -> {
            try {
                return VersionJsonAdapter.deserialize(object);
            } catch (final Exception e) {
                LOGGER.warn("Failed to parse version", e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

}