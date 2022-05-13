<p align="center">
  <img src="assets/logo.png" alt="Raised fist holding a one-by-four" width="256" />
</p>

# Tertis

A [libgdx](https://libgdx.com/) game written, while briefly under the weather,
in [Scala](https://www.scala-lang.org/), the premier programming language
for contemporary mobile and game development. Based loosely on a
[prior thing](https://www.youtube.com/watch?v=YYGulsgO-os).

The code is horrid and shameful. It contains state and mutation, inconsistencies,
aberrations and general travesties. Nothing is nice here.

At the time of writing this uses JDK 11, Scala 2.13.8, libgdx 1.11.0 and Android SDK 30.

## Building for the desktop

* Run it:

```shell
./gradlew desktop:run
```

* Or package it:

```shell
./gradlew desktop:dist
```

* Achieving greatness:

```shell
desktop/build/libs/desktop-0.1.jar
```

* Which you can run:

```shell
java -jar desktop/build/libs/desktop-0.1.jar
# or, on a Mac
java -XstartOnFirstThread -jar desktop/build/libs/desktop-0.1.jar
```

## Building for Android

* Get a keystore.

* Create `keystore.properties`:

```properties
storePassword=<password>
keyPassword=<password>
keyAlias=<alias>
storeFile=<file>
```

* Build it:

```shell
./gradlew assemble
```

* Achieve a result:

```shell
android/build/outputs/apk/release/android-release.apk
```

* See also the [Android docs](https://developer.android.com/studio/build/building-cmdline).

## License

[Apache License, Version 2.0](LICENSE.md)

## Credits

1. assets/click.mp3 - https://freesound.org/people/JonnyRuss01/sounds/478197/
2. assets/drop.mp3 - https://freesound.org/people/TampaJoey/sounds/588502/
3. assets/crash.mp3 - https://freesound.org/people/timgormly/sounds/170958/
4. assets/triangle.mp3 - https://freesound.org/people/acclivity/sounds/31189/
5. assets/gong.mp3 - https://freesound.org/people/josemaria/sounds/55438/
6. assets/OpenSans-Regular.ttf - https://fonts.google.com/specimen/Open+Sans
7. assets/tap.png - https://www.iconfinder.com/icons/446301/finger_gesture_hand_interactive_scroll_swipe_tap_icon
8. the raised fist - https://en.wikipedia.org/wiki/Raised_fist#/media/File:Fist_.svg
