package test.timeserver;

import java.util.Date;

import simpleserver.net.server.Request;
import simpleserver.net.server.event.EventAdapter;




/**
 * ��־��¼
 */
public class LogHandler extends EventAdapter {
	public LogHandler() {
	}

	public void onClosed(Request request) throws Exception {
		String log = new Date().toString() + " from "
				+ request.getAddress().toString();
		System.out.println(log);
	}

	public void onError(String error) {
		System.out.println("Error: " + error);
	}
	

}
