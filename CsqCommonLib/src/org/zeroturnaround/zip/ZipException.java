package org.zeroturnaround.zip;

public class ZipException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ZipException(String msg) {
		super(msg);
	}

	public ZipException(Exception e) {
		super(e);
	}

	public ZipException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
