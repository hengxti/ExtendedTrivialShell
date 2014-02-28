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
		
		for (File curFile: FileListing){
			System.out.print(curFile.getName() + " ");
		}//TODO formating, params
		System.out.println();
		
	}
	
}