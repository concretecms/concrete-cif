package org.concretecms_community.concrete_cif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class App {

	private enum Operation {
		Help,
		Version,
		Check
	}

	public static void main(String[] args) {
		System.exit(execute(args));
	}

	private static void showSyntax() {
		try {
			String myPath = App.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();
			String myName = myPath.substring(myPath.lastIndexOf('/') + 1);
			System.out.println("Syntax: " + myName);
			System.out.println(" <-h|--help|-v|--version|path1... pathN>");
		} catch (URISyntaxException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void showVersion() {
		String myVersion = App.class.getPackage().getImplementationVersion();
		System.err.println(myVersion);
	}

	private static Operation getOperation(String[] args) {
		Operation result = Operation.Check;
		for (String arg : args) {
			if (arg.equals("-h") || arg.equals("--help")) {
				return Operation.Help;
			}
			if (arg.equals("-v") || arg.equals("--version")) {
				result = Operation.Version;
			}
			if (arg.equals("--")) {
				break;
			}
		}
		return result;
	}

	private static List<File> getFiles(String[] args) throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		boolean optionsStopped = false;
		for (String arg : args) {
			if (optionsStopped == false && arg.equals("--")) {
				optionsStopped = true;
				continue;
			}
			File file = new File(arg);
			if (!file.exists()) {
				throw new FileNotFoundException("Failed to find the file/directory " + arg);
			}
			file = file.getAbsoluteFile();
			if (!result.contains(file)) {
				result.add(file);
			}
		}
		return result;
	}

	private static URL getXSDResource() throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		URL url = classLoader.getResource("concrete-cif-1.0.xsd");
		if (url == null) {
			throw new IOException("Failed to find the XSD!");
		}
		return url;
	}

	private static int execute(String[] args) {
		switch (getOperation(args)) {
			case Help:
				showSyntax();
				return 0;
			case Version:
				showVersion();
				return 0;
		}
		List<File> files;
		try {
			files = getFiles(args);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return 2;
		}
		if (files.isEmpty()) {
			System.out.println("No file specified.");
			return 0;
		}
		System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/XML/XMLSchema/v1.1",
				"org.apache.xerces.jaxp.validation.XMLSchemaFactory");
		SchemaValidator validator;
		try {
			validator = new SchemaValidator(getXSDResource());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return 2;
		} catch (SAXParseException e) {
			System.err.println(e.getMessage());
			System.err.println("Line: " + e.getLineNumber());
			return 2;
		} catch (SAXException e) {
			System.err.println(e.getClass().getCanonicalName());
			return 2;
		}
		int exitCode = 0;
		for (File file : files) {
			try {
				if (new Processor(validator, file).execute() == false) {
					if (exitCode == 0) {
						exitCode = 1;
					}

				}
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				exitCode = 2;
			}
		}
		return exitCode;
	}

}
