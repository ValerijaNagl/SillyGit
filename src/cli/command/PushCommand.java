package cli.command;

import java.io.File;

import app.AppConfig;
import app.ChordState;
import app.WorkWithFiles;
import data.CommitResponseObject;

public class PushCommand implements CLICommand {

	@Override
	public String commandName() {
		return "push";
	}

	@Override
	public void execute(String args) {
		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (!args.equals("")) {
			
			File file = new File(AppConfig.WORK_ROUTE_ABSOLUTE + File.separator);
			File commitFile = WorkWithFiles.searchFile(file, args);

			if (commitFile != null) {
				
				if (!commitFile.isDirectory()) {

					int hash = ChordState.hash(args);
					CommitResponseObject conflictObject = AppConfig.myServentInfo.getConflicts().get(hash);
					AppConfig.chordState.push(hash,conflictObject,conflictObject.getVersion());
				
				} else {

				}
			} else {
				System.err.println("I dont' have that file in my root");
				AppConfig.unlock();
			}

		} else {
			System.err.println("Push should have one argument.");
			AppConfig.unlock();
		}

	}

}
