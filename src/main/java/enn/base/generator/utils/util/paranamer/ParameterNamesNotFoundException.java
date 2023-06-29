//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.paranamer;

public class ParameterNamesNotFoundException extends RuntimeException {
	public static final String __PARANAMER_DATA = "v1.0 \n<init> java.lang.String message \n";
	private Exception cause;

	public ParameterNamesNotFoundException(String message, Exception cause) {
		super(message);
		this.cause = cause;
	}

	public ParameterNamesNotFoundException(String message) {
		super(message);
	}

	public Throwable getCause() {
		return this.cause;
	}
}
