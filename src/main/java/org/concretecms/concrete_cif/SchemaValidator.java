package org.concretecms.concrete_cif;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

class SchemaValidator {
	private final Schema schema;

	public SchemaValidator(URL xsdLocation) throws SAXException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
		// Enable full XPath 2.0 for XSD 1.1 Conditional Type Assignment
		// @see https://xerces.apache.org/xerces2-j/faq-xs.html#faq-3
		factory.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", true);
		this.schema = factory.newSchema(xsdLocation);
	}

	public ValidationResult validate(File file) throws SAXException, IOException {
		Validator validator = this.schema.newValidator();
		ErrorHandler errorHandler = new ErrorHandler();
		validator.setErrorHandler(errorHandler);
		StreamSource source = new StreamSource(file);
		validator.validate(source);
		return errorHandler.getResult();
	}
}
