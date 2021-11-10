package cli.command;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import app.AppConfig;
import app.WorkWithFiles;
import cli.CLIParser;
import data.HandOverValuesObject;
import servent.message.HandOverValuesToSuccessor;
import servent.message.util.MessageUtil;

public class QuitCommand implements CLICommand {

	private CLIParser parser;

	public QuitCommand(CLIParser parser) {
		this.parser = parser;
	}

	@Override
	public String commandName() {
		return "quit";
	}

	@Override
	public void execute(String args) {

		try {
			AppConfig.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	
		try {
			Socket bsSocket = new Socket( AppConfig.BOOTSTRAP_IP, AppConfig.BOOTSTRAP_PORT);

			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			String toSend = String.valueOf(AppConfig.myServentInfo.getListenerPort()) + "-"
					+ AppConfig.myServentInfo.getIpAddress();
			bsWriter.write("Exit\n" + toSend + "\n");
			bsWriter.flush();

			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		parser.stop();

		Map<Integer, ArrayList<HandOverValuesObject>> toSend = new HashMap<>();

		for (Entry<Integer, ArrayList<File>> valueEntry : AppConfig.chordState.getValueMap().entrySet()) {
			ArrayList<HandOverValuesObject> filesForHash = new ArrayList();
			for (File f : valueEntry.getValue()) {

				String absolutePath = f.getAbsolutePath().replace("\\", "/");
				String[] splitArgs = absolutePath.split("/");

				List<String> contentLines;
				try {
					contentLines = Files.readAllLines(Paths.get(f.getAbsolutePath()));
					String content = WorkWithFiles.getContent(contentLines);

					HandOverValuesObject obj = new HandOverValuesObject(splitArgs[splitArgs.length - 1], content);
					filesForHash.add(obj);

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			toSend.put(valueEntry.getKey(), filesForHash);
		}

		HandOverValuesToSuccessor giveMyFilesMessage = new HandOverValuesToSuccessor(
				AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
				AppConfig.chordState.getNextNode().getListenerPort(), AppConfig.chordState.getNextNode().getIpAddress(),
				toSend, AppConfig.myServentInfo.getChordId());

		MessageUtil.sendMessage(giveMyFilesMessage);

	}

}
