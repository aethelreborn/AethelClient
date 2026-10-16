# AethelClient

A clean, smooth, dark-blue glassmorphic Fabric client mod for Minecraft 1.21.11.

## Features

- **Dark blue glassmorphism ClickGui** — frosted navy glass panels, azure accents, smooth open/close animations, live search and animated setting cards
- **ClickGui module** — the GUI key is a real module bind: set it from the GUI, the `.bind` command or the config, and the same key opens and closes the panel
- **Self Destruct** — Vape-style instant hide: disables every module, silences HUD/chat/keys and stops all client activity until the next game restart (also available as `.selfdestruct`)
- **Precise combat & utility modules** — Aura, TriggerBot, AutoClicker (jittered CPS), Reach (vanilla attribute modifiers), NoFall, Velocity, Criticals, AutoTotem, AntiBot, AntiAFK, FullBright, Speed, Fly, ESP/HUD suites and many more

## Commands

Prefix is `.` (configurable via `.prefix`):

- `.selfdestruct` — hide the client immediately
- `.bind set <module> <key>` — rebind modules, including the ClickGui key
- `.help` — list everything

## Building

AethelClient builds with Gradle on Java 21. CI (GitHub Actions) builds every push to `main` and automatically publishes tagged releases (`v<version>` from `build.gradle`) — you never need to build locally.

```bash
./gradlew build
```

The built mod jar ends up in `build/libs`.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own project.
