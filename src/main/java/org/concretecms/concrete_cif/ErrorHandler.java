package org.concretecms.concrete_cif;

import java.util.List;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class ErrorHandler implements org.xml.sax.ErrorHandler {
	private List<SAXParseException> warnings;
	private List<SAXParseException> errors;
	private List<SAXParseException> fatalErrors;

	public ErrorHandler() {
		this.warnings = new ArrayList<SAXParseException>();
		this.errors = new ArrayList<SAXParseException>();
		this.fatalErrors = new ArrayList<SAXParseException>();
	}

	public void warning(SAXParseException e) {
		this.warnings.add(e);
	}

	public void error(SAXParseException e) {
		this.errors.add(e);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		this.fatalErrors.add(e);
	}

	public ValidationResult getResult() {
		return new ValidationResult(this.warnings, this.errors, this.fatalErrors);
	}
}
