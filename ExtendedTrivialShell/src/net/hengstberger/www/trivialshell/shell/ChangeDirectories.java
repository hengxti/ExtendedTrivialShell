package net.hengstberger.www.trivialshell.shell;

import java.io.File;

class ChangeDirectories extends BuiltinCommand{

	ChangeDirectories(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		if(args.length == 2){
			if(args[0].toLowerCase()=="cd"){
				// TODO if dir exsists
				shell.setCwd(new File(args[1]));
			}
		}
	}

}
