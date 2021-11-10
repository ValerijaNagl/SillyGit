package servent.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import app.AppConfig;
import app.ServentInfo;
import app.WorkWithFiles;
import data.HandOverValuesObject;
import servent.message.SuccessorAnswerMessage;
import servent.message.HandOverValuesToSuccessor;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class HandOverValuesToSuccessorHandler implements MessageHandler {

	private Message clientMessage;

	public HandOverValuesToSuccessorHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.HAND_OVER_VALUES) {

				HandOverValuesToSuccessor handOverValuesToSuccessor = (HandOverValuesToSuccessor) clientMessage;
				int chordId = Integer.parseInt(handOverValuesToSuccessor.getMessageText());
				Map<Integer, ArrayList<HandOverValuesObject>> map = new HashMap<>(
						handOverValuesToSuccessor.getToSend());

				for (Entry<Integer, ArrayList<HandOverValuesObject>> valueEntry : map.entrySet()) {
					ArrayList<HandOverValuesObject> list = valueEntry.getValue();
					ArrayList<File> newValues = new ArrayList<>();
					for (HandOverValuesObject object : list) {

						File newFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator + object.getName());

						try {
							newFile.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						WorkWithFiles.writeToFile(newFile.getAbsolutePath(), object.getContent());
						newValues.add(newFile);

					}
					AppConfig.chordState.getValueMap().put(valueEntry.getKey(), newValues);

				}

				ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
				SuccessorAnswerMessage successorAnswerMessage = new SuccessorAnswerMessage(
						AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
						nextNode.getListenerPort(), nextNode.getIpAddress(), chordId);
				MessageUtil.sendMessage(successorAnswerMessage);

			} else {
				AppConfig.timestampedErrorPrint("HAND_OVER_VALUES can only handle HAND_OVER_VALUES message");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
