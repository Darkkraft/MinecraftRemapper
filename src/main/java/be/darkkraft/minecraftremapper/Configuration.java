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

import com.beust.jcommander.Parameter;

final class Configuration {

    @Parameter(order = 1, names = {"--help", "-h"}, help = true)
    private boolean help;

    @Parameter(order = 2, names = {"--list", "-l"}, description = "List all available versions.")
    private boolean list;

    @Parameter(order = 3, names = {"--version", "-v"}, description = "Select version to remap.")
    private String version;

    @Parameter(order = 4, names = {"--type", "-t"}, description = "Select the type between 'server' and 'client'.")
    private DirectionType type;

    @Parameter(order = 5, names = {"--remap", "-r"}, description = "Remap jar after downloading.")
    private boolean remap = true;

    @Parameter(order = 6, names = {"--decompile", "-d"}, description = "Decompile jar after remapping.")
    private boolean decompile;

    @Parameter(order = 7, names = {"--output-directory", "-o"}, description = "Output directory.")
    private String outputDirectory = "MinecraftRemapper";

    public boolean isHelp() {
        return this.help;
    }

    public boolean isList() {
        return this.list;
    }

    public String getVersion() {
        return this.version;
    }

    public DirectionType getType() {
        return this.type;
    }

    public boolean isRemap() {
        return this.remap;
    }

    public boolean isDecompile() {
        return this.decompile;
    }

    public String getOutputDirectory() {
        return this.outputDirectory;
    }

}