package net.hengstberger.www.trivialshell.shell;

import java.io.FileNotFoundException;

public class NoSuchFileOrDirectoryException extends FileNotFoundException {
	
	private static final long serialVersionUID = 1L;

	public NoSuchFileOrDirectoryException(String dirPath) {
		super("No such file or directory " + dirPath);
	}
}
