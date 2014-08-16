package org.zeroturnaround.zip;

public class ZipBreakException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ZipBreakException(String msg) {
		super(msg);
	}

	public ZipBreakException(Exception e) {
		super(e);
	}

	public ZipBreakException() {
		super();
	}
}
