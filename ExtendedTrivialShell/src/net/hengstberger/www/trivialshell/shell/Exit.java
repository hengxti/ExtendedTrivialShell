package net.hengstberger.www.trivialshell.shell;

public class Exit extends BuiltinCommand{

	Exit(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		System.out.print("\n exiting!");
		System.exit(0);
	}

}
