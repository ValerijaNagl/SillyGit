package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import servent.handler.AddHandler;
import servent.handler.PullHandler;
import servent.handler.CommitHandler;
import servent.handler.CommitResponseHandler;
import servent.handler.SuccessorAnswerGiveMyFilesAcceptedHandler;
import servent.handler.HandOverValuesToSuccessorHandler;
import servent.handler.MessageHandler;
import servent.handler.NewNodeHandler;
import servent.handler.NullHandler;
import servent.handler.PushHandler;
import servent.handler.ExitHandler;
import servent.handler.UnlockHandler;
import servent.handler.RemoveHandler;
import servent.handler.UpdatePredecessorHandler;
import servent.handler.SorryHandler;
import servent.handler.PullAnswerHandler;
import servent.handler.HandleLockHandler;
import servent.handler.UpdateHandler;
import servent.handler.WelcomeHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	public SimpleServentListener() {
	}

	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
		
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				switch (clientMessage.getMessageType()) {
				case NEW_NODE:
					messageHandler = new NewNodeHandler(clientMessage);
					break;
				case WELCOME:
					messageHandler = new WelcomeHandler(clientMessage);
					break;
				case SORRY:
					messageHandler = new SorryHandler(clientMessage);
					break;
				case UPDATE:
					messageHandler = new UpdateHandler(clientMessage);
					break;
				case ADD:
					messageHandler = new AddHandler(clientMessage);
					break;
				case PULL:
					messageHandler = new PullHandler(clientMessage);
					break;
				case PULL_ANSWER:
					messageHandler = new PullAnswerHandler(clientMessage);
					break;
				case HANDLE_LOCK:
					messageHandler = new HandleLockHandler(clientMessage);
					break;
				case UNLOCK:
					messageHandler = new UnlockHandler(clientMessage);
					break;
				case COMMIT:
					messageHandler = new CommitHandler(clientMessage);
					break;
				case REMOVE:
					messageHandler = new RemoveHandler(clientMessage);
					break;
				case QUIT:
					messageHandler = new ExitHandler(clientMessage);
					break;
				case HAND_OVER_VALUES:
					messageHandler = new HandOverValuesToSuccessorHandler(clientMessage);
					break;
				case SUCCESSOR_ANSWER:
					messageHandler = new SuccessorAnswerGiveMyFilesAcceptedHandler(clientMessage);
					break;
				case UPDATE_PREDECESSOR:
					messageHandler = new UpdatePredecessorHandler(clientMessage);
					break;
				case PUSH:
					messageHandler = new PushHandler(clientMessage);
					break;
				case COMMIT_RESPONSE:
					messageHandler = new CommitResponseHandler(clientMessage);
					break;
				case UPDATE_VERSION_AFTER_CONFLICT:
					messageHandler = new UpdateHandler(clientMessage);
					break;
				case POISON:
					break;
				}
				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
