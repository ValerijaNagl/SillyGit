package servent.message;

public class UpdatePredecessorMessage extends BasicMessage {

	
	private static final long serialVersionUID = -2754306286061208512L;
	private String predecessorIp;
	private int  predecessorPort;
	
	public UpdatePredecessorMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, 
			String predecessorIp, int predecessorPort) {
		super(MessageType.UPDATE_PREDECESSOR, senderPort, senderIp, receiverPort, receiverIp, "");
		this.predecessorIp =predecessorIp;
		this.predecessorPort = predecessorPort;
	}
	
	public String getPredecessorIp() {
		return predecessorIp;
	}
	
	
	public int getPredecessorPort() {
		return predecessorPort;
	}

}
