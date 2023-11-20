Trap { 
    Write-Error $_ -ErrorAction Continue
    exit 1 
}
$Global:ErrorActionPreference = 'Stop'
$Global:ProgressPreference = 'SilentlyContinue'
Add-Type -Assembly 'System.IO.Compression.Filesystem'

# NOTE: These versions should be un sync with the ones of the /pom.xml and /bin/install-xerces-m2.bat files
Set-Variable -Force -Option ReadOnly -Scope Script -Name XERCES_VERSION -Value 2.12.2
Set-Variable -Force -Option ReadOnly -Scope Script -Name XMLAPIS_VERSION -Value 1.4.02
Set-Variable -Force -Option ReadOnly -Scope Script -Name XPATH_VERSION -Value 1.2.1
Set-Variable -Force -Option ReadOnly -Scope Script -Name JAVACUP_VERSION -Value 10k
Set-Variable -Force -Option ReadOnly -Scope Script -Name ICU4J_VERSION -Value '4.2'

Write-Host -NoNewLine -Object 'Checking environment... '
$M2_PATH=mvn help:evaluate '-Dexpression=settings.localRepository' -q -DforceStdout
If ($true `
    -and (Test-Path -PathType Leaf "$M2_PATH\xml-apis\xml-apis\$XMLAPIS_VERSION\xml-apis-$XMLAPIS_VERSION.jar") `
    -and (Test-Path -PathType Leaf "$M2_PATH\xerces\xercesImpl\$XERCES_VERSION\xercesImpl-$XERCES_VERSION-xml-schema-1.1.jar") `
    -and (Test-Path -PathType Leaf "$M2_PATH\org\eclipse\wst\xml\xpath2\$XPATH_VERSION\xpath2-$XPATH_VERSION.jar") `
    -and (Test-Path -PathType Leaf "$M2_PATH\edu\princeton\cup\java-cup\$JAVACUP_VERSION\java-cup-$JAVACUP_VERSION.jar") `
    -and (Test-Path -PathType Leaf "$M2_PATH\com\ibm\icu\icu4j\$ICU4J_VERSION\icu4j-$ICU4J_VERSION.jar") `
) {
    Write-Host -Object 'xerces is already installed.'
    exit 0
}
Write-Host -Object 'we need to install xerces.'

Write-Host -NoNewLine -Object 'Creating temporary directory... '
$DOWNLOAD_DIR = [System.IO.Path]::GetTempFileName()
Remove-Item -LiteralPath $DOWNLOAD_DIR
New-Item -ItemType Directory -Path $DOWNLOAD_DIR | Out-Null
Write-Host -Object 'done.'

Write-Host -NoNewLine -Object 'Downloading xerces... '
Invoke-WebRequest -Uri "https://dlcdn.apache.org/xerces/j/binaries/Xerces-J-bin.$XERCES_VERSION-xml-schema-1.1.zip" -OutFile "$DOWNLOAD_DIR\xerces.zip" -UseBasicParsing
Write-Host -Object 'done.'

Write-Host -NoNewLine -Object 'Extracting xerces... '
Expand-Archive -LiteralPath "$DOWNLOAD_DIR\xerces.zip" -DestinationPath "$DOWNLOAD_DIR"
$XERCES_DIR = "$DOWNLOAD_DIR\xerces-$($XERCES_VERSION -replace '\.','_')-xml-schema-1.1"
Write-Host -Object 'done.'

Write-Host -NoNewLine -Object 'Checking versions... '
function Test-JarVersion {
    param (
        [string][Parameter(Mandatory)]
        $jarFile,
        [string][Parameter(Mandatory)]
        $expectedVersion
    )
    $archive = [IO.Compression.ZipFile]::OpenRead("$XERCES_DIR\$jarFile")
    try {
        $manifestEntry = $archive.GetEntry('META-INF/MANIFEST.MF')
        if (-not $manifestEntry) {
            throw "Unable to find the manifest of $jarFile"
        }
        $manifestStream = $manifestEntry.Open()
        try {
            $manifestReader = [IO.StreamReader]::new($manifestStream)
            try {
                while (-not $manifestReader.EndOfStream) {
                    $line = $manifestReader.ReadLine()
                    if (($line -match '^Bundle-Version:|^Implementation-Version:')) {
                        $version = ($line -split ' ')[1]
                        if ($expectedVersion -ne $version) {
                            throw "The version of $jarFile should be $expectedVersion, but it's $version"
                        }
                        return
                    }
                }
            } finally {
                $manifestReader.Dispose()
            }
        } finally {
            $manifestStream.Dispose()
        }
    } finally {
        $archive.Dispose()
    }
    throw "Unable to find the version of $jarFile"
}
# We don't need to check the xerces version: it's already in the URL
# We don't need to check the xpath2 version: it's already in its file name
# We don't need to check the cuv version version: it's already in its file name
Test-JarVersion 'xml-apis.jar' "$XMLAPIS_VERSION"
Test-JarVersion 'icu4j.jar' "$ICU4J_VERSION"
Write-Host -Object 'done.'

Write-Host -NoNewLine -Object 'Installing JARs to local m2 repository'

& mvn install:install-file `
    -Dfile="$XERCES_DIR\xml-apis.jar" `
    -DgroupId=xml-apis `
    -DartifactId=xml-apis `
    -Dversion="$XMLAPIS_VERSION" `
    -Dpackaging=jar `
    -DgeneratePom=true
if ($LASTEXITCODE -ne 0) { exit 1 }

& mvn install:install-file `
    -Dfile="$XERCES_DIR\xercesImpl.jar" `
    -DgroupId=xerces `
    -DartifactId=xercesImpl `
    -Dversion="$XERCES_VERSION" `
    '-Dclassifier=xml-schema-1.1' `
    -Dpackaging=jar `
    -DgeneratePom=true
if ($LASTEXITCODE -ne 0) { exit 1 }

& mvn install:install-file `
    -Dfile="$XERCES_DIR\org.eclipse.wst.xml.xpath2.processor_$XPATH_VERSION.jar" `
    '-DgroupId=org.eclipse.wst.xml' `
    -DartifactId=xpath2 `
    -Dversion="$XPATH_VERSION" `
    -Dpackaging=jar `
    -DgeneratePom=true
if ($LASTEXITCODE -ne 0) { exit 1 }

& mvn install:install-file `
    -Dfile="$XERCES_DIR\cupv$JAVACUP_VERSION-runtime.jar" `
    '-DgroupId=edu.princeton.cup' `
    -DartifactId=java-cup `
    -Dversion="$JAVACUP_VERSION" `
    -Dpackaging=jar `
    -DgeneratePom=true
if ($LASTEXITCODE -ne 0) { exit 1 }

& mvn install:install-file `
    -Dfile="$XERCES_DIR\icu4j.jar" `
    '-DgroupId=com.ibm.icu' `
    -DartifactId=icu4j `
    -Dversion="$ICU4J_VERSION" `
    -Dpackaging=jar `
    -DgeneratePom=true
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host -NoNewLine -Object 'Deleting temporary directory... '
#Remove-Item -Recurse -Force -LiteralPath "$DOWNLOAD_DIR"
Write-Host -Object 'done.'
