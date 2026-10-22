#!/bin/bash
set -e

# Build and install Apple TV launcher for Android TV (MiTV-MOOQ1)
ANDROID_JAR="/d/Dev/Android/platforms/android-34/android.jar"
BUILD_TOOLS="/d/Dev/Android/build-tools/35.0.0"
JDK19="/c/Program Files/Java/jdk-19"
KEYSTORE="D:/Agent Hermes/mitv-debloat/test_apk/debug.keystore"
ADB="/d/Dev/Android/platform-tools/adb.exe"
TARGET="${1:-[fe80::51d2:1046:3e07:366d%20]:5555}"

export PATH="$JDK19/bin:$PATH"
export JAVA_HOME="$JDK19"

cd "D:/Agent Hermes/mitv-debloat/launcher_app"

echo "[1/6] Compiling resources with aapt2..."
"$BUILD_TOOLS/aapt2.exe" compile --dir res -o compiled_res.zip

echo "[2/6] Linking resources with aapt2..."
"$BUILD_TOOLS/aapt2.exe" link -I "$ANDROID_JAR" --manifest AndroidManifest.xml --java gen -o dist/unaligned_res.apk compiled_res.zip --auto-add-overlay

echo "[3/6] Compiling Java source..."
"$JDK19/bin/javac.exe" -source 1.8 -target 1.8 -bootclasspath "$ANDROID_JAR" -cp "$ANDROID_JAR;gen" -d bin src/com/appletv/launcher/MainActivity.java gen/com/appletv/launcher/R.java

echo "[4/6] Dexing bytecode with d8..."
cmd.exe /c "$BUILD_TOOLS/d8.bat" --min-api 28 --output bin/dex bin/com/appletv/launcher/*.class

echo "[5/6] Packaging and aligning APK..."
python -c "import zipfile, os; zin = zipfile.ZipFile('dist/unaligned_res.apk', 'r'); zout = zipfile.ZipFile('dist/unaligned_combined.apk', 'w', zipfile.ZIP_DEFLATED); [zout.writestr(i, zin.read(i.filename)) for i in zin.infolist()]; zout.write('bin/dex/classes.dex', 'classes.dex'); zout.write('assets/fonts/Outfit.ttf', 'assets/fonts/Outfit.ttf'); zin.close(); zout.close()"
rm -f dist/appletv-launcher.apk
"$BUILD_TOOLS/zipalign.exe" -f -p 4 dist/unaligned_combined.apk dist/appletv-launcher.apk

echo "[6/6] Signing APK..."
cmd.exe /c "$BUILD_TOOLS/apksigner.bat" sign --ks "$KEYSTORE" --ks-pass pass:android --key-pass pass:android dist/appletv-launcher.apk

echo "Installing APK to device $TARGET..."
"$ADB" -s "$TARGET" install -r dist/appletv-launcher.apk
"$ADB" -s "$TARGET" shell pm grant com.appletv.launcher android.permission.ACCESS_FINE_LOCATION
"$ADB" -s "$TARGET" shell pm grant com.appletv.launcher android.permission.ACCESS_COARSE_LOCATION
"$ADB" -s "$TARGET" shell cmd package set-home-activity com.appletv.launcher/.MainActivity
"$ADB" -s "$TARGET" shell am start -n com.appletv.launcher/.MainActivity

echo "Deployment finished successfully!"
