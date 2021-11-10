package servent.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.HandOverValuesObject;

public class HandOverValuesToSuccessor extends BasicMessage {

	private static final long serialVersionUID = 1L;
	private final Map<Integer, ArrayList<HandOverValuesObject>> toSend;

	public HandOverValuesToSuccessor(int senderPort, String senderIp, int receiverPort, String receiverIp,
			Map<Integer, ArrayList<HandOverValuesObject>> toSend, int chorId) {
		super(MessageType.HAND_OVER_VALUES, senderPort, senderIp, receiverPort, receiverIp, String.valueOf(chorId));
		this.toSend = new HashMap<>(toSend);

	}

	public Map<Integer, ArrayList<HandOverValuesObject>> getToSend() {
		return toSend;
	}

}
