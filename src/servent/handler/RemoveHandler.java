package servent.handler;

import java.io.File;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.WorkWithFiles;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UnlockMessage;
import servent.message.RemoveMessage;
import servent.message.util.MessageUtil;

public class RemoveHandler implements MessageHandler {

	private Message clientMessage;

	public RemoveHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
		if (clientMessage.getMessageType() == MessageType.REMOVE) {
			RemoveMessage removeMessage = (RemoveMessage) clientMessage;

				int hash = removeMessage.getHash();
				Integer chordId = Integer.parseInt(removeMessage.getMessageText());

				if (AppConfig.chordState.isKeyMine(hash)) {

					File storage = new File(AppConfig.STORAGE_ABSOLUTE + File.separator);
					File[] files = storage.listFiles();

					for (int i = 0; i < files.length; i++) {

						String relative =  WorkWithFiles.relativePath(
								WorkWithFiles.getFileWithoutVersion(files[i].getAbsolutePath()), storage.getAbsolutePath());

						if (!relative.equals("")) {
							if (ChordState.hash(relative) == hash) {
								boolean ret = files[i].delete();
							}
						}
					}

					AppConfig.chordState.getValueMap().remove(hash);
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
					
					UnlockMessage unlockMessage = new UnlockMessage(
							AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
							nextNode.getListenerPort(), nextNode.getIpAddress(), chordId, "Delete done!");

					MessageUtil.sendMessage(unlockMessage);

				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
					RemoveMessage newRemoveMessage = new RemoveMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							hash, chordId);
					MessageUtil.sendMessage(newRemoveMessage);

				}

		} else{
			System.err.println("Remove handler can only handle REMOVE message");
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
