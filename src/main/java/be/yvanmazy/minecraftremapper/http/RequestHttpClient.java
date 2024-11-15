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

package be.yvanmazy.minecraftremapper.http;

import be.yvanmazy.minecraftremapper.http.exception.RequestHttpException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.util.function.Consumer;

public interface RequestHttpClient {

    @Contract("-> new")
    @NotNull
    static RequestHttpClient newDefault() {
        return newDefault(HttpClient.newHttpClient());
    }

    @Contract("_ -> new")
    @NotNull
    static RequestHttpClient newDefault(final @NotNull Consumer<HttpClient.Builder> consumer) {
        final HttpClient.Builder builder = HttpClient.newBuilder();
        consumer.accept(builder);
        return newDefault(builder.build());
    }

    @Contract("_ -> new")
    @NotNull
    static RequestHttpClient newDefault(final @NotNull HttpClient httpClient) {
        return new DefaultRequestHttpClient(httpClient);
    }

    @NotNull
    String getString(final @NotNull String url) throws RequestHttpException;

    byte @NotNull [] getBytes(final @NotNull String url) throws RequestHttpException;

}