#!/bin/sh

set -o errexit
set -o nounset

cd /tmp

printf 'Copying jar to temporary directory... '
cp "$GITHUB_WORKSPACE/concrete-cif.jar" .
printf 'done.\n'

printf 'Test good CIFs with jar... '
if ! java -jar concrete-cif.jar "$GITHUB_WORKSPACE/src/test/resources/cifs-good" >concrete-cif.jar.log 2>&1; then
    printf 'FAILED!\n'
    cat concrete-cif.jar.log
    exit 1
fi
printf 'passed.\n'

printf 'Test bad CIFs with jar... '
if java -jar concrete-cif.jar "$GITHUB_WORKSPACE/src/test/resources/cifs-bad" >concrete-cif.jar.log 2>&1; then
    printf 'FAILED!\n'
    cat concrete-cif.jar.log
    exit 1
fi
printf 'passed.\n'

rm concrete-cif.jar
rm concrete-cif.jar.log
