# 🔄 MinecraftRemapper

**MinecraftRemapper** is a Java program designed to **decompile** and **remap** the Minecraft game code using the
mappings provided by Mojang. You can use all versions from **1.14** (version where the mapping was proposed by Mojang)
until the **latest**. You can also use **snapshots**.

Typically, decompiling and remapping takes less than 2 minutes and less than 1 minute for the server.

## Usage

Java 17+ must be installed on your machine.

1. Download the jar in the releases section.
2. Created a folder and put the jar inside.
3. Run the jar with the parameters you want.

```bash
java -jar MinecraftRemapper.jar -v 1.20.4 -t client -o out -d
```

`-v 1.20.4` : Specify the version of Minecraft to use.\
`-t client` : Target client or server for decompiling.\
`-o out` : Specify the output directory.\
`-d` : Enable decompilation after remapping; if not set, only the remapped jar is built.\
Use `-l` to show all available versions.

## Using as a Maven/Gradle Dependency

The latest version is: ![Release](https://jitpack.io/v/Darkkraft/MinecraftRemapper.svg)

### Gradle

Repository:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

Dependency:

```gradle
compileOnly 'com.github.Darkkraft:MinecraftRemapper:<VERSION>'
```

### Maven

Repository:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Dependency:

```xml
<dependency>
    <groupId>com.github.Darkkraft</groupId>
    <artifactId>MinecraftRemapper</artifactId>
    <version><VERSION></version>
</dependency>
```

## Credits
Remapper: [SpecialSource](https://github.com/md-5/SpecialSource/)\
Decompiler: [Vineflower](https://github.com/Vineflower/vineflower)

## Legal Notice

The code of Minecraft is owned and licensed by Microsoft. It is strictly prohibited to publicly publish any code
produced
by MinecraftRemapper. Output files should only be used for personal purposes.

## License

This project is licensed under the [MIT License](https://opensource.org/license/mit) - see the LICENSE file for details.