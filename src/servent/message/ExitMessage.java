package servent.message;

public class ExitMessage extends BasicMessage {

	private static final long serialVersionUID = 1L;

	public ExitMessage(int senderPort, String senderIp, int receiverPort, String receiverIp,
			int chordId) {
		super(MessageType.QUIT, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
	}

}
