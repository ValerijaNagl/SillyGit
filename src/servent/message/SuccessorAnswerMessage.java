package servent.message;

public class SuccessorAnswerMessage extends BasicMessage {
	
	private static final long serialVersionUID = 1L;

	public SuccessorAnswerMessage(int senderPort, String senderIp, int receiverPort, String receiverIp,
			int chordId) {
		super(MessageType.SUCCESSOR_ANSWER, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));

	}

}
