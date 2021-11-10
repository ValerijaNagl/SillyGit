package cli.command;

import app.AppConfig;
import app.ChordState;
import data.CommitResponseObject;

public class ViewCommand implements CLICommand {

	@Override
	public String commandName() {
		return "view";
	}

	@Override
	public void execute(String args) {
		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (!args.equals("")) {

			int hash = ChordState.hash(args);

			CommitResponseObject conflictObject = AppConfig.myServentInfo.getConflicts().get(hash);

			if (conflictObject != null) {
				System.out.print("Updated file: ");
				System.out.println(conflictObject.getContent());
				System.out.println("Choose an option: view, pull_confl, push");

				AppConfig.unlock();
			} else {
				System.err.println("There is no file with that name");
				AppConfig.unlock();
			}

		} else {
			System.err.println("View should have one argument.");
			AppConfig.unlock();
		}

	}

}
