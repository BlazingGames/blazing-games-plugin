This repo is now [hosted on Codeberg](https://codeberg.org/BlazingGames/blazing-games-plugin). Further updates will be available there.

The original `README.md` follows below:

---

# blazing-games-plugin

![Latest Release](https://git.ivycollective.dev/BlazingGames/blazing-games-plugin/badges/release.svg)
![Build Status](https://git.ivycollective.dev/BlazingGames/blazing-games-plugin/badges/workflows/build.yml/badge.svg?label=build+status)

The plugin powering the Blazing Games minecraft server, with computers, enchanting altars, spawner modification, and more!

## Usage

Releases of prebuilt jars are [available here](https://git.ivycollective.dev/BlazingGames/blazing-games-plugin/releases). Otherwise, please build the plugin yourself (see the Development section).

Instructions: Place the plugin's jar file inside your `plugins` folder and restart your server.

Most features should be configured out of the box. For those needing advanced configuration, see the `CONFIG.md` file.

## Development

This is a standard Paper plugin using Gradle.

To build, use: `./gradlew build`

## Testing

This plugins supports testing. To run tests, use: `./gradlew build -Ptest=true`, and load the plugin normally. Once tests are done running, the file `TESTS_RESULT` in the server files directory will contain `true` if tests passed or `false` if tests failed.

## License

This plugin is licensed under the Apache License (version 2.0). For more information, please read the NOTICE and LICENSE files.

