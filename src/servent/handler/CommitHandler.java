package servent.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import app.AppConfig;
import app.ServentInfo;
import app.WorkWithFiles;
import data.FileObject;
import data.CommitResponseObject;
import servent.message.CommitMessage;
import servent.message.CommitResponseMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UnlockMessage;
import servent.message.util.MessageUtil;

public class CommitHandler implements MessageHandler {

	private Message clientMessage;

	public CommitHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {
			if (clientMessage.getMessageType() == MessageType.COMMIT) {
				CommitMessage commitMessage = (CommitMessage) clientMessage;

				boolean conflictHappened = false;
				int hash = commitMessage.getHash();
				Integer chordId = Integer.parseInt(commitMessage.getMessageText());
				int version = commitMessage.getVersion();
				FileObject commitObject = commitMessage.getCommitObject();

				if (AppConfig.chordState.isKeyMine(hash)) {
					if (AppConfig.chordState.getValueMap().containsKey(hash)) {
						int max = -1;
						List<File> filesWithHash = AppConfig.chordState.getValueMap().get(hash);
						max = filesWithHash.size() - 1;

						if (max > version) {
							conflictHappened = true;
						} else if (max != -1) {

							File oldFile = filesWithHash.get(filesWithHash.size() - 1);
							List<String> contentLines;
							try {
								contentLines = Files.readAllLines(Paths.get(oldFile.getAbsolutePath()));
								String oldContent = WorkWithFiles.getContent(contentLines);

								if (!oldContent.trim().equals(commitObject.getContent().trim())) {
									max = max + 1;
									File newFile = new File(AppConfig.STORAGE_ABSOLUTE + File.separator
											+ commitObject.getName() + "_" + max + commitObject.getExtension());

									WorkWithFiles.writeToFile(newFile.getAbsolutePath(), commitObject.getContent());
									AppConfig.chordState.getValueMap().get(hash).add(newFile);
								}

							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						if (filesWithHash.get(filesWithHash.size() - 1) != null) {
							File updatedFile = filesWithHash.get(filesWithHash.size() - 1);
							List<String> contentLines;
							try {
								contentLines = Files.readAllLines(Paths.get(updatedFile.getAbsolutePath()));
								String content = WorkWithFiles.getContent(contentLines);
								CommitResponseObject conflictObject = new CommitResponseObject(filesWithHash.size() - 1,
										content, commitObject.getName(), commitObject.getExtension());

								ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);

								CommitResponseMessage commitResponseMessage = new CommitResponseMessage(
										AppConfig.myServentInfo.getListenerPort(),
										AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(),
										nextNode.getIpAddress(), chordId, hash, conflictObject, conflictHappened);

								MessageUtil.sendMessage(commitResponseMessage);

							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
							UnlockMessage unlockMessage = new UnlockMessage(AppConfig.myServentInfo.getListenerPort(),
									AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(),
									nextNode.getIpAddress(), chordId,
									"File is either deleted or doesn't exist in storage!");

							MessageUtil.sendMessage(unlockMessage);
						}
					} else {
						ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
						UnlockMessage unlockMessage = new UnlockMessage(AppConfig.myServentInfo.getListenerPort(),
								AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(),
								nextNode.getIpAddress(), chordId,
								"File is either deleted or doesn't exist in storage!");

						MessageUtil.sendMessage(unlockMessage);
					}
				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
					CommitMessage pm = new CommitMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							hash, commitObject, version, chordId);
					MessageUtil.sendMessage(pm);
				}

			} else {
				AppConfig.timestampedErrorPrint("Commit handler can only handle COMMIT messages");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
