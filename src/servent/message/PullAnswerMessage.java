package servent.message;

import data.FileObject;

public class PullAnswerMessage extends BasicMessage {

	private static final long serialVersionUID = 1L;
	private FileObject addObject;
	private final int newVersion;

	public PullAnswerMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, FileObject addObject, int chordId, int newVersion) {
		super(MessageType.PULL_ANSWER, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.addObject = addObject;
		this.newVersion = newVersion;
	}
	
	public FileObject getAddObject() {
		return addObject;
	}

	
	public int getNewVersion() {
		return newVersion;
	}

}
