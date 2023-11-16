package org.concretecms_community.concrete_cif;

import java.util.ArrayList;
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

	public String toString() {
		if (this.isEmpty()) {
			return "";
		}
		List<SAXParseException> w = sortList(this.warnings);
		List<SAXParseException> e = sortList(this.errors);
		List<SAXParseException> fe = sortList(this.fatalErrors);
		StringBuilder result = new StringBuilder();
		for (;;) {
			Integer lineNumber = getNextLineNumber(w, e, fe);
			if (lineNumber == null) {
				break;
			}
			result.append("Line ").append(lineNumber).append(System.lineSeparator());
			popMessageAtLine(result, lineNumber, "WARNING", w);
			popMessageAtLine(result, lineNumber, "ERROR", e);
			popMessageAtLine(result, lineNumber, "FATAL ERROR", fe);
		}
		return result.toString();
	}

	private static List<SAXParseException> sortList(List<SAXParseException> list) {
		List<SAXParseException> result = new ArrayList<SAXParseException>(list);
		result.sort((o1, o2) -> {
			int delta = o1.getLineNumber() - o2.getLineNumber();
			if (delta == 0) {
				delta = o1.getColumnNumber() - o2.getColumnNumber();
			}
			return delta;
		});

		return result;
	}

	private static Integer getNextLineNumber(List<SAXParseException> w, List<SAXParseException> e,
			List<SAXParseException> fe) {
		Integer firstInW = w.size() == 0 ? null : w.get(0).getLineNumber();
		Integer firstInE = e.size() == 0 ? null : e.get(0).getLineNumber();
		Integer firstInFE = fe.size() == 0 ? null : fe.get(0).getLineNumber();
		if (firstInW == null && firstInE == null && firstInFE == null) {
			return null;
		}
		return Math.min(
				Math.min(firstInW == null ? Integer.MAX_VALUE : firstInW,
						firstInE == null ? Integer.MAX_VALUE : firstInE),
				firstInFE == null ? Integer.MAX_VALUE : firstInFE);
	}

	private static void popMessageAtLine(StringBuilder result, int lineNumber, String level,
			List<SAXParseException> list) {
		while (list.size() > 0 && list.get(0).getLineNumber() <= lineNumber) {
			SAXParseException entry = list.remove(0);
			result.append('[').append(level).append("] ").append(entry.getMessage()).append(" (at column ")
					.append(entry.getColumnNumber()).append(')').append(System.lineSeparator());
		}
	}
}
