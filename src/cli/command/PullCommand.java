package cli.command;

import app.AppConfig;
import app.ChordState;
import mutex.GlobalMutex;

public class PullCommand implements CLICommand {

	@Override
	public String commandName() {
		return "pull";
	}

	@Override
	public void execute(String args) {

		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		String[] splitArgs = args.split(" ");

		if (splitArgs.length == 1 || splitArgs.length == 2) {

			String name = "";
			int version = -1;

			if (splitArgs.length == 1) {
				name = splitArgs[0];
			}
			if (splitArgs.length == 2) {
				name = splitArgs[0];
				try {
					version = Integer.parseInt(splitArgs[1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

			}

			int hashPath = ChordState.hash(name);
			AppConfig.chordState.pull(hashPath, version);

		} else {
			System.err.println("Pull command should have one or two arguments.");
			AppConfig.unlock();
		}

	}

}
