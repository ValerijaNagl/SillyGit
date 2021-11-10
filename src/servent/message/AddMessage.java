package servent.message;

import data.FileObject;

public class AddMessage extends BasicMessage {

	
	private static final long serialVersionUID = 1448253009574569714L;
	private final int hash;
	private final FileObject addObject;
	

	public AddMessage(int senderPort, String senderIp, int receiverPort, String receiverIp, int hash, FileObject addObject, int chordId) {
		super(MessageType.ADD, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.hash = hash;
		this.addObject = addObject;
	}
	
	public FileObject getAddObject() {
		return addObject;
	}
	
	public int getHash() {
		return hash;
	}
	
}
