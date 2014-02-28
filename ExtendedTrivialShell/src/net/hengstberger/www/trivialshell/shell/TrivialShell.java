package net.hengstberger.www.trivialshell.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

public class TrivialShell {

	private File cwd; // current work directory
	private Dictionary<String, BuiltinCommand> builtinComands;
	
	
	public TrivialShell() {
		builtinComands = new Hashtable<String, BuiltinCommand>();
		builtinComands.put(Exit.COMMAND_NAME, new Exit(this));
		builtinComands.put(ChangeDirectories.COMMAND_NAME, new ChangeDirectories(this));
		builtinComands.put(ListDirectories.COMMAND_NAME, new ListDirectories(this));
		builtinComands.put(ParentWorkDirectory.COMMAND_NAME, new ParentWorkDirectory(this));
	}

	public static void main(String[] args) {
		try {
			TrivialShell HShell = new TrivialShell();
			HShell.runShell();
		} catch (Exception e) {
			System.err.println("Critial Error in the shell occured.");
			e.printStackTrace();
		}
	}

	public void runShell() {
		// TODO Auto-generated method stub
		
	}
	
	private void parseCommand(String line){
		String[] cmdArgs = line.split(" ");
		String cmd = null;
		String[] args = null;
		
		if(line.length()<=0){
			return;
		}
		
		cmd = cmdArgs[0];
		args = Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length);
		//find builtin commands
		
	}

	public File getCwd() {
		return cwd;
	}

	public void setCwd(File cwd) {
		this.cwd = cwd;
	}

}
