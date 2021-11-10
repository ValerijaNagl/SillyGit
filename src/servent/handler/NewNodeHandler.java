package servent.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import app.AppConfig;
import app.ServentInfo;
import app.WorkWithFiles;
import data.HandOverValuesObject;
import mutex.GlobalMutex;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.NewNodeMessage;
import servent.message.SorryMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class NewNodeHandler implements MessageHandler {

	private Message clientMessage;

	public NewNodeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
				int newNodePort = clientMessage.getSenderPort();
				String newNodeIp = clientMessage.getSenderIp();
				ServentInfo newNodeInfo = new ServentInfo(newNodeIp, newNodePort);

				if (AppConfig.chordState.isCollision(newNodeInfo.getChordId())) {
					Message sry = new SorryMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), clientMessage.getSenderPort(),
							clientMessage.getSenderIp());
					MessageUtil.sendMessage(sry);
					return;
				}

				boolean isMyPred = AppConfig.chordState.isKeyMine(newNodeInfo.getChordId());
				if (isMyPred) {

					try {
						AppConfig.lock();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if (!AppConfig.chordState.isKeyMine(newNodeInfo.getChordId())) {
						AppConfig.unlock();
						ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newNodeInfo.getChordId());
						NewNodeMessage nnm = new NewNodeMessage(newNodePort, newNodeIp, nextNode.getListenerPort(),
								nextNode.getIpAddress());
						MessageUtil.sendMessage(nnm);
					}

					ServentInfo hisPred = AppConfig.chordState.getPredecessor();
					if (hisPred == null) {
						hisPred = AppConfig.myServentInfo;
					}

					AppConfig.chordState.setPredecessor(newNodeInfo);

					Map<Integer, ArrayList<File>> myValues = AppConfig.chordState.getValueMap();
					Map<Integer, ArrayList<File>> hisValues = new HashMap<>();

					int myId = AppConfig.myServentInfo.getChordId();
					int hisPredId = hisPred.getChordId();
					int newNodeId = newNodeInfo.getChordId();

					for (Entry<Integer, ArrayList<File>> valueEntry : myValues.entrySet()) {
						if (hisPredId == myId) { 
							if (myId < newNodeId) {
								if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > myId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							} else {
								if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > myId) {

									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							}
						}
						if (hisPredId < myId) { 
							if (valueEntry.getKey() <= newNodeId) {

								hisValues.put(valueEntry.getKey(), valueEntry.getValue());
							}
						} else { 
							if (hisPredId > newNodeId) { 
								if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > hisPredId) {

									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							} else { 
								if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > hisPredId) {

									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							}

						}

					}
					for (Integer key : hisValues.keySet()) { 
						myValues.remove(key);
					}

					// brisemo fajlove koje sada pripadaju novom cvoru
					for (ArrayList<File> files : hisValues.values()) {
						for (File f : files) {
							boolean ret = f.delete();
						}
					}
					AppConfig.chordState.setValueMap(myValues);

					// pravimo handover fajls kako bi novi cvor prepisao kod sebe fajlove
					Map<Integer, ArrayList<HandOverValuesObject>> toSend = new HashMap<>();

					for (Entry<Integer, ArrayList<File>> valueEntry : hisValues.entrySet()) {
						ArrayList<HandOverValuesObject> filesForHash = new ArrayList();
						for (File f : valueEntry.getValue()) {

							String absolutePath = f.getAbsolutePath().replace("\\", "/");
							String[] splitArgs = absolutePath.split("/");

							List<String> contentLines;
							try {
								contentLines = Files.readAllLines(Paths.get(f.getAbsolutePath()));
								String content = WorkWithFiles.getContent(contentLines);

								HandOverValuesObject obj = new HandOverValuesObject(splitArgs[splitArgs.length - 1],
										content);
								filesForHash.add(obj);

							} catch (IOException e) {
								e.printStackTrace();
							}

						}
						toSend.put(valueEntry.getKey(), filesForHash);
					}

					WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), newNodePort, newNodeIp, hisValues, toSend);
					MessageUtil.sendMessage(wm);

				} else { 
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newNodeInfo.getChordId());
					NewNodeMessage nnm = new NewNodeMessage(newNodePort, newNodeIp, nextNode.getListenerPort(),
							nextNode.getIpAddress());
					MessageUtil.sendMessage(nnm);
				}

			} else {
				AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
