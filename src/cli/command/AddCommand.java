package cli.command;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import app.AppConfig;
import app.ChordState;
import app.WorkWithFiles;
import data.FileObject;

public class AddCommand implements CLICommand {

	@Override
	public String commandName() {
		return "add";
	}

	@Override
	public void execute(String args) {
		
		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (!args.equals("")) {
			try {
			
				File file = new File(AppConfig.WORK_ROUTE_ABSOLUTE + File.separator);
				File addFile = WorkWithFiles.searchFile(file, args);

				if (addFile != null) {
									
					if (!addFile.isDirectory()) {
						
						int hashPath = ChordState.hash(args);

						if (hashPath < 0 || hashPath >= ChordState.CHORD_SIZE) {
							throw new NumberFormatException();
						}
						if (hashPath < 0) {
							throw new NumberFormatException();
						}
						
						List<String> contentLines = Files.readAllLines(Paths.get(AppConfig.WORK_ROUTE_ABSOLUTE + File.separator + args));
						String content = WorkWithFiles.getContent(contentLines);
						FileObject toSend = new FileObject(args, content, WorkWithFiles.getFileExtension(addFile));
						AppConfig.chordState.add(hashPath, toSend);
					} 
					
				} else {
					System.err.println("File is not in my root.");
					AppConfig.unlock();
				}

			} catch (Exception e) {
				e.printStackTrace();
				AppConfig.unlock();
			}
		} else {
			System.err.println("Add command should have one argument.");
			AppConfig.unlock();
		}

	}

}
