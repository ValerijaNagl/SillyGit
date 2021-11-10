package app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import data.CommitResponseObject;
import data.FileObject;
import servent.message.AddMessage;
import servent.message.CommitMessage;
import servent.message.PullMessage;
import servent.message.PushMessage;
import servent.message.RemoveMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class ChordState {

	public static int CHORD_SIZE;

	public static int hash(String forHash) {
		return (forHash.hashCode() > 0 ? forHash.hashCode() : -forHash.hashCode()) % CHORD_SIZE;
	}

	private int chordLevel;
	private ServentInfo[] successorTable;
	private ServentInfo predecessorInfo;
	public List<ServentInfo> allNodeInfo;
	private Map<Integer, ArrayList<File>> valueMap;

	public ChordState() {
		this.chordLevel = 1;
		int tmp = CHORD_SIZE;
		while (tmp != 2) {
			if (tmp % 2 != 0) {
				throw new NumberFormatException();
			}
			tmp /= 2;
			this.chordLevel++;
		}

		successorTable = new ServentInfo[chordLevel];
		for (int i = 0; i < chordLevel; i++) {
			successorTable[i] = null;
		}
		predecessorInfo = null;
		valueMap = new HashMap<>();
		allNodeInfo = new ArrayList<>();
	}

	public void init(WelcomeMessage welcomeMsg) {

		successorTable[0] = new ServentInfo(welcomeMsg.getSenderIp(), welcomeMsg.getSenderPort());
		this.valueMap = welcomeMsg.getValues();

		try {
			Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP, AppConfig.BOOTSTRAP_PORT);

			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			String toSend = AppConfig.myServentInfo.getListenerPort() + "-" + AppConfig.myServentInfo.getIpAddress();
			bsWriter.write("New\n" + toSend + "\n");

			bsWriter.flush();
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setAllNodeInfo(List<ServentInfo> allNodeInfo) {
		this.allNodeInfo = allNodeInfo;
	}

	public int getChordLevel() {
		return chordLevel;
	}

	public ServentInfo[] getSuccessorTable() {
		return successorTable;
	}

	public ServentInfo getNextNode() {
		return successorTable[0];
	}

	public Integer getNextNodePort() {
		if (successorTable[0] == null)
			return null;
		return successorTable[0].getListenerPort();
	}

	public String getNextNodeIp() {
		return successorTable[0].getIpAddress();
	}

	public ServentInfo getPredecessor() {
		return predecessorInfo;
	}

	public void setPredecessor(ServentInfo newNodeInfo) {
		this.predecessorInfo = newNodeInfo;
	}

	public Map<Integer, ArrayList<File>> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<Integer, ArrayList<File>> valueMap) {
		this.valueMap = valueMap;
	}

	public boolean isCollision(int chordId) {
		if (chordId == AppConfig.myServentInfo.getChordId()) {
			return true;
		}
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() == chordId) {
				return true;
			}
		}
		return false;
	}

	public boolean isKeyMine(int key) {
		if (predecessorInfo == null) {
			return true;
		}

		int predecessorChordId = predecessorInfo.getChordId();
		int myChordId = AppConfig.myServentInfo.getChordId();
		System.out.println(" predecesor " + predecessorChordId + " ja sam " + myChordId);
		if (predecessorChordId < myChordId) { // no overflow
			if (key <= myChordId && key > predecessorChordId) {
				return true;
			}
		} else {
			if (key <= myChordId || key > predecessorChordId) {
				return true;
			}
		}

		return false;
	}

	public ServentInfo getNextNodeForKey(int key) {
		if (isKeyMine(key)) {
			return AppConfig.myServentInfo;
		}

		int startInd = 0;

		if (key < AppConfig.myServentInfo.getChordId()) {
			int skip = 1;
			while (successorTable[skip].getChordId() > successorTable[startInd].getChordId()) {
				startInd++;
				skip++;
			}
		}

		int previousId = successorTable[startInd].getChordId();

		for (int i = startInd + 1; i < successorTable.length; i++) {
			if (successorTable[i] == null) {
				AppConfig.timestampedErrorPrint("Couldn't find successor for " + key);
				break;
			}

			int successorId = successorTable[i].getChordId();

			if (successorId >= key) {
				return successorTable[i - 1];
			}
			if (key > previousId && successorId < previousId) { // overflow
				return successorTable[i - 1];
			}
			previousId = successorId;
		}

		return successorTable[0];
	}

	public void updateSuccessorTable() {

		int currentNodeIndex = 0;
		ServentInfo currentNode = allNodeInfo.get(currentNodeIndex);
		successorTable[0] = currentNode;

		int currentIncrement = 2;

		ServentInfo previousNode = AppConfig.myServentInfo;

		for (int i = 1; i < chordLevel; i++, currentIncrement *= 2) {

			int currentValue = (AppConfig.myServentInfo.getChordId() + currentIncrement) % CHORD_SIZE;

			int currentId = currentNode.getChordId();
			int previousId = previousNode.getChordId();

			while (true) {
				if (currentValue > currentId) {

					if (currentId > previousId || currentValue < previousId) {
						previousId = currentId;
						currentNodeIndex = (currentNodeIndex + 1) % allNodeInfo.size();
						currentNode = allNodeInfo.get(currentNodeIndex);
						currentId = currentNode.getChordId();
					} else {
						successorTable[i] = currentNode;
						break;
					}
				} else {
					ServentInfo nextNode = allNodeInfo.get((currentNodeIndex + 1) % allNodeInfo.size());
					int nextNodeId = nextNode.getChordId();
					if (nextNodeId < currentId && currentValue <= nextNodeId) {
						previousId = currentId;
						currentNodeIndex = (currentNodeIndex + 1) % allNodeInfo.size();
						currentNode = allNodeInfo.get(currentNodeIndex);
						currentId = currentNode.getChordId();
					} else {
						successorTable[i] = currentNode;
						break;
					}
				}
			}
		}
	}

	public void addNodes(List<ServentInfo> newNodes) {
		allNodeInfo.addAll(newNodes);

		allNodeInfo.sort(new Comparator<ServentInfo>() {

			@Override
			public int compare(ServentInfo o1, ServentInfo o2) {
				return o1.getChordId() - o2.getChordId();
			}

		});

		List<ServentInfo> newList = new ArrayList<>();
		List<ServentInfo> newList2 = new ArrayList<>();

		int myId = AppConfig.myServentInfo.getChordId();
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() < myId) {
				newList2.add(serventInfo);
			} else {
				newList.add(serventInfo);
			}
		}

		allNodeInfo.clear();
		allNodeInfo.addAll(newList);
		allNodeInfo.addAll(newList2);
		System.out.println("All node info " + allNodeInfo.toString());
		if (newList2.size() > 0) {
			predecessorInfo = newList2.get(newList2.size() - 1);
		} else {
			predecessorInfo = newList.get(newList.size() - 1);
		}

		updateSuccessorTable();
	}

	public void add(int hash, FileObject addObject) {

		if (isKeyMine(hash)) {

			File newFile = new File(AppConfig.STORAGE_ABSOLUTE + AppConfig.STORAGE_ABSOLUTE + File.separator + "-0"
					+ addObject.getExtension());

			try {
				newFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			WorkWithFiles.writeToFile(newFile.getAbsolutePath(), addObject.getContent());
			ArrayList<File> list = new ArrayList<File>();
			list.add(newFile);
			valueMap.put(hash, list);

			AppConfig.unlock();

		} else {
			ServentInfo nextNode = getNextNodeForKey(hash);
			AddMessage addMessage = new AddMessage(AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
					hash, addObject, AppConfig.myServentInfo.getChordId());
			MessageUtil.sendMessage(addMessage);
		}
	}

	public void commit(int hash, FileObject commitObject, int version) {

		if (isKeyMine(hash)) {
			if (valueMap.containsKey(hash)) {
				int max = -1;
				List<File> filesWithHash = valueMap.get(hash);
				max = filesWithHash.size() - 1;

				if (max > version) {

					File lastUpdated = filesWithHash.get(filesWithHash.size() - 1);

					List<String> contentLines;
					try {
						contentLines = Files.readAllLines(Paths.get(lastUpdated.getAbsolutePath()));
						String content = WorkWithFiles.getContent(contentLines);

						CommitResponseObject conflictObject = new CommitResponseObject(max, content,
								commitObject.getName(), commitObject.getExtension());
						AppConfig.myServentInfo.getConflicts().put(hash, conflictObject);
						
						System.out.println("You have a conflict for: ");
						for (Entry<Integer, CommitResponseObject> valueEntry : AppConfig.myServentInfo.getConflicts()
								.entrySet()) {
							System.out.println(valueEntry.getValue().getName());
						}

						System.out.println(
								"Choose one of options if you want to resolve the conflict: view, pull_confl, push");
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (max != -1) {

					File oldFile = filesWithHash.get(filesWithHash.size() - 1);
					List<String> contentLines;
					try {
						contentLines = Files.readAllLines(Paths.get(oldFile.getAbsolutePath()));
						String oldContent = WorkWithFiles.getContent(contentLines);

						if (!oldContent.trim().equals(commitObject.getContent().trim())) {
							max = max + 1;
							File newFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator + commitObject.getName()
									+ "-" + max + commitObject.getExtension());

							WorkWithFiles.writeToFile(newFile.getAbsolutePath(), commitObject.getContent());

							AppConfig.myServentInfo.getVersions().put(commitObject.getName(), max);
							AppConfig.chordState.getValueMap().get(hash).add(newFile);
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			} else {
				System.out.println("File is deleted or doesn't exist in storage!");
			}

			AppConfig.unlock();

		} else {
			ServentInfo nextNode = getNextNodeForKey(hash);
			CommitMessage commitMessage = new CommitMessage(AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(), hash,
					commitObject, version, AppConfig.myServentInfo.getChordId());
			MessageUtil.sendMessage(commitMessage);
		}
	}

	public void push(int hash, CommitResponseObject conflictObject, int version) {

		if (isKeyMine(hash)) {
			if (valueMap.containsKey(hash)) {

				List<File> filesWithHash = valueMap.get(hash);

				File commitFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator
						+ WorkWithFiles.getFileWithoutVersion(conflictObject.getName()) + "-" + filesWithHash.size()
						+ conflictObject.getExtension());

				WorkWithFiles.writeToFile(commitFile.getAbsolutePath(), conflictObject.getContent());
				getValueMap().get(hash).add(commitFile);

				AppConfig.myServentInfo.getVersions().put(conflictObject.getName(), filesWithHash.size());
				System.out.println("Push done!");
				AppConfig.unlock();
			} else {
				System.out.println("File is deleted or doesn't exist in storage!");
			}
			AppConfig.unlock();
		} else {
			ServentInfo nextNode = getNextNodeForKey(hash);
			PushMessage pm = new PushMessage(AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(), hash,
					conflictObject, AppConfig.myServentInfo.getChordId(), conflictObject.getVersion());
			MessageUtil.sendMessage(pm);
		}
	}

	public void pull(int hash, int version) {
		if (AppConfig.chordState.isKeyMine(hash)) {
			if (valueMap.containsKey(hash)) {

				File found = null;
				File storage = new File(AppConfig.STORAGE_ABSOLUTE + File.separator);
				File[] files = storage.listFiles();

				int pullVersion = -1;
				for (int i = 0; i < files.length; i++) {

					String relative = WorkWithFiles.relativePath(
							WorkWithFiles.getFileWithoutVersion(files[i].getAbsolutePath()), storage.getAbsolutePath());

					if (!relative.equals("")) {
						if (ChordState.hash(relative) == hash) {
							if (!files[i].isDirectory()) {
								List<File> filesWithHash = valueMap.get(hash);
								if (version == -1) {
									if (filesWithHash.size() > 0) {
										found = filesWithHash.get(filesWithHash.size() - 1);
										pullVersion = filesWithHash.size() - 1;
									}

								} else {
									if (filesWithHash.size() > 0)
										found = filesWithHash.get(version);
								}
							}
						}
					}
				}

				if (found != null) {
					List<String> contentLines;
					try {
						contentLines = Files.readAllLines(Paths.get(found.getAbsolutePath()));
						String content = WorkWithFiles.getContent(contentLines);
						File newFile = new File(AppConfig.WORK_ROUTE_ABSOLUTE + File.separator
								+ WorkWithFiles.getFileWithoutVersion(found.getName())
								+ WorkWithFiles.getFileExtension(found));

						WorkWithFiles.writeToFile(newFile.getAbsolutePath(), content);
						AppConfig.myServentInfo.getVersions().put(found.getName(), pullVersion);
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					System.out.println("File with that version isn't found.");

				}
			} else {
				System.out.println("File is deleted or doesn't exist in storage!");
			}
			AppConfig.unlock();

		} else {

			ServentInfo nextNode = getNextNodeForKey(hash);
			PullMessage pullMessage = new PullMessage(AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(), hash,
					version, AppConfig.myServentInfo.getChordId());
			MessageUtil.sendMessage(pullMessage);

		}
	}

	public void remove(int hash) {

		if (isKeyMine(hash)) {
			if (valueMap.containsKey(hash)) {
				File storage = new File(AppConfig.STORAGE_ABSOLUTE + File.separator);
				File[] files = storage.listFiles();

				for (int i = 0; i < files.length; i++) {

					String relative = WorkWithFiles.relativePath(
							WorkWithFiles.getFileWithoutVersion(files[i].getAbsolutePath()), storage.getAbsolutePath());

					if (!relative.equals("")) {

						if (ChordState.hash(relative) == hash) {
							boolean ret = files[i].delete();
						}
					}
				}
				AppConfig.chordState.getValueMap().remove(hash);
			} else {
				System.out.println("File is deleted or doesn't exist in storage!");
			}
			AppConfig.unlock();

		} else {
			ServentInfo nextNode = getNextNodeForKey(hash);
			RemoveMessage rm = new RemoveMessage(AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(), hash,
					AppConfig.myServentInfo.getChordId());
			MessageUtil.sendMessage(rm);
		}
	}

}
