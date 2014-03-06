package net.hengstberger.www.trivialshell.shell;

import java.io.File;

class ChangeDirectories extends BuiltinCommand{

	final static String COMMAND_NAME = "cd";
	
	ChangeDirectories(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		if(args.length == 1){
			
			File newDir = new File(shell.getCwd().getCanonicalPath()+ File.separator +args[0]);			
			
			if (!newDir.exists()||!newDir.isDirectory()){
				throw new NoSuchFileOrDirectoryException(newDir.getCanonicalPath());
			}
			
			shell.setCwd(newDir);
		}
	}

}
