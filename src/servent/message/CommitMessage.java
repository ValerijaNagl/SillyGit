package servent.message;

import data.FileObject;

public class CommitMessage extends BasicMessage {

	
	private static final long serialVersionUID = -2452874439664150424L;
	private final FileObject commitObject;
	private final int version, hash;

	public CommitMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int hash,
			FileObject commitObject, int version, int chordId) {

		super(MessageType.COMMIT, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.commitObject = commitObject;
		this.hash = hash;
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public int getHash() {
		return hash;
	}

	public FileObject getCommitObject() {
		return commitObject;
	}

}
