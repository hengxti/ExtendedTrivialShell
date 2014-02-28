package net.hengstberger.www.trivialshell.shell;

public class ParentWorkDirectory extends BuiltinCommand{

	final static String COMMAND_NAME = "pwd";
	
	ParentWorkDirectory(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		System.out.println(shell.getCwd());
	}

}
