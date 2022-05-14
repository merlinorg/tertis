#!/bin/sh
mkdir Tertis.iconset
sips -z 16 16     assets/logo.png --out Tertis.iconset/icon_16x16.png
sips -z 32 32     assets/logo.png --out Tertis.iconset/icon_16x16@2x.png
sips -z 32 32     assets/logo.png --out Tertis.iconset/icon_32x32.png
sips -z 64 64     assets/logo.png --out Tertis.iconset/icon_32x32@2x.png
sips -z 128 128   assets/logo.png --out Tertis.iconset/icon_128x128.png
sips -z 256 256   assets/logo.png --out Tertis.iconset/icon_128x128@2x.png
sips -z 256 256   assets/logo.png --out Tertis.iconset/icon_256x256.png
sips -z 512 512   assets/logo.png --out Tertis.iconset/icon_256x256@2x.png
sips -z 512 512   assets/logo.png --out Tertis.iconset/icon_512x512.png
cp assets/logo.png Tertis.iconset/icon_512x512@2x.png
iconutil -c icns Tertis.iconset
rm -R Tertis.iconset
