package net.frontlinesms.messaging;

import java.util.*;

import serial.*;

public class AllModemsDetector {
//> INSTANCE PROPERTIES
	private Logger log = new Logger(getClass());
	private Map<String, ATDeviceDetector> detectors;
	private ATDeviceDetectorListener listener;
	
//> ACCESSORS
	public void setListener(ATDeviceDetectorListener listener) {
		this.listener = listener;
	}

//> DETECTION METHODS
	/** Trigger detection, and return the results when it is completed. */
	public ATDeviceDetector[] detectBlocking() {
		refresh();
		waitUntilDetectionComplete();
		return getDetectors();
	}
	
	/** Trigger detection for fresh ports, and restart any finished detectors. */
	public synchronized void refresh() {
		System.out.println("Refreshing detectors...");
		if(detectors == null) {
			detectors = new HashMap<String, ATDeviceDetector>();
		}
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()) {
			CommPortIdentifier port = ports.nextElement();
			if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ATDeviceDetector d = detectors.get(port.getName());
				if(d != null && !d.isFinished()) {
					System.out.println("Already detecting on port: " + port.getName());
				} else {
					System.out.println("Beginning detection for serial port: " + port.getName());
					d = new ATDeviceDetector(port, listener);
					detectors.put(port.getName(), d);
					d.start();
				}
			} else {
				System.out.println("Ignoring non-serial port: " + port.getName());
			}
		}
		System.out.println("All detectors refreshed.");
	}
	
	public synchronized void reset() {
		if(detectors!=null) for(ATDeviceDetector d : getDetectors()) {
			d.interrupt();
		}
		detectors = null;
	}

//> ACCESSORS
	/** Get the detectors. */
	public synchronized ATDeviceDetector[] getDetectors() {
		if(detectors == null) {
			return new ATDeviceDetector[0];
		} else {
			return detectors.values().toArray(new ATDeviceDetector[detectors.size()]);
		}
	}
	
//> STATIC HELPER METHODS	
	/** Blocks until all detectors have completed execution. */
	private void waitUntilDetectionComplete() {
		ATDeviceDetector[] detectors = getDetectors();
		boolean completed = true;
		do {
			for (ATDeviceDetector portDetector : detectors) {
				if(!portDetector.isFinished()) {
					completed = false;
				}
			}
			Utils.sleep(500);
		} while(!completed);
	}
}
