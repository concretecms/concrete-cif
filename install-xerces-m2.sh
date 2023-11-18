#!/bin/sh

set -o errexit
set -o nounset

XERCES_VERSION=2.12.2
XMLAPIS_VERSION=1.4.02
XPATH_VERSION=1.2.1
JAVACUP_VERSION=10k
ICU4J_VERSION='4.2'

printf 'Creating temporary directory... '
DOWNLOAD_DIR="$(mktemp -d)"
printf 'done.\n'

printf 'Downloading and extracting xerces... '
curl -sSLf "https://dlcdn.apache.org//xerces/j/binaries/Xerces-J-bin.$XERCES_VERSION-xml-schema-1.1.tar.gz" | tar -xz -C "$DOWNLOAD_DIR" --strip-components=1
printf 'done.\n'

printf 'Installing JARs to local m2 repository\n'

mvn install:install-file \
    -Dfile="$DOWNLOAD_DIR/xml-apis.jar" \
    -DgroupId=xml-apis \
    -DartifactId=xml-apis \
    -Dversion=1.4.02 \
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
    -Dfile="$DOWNLOAD_DIR/cupv10k-runtime.jar" \
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
