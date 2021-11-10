package servent.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.WorkWithFiles;
import data.FileObject;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullAnswerMessage;
import servent.message.PullMessage;
import servent.message.UnlockMessage;
import servent.message.util.MessageUtil;

public class PullHandler implements MessageHandler {

	private Message clientMessage;

	public PullHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.PULL) {

				PullMessage pullMessage = (PullMessage) clientMessage;
				int hash = pullMessage.getHash();
				int version = pullMessage.getVersion();
				int chordId = Integer.parseInt(pullMessage.getMessageText());

				if (AppConfig.chordState.isKeyMine(hash)) {
					if (AppConfig.chordState.getValueMap().containsKey(hash)) {
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
										List<File> filesWithHash = AppConfig.chordState.getValueMap().get(hash);
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
								FileObject toSend = new FileObject(WorkWithFiles.getFileWithoutVersion(found.getName()), content, WorkWithFiles.getFileExtension(found));
								
								ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
								PullAnswerMessage pullAnswerMessage = new PullAnswerMessage(
										AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
										nextNode.getListenerPort(), nextNode.getIpAddress(), toSend, chordId, pullVersion);
								
								MessageUtil.sendMessage(pullAnswerMessage);
								
								
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
							
						} else {
							
							ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
							UnlockMessage unlockMessage = new UnlockMessage(
									AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
									nextNode.getListenerPort(), nextNode.getIpAddress(), chordId,"File is doesn't exist in storage");

							MessageUtil.sendMessage(unlockMessage);
						}
					} else {
						ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
						UnlockMessage unlockMessage = new UnlockMessage(
								AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
								nextNode.getListenerPort(), nextNode.getIpAddress(), chordId,
								"File is doesn't exist in storage");

						MessageUtil.sendMessage(unlockMessage);
					}
				} else {

					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);

					PullMessage pullMessageNew = new PullMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							pullMessage.getHash(), pullMessage.getVersion(), chordId);
					MessageUtil.sendMessage(pullMessageNew);

				}

			} else {
				AppConfig.timestampedErrorPrint("Pull handler can handle only PULL messages");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}