package servent.handler;

import app.AppConfig;
import mutex.GlobalMutex;
import servent.message.Message;
import servent.message.MessageType;

public class HandleLockHandler implements MessageHandler {

	private final Message clientMessage;

	public HandleLockHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.HANDLE_LOCK) {
				GlobalMutex.receiveToken();
			} else {
				AppConfig.timestampedErrorPrint("Handler lock can only handle HANDLE_LOCK message");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
