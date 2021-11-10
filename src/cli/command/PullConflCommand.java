package cli.command;

import java.io.File;

import app.AppConfig;
import app.ChordState;
import app.WorkWithFiles;
import data.CommitResponseObject;

public class PullConflCommand implements CLICommand {

	@Override
	public String commandName() {
		return "pull_confl";
	}

	@Override
	public void execute(String args) {
		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (!args.equals("")) {

			int hashFileName = ChordState.hash(args);

			CommitResponseObject conflictObject = AppConfig.myServentInfo.getConflicts().get(hashFileName);

			if (conflictObject != null) {
				File conflictFile = new File(
						AppConfig.WORK_ROUTE_ABSOLUTE + File.separator + args + conflictObject.getExtension());

				WorkWithFiles.writeToFile(conflictFile.getAbsolutePath(), conflictObject.getContent());

				AppConfig.myServentInfo.getVersions().put(args, conflictObject.getVersion());
				AppConfig.myServentInfo.getConflicts().remove(hashFileName);
				AppConfig.timestampedStandardPrint("Pull in conflict is done!");
				AppConfig.unlock();
			} else {
				AppConfig.timestampedErrorPrint("I don't have that file in my root");
				AppConfig.unlock();
			}

		} else {
			System.err.println("pull_conflict should have one argument.");
			AppConfig.unlock();
		}

	}

}