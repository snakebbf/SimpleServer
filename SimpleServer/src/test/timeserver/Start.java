package test.timeserver;

import simpleserver.net.server.Notifier;
import simpleserver.net.server.Server;

public class Start {

	public static void main(String[] args) {
		try {
			
			
			LogHandler loger = new LogHandler();
		
			TimeHandler timer = new TimeHandler();
			
			Notifier notifier = Notifier.getNotifier();
			notifier.addListener(loger);
			notifier.addListener(timer);
			
		
			System.out.println("Server starting ...");
			Server server = new Server(5100);
			
			Thread tServer = new Thread(server);
			
			System.out.println(java.nio.channels.spi.SelectorProvider.provider());
			
			tServer.start();

		} catch (Exception e) {
			System.out.println("Server error: " + e.getMessage());
			System.exit(-1);
		}
	}
}
