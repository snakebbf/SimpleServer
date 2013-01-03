package simpleserver.net.server;

import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

/**
 * <p>Title: 回应线程</p>
 * <p>Description: 用于向客户端发送信息</p>
 * @author snakebbf;starboy
 * @version 1.1
 */

public final class Writer extends Thread {
	private BlockingQueue<SelectionKey> wpool;
	private Server server;
    private Notifier notifier ;

    public Writer(Server server) {
    	this.server = server;
    	this.notifier = server.notifier.getNotifier();
    	this.wpool = server.wpool;
    }

    /**
     * SMS发送线程主控服务方法,负责调度整个处理过程
     */
    public void run() {
    	while(true){
    		try{
		    	SelectionKey key=wpool.take();
		        write(key);
    		}catch (Exception e) {
    			continue;
    		}
    	}
    }

    /**
     * 处理向客户发送数据
     * @param key SelectionKey
     * 
     */
    public void write(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Response response = new Response(sc);

            // 触发onWrite事件
            notifier.fireOnWrite((Request)key.attachment(), response);
            
            // 触发onClosed事件
            notifier.fireOnClosed((Request)key.attachment());
         
            
            KeyAndOps keyOps = new KeyAndOps(key,KeyAndOps.OP_CLOSE);
            server.processKeyOps(keyOps);
            
        }
        catch (Exception e) {
        	
        	e.printStackTrace();
        	
            notifier.fireOnError("Error occured in Writer: " + e.getMessage());
        }
    }

}
