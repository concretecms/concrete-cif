#!/bin/sh

set -o errexit
set -o nounset

# NOTE: These versions should be un sync with the ones of the pom.xml file
XERCES_VERSION=2.12.2
XMLAPIS_VERSION=1.4.02
XPATH_VERSION=1.2.1
JAVACUP_VERSION=10k
ICU4J_VERSION=4.2

printf 'Creating temporary directory... '
DOWNLOAD_DIR="$(mktemp -d)"
printf 'done.\n'

printf 'Downloading and extracting xerces... '
curl -sSLf "https://dlcdn.apache.org//xerces/j/binaries/Xerces-J-bin.$XERCES_VERSION-xml-schema-1.1.tar.gz" | tar -xz -C "$DOWNLOAD_DIR" --strip-components=1
printf 'done.\n'

printf 'Checking versions...'
checkJarVersion() {
    checkJarVersion_found="$(unzip -p "$DOWNLOAD_DIR/$1" META-INF/MANIFEST.MF | tr -d '\r' | grep -m 1 -E 'Bundle-Version:|Implementation-Version:' | cut -d' ' -f 2)"
    if [ "$2" != "$checkJarVersion_found" ]; then
        printf "The version of %s should be >%s<, but it's >%s<\n" "$1" "$2" "$checkJarVersion_found"
        exit 1
    fi
}
# We don't need to check the xerces version: it's already in the URL
# We don't need to check the xpath2 version: it's already in its file name
# We don't need to check the cuv version version: it's already in its file name
checkJarVersion xml-apis.jar "$XMLAPIS_VERSION"
checkJarVersion icu4j.jar "$ICU4J_VERSION"
printf 'done.\n'

printf 'Installing JARs to local m2 repository\n'

mvn install:install-file \
    -Dfile="$DOWNLOAD_DIR/xml-apis.jar" \
    -DgroupId=xml-apis \
    -DartifactId=xml-apis \
    -Dversion="$XMLAPIS_VERSION" \
    -Dpackaging=jar \
    -DgeneratePom=true

mvn install:install-file \
    -Dfile="$DOWNLOAD_DIR/xercesImpl.jar" \
    -DgroupId=xerces \
    -DartifactId=xercesImpl \
    -Dversion="$XERCES_VERSION" \
    -Dclassifier=xml-schema-1.1 \
    -Dpackaging=jar \
    -DgeneratePom=true

mvn install:install-file \
    -Dfile="$DOWNLOAD_DIR/org.eclipse.wst.xml.xpath2.processor_$XPATH_VERSION.jar" \
    -DgroupId=org.eclipse.wst.xml \
    -DartifactId=xpath2 \
    -Dversion="$XPATH_VERSION" \
    -Dpackaging=jar \
    -DgeneratePom=true

mvn install:install-file \
    -Dfile="$DOWNLOAD_DIR/cupv$JAVACUP_VERSION-runtime.jar" \
    -DgroupId=edu.princeton.cup \
    -DartifactId=java-cup \
    -Dversion="$JAVACUP_VERSION" \
    -Dpackaging=jar \
    -DgeneratePom=true

mvn install:install-file \
    -Dfile="$DOWNLOAD_DIR/icu4j.jar" \
    -DgroupId=com.ibm.icu \
    -DartifactId=icu4j \
    -Dversion="$ICU4J_VERSION" \
    -Dpackaging=jar \
    -DgeneratePom=true

printf 'Deleting temporary directory... '
rm -rf "$DOWNLOAD_DIR"
printf 'done.\n'
