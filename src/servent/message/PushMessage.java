package servent.message;

import data.CommitResponseObject;

public class PushMessage extends BasicMessage {

	private static final long serialVersionUID = 1L;

	private final CommitResponseObject conflictObject;
	private final int hash, version;

	public PushMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int hash,
			CommitResponseObject conflictObject, int chordId, int version) {
		super(MessageType.PUSH, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.conflictObject = conflictObject;
		this.hash = hash;
		this.version = version;
		
	}

	public int getVersion() {
		return version;
	}

	public int getHash() {
		return hash;
	}
	
	public CommitResponseObject getConflictObject() {
		return conflictObject;
	}

}
