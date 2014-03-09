package net.hengstberger.www.trivialshell.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class TrivialShell {

	private File cwd; // current work directory
	private Dictionary<String, BuiltinCommand> builtinCommands;
	private String hostName;

	public TrivialShell() {
		builtinCommands = new Hashtable<String, BuiltinCommand>();
		builtinCommands.put(Exit.COMMAND_NAME, new Exit(this));
		builtinCommands.put(ChangeDirectories.COMMAND_NAME,
				new ChangeDirectories(this));
		builtinCommands.put(ListDirectories.COMMAND_NAME, new ListDirectories(
				this));
		builtinCommands.put(ParentWorkDirectory.COMMAND_NAME,
				new ParentWorkDirectory(this));
		hostName = findHostName();
		cwd = new File(System.getProperty("user.home"));
	}

	private String findHostName() {
		String hostName = "localhost";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// can not find hostname
			// e.printStackTrace();
		}
		return hostName;
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

	public void runShell() throws IOException {
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		History history = new History();
		printPrompt();

// //TODO code for Tab and history		
//		int character=br.read();
//		while (character != -1) { // while no End of line found
//			
//			switch(character){
//			case '\t': System.out.print("tab rec"); break;
//			case 38: System.out.print("arrow up"); break; // arrow up
//			case 40: System.out.print("arrow down"); break; // arrow down
//			default:
//			}
//			
//
//			character=br.read();
//			line.append(character);
//		} // EoL found
//		parseCommand(line.toString());
//		history.update(line.toString());
//		printPrompt();
		
		 while((line = br.readLine())!=null){
		 parseCommand(line);
		 printPrompt();
		 }


	}

	private void printPrompt() throws IOException {
		System.out.print(hostName + "@" + cwd.getCanonicalPath() + "> ");

	}

	private void parseCommand(String line) {
		String[] cmdArgs = line.split(" ");
		String cmd = null;
		String[] args = null;

		if (line.length() <= 0) {
			return;
		}

		cmd = cmdArgs[0];
		args = Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length);
		// look for builtin commands
		for (Enumeration<String> builtinCommandNames = builtinCommands.keys(); builtinCommandNames
				.hasMoreElements();) {
			String builtinCommandName = builtinCommandNames.nextElement();
			if (cmd.equals(builtinCommandName)) {
				try {
					builtinCommands.get(builtinCommandName).execute(args);
				} catch (Exception e) {
					System.err.print("Error executing: " + builtinCommandName
							+ " in " + e.toString());
					// e.printStackTrace();
				}
				return;
			}
		}
	}

	public File getCwd() {
		return cwd;
	}

	public void setCwd(File cwd) {
		this.cwd = cwd;
	}

}
