package cli.command;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import app.AppConfig;
import app.ChordState;
import app.WorkWithFiles;
import data.FileObject;

public class CommitCommand implements CLICommand {

	@Override
	public String commandName() {
		return "commit";
	}

	@Override
	public void execute(String args) {
		try {
			AppConfig.unlock();

			if (!args.equals("")) {

				File file = new File(AppConfig.WORK_ROUTE_ABSOLUTE);
				File commitFile = WorkWithFiles.searchFile(file, args);

				if (commitFile != null) {
					

					if (!commitFile.isDirectory()) {

						int hashPath = ChordState.hash(args);

						List<String> contentLines;

						contentLines = Files.readAllLines(Paths.get(AppConfig.WORK_ROUTE_ABSOLUTE + File.separator + args));
						String content = WorkWithFiles.getContent(contentLines);
						FileObject toSend = new FileObject(args, content, WorkWithFiles.getFileExtension(commitFile));

						AppConfig.chordState.commit(hashPath, toSend, AppConfig.myServentInfo.getVersions().get(args));

					}

				} else {
					System.err.println("File is not in my root.");
					AppConfig.unlock();
				}

			} else {
				System.err.println("Commit command should have one argument.");
				AppConfig.unlock();
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

}
