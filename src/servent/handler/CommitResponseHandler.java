package servent.handler;

import java.util.Map.Entry;

import app.AppConfig;
import app.ServentInfo;
import data.CommitResponseObject;
import servent.message.CommitResponseMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class CommitResponseHandler implements MessageHandler {

	private Message clientMessage;

	public CommitResponseHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.COMMIT_RESPONSE) {
			
			CommitResponseMessage conflictMessage = (CommitResponseMessage) clientMessage;
			int chordId = Integer.parseInt(conflictMessage.getMessageText());
			int hash = conflictMessage.getHash();
			CommitResponseObject conflictObject = conflictMessage.getConflictObject();

			if (AppConfig.myServentInfo.getChordId() == chordId) {

				if (!conflictMessage.isConflictHappened()) {
					System.out.println("File is commited and there was no conflict.");
					AppConfig.unlock();

				
				} else {
					
					AppConfig.myServentInfo.getConflicts().put(hash, conflictObject);

					System.out.println("You have a conflict for: ");
					for (Entry<Integer, CommitResponseObject> valueEntry : AppConfig.myServentInfo.getConflicts()
							.entrySet()) {
						System.out.println(valueEntry.getValue().getName());
					}

					System.out.println("Choose one of options if you want to resolve the conflict: view, pull_confl, push");

					AppConfig.unlock();
				}

			} else {
				ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
				CommitResponseMessage newCommitResponseMessage = new CommitResponseMessage(AppConfig.myServentInfo.getListenerPort(),
						AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
						chordId, hash, conflictObject, conflictMessage.isConflictHappened());

				MessageUtil.sendMessage(newCommitResponseMessage);
			}

		} else {
			AppConfig.timestampedErrorPrint("Conflict handler got a message that is not CONFLICT");
		}

	}

}
