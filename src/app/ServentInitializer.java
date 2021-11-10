package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import mutex.GlobalMutex;
import mutex.GlobalMutex;
import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

public class ServentInitializer implements Runnable {
	
	
	public ServentInitializer() {
		
	}

	private String getSomeServent() {
		int bsPort = AppConfig.BOOTSTRAP_PORT;
		String bsIp = AppConfig.BOOTSTRAP_IP;
		
		String retVal = "-2";
		
		try {
			Socket bsSocket = new Socket(bsIp, bsPort);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			String toSend = String.valueOf(AppConfig.myServentInfo.getListenerPort()) + "-" + AppConfig.myServentInfo.getIpAddress();
			bsWriter.write("Hail\n" + toSend + "\n");
			bsWriter.flush();
			
			Scanner bsScanner = new Scanner(bsSocket.getInputStream());
			retVal = bsScanner.nextLine();
			
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@Override
	public void run() {
		String someServentPortIp = getSomeServent();
		
		if (someServentPortIp.equals("-2")) {
			AppConfig.timestampedErrorPrint("Error in contacting bootstrap. Exiting...");
			System.exit(0);
		}
		if (someServentPortIp.equals("-1")) { 
			GlobalMutex.receiveToken();
			AppConfig.timestampedStandardPrint("First node in Chord system.");
		} else { 
			String serventInfo[] = someServentPortIp.split("-");
			
			String receiverIp = serventInfo[1];
			Integer receiverPort = null;
			try {
				receiverPort = Integer.parseInt(serventInfo[0]);
			} catch (NumberFormatException e) {
				AppConfig.timestampedErrorPrint("Problem reading port. Exiting...");
				
			}
			NewNodeMessage nnm = new NewNodeMessage(AppConfig.myServentInfo.getListenerPort(), 
					AppConfig.myServentInfo.getIpAddress(),
					receiverPort,
					receiverIp);
			MessageUtil.sendMessage(nnm);
			
		
			
		}
	}

}
