package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import mutex.GlobalMutex;
import servent.SimpleServentListener;



public class AppConfig {

	
	public static ServentInfo myServentInfo;
	public static ReentrantLock lock = new ReentrantLock();
	public static SimpleServentListener simpleServentListener;
	
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();

		System.out.println(timeFormat.format(now) + " - " + message);
	}

	
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();

		System.err.println(timeFormat.format(now) + " - " + message);
	}

	public static boolean INITIALIZED = false;
	public static int BOOTSTRAP_PORT;
	public static String BOOTSTRAP_IP;
	public static int SERVENT_COUNT;
	public static String STORAGE;
	public static String WORK_ROUTE;
	public static String STORAGE_ABSOLUTE;
	public static String WORK_ROUTE_ABSOLUTE;
	public static ChordState chordState;

	
	public static void readConfig(String configName, int serventId) {

		Properties properties = new Properties();
		
		String myIpAddress = "";
        try {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
  
            BufferedReader sc =
            new BufferedReader(new InputStreamReader(url_name.openStream()));
  
            myIpAddress = sc.readLine().trim();
        }catch (Exception e){
        	myIpAddress = "Cannot Execute Properly";
        }

		try {
			properties.load(new FileInputStream(new File(configName)));

		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}

		try {
			BOOTSTRAP_PORT = Integer.parseInt(properties.getProperty("bs.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_port. Exiting...");
			System.exit(0);
		}

		try {
			BOOTSTRAP_IP = myIpAddress;
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_ip. Exiting...");
			System.exit(0);
		}

		try {
			SERVENT_COUNT = Integer.parseInt(properties.getProperty("servent_count"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading servent_count. Exiting...");
			System.exit(0);
		}

		try {
			int chordSize = Integer.parseInt(properties.getProperty("chord_size"));

			ChordState.CHORD_SIZE = chordSize;
			chordState = new ChordState();

		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading chord_size. Must be a number that is a power of 2. Exiting...");
			System.exit(0);
		}

		

		int serventPort = -1;
		
		String portProperty = "servent" + serventId + ".port";
		
		try {
			serventPort = Integer.parseInt(properties.getProperty(portProperty));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading " + portProperty + ". Exiting...");
			System.exit(0);
		}

		myServentInfo = new ServentInfo(myIpAddress, serventPort);

		STORAGE = properties.getProperty("storage.route" + serventId);
		WORK_ROUTE = properties.getProperty("work.route" + serventId);

		STORAGE_ABSOLUTE = (System.getProperty("user.dir")) + "/chord/" + STORAGE;
		WORK_ROUTE_ABSOLUTE = (System.getProperty("user.dir")) + "/chord/" + WORK_ROUTE;

		File storageProjectDir = new File(STORAGE_ABSOLUTE);
		File workProjectDir = new File(WORK_ROUTE_ABSOLUTE);

		if (!storageProjectDir.exists()) {
			try {
				storageProjectDir.mkdir();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}
		if (!workProjectDir.exists()) {
			try {
				workProjectDir.mkdir();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}
		
		Test test = new Test();
	}

	public static void unlock() {
		GlobalMutex.unlock();
		AppConfig.lock.unlock();
	}
	
	public static void lock() throws InterruptedException {
		lock.lock();
		GlobalMutex.lock();
	}
}
