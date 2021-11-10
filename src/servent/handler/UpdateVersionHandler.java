package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateVersionAfterConflict;
import servent.message.util.MessageUtil;

public class UpdateVersionHandler implements MessageHandler {

	private Message clientMessage;

	public UpdateVersionHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.UPDATE_VERSION_AFTER_CONFLICT) {
				UpdateVersionAfterConflict udateVersionAfterConflict = (UpdateVersionAfterConflict) clientMessage;
				int chordId = Integer.parseInt(udateVersionAfterConflict.getMessageText());
				
				if (AppConfig.myServentInfo.getChordId() == chordId) {

					AppConfig.myServentInfo.getVersions().put(udateVersionAfterConflict.getFile(),udateVersionAfterConflict.getVersion());
					AppConfig.unlock();
				
				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
					UpdateVersionAfterConflict uvac = new UpdateVersionAfterConflict(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							udateVersionAfterConflict.getFile(),udateVersionAfterConflict.getVersion(),chordId);

					MessageUtil.sendMessage(uvac);
				}
			}else {
				System.err.println("UPDATE_VERSION_AFTER_CONFLICT handler got a message that is not UPDATE_VERSION_AFTER_CONFLICT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
