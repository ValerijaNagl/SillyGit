package servent.message;

import data.CommitResponseObject;

public class CommitResponseMessage extends BasicMessage {

	private static final long serialVersionUID = 1L;
	private final int hash;
	private CommitResponseObject conflictObject;
	private boolean conflictHappened;

	public CommitResponseMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int chordId, int hash,
			CommitResponseObject conflictObject, boolean conflictHappened) {
		super(MessageType.COMMIT_RESPONSE, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.hash = hash;
		this.conflictObject = conflictObject;
		this.conflictHappened = conflictHappened;
	}
	
	public boolean isConflictHappened() {
		return conflictHappened;
	}
	
	public int getHash() {
		return hash;
	}

	public CommitResponseObject getConflictObject() {
		return conflictObject;
	}
		
}
