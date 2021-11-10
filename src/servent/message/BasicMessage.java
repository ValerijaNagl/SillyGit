package servent.message;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import app.ChordState;

public class BasicMessage implements Message {

	private static final long serialVersionUID = -9075856313609777945L;
	private final MessageType type;
	private final int senderPort;
	private final int receiverPort;
	private final String messageText;
	private final String senderIp;
	private final String receiverIp;
	private static AtomicInteger messageCounter = new AtomicInteger(0);
	private final int messageId;

	public BasicMessage(MessageType type, int senderPort, String senderIp, int receiverPort, String receiverIp) {
		this.type = type;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.senderIp = senderIp;
		this.receiverIp = receiverIp;
		this.messageText = "";

		this.messageId = messageCounter.getAndIncrement();
	}

	public BasicMessage(MessageType type, int senderPort, String senderIp, int receiverPort, String receiverIp,
			String messageText) {
		this.type = type;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.senderIp = senderIp;
		this.receiverIp = receiverIp;
		this.messageText = messageText;

		this.messageId = messageCounter.getAndIncrement();
	}

	@Override
	public MessageType getMessageType() {
		return type;
	}

	@Override
	public int getReceiverPort() {
		return receiverPort;
	}

	@Override
	public String getReceiverIp() {
		return receiverIp;
	}

	@Override
	public String getSenderIp() {
		return senderIp;
	}

	@Override
	public int getSenderPort() {
		return senderPort;
	}

	@Override
	public String getMessageText() {
		return messageText;
	}

	@Override
	public int getMessageId() {
		return messageId;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicMessage) {
			BasicMessage other = (BasicMessage) obj;

			if (getMessageId() == other.getMessageId() && getSenderPort() == other.getSenderPort()) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getSenderPort());
	}

	@Override
	public String toString() {
		return "[" + ChordState.hash(getSenderPort() + "," + getSenderIp()) + "|" + getMessageId() + "|"
				+ getMessageText() + "|" + getMessageType() + "|" + "|"
				+ ChordState.hash(getReceiverPort() + "," + getReceiverIp()) + "]";
	}

}
