package net.hengstberger.www.trivialshell.shell;

abstract class BuiltinCommand{

	protected TrivialShell shell;
	
	BuiltinCommand(TrivialShell shell) {
		this.shell = shell;
	}
	private BuiltinCommand(){} // forbid empty constructor
	
	public abstract void execute(String[] args) throws Exception; 
	
}
