//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneratorException extends RuntimeException {
	public List<Exception> exceptions = new ArrayList();

	public GeneratorException() {
	}

	public GeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneratorException(String message) {
		super(message);
	}

	public GeneratorException(String message, List<Exception> exceptions) {
		super(message);
		this.exceptions = exceptions;
	}

	public GeneratorException(Throwable cause) {
		super(cause);
	}

	public List<Exception> getExceptions() {
		return this.exceptions;
	}

	public void setExceptions(List<Exception> exceptions) {
		if (exceptions == null) {
			throw new NullPointerException("'exceptions' must be not null");
		} else {
			this.exceptions = exceptions;
		}
	}

	public GeneratorException add(Exception e) {
		this.exceptions.add(e);
		return this;
	}

	public GeneratorException addAll(List<Exception> excetpions) {
		this.exceptions.addAll(excetpions);
		return this;
	}

	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream s) {
		s.println("GeneratorException:" + this.getMessage());
		Iterator i$ = this.exceptions.iterator();

		while(i$.hasNext()) {
			Exception e = (Exception)i$.next();
			e.printStackTrace(s);
			s.println("--------------------------------------------------------------------------------------------------------------------------------");
		}

	}

	public void printStackTrace(PrintWriter s) {
		s.println("GeneratorException:" + this.getMessage());
		Iterator i$ = this.exceptions.iterator();

		while(i$.hasNext()) {
			Exception e = (Exception)i$.next();
			e.printStackTrace(s);
			s.println("--------------------------------------------------------------------------------------------------------------------------------");
		}

	}

	public String toString() {
		StringWriter out = new StringWriter();
		Iterator i$ = this.exceptions.iterator();

		while(i$.hasNext()) {
			Exception e = (Exception)i$.next();
			out.append(e.toString() + "\n");
		}

		return out.toString();
	}
}
