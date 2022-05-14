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

At the time of writing this was built with JDK 17, Scala 2.13.8, libgdx 1.11.0
and Android SDK 29.

## Install the Android SDK command-line tools

Before building this you need to install the Android SDK command-line tools, even
if you're just building the Desktop version.

* Go to the [Android Studio](https://developer.android.com/studio#command-tools) Website
and witness the command line tools.

* Then decide to just use [Homebrew](https://brew.sh/):

```shell
brew install android-commandlinetools
```

* Then install the build tools and platforms (while agreeing to sell your firstborn):

```shell
sdkmanager 'platforms;android-29' 'build-tools;29.0.2' tools
```

* Create a file `local.properties`:

```properties
sdk.dir=/opt/homebrew/share/android-commandlinetools/
```

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
desktop/build/libs/desktop-1.0.jar
```

* Which you can run:

```shell
java -jar desktop/build/libs/desktop-1.0.jar
# or, on a Mac
java -XstartOnFirstThread -jar desktop/build/libs/desktop-1.0.jar
```

### Packaging for a Mac

On the Mac you need an icon. See [this answer](https://stackoverflow.com/a/20703594) for incantations.

With this and JDK 17 installed:

```shell
export JAVA_HOME=/path/to/java/17
jpackage \
  --input desktop/build/libs/ \
  --name Tertis \
  --app-version 1.0 \
  --main-jar desktop-1.0.jar \
  --main-class org.merlin.tertis.DesktopLauncher \
  --type dmg \
  --java-options '-XstartOnFirstThread' \
  --icon mac/Tertis.icns
```

Yielding: `Tertis-1.0.dmg`.

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
./gradlew android:assemble
```

* Achieve a result:

```shell
android/build/outputs/apk/release/android-release.apk
```

* See also the [Android docs](https://developer.android.com/studio/build/building-cmdline).

### Running in the Android emulator

It's probably easiest to just fire up IntelliJ, select the android run configuration, use the
AVD Manager UI to create a Pixel 5 device running Android 11 and be done.


## License

[Apache License, Version 2.0](LICENSE.md)

## Credits

1. Audio editing with [Audacity](https://www.audacityteam.org/)
2. Vector editing with [Inkscape](https://inkscape.org/)
3. Pixel editing with [Gimp](https://www.gimp.org/)
4. The raised fist - https://en.wikipedia.org/wiki/Raised_fist#/media/File:Fist_.svg
5. `assets/click.mp3` - https://freesound.org/people/JonnyRuss01/sounds/478197/
6. `assets/drop.mp3` - https://freesound.org/people/TampaJoey/sounds/588502/
7. `assets/crash.mp3` - https://freesound.org/people/timgormly/sounds/170958/
8. `assets/triangle.mp3` - https://freesound.org/people/acclivity/sounds/31189/
9. `assets/gong.mp3` - https://freesound.org/people/josemaria/sounds/55438/
10. `assets/OpenSans-Regular.ttf` - https://fonts.google.com/specimen/Open+Sans
11. `assets/tap.png` - https://www.iconfinder.com/icons/446301/finger_gesture_hand_interactive_scroll_swipe_tap_icon
