package net.hengstberger.www.trivialshell.shell;

import java.util.LinkedList;
import java.util.List;

class History {
	private static final int HISTORYSIZE = 5; // must be >1
	List<String> history = new LinkedList<String>();

	void update(String line) {
		history.add(line);
		if (history.size() > HISTORYSIZE) {
			history.remove(history.size() - 1);
		}
	}
	
	String get(int index){
		return history.get(index);
	}
}
