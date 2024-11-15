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

package be.yvanmazy.minecraftremapper.setting;

import be.yvanmazy.minecraftremapper.DirectionType;
import be.yvanmazy.minecraftremapper.http.RequestHttpClient;
import be.yvanmazy.minecraftremapper.version.Version;
import com.google.gson.Gson;

import java.util.Objects;

public record PreparationSettings(RequestHttpClient httpClient, Gson gson, DirectionType target, Version version, String outputDirectory,
                                  boolean remap, boolean decompile) {

    public PreparationSettings {
        Objects.requireNonNull(httpClient, "httpClient must not be null");
        Objects.requireNonNull(gson, "gson must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(version, "version must not be null");
        Objects.requireNonNull(outputDirectory, "outputDirectory must not be null");
    }

    public String getTargetKey() {
        return this.target.getKey();
    }

}