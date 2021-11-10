package servent.message;

public class RemoveMessage extends BasicMessage {

	
	private static final long serialVersionUID = 1L;
	private final Integer hash;
	

	public RemoveMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int hash,int chordId) {
		
		super(MessageType.REMOVE, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.hash = hash;
	}
	
	
	public Integer getHash() {
		return hash;
	}
	
}
