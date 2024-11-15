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

package be.yvanmazy.minecraftremapper.version;

import com.google.gson.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class VersionJsonAdapter implements JsonSerializer<Version>, JsonDeserializer<Version> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public Version deserialize(final JsonElement json,
                               final Type typeOfT,
                               final JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json.getAsJsonObject());
    }

    @Override
    public JsonElement serialize(final Version src, final Type typeOfSrc, final JsonSerializationContext context) {
        return serialize(src);
    }

    @Contract("_ -> new")
    public static @NotNull JsonObject serialize(final @NotNull Version version) {
        final JsonObject object = new JsonObject();
        object.addProperty("id", version.id());
        object.addProperty("type", version.type().name());
        object.addProperty("url", version.url());
        if (version.time() != null) {
            object.addProperty("time", FORMATTER.format(version.time()));
        }
        if (version.releaseTime() != null) {
            object.addProperty("releaseTime", FORMATTER.format(version.releaseTime()));
        }
        return object;
    }

    @Contract("_ -> new")
    public static @NotNull Version deserialize(final JsonObject object) {
        final String id = object.get("id").getAsString();
        final String rawType = object.get("type").getAsString();
        final VersionType type = VersionType.fromString(rawType);
        if (type == null) {
            throw new IllegalArgumentException("Invalid version type: '" + rawType + "' for version '" + id + "'");
        }
        final String url = object.get("url").getAsString();
        final OffsetDateTime time = parseTime(object.get("time"));
        final OffsetDateTime releaseTime = parseTime(object.get("releaseTime"));

        return new Version(id, type, url, time, releaseTime);
    }

    private static OffsetDateTime parseTime(final JsonElement element) {
        if (element instanceof final JsonPrimitive primitive && primitive.isString()) {
            return parseTime(element.getAsString());
        }
        return null;
    }

    private static OffsetDateTime parseTime(final @NotNull String string) {
        return OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}