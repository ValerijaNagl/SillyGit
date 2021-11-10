package servent.message;

public class UnlockMessage extends BasicMessage {

	private static final long serialVersionUID = 1L;
	private String print;


	public UnlockMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int chordId,
			String print) {

		super(MessageType.UNLOCK, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));

		this.print = print;
		

	}

	
	public String getPrint() {
		return print;
	}

	

}
