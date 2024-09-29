#!/usr/bin/env sh
set -e

extract_export_package_value_for_buildxml() {
  sed -z -E 's:\r?\n ::g' "$1" \
    | grep '^Export-Package' \
    | sed 's/^Export-Package: //' \
    | sed 's/",/",\n/g' \
    | sed 's/1\.0\.0/${osgi.version}/g' \
    | sed 's/"/\&quot;/g'
}

rm -rf tmp

mkdir tmp

cp -r src tmp

mvn \
-f create-export-package-metadata-pom.xml \
-DsourceDirectory=tmp/src \
-DoutputDirectory=tmp/target \
-DexportedPackages=com.sun.unijna,com.sun.unijna.ptr,com.sun.unijna.win32 \
clean package

cp -r contrib/platform/src tmp

mvn \
-f create-export-package-metadata-pom.xml \
-DsourceDirectory=tmp/src \
-DoutputDirectory=tmp/target-platform \
-DexportedPackages=\
com.sun.unijna.platform,\
com.sun.unijna.platform.dnd,\
com.sun.unijna.platform.linux,\
com.sun.unijna.platform.mac,\
com.sun.unijna.platform.unix,\
com.sun.unijna.platform.unix.aix,\
com.sun.unijna.platform.unix.solaris,\
com.sun.unijna.platform.win32,\
com.sun.unijna.platform.win32.COM,\
com.sun.unijna.platform.win32.COM.tlb,\
com.sun.unijna.platform.win32.COM.tlb.imp,\
com.sun.unijna.platform.win32.COM.util,\
com.sun.unijna.platform.win32.COM.util.annotation,\
com.sun.unijna.platform.wince \
-DimportedPackages=com.sun.unijna,com.sun.unijna.ptr,com.sun.unijna.win32 \
clean package

echo 'build.xml: Export-Package:'
echo
extract_export_package_value_for_buildxml tmp/target/META-INF/MANIFEST.MF
echo
echo

echo 'contrib/platform/build.xml: Export-Package:'
echo
extract_export_package_value_for_buildxml tmp/target-platform/META-INF/MANIFEST.MF
echo
echo

rm -r tmp
