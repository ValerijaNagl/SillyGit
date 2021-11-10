package servent.message;

public class PullMessage extends BasicMessage {

	private static final long serialVersionUID = -4536785660919652205L;
	private final int hash, version;
	
	public PullMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int hash, int version, int chordId) {
		super(MessageType.PULL, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.hash = hash;
		this.version = version;
	}
	
	
	public int getHash() {
		return hash;
	}
	
	public int getVersion() {
		return version;
	}
}
