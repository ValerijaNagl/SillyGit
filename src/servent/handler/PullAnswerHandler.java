package servent.handler;

import java.io.File;
import java.io.IOException;

import app.AppConfig;
import app.ServentInfo;
import app.WorkWithFiles;
import data.FileObject;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullAnswerMessage;
import servent.message.util.MessageUtil;

public class PullAnswerHandler implements MessageHandler {

	private Message clientMessage;

	public PullAnswerHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		try {

			if (clientMessage.getMessageType() == MessageType.PULL_ANSWER) {

				PullAnswerMessage pullAnswerMessage = (PullAnswerMessage) clientMessage;

				int chordId = Integer.parseInt(pullAnswerMessage.getMessageText());

				if (AppConfig.myServentInfo.getChordId() == chordId) {
					
					FileObject addObject = pullAnswerMessage.getAddObject();

					File newFile = new File(AppConfig.WORK_ROUTE_ABSOLUTE + File.separator + addObject.getName() + addObject.getExtension());

					try {
						newFile.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					AppConfig.myServentInfo.getVersions().put(addObject.getName(), pullAnswerMessage.getNewVersion());
					WorkWithFiles.writeToFile(newFile.getAbsolutePath(), addObject.getContent());
				
					System.out.println("Pull finished!");
					AppConfig.unlock();
				
				} else {
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
					PullAnswerMessage newPullAnswerMessage = new PullAnswerMessage(AppConfig.myServentInfo.getListenerPort(),
							AppConfig.myServentInfo.getIpAddress(), nextNode.getListenerPort(), nextNode.getIpAddress(),
							pullAnswerMessage.getAddObject(),chordId, pullAnswerMessage.getNewVersion());

					MessageUtil.sendMessage(newPullAnswerMessage);
				}
			}else {
				AppConfig.timestampedErrorPrint("PULL_ANSWER can only handle PULL_ANSWER message");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
