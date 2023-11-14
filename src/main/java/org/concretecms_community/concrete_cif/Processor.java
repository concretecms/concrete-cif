package org.concretecms_community.concrete_cif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class Processor {
	private final SchemaValidator validator;
	private final File file;
	private static final Pattern rxConcreteCif = Pattern.compile("<concrete5?-cif(>|\\s|$)", Pattern.CASE_INSENSITIVE);
	private final String omitPrefix;

	public Processor(SchemaValidator validator, File file) {
		this.validator = validator;
		this.file = file;
		if (file.isDirectory()) {
			this.omitPrefix = file.getAbsoluteFile().getParent() + File.separatorChar;
		} else {
			this.omitPrefix = "";
		}
	}

	public boolean execute() throws FileNotFoundException {
		return process(this.file);
	}

	private boolean process(File file) throws FileNotFoundException {
		if (file.isDirectory()) {
			return this.processDirectory(file);
		} else {
			return this.processFile(file);
		}
	}

	private boolean processDirectory(File directory) throws FileNotFoundException {
		boolean allSuccess = true;
		for (File entry : directory.listFiles()) {
			if (this.process(entry) == false) {
				allSuccess = false;
			}
		}
		return allSuccess;
	}

	private boolean shouldProcessFile(File file) throws FileNotFoundException {
		String name = file.getName();
		int dotPosition = name.lastIndexOf('.');
		String extension = dotPosition < 0 ? "" : name.substring(dotPosition);
		if (!extension.equalsIgnoreCase(".xml")) {
			return false;
		}
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				line = line.replace("<?xml", "").replace("<!--", "");
				if (rxConcreteCif.matcher(line).find()) {
					return true;
				}
				if (line.indexOf('<') >= 0) {
					return false;
				}

			}
		}
		return false;
	}

	private boolean processFile(File file) throws FileNotFoundException {
		if (!this.shouldProcessFile(file)) {
			return true;
		}
		String path = file.getAbsolutePath();
		if (this.omitPrefix.length() > 0 && path.startsWith(this.omitPrefix)) {
			path = path.substring(this.omitPrefix.length());
		}
		System.out.print(path + "... ");
		ValidationResult result;
		try {
			result = this.validator.validate(file);
		} catch (SAXException | IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
		if (result.isEmpty()) {
			System.out.println("passed.");
			return true;
		}
		System.out.println("FAILED.");
		if (!result.warnings.isEmpty()) {
			System.err.println("> WARNINGS");
			for (SAXParseException item : result.warnings) {
				System.err.println("Line " + item.getLineNumber() + ": " + item.getMessage());
			}
		}
		if (!result.errors.isEmpty()) {
			System.err.println("> ERRORS");
			for (SAXParseException item : result.errors) {
				System.err.println("Line " + item.getLineNumber() + ": " + item.getMessage());
			}
		}
		if (!result.fatalErrors.isEmpty()) {
			System.err.println("> ERRORS");
			for (SAXParseException item : result.fatalErrors) {
				System.err.println("Line " + item.getLineNumber() + ": " + item.getMessage());
			}
		}
		return false;
	}
}
