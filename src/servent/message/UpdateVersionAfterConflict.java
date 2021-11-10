package servent.message;

public class UpdateVersionAfterConflict extends BasicMessage {

	private static final long serialVersionUID = 1L;
	private final int version;
	private final String file;
	

	public UpdateVersionAfterConflict(int senderPort, String senderIp, int receiverPort, String receiverIp, String file, int version, int chordId) {
		super(MessageType.UPDATE_VERSION_AFTER_CONFLICT, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chordId));
		this.version = version;
		this.file = file;
	}
	
	
	public int getVersion() {
		return version;
	}
	
	public String getFile() {
		return file;
	}
	
}
