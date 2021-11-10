package servent.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import app.AppConfig;
import app.WorkWithFiles;
import data.HandOverValuesObject;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;

	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage) clientMessage;

			for (Entry<Integer, ArrayList<HandOverValuesObject>> valueEntry : welcomeMsg.getFiles().entrySet()) {
				ArrayList<HandOverValuesObject> list = valueEntry.getValue();
				ArrayList<File> newValues = new ArrayList<>();
				for (HandOverValuesObject object : list) {

					File newFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator + object.getName());

					try {
						newFile.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					WorkWithFiles.writeToFile(newFile.getAbsolutePath(), object.getContent());
					newValues.add(newFile);

				}
			}

			AppConfig.chordState.init(welcomeMsg);

			UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(), AppConfig.chordState.getNextNodePort(),
					AppConfig.chordState.getNextNodeIp(), "");
			MessageUtil.sendMessage(um);

		} else {
			System.err.println("Welcome handler got a message that is not WELCOME");
		}

	}

}
