package app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import data.CommitResponseObject;


public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final String ipAddress;
	private final int listenerPort;
	private final int chordId;
	private Map<Integer, CommitResponseObject> conflicts;
	private Map<String, Integer> versions;
	
	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		this.chordId = ChordState.hash(listenerPort + ipAddress);
		this.conflicts = new HashMap<>();
	}
	
	
	
	public Map<Integer, CommitResponseObject> getConflicts() {
		return conflicts;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public int getChordId() {
		return chordId;
	}
	
	public Map<String, Integer> getVersions() {
		return versions;
	}
	
	@Override
	public String toString() {
		return "[" + chordId + "|" + ipAddress + "|" + listenerPort + "]";
	}

}
