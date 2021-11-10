package servent.message;

public class HandleLockMessage extends BasicMessage {

	
	private static final long serialVersionUID = 1L;

	public HandleLockMessage(int senderPort, String senderIp, int receiverPort, String receiverIp) {
		super(MessageType.HANDLE_LOCK, senderPort, senderIp, receiverPort, receiverIp);
	}
}
