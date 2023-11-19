@echo off
setlocal enableextensions

rem NOTE: These versions should be un sync with the ones of the /pom.xml and /bin/install-xerces-m2.sh files
set XERCES_VERSION=2.12.2
set XMLAPIS_VERSION=1.4.02
set XPATH_VERSION=1.2.1
set JAVACUP_VERSION=10k
set ICU4J_VERSION=4.2

echo|set /p="Checking environment... "
for /f %%i in ('mvn help:evaluate "-Dexpression=settings.localRepository" -q -DforceStdout') do set M2_PATH=%%i
if exist "%M2_PATH%\xml-apis\xml-apis\%XMLAPIS_VERSION%\xml-apis-%XMLAPIS_VERSION%.jar" (
    if exist "%M2_PATH%\xerces\xercesImpl\%XERCES_VERSION%\xercesImpl-%XERCES_VERSION%-xml-schema-1.1.jar" (
        if exist "%M2_PATH%\\org\eclipse\wst\xml\xpath2\%XPATH_VERSION%\xpath2-%XPATH_VERSION%.jar" (
            if exist "%M2_PATH%\edu\princeton\cup\java-cup\%JAVACUP_VERSION%\java-cup-%JAVACUP_VERSION%.jar" (
                if exist "%M2_PATH%\com\ibm\icu\icu4j\%ICU4J_VERSION%\icu4j-%ICU4J_VERSION%.jar" (
                    echo xerces is already installed.
                    exit /b 0
                )
            )
        )
    )
)
echo we need to install xerces.

echo|set /p="Creating temporary directory... "
:newDownloaDir
set DOWNLOAD_DIR=%TMP%\xerces~%RANDOM%.tmp
if exist "%DOWNLOAD_DIR%" goto :newDownloaDir
mkdir "%DOWNLOAD_DIR%"
echo done.

bitsadmin.exe /transfer "Downloading xerces" /download /priority FOREGROUND "https://dlcdn.apache.org//xerces/j/binaries/Xerces-J-bin.%XERCES_VERSION%-xml-schema-1.1.zip" "%DOWNLOAD_DIR%\xerces.zip"
if ERRORLEVEL 1 exit /b 1

echo|set /p="Extracting xerces... "
set VBS="%DOWNLOAD_DIR%\_.vbs"
echo On Error Goto 0 >"%VBS%"
echo Set oShell = CreateObject("Shell.Application") >>"%VBS%"
echo Set destination = oShell.NameSpace("%DOWNLOAD_DIR%") >>"%VBS%"
echo Set zipRootItems = oShell.NameSpace("%DOWNLOAD_DIR%\xerces.zip").Items >>"%VBS%"
echo For Each zipItem In oShell.NameSpace(zipRootItems.Item(0).Path).Items >>"%VBS%"
echo     If Right(zipItem.Name, 4) = ".jar" Then >>"%VBS%"
echo         destination.CopyHere zipItem >>"%VBS%"
echo     End If >>"%VBS%"
echo Next >>"%VBS%"
echo Set oShell = Nothing >>"%VBS%"
cscript.exe //nologo "%VBS%"
if ERRORLEVEL 1 exit /b 1
echo done.

echo Installing JARs to local m2 repository

call mvn install:install-file ^
    -Dfile="%DOWNLOAD_DIR%\xml-apis.jar" ^
    -DgroupId=xml-apis ^
    -DartifactId=xml-apis ^
    -Dversion="%XMLAPIS_VERSION%" ^
    -Dpackaging=jar ^
    -DgeneratePom=true
if ERRORLEVEL 1 exit /b 1

call mvn install:install-file ^
    -Dfile="%DOWNLOAD_DIR%\xercesImpl.jar" ^
    -DgroupId=xerces ^
    -DartifactId=xercesImpl ^
    -Dversion="%XERCES_VERSION%" ^
    -Dclassifier=xml-schema-1.1 ^
    -Dpackaging=jar ^
    -DgeneratePom=true
if ERRORLEVEL 1 exit /b 1

call mvn install:install-file ^
    -Dfile="%DOWNLOAD_DIR%\org.eclipse.wst.xml.xpath2.processor_%XPATH_VERSION%.jar" ^
    -DgroupId=org.eclipse.wst.xml ^
    -DartifactId=xpath2 ^
    -Dversion="%XPATH_VERSION%" ^
    -Dpackaging=jar ^
    -DgeneratePom=true
if ERRORLEVEL 1 exit /b 1

call mvn install:install-file ^
    -Dfile="%DOWNLOAD_DIR%\cupv%JAVACUP_VERSION%-runtime.jar" ^
    -DgroupId=edu.princeton.cup ^
    -DartifactId=java-cup ^
    -Dversion="%JAVACUP_VERSION%" ^
    -Dpackaging=jar ^
    -DgeneratePom=true
if ERRORLEVEL 1 exit /b 1

call mvn install:install-file ^
    -Dfile="%DOWNLOAD_DIR%\icu4j.jar" ^
    -DgroupId=com.ibm.icu ^
    -DartifactId=icu4j ^
    -Dversion="%ICU4J_VERSION%" ^
    -Dpackaging=jar ^
    -DgeneratePom=true
if ERRORLEVEL 1 exit /b 1

echo|set /p="Deleting temporary directory... "
rmdir /s /q "%DOWNLOAD_DIR%"
echo done.
