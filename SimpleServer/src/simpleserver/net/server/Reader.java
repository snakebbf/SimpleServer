package simpleserver.net.server;

import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * <p>Title: 读线程</p>
 * <p>Description: 该线程用于读取客户端数据</p>
 * @author snakebbf;starboy
 * @version 1.1
 */

public class Reader extends Thread {
	private Server server;
    private BlockingQueue<SelectionKey> rpool;
    private Notifier notifier;

    public Reader(Server server) {
    	this.notifier = server.notifier;
    	this.server = server;
    	this.rpool = server.rpool;
    }

    public void run() {
        while (true) {
            try {
                SelectionKey key = rpool.take();
                // 读取数据
                read(key);
            }
            catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * 读取客户端发出请求数据
     * @param sc 套接通道
     */
    private static int BUFFER_SIZE = 1024;
    public static byte[] readRequest(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int off = 0; //实际总读取的字节长度
        int r = 0; //每次buffer读取的字节长度
        byte[] data = new byte[BUFFER_SIZE * 10];
        while ( true ) {
            buffer.clear();
            //从sc中读取数据到buffer
            r = sc.read(buffer);
            if (r == -1) {
            	break;
            }
            
            //将数据读取到data中,data大小不足进行扩容
            if ( (off + r) > data.length) {
                data = grow(data, BUFFER_SIZE * 10);
            }
            //buffer数据copy到data中
            byte[] buf = buffer.array();
            System.arraycopy(buf, 0, data, off, r);
            off = off+r;
        }

    	
        //返回实际的总读取数据
        byte[] req = new byte[off];
        System.arraycopy(data, 0, req, 0, off);
        return req;
    }

    /**
     * 处理连接数据读取
     * @param key SelectionKey
     */
    public void read(SelectionKey key) {
        try {
            // 读取客户端数据
            SocketChannel sc = (SocketChannel) key.channel();
            byte[] clientData =  readRequest(sc);

            Request request = (Request)key.attachment();
            request.setDataInput(clientData);

            // 触发onRead
            notifier.fireOnRead(request);
            

            // 提交主控线程进行写处理
            KeyAndOps keyWOps = new KeyAndOps(key,SelectionKey.OP_WRITE);
             
            server.processKeyOps(keyWOps);
            
        }
        catch (Exception e) {
        	e.printStackTrace();
            notifier.fireOnError("Error occured in Reader: " + e.getMessage());
        }
    }
    
    /**
     * 数组扩容
     * @param src byte[] 源数组数据
     * @param size int 扩容的增加量
     * @return byte[] 扩容后的数组
     */
    public static byte[] grow(byte[] src, int size) {
        byte[] tmp = new byte[src.length + size];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }
}
