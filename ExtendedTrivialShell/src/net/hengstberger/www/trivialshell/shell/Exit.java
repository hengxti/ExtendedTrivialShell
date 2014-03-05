package net.hengstberger.www.trivialshell.shell;

public class Exit extends BuiltinCommand {

	final static String COMMAND_NAME = "exit";

	Exit(TrivialShell shell) {
		super(shell);
	}

	@Override
	public void execute(String[] args) throws Exception {
		System.out.print("\nexiting! Goodbye :)");
		System.exit(0);
	}

}
