package cli.command;

import app.AppConfig;
import app.ChordState;
import mutex.GlobalMutex;

public class RemoveCommand implements CLICommand {

	@Override
	public String commandName() {
		return "remove";
	}

	@Override
	public void execute(String args) {
		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (!args.equals("")) {
			AppConfig.chordState.remove(ChordState.hash(args));	 
		} else {
			System.err.println("Remove should have one argument");
			AppConfig.unlock();
		}

	}

}
