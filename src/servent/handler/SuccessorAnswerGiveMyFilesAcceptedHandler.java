package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.SuccessorAnswerMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.ExitMessage;
import servent.message.util.MessageUtil;

public class SuccessorAnswerGiveMyFilesAcceptedHandler implements MessageHandler {

	private Message clientMessage;

	public SuccessorAnswerGiveMyFilesAcceptedHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.SUCCESSOR_ANSWER) {
				SuccessorAnswerMessage giveMyFilesAcceptedMessage = (SuccessorAnswerMessage) clientMessage;
				int chordId = Integer.parseInt(giveMyFilesAcceptedMessage.getMessageText());
				
				if (chordId == AppConfig.myServentInfo.getChordId()) {

					ExitMessage exitMessage = new ExitMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(),
							AppConfig.chordState.getNextNode().getListenerPort(),
							AppConfig.chordState.getNextNode().getIpAddress(), AppConfig.myServentInfo.getChordId());
					MessageUtil.sendMessage(exitMessage);
					
				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
					SuccessorAnswerMessage newSuccessorAnswerMessage = new SuccessorAnswerMessage(
							AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
							nextNode.getListenerPort(), nextNode.getIpAddress(), chordId);
					MessageUtil.sendMessage(newSuccessorAnswerMessage);
				}
			} else {
				System.err.println("SUCCESSOR_ANSWER handler got a message that is not SUCCESSOR_ANSWER");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
