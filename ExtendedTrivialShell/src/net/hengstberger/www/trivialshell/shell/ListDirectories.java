package net.hengstberger.www.trivialshell.shell;

import java.io.File;

class ListDirectories extends BuiltinCommand{

	final static String COMMAND_NAME= "ls";
	
	ListDirectories(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		File cwd = shell.getCwd();
		File[] FileListing = cwd.listFiles();
		StringBuilder listing  = new StringBuilder();
		
		for (File curFile: FileListing){
			StringBuilder entry = new StringBuilder();
			if(curFile.isHidden()){
				entry.append("(");
			}
			entry.append(curFile.getName());
			if(curFile.isDirectory()){
				entry.append(File.separator);
			}
			if(curFile.isHidden()){
				entry.append(")");
			}
			entry.append(" ");
			listing.append(entry);
		}//TODO params
		
		//TODO listing alle 80 Zeichen abteilen
		System.out.println(listing.toString());
		
	}

	
}