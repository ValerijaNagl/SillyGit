package servent.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import app.AppConfig;
import app.ServentInfo;
import app.WorkWithFiles;
import data.FileObject;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UnlockMessage;
import servent.message.util.MessageUtil;

public class AddHandler implements MessageHandler {

	private Message clientMessage;

	public AddHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.ADD) {
				AddMessage addMessage = (AddMessage) clientMessage;

				int hash = addMessage.getHash();
				Integer chordId = Integer.parseInt(addMessage.getMessageText());
				FileObject addObject = addMessage.getAddObject();

				if (AppConfig.chordState.isKeyMine(hash)) {

					File newFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator + "-0" + addObject.getExtension());

					try {
						newFile.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					WorkWithFiles.writeToFile(newFile.getAbsolutePath(), addObject.getContent());

					ArrayList<File> list = new ArrayList<File>();
					list.add(newFile);
					AppConfig.chordState.getValueMap().put(hash, list);

					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
					UnlockMessage unlockMessage = new UnlockMessage(
							AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
							nextNode.getListenerPort(), nextNode.getIpAddress(), chordId, "Add done!");

					MessageUtil.sendMessage(unlockMessage);

				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
					AddMessage newAddMessage = new AddMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							hash, addMessage.getAddObject(), chordId);
					MessageUtil.sendMessage(newAddMessage);
				}

			} else {
				AppConfig.timestampedErrorPrint("Add handler can only handle ADD messages.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
