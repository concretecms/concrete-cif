[![Build](https://github.com/concretecms/concrete-cif/actions/workflows/build.yml/badge.svg)](https://github.com/concretecms/concrete-cif/actions/workflows/build.yml)

# Validation of Concrete CIF files

You can install almost everything in ConcreteCMS/concrete5 by using XML files in the so-called [CIF format](https://documentation.concretecms.org/developers/packages/install-content-using-content-interchange-format-cif).

You can check the validity of such files by using an XML Schema (that's an xsd file).

Because of the complexity of the CIF files, we need to use XML Schema v1.1.

The problem is that PHP and many XML validators only support XML Schema v1.0.

In order to solve this problem, you can use the [`concrete-cif.jar` app](https://github.com/concretecms/concrete-cif/releases/latest/download/concrete-cif.jar) you can find in the [GitHub releases](https://github.com/concretecms/concrete-cif/releases) (of course you can use the [`concrete-cif-1.0.xsd` file](https://github.com/concretecms/concrete-cif/releases/latest/download/concrete-cif-1.0.xsd) directly).

## Using `concrete-cif.jar`

1. Install Java (at least version 11). If you don't know how to do that, take a look at [Eclipse Temurin](https://adoptium.net/))
2. [Download](https://github.com/concretecms/concrete-cif/releases/latest/download/concrete-cif.jar) the latest version
3. Call concrete-cif.jar by passing it the files (or the directories) you want to check.  
   For example:
   ```sh
   java -jar concrete-cif.jar /path/to/concrete
   ```

## Compiling `concrete-cif.jar`

1. You need a Java JDK (at least 11) - see for example [Eclipse Temurin](https://adoptium.net/)
2. You need [Apache Maven](https://maven.apache.org/)
3. You need to download Xerces locally by using the `bin/install-xerces-m2.sh` script (on Windows: `bin\install-xerces-m2.bat` or `bin\install-xerces-m2.ps1`)
4. Run `mvn verify`

## Using the XSD in an IDE

You can use the XSD in CIF files so that your IDE can offer validation and auto-completion.

In order to do that, simply start your CIF files with:

```xml
<concrete5-cif
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="https://github.com/concretecms/concrete-cif/releases/latest/download/concrete-cif-1.0.xsd"
    version="1.0"
>
   <!-- up to you ;) -->
</concrete5-cif>
```

Please remark that the XML Schema is written in version 1.1, and so far most of the IDEs only support version 1.0.
