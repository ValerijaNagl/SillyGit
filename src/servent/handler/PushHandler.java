package servent.handler;

import java.io.File;
import java.util.List;

import app.AppConfig;
import app.ServentInfo;
import app.WorkWithFiles;
import data.CommitResponseObject;
import servent.message.CommitMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PushMessage;
import servent.message.UnlockMessage;
import servent.message.UpdateVersionAfterConflict;
import servent.message.util.MessageUtil;

public class PushHandler implements MessageHandler {

	private Message clientMessage;

	public PushHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.PUSH) {
				PushMessage pushMessage = (PushMessage) clientMessage;

				CommitResponseObject conflictObject = pushMessage.getConflictObject();
				int chordId = Integer.parseInt(pushMessage.getMessageText());
				int hash = pushMessage.getHash();
				int version = pushMessage.getVersion();

				if (AppConfig.myServentInfo.getChordId() != chordId) {
					if (AppConfig.chordState.isKeyMine(hash)) {

						List<File> filesWithHash = AppConfig.chordState.getValueMap().get(hash);

						File commitFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator
								+ WorkWithFiles.getFileWithoutVersion(conflictObject.getName()) + "_"
								+ filesWithHash.size() + conflictObject.getExtension());

						WorkWithFiles.writeToFile(commitFile.getAbsolutePath(), conflictObject.getContent());
						AppConfig.chordState.getValueMap().get(hash).add(commitFile);

						ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
						
						UpdateVersionAfterConflict updateVersionAfterConflictMessage = new UpdateVersionAfterConflict(
								AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
								nextNode.getListenerPort(), nextNode.getIpAddress(), conflictObject.getName(),
								filesWithHash.size(), chordId);

						MessageUtil.sendMessage(updateVersionAfterConflictMessage);

					} else {
						ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
						PushMessage pm = new PushMessage(AppConfig.myServentInfo.getListenerPort(),
								AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(),
								nextNode.getIpAddress(), hash,pushMessage.getConflictObject(),
								chordId, version);
						MessageUtil.sendMessage(pm);
					}
				} else {
					System.err.println("There is no such conflict of version!");
					AppConfig.unlock();
					
				}
			}else {
				System.err.println("PUSH handler can handle only PUSH messages");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
