/*
 * MIT License
 *
 * Copyright (c) 2024 Darkkraft
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

package be.darkkraft.minecraftremapper.http;

import be.darkkraft.minecraftremapper.http.exception.RequestHttpException;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

final class DefaultRequestHttpClient implements RequestHttpClient {

    private final HttpClient client;

    public DefaultRequestHttpClient(final @NotNull HttpClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    @Override
    public @NotNull String getString(final @NotNull String url) throws RequestHttpException {
        return this.get(url, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public byte @NotNull [] getBytes(final @NotNull String url) throws RequestHttpException {
        return this.get(url, HttpResponse.BodyHandlers.ofByteArray());
    }

    private <T> T get(final @NotNull String url, final @NotNull HttpResponse.BodyHandler<T> bodyHandler) throws RequestHttpException {
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            return this.client.send(request, bodyHandler).body();
        } catch (final Exception e) {
            throw new RequestHttpException(e);
        }
    }

}