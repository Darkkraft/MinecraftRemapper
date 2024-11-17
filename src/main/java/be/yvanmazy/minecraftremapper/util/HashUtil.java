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

package be.yvanmazy.minecraftremapper.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    private HashUtil() throws IllegalAccessException {
        throw new IllegalAccessException("You cannot instantiate a utility class");
    }

    public static @NotNull String hash(final @NotNull Path path) throws IOException {
        try (final InputStream in = Files.newInputStream(path)) {
            return hash(in);
        }
    }

    public static @NotNull String hash(final @NotNull InputStream stream) throws IOException {
        final MessageDigest hash;
        try {
            hash = MessageDigest.getInstance("SHA-1");
        } catch (final NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
        final byte[] buf = new byte[1024];
        int count;
        try (stream) {
            while ((count = stream.read(buf)) != -1) hash.update(buf, 0, count);
        }

        final byte[] bytes = hash.digest();
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

}