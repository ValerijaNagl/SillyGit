package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.ExitMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdatePredecessorMessage;
import servent.message.util.MessageUtil;

public class ExitHandler implements MessageHandler {

	private Message clientMessage;

	public ExitHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.QUIT) {
				ExitMessage quitMessage = (ExitMessage) clientMessage;
				int chordId = Integer.parseInt(quitMessage.getMessageText());

				if (AppConfig.myServentInfo.getChordId() == chordId) {

					ServentInfo myPredecessor = AppConfig.chordState.getPredecessor();
					
					UpdatePredecessorMessage updatePredecessorMessage = new UpdatePredecessorMessage(
							AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
							AppConfig.chordState.getNextNodePort(), AppConfig.chordState.getNextNodeIp(),
							myPredecessor.getIpAddress(),
							myPredecessor.getListenerPort());

					MessageUtil.sendMessage(updatePredecessorMessage);

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					AppConfig.simpleServentListener.stop();

				} else {

					AppConfig.chordState.allNodeInfo.removeIf(curent -> curent.getChordId() == chordId);

					ExitMessage nextExitMessage = new ExitMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), AppConfig.chordState.getNextNodePort(),
							AppConfig.chordState.getNextNodeIp(), chordId);

					MessageUtil.sendMessage(nextExitMessage);

					AppConfig.chordState.updateSuccessorTable();

				}
			} else {
				AppConfig.timestampedErrorPrint("QUIT handler got a message that is not QUIT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
