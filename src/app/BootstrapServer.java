package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BootstrapServer {

	private volatile boolean working = true;
	private List<Integer> ports;
	private List<String> ips;
	
	private class CLIWorker implements Runnable {
		@Override
		public void run() {
			Scanner sc = new Scanner(System.in);
			
			String line;
			while(true) {
				line = sc.nextLine();
				
				if (line.equals("stop")) {
					working = false;
					break;
				}
			}
			
			sc.close();
		}
	}
	
	public BootstrapServer() {
		ports = new ArrayList<Integer>();
		ips = new ArrayList<String>();
	}
	
	public void doBootstrap(int bsPort) {
		Thread cliThread = new Thread(new CLIWorker());
		cliThread.start();
		
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(bsPort);
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e1) {
			AppConfig.timestampedErrorPrint("Problem while opening listener socket.");
			System.exit(0);
		}
		
		Random rand = new Random(System.currentTimeMillis());
		
		while (working) {
			try {
				Socket newServentSocket = listenerSocket.accept();
				
		
				
				Scanner socketScanner = new Scanner(newServentSocket.getInputStream());
				String message = socketScanner.nextLine();
				
				
				if (message.equals("Hail")) {
					String newServentInfo = socketScanner.nextLine();
					
					String serventInfo[] = newServentInfo.split("-");
					
					String ip = serventInfo[1];
					Integer port = null;
					try {
						port = Integer.parseInt(serventInfo[0]);
					} catch (NumberFormatException e) {
						AppConfig.timestampedErrorPrint("Problem reading port. Exiting...");
						System.exit(0);
					}
					
							
					System.out.println("Got " + port + " with ip " + ip );
					PrintWriter socketWriter = new PrintWriter(newServentSocket.getOutputStream());
					
					if (ports.size() == 0 && ips.size()==0) {
						socketWriter.write(String.valueOf(-1) + "\n");
						ports.add(port); 
						ips.add(ip);
					} else {
						int random = rand.nextInt(ports.size());
						
						int randServentPort = ports.get(random);
						String randServentIp = ips.get(random); 
						String toSend = randServentPort+"-"+randServentIp;
						socketWriter.write(toSend + "\n");
					}
					
					socketWriter.flush();
					newServentSocket.close();
				} else if (message.equals("New")) {
					
					String newServentInfo = socketScanner.nextLine();
					
					String serventInfo[] = newServentInfo.split("-");
					
					String ip = serventInfo[1];
					Integer port = null;
					try {
						port = Integer.parseInt(serventInfo[0]);
					} catch (NumberFormatException e) {
						AppConfig.timestampedErrorPrint("Problem reading port. Exiting...");
						System.exit(0);
					}
					
					System.out.println("Adding " + port + " with ip" + ip);
					
					ports.add(port);
					ips.add(ip);
					newServentSocket.close();
				
				}else if(message.equals("Exit")) {
					String exitServentInfo = socketScanner.nextLine();
					
					String serventInfo[] = exitServentInfo.split("-");
					
					String ip = serventInfo[1];
					Integer port = null;
					try {
						port = Integer.parseInt(serventInfo[0]);
					} catch (NumberFormatException e) {
						AppConfig.timestampedErrorPrint("Problem reading port. Exiting...");
						System.exit(0);
					}
					
					
					System.out.println("Exiting " + port);
					
					ports.remove(port);
					ips.remove(ip);
					newServentSocket.close();
					
				}
				
			} catch (SocketTimeoutException e) {
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expects one command line argument - the port to listen on.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			AppConfig.timestampedErrorPrint("Bootstrap started without port argument.");
		}
		
		int bsPort = 0;
		try {
			bsPort = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Bootstrap port not valid: " + args[0]);
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Bootstrap server started on port: " + bsPort);
		
		BootstrapServer bs = new BootstrapServer();
		bs.doBootstrap(bsPort);
	}
}
