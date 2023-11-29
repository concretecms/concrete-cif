package org.concretecms.concrete_cif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class App {

	protected final PrintStream standardOutput;
	protected final PrintStream standardError;

	private enum Operation {
		Help, Version, Check
	}

	public static void main(String[] args) {
		System.exit(new App(System.out, System.err).execute(args));
	}

	protected App(PrintStream standardOutput, PrintStream standardError) {
		this.standardOutput = standardOutput;
		this.standardError = standardError;
	}

	private void showSyntax() {
		try {
			String myPath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			String myName = myPath.substring(myPath.lastIndexOf('/') + 1);
			this.standardOutput.println("Syntax: " + myName);
			this.standardOutput.println(" <-h|--help|-v|--version|path1... pathN>");
		} catch (URISyntaxException e) {
			this.standardError.println(e.getMessage());
		}
	}

	private void showVersion() {
		String myVersion = App.class.getPackage().getImplementationVersion();
		this.standardError.println(myVersion);
	}

	private Operation getOperation(String[] args) {
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

	private List<File> getFiles(String[] args) throws FileNotFoundException {
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

	private URL getXSDResource() throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		URL url = classLoader.getResource("concrete-cif-1.0.xsd");
		if (url == null) {
			throw new IOException("Failed to find the XSD!");
		}
		return url;
	}

	protected int execute(String[] args) {
		switch (getOperation(args)) {
		case Help:
			showSyntax();
			return 0;
		case Version:
			showVersion();
			return 0;
		case Check:
		default:
			break;
		}
		List<File> files;
		try {
			files = getFiles(args);
		} catch (FileNotFoundException e) {
			this.standardError.println(e.getMessage());
			return 2;
		}
		if (files.isEmpty()) {
			this.standardOutput.println("No file specified.");
			return 0;
		}
		SchemaValidator validator;
		try {
			validator = new SchemaValidator(getXSDResource());
		} catch (IOException e) {
			this.standardError.println(e.getMessage());
			return 2;
		} catch (SAXParseException e) {
			this.standardError.println(e.getMessage());
			this.standardError.println("Line: " + e.getLineNumber());
			return 2;
		} catch (SAXException e) {
			this.standardError.println(e.getClass().getCanonicalName());
			return 2;
		}
		int exitCode = 0;
		for (File file : files) {
			try {
				if (new Processor(this.standardOutput, this.standardError, validator, file).execute() == false) {
					if (exitCode == 0) {
						exitCode = 1;
					}

				}
			} catch (FileNotFoundException e) {
				this.standardError.println(e.getMessage());
				exitCode = 2;
			}
		}
		return exitCode;
	}

}
