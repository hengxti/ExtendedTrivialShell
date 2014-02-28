package net.hengstberger.www.trivialshell.shell;

import java.io.File;

public class TrivialShell {

	private File cwd; // current work directory
	
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
		
	}

	public File getCwd() {
		return cwd;
	}

	public void setCwd(File cwd) {
		this.cwd = cwd;
	}

}
