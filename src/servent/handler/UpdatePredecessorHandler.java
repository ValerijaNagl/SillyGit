package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdatePredecessorMessage;

public class UpdatePredecessorHandler implements MessageHandler {

	private Message clientMessage;

	public UpdatePredecessorHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.UPDATE_PREDECESSOR) {

				UpdatePredecessorMessage setPredecessorMessage = (UpdatePredecessorMessage) clientMessage;
				AppConfig.chordState.setPredecessor(
						new ServentInfo(setPredecessorMessage.getPredecessorIp(), setPredecessorMessage.getPredecessorPort()));
				
				AppConfig.unlock();

			}else {
				System.err.println("UPDATE_PREDECESSOR handler got a message that is not UPDATE_PREDECESSOR");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
