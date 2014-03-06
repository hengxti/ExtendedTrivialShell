package net.hengstberger.www.trivialshell.shell;

import java.io.FileNotFoundException;

public class NoSuchFileOrDirectoryException extends FileNotFoundException {
	
	public NoSuchFileOrDirectoryException(String dirPath) {
		super("No such file or directory " + dirPath);
	}
}
