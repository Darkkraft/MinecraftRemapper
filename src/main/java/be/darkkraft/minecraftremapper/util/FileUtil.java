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

package be.darkkraft.minecraftremapper.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public final class FileUtil {

    private FileUtil() throws IllegalAccessException {
        throw new IllegalAccessException("You cannot instantiate a utility class");
    }

    public static boolean isValidJar(final @NotNull Path path) {
        if (Files.notExists(path)) {
            return false;
        }
        try (final ZipFile ignored = new ZipFile(path.toFile())) {
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    public static void recursiveDelete(final @NotNull Path directory) throws IOException {
        if (Files.notExists(directory)) {
            return;
        }
        try (final Stream<Path> stream = Files.walk(directory)) {
            for (final Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

}