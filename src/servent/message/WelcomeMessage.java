package servent.message;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.HandOverValuesObject;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<Integer, ArrayList<File>> values;
	private Map<Integer, ArrayList<HandOverValuesObject>> files;

	public WelcomeMessage(int senderPort, String senderIp, int receiverPort, String receiverIp,
			Map<Integer, ArrayList<File>> values, Map<Integer, ArrayList<HandOverValuesObject>> files) {
		super(MessageType.WELCOME, senderPort, senderIp, receiverPort, receiverIp);

		this.values = new HashMap<>(values);
		this.files = new HashMap<>(files);
		

	}

	public Map<Integer, ArrayList<HandOverValuesObject>> getFiles() {
		return files;
	}

	public Map<Integer, ArrayList<File>> getValues() {
		return values;
	}
}
