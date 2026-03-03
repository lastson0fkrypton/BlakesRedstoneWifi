# BlakesRedstoneWifi
1.21.11 Fabric mod introducing Redstone Wifi Emitters and Receivers

<img width="1102" height="495" alt="image" src="https://github.com/user-attachments/assets/b9af1ac3-3b8b-405f-9750-02159f5517ea" />


## Features

- `Wifi Emitter` block
- Wifi Emitters will accept a redstone circuit level and then pass it without reduction to the similarly tagged Wifi Receiver block
- `Wifi Receiver` block
- Tagged with the same name as the Wifi Emitter will continue the redstone circuit at the level input to the Emitter.
- `Wifi Emitter` can be found in the redstone tab, and can be crafted with a Sculk Sensor surrounded by redstone dust.
- `Wifi Receiver` can be found in the redstone tab, and can be crafted with a Sculk Shrieker surrounded by redstone dust.

## Version target

This project is currently configured for **Minecraft 1.21.11** with Fabric Loader **0.18.4**.

If you specifically need another patch line (for example `1.21.11` if/when available), update these values in `gradle.properties`:

- `minecraft_version`
- `yarn_mappings`
- `fabric_version`

## Run in dev

1. Install **JDK 21**.
2. From project root, use the wrapper commands:
   - `./gradlew start` (macOS/Linux)
   - `gradlew.bat start` (Windows)

`start` is an alias for Fabric's `runClient` task.

### Local Gradle bootstrap script (Windows)

If you want Gradle downloaded automatically and kept local to this repo:

- `powershell -ExecutionPolicy Bypass -File .\scripts\gradle-local.ps1 start`

This script will:

- use local cache folder `.gradle-local`
- auto-download Gradle into `.tools` only if needed
- generate wrapper if missing
- run the task you pass (`start`, `buildMod`, `copyModJar`, etc.)

## Build mod jar

- `./gradlew buildMod` (macOS/Linux)
- `gradlew.bat buildMod` (Windows)

`buildMod` is an alias for `build`.

The built, remapped mod jar is created in:

- `build/libs`

## Copy jar to MultiMC mods folder

You can build and copy in one command:

- `gradlew.bat copyModJar -PmodsDir="C:\\Path\\To\\MultiMC\\instances\\MyInstance\\.minecraft\\mods"`

Or set a default once in `gradle.properties`:

- `minecraft_mods_dir=run/mods`

Then run simply:

- `gradlew.bat copyModJar`

Or on macOS/Linux:

- `./gradlew copyModJar -PmodsDir="/path/to/instance/.minecraft/mods"`

Using the local bootstrap script on Windows:

- `powershell -ExecutionPolicy Bypass -File .\scripts\gradle-local.ps1 copyModJar -ModsDir "C:\\Path\\To\\MultiMC\\instances\\MyInstance\\.minecraft\\mods"`
