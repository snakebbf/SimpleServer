package simpleserver.net.server;

import java.nio.channels.SelectionKey;

/**
 * <p>Title: 事件操作封装类</p>
 * @author snakebbf;starboy
 * @version 1.0
 */
public class KeyAndOps {
    
	public static final int OP_READ = SelectionKey.OP_ACCEPT;
    public static final int OP_WRITE = SelectionKey.OP_WRITE;
    public static final int OP_CLOSE = 1 << 5;
	
    private SelectionKey key;
	private int ops;
	
	public KeyAndOps(SelectionKey key,int ops){
		this.key = key;
		this.ops = ops;
	}
	
	public SelectionKey getKey() {
		return key;
	}
	public void setKey(SelectionKey key) {
		this.key = key;
	}
	public int getOps() {
		return ops;
	}
	public void setOps(int ops) {
		this.ops = ops;
	}


}
