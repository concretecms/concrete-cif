[![Build](https://github.com/concrete5-community/concrete-cif/actions/workflows/build.yml/badge.svg)](https://github.com/concrete5-community/concrete-cif/actions/workflows/build.yml)

# Validation of Concrete CIF files

You can install almost everything in ConcreteCMS/concrete5 by using XML files in the so-called [CIF format](https://documentation.concretecms.org/developers/packages/install-content-using-content-interchange-format-cif).

You can check the validity of such files by using an XML Schema (that's an xsd file).

Because of the complexity of the CIF files, we need to use XML Schema v1.1.

The problem is that PHP and many XML validators only support XML Schema v1.0.

In order to solve this problem, you can use the [`concrete-cif.jar` app](https://github.com/concrete5-community/concrete-cif/releases/latest/download/concrete-cif.jar) you can find in the [GitHub releases](https://github.com/concrete5-community/concrete-cif/releases) (of course you can use the [`concrete-cif-1.0.xsd` file](https://github.com/concrete5-community/concrete-cif/releases/latest/download/concrete-cif-1.0.xsd) directly).

## Using `concrete-cif.jar`

1. Install Java (at least version 17). If you don't know how to do that, take a look at [Eclipse Adoptium](https://adoptium.net/))
2. [Download](https://github.com/concrete5-community/concrete-cif/releases/latest/download/concrete-cif.jar) the latest version
3. Call concrete-cif.jar by passing it the files (or the directories) you want to check.  
   For example:
   ```sh
   java -jar concrete-cif.jar /path/to/concrete
   ```
