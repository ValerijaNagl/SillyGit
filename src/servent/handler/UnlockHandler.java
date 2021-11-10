package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UnlockMessage;
import servent.message.util.MessageUtil;

public class UnlockHandler implements MessageHandler {

	private Message clientMessage;

	public UnlockHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.UNLOCK) {
				UnlockMessage unlockMessage = (UnlockMessage) clientMessage;
				int chordId = Integer.parseInt(unlockMessage.getMessageText());
				if (AppConfig.myServentInfo.getChordId() == chordId) {

					System.out.println(unlockMessage.getPrint());
					AppConfig.unlock();
					
				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
					UnlockMessage newUnlockMessage = new UnlockMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							chordId, unlockMessage.getPrint());

					MessageUtil.sendMessage(newUnlockMessage);
				}
			}else {
				System.err.println("UNLOCK handler got a message that is not UNLOCK");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
