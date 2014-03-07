package net.hengstberger.www.trivialshell.shell;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class ListDirectories extends BuiltinCommand {

	final static String COMMAND_NAME = "ls";

	ListDirectories(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		File cwd = shell.getCwd();
		File[] FileListing = cwd.listFiles();
		StringBuilder listing = new StringBuilder();
		
		if (args.length <= 0) { // no arguments
			for (File curFile : FileListing) {
				StringBuilder entry = new StringBuilder();
				entry = getSimpleFileEntry(curFile);
				listing.append(entry);
			}
			// TODO listing alle 80 Zeichen abteilen
		} else {// args.lengh > 0
			if (args[0].startsWith("-")) {
				args[0] = args[0].substring(1);
				if (args[0].contains("l")) {
					// print long format
					listing.append("Total " +FileListing.length +"\n");
					for (File curFile : FileListing) {
						StringBuilder entry = new StringBuilder();
						entry = getSimpleFileEntry(curFile);
						// TODO do nice tab formating
						if (curFile.isDirectory()) {
							entry.append("d");
						} else {
							entry.append("-");
						}
						// this are applications rights:
						// for filesystem rights check
						// https://weblogs.java.net/blog/kalali/archive/2010/06/23/introducing-nio2-jsr-203-part-3-file-system-attributes-and-permission
						if (curFile.canRead()) {
							entry.append("r");
						} else {
							entry.append("-");
						}
						if (curFile.canWrite()) {
							entry.append("w");
						} else {
							entry.append("-");
						}
						if (curFile.canExecute()) {
							entry.append("x");
						} else {
							entry.append("-");
						}
						entry.append("  ");

						// http://www.ntu.edu.sg/home/ehchua/programming/java/DateTimeCalendar.html
						Date lastModified = new Date(curFile.lastModified());
						SimpleDateFormat dateFormatter = new SimpleDateFormat(
								"yyyy.MM.dd 'at' hh:mm:ss");
						entry.append(dateFormatter.format(lastModified));
						listing.append(entry+"\n");
					}
				}
			}
		}
		if(listing.length()>1){
			listing.deleteCharAt(listing.length()-1);
		}
		System.out.println(listing.toString());
	}

	private StringBuilder getSimpleFileEntry(File curFile) {
		StringBuilder entry = new StringBuilder();
		if (curFile.isHidden()) {
			entry.append("(");
		}
		entry.append(curFile.getName());
		if (curFile.isDirectory()) {
			entry.append(File.separator);
		}
		if (curFile.isHidden()) {
			entry.append(")");
		}
		entry.append(" ");

		return entry;
	}
}