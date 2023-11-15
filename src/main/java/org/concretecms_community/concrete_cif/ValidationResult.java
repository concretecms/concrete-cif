package org.concretecms_community.concrete_cif;

import java.util.List;

import org.xml.sax.SAXParseException;

class ValidationResult {
	public final List<SAXParseException> warnings;
	public final List<SAXParseException> errors;
	public final List<SAXParseException> fatalErrors;

	public ValidationResult(List<SAXParseException> warnings, List<SAXParseException> errors,
			List<SAXParseException> fatalErrors) {
		this.warnings = warnings;
		this.errors = errors;
		this.fatalErrors = fatalErrors;
	}

	public boolean isEmpty() {
		return this.warnings.isEmpty() && this.errors.isEmpty() && this.fatalErrors.isEmpty();
	}
}
