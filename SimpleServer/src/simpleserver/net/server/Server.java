package simpleserver.net.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * Title: 主控服务线程
 * </p>
 * 
 * @author snakebbf;starboy
 * @version 1.1
 */

public class Server implements Runnable {
	protected BlockingQueue<SelectionKey> wpool = new LinkedBlockingQueue<SelectionKey>();
	protected BlockingQueue<SelectionKey> rpool = new LinkedBlockingQueue<SelectionKey>();
	private BlockingQueue<KeyAndOps> keyOpsPool = new LinkedBlockingQueue<KeyAndOps>(); // 回应池
	private Selector selector;
	private ServerSocketChannel sschannel;
	private InetSocketAddress address;
	protected Notifier notifier;
	private int port;
	private AtomicBoolean wakenUp=new AtomicBoolean();
	/**
	 * 创建主控服务线程
	 * 
	 * @param port
	 *            服务端口
	 * @throws java.lang.Exception
	 */
	private static int MAX_THREADS = 4;

	public Server(int port) throws Exception {
		this.port = port;

		// 获取事件触发器
		notifier = Notifier.getNotifier();

		// 创建读写线程池
		for (int i = 0; i < MAX_THREADS; i++) {
			Thread r = new Reader(this);
			Thread w = new Writer(this);
			r.start();
			w.start();
		}

		// 创建无阻塞网络套接
		selector = Selector.open();
		sschannel = ServerSocketChannel.open();
		sschannel.configureBlocking(false);
		address = new InetSocketAddress(port);
		ServerSocket ss = sschannel.socket();
		ss.setReuseAddress(true);
		ss.bind(address);
		sschannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void run() {
		System.out.println("Server started ...");
		System.out.println("Server listening on port: " + port);
		// 监听
		while (true) {
			try {
				wakenUp.set(false);
				int num = 0;
				num = selector.select(); // 阻塞直到,事件,wakeup被调用,或者IO close异常

				if (num > 0) {
					Set selectedKeys = selector.selectedKeys();
					Iterator it = selectedKeys.iterator();

					while (it.hasNext()) { // Set非线程安全
						SelectionKey key = (SelectionKey) it.next();

						// 将此key从set中移除
						it.remove();
						// 处理IO事件
						if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
							// Accept the new connection
							ServerSocketChannel ssc = (ServerSocketChannel) key
									.channel();
							notifier.fireOnAccept();

							SocketChannel sc = ssc.accept();
							sc.configureBlocking(false);
							
							//存在问题
							
						
							// 触发接受连接事件
							Request request = new Request(sc);
							notifier.fireOnAccepted(request);

							// 注册读操作,以进行下一步的读操作
							// 将request作为attachment
							sc.register(selector, SelectionKey.OP_READ, request);

						} else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
							// 线程池处理写事件
							wpool.add(key);
							//Writer.processRequest(key); // 提交写服务线程向客户端发送回应数据
							key.interestOps(1);
							key.cancel();
						}else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
							// 线程池处理读事件
							//	Reader.processRequest(key); // 提交读服务线程读取客户端数据
							rpool.add(key);
							key.cancel();

						} 
					}

				} else {
					// 此处处理有问题,wakeup 没有事件因此注册写事件
					HandleAllKeyOps(); // 在Selector中注册新的写通道
				}
			} catch (Exception e) {
				e.printStackTrace();
				notifier.fireOnError("Error occured in Server: "
						+ e.getMessage());
				continue;
			}
		}
	}

	/**
	 * 添加新的通道注册
	 */
	private void HandleAllKeyOps() {
		KeyAndOps keyOps;
		while ((keyOps = (KeyAndOps) keyOpsPool.poll()) != null) {
			HanleKeyOps(keyOps);
		}
	}

	/**
	 * 处理新的Socket动作
	 * 
	 * */
	private void HanleKeyOps(KeyAndOps keyOps) {

		SelectionKey key = keyOps.getKey();
		int ops = keyOps.getOps();
		SocketChannel schannel = (SocketChannel) key.channel();
		try {

			if ((ops & KeyAndOps.OP_WRITE) > 0) {
				schannel.register(selector, SelectionKey.OP_WRITE,
						key.attachment());
			}

			if ((ops & KeyAndOps.OP_READ) > 0) {
				schannel.register(selector, SelectionKey.OP_READ,
						key.attachment());
			}

			if ((ops & KeyAndOps.OP_CLOSE) > 0) {
				SocketChannel sc = (SocketChannel) key.channel();
				sc.finishConnect();
				sc.socket().close();
				sc.close();
			}

		} catch (Exception e) {
			try {
				schannel.finishConnect();
				schannel.close();
				schannel.socket().close();
				notifier.fireOnClosed((Request) key.attachment());
			} catch (Exception e1) {
			}
			notifier.fireOnError("Error occured in addRegister: "
					+ e.getMessage());
		}

	}

	/**
	 * 提交新的客户端写请求于主服务线程的回应池中
	 * 
	 * @throws InterruptedException
	 */
	public void processKeyOps(KeyAndOps keyOps)
			throws InterruptedException {
		// wpool.add(wpool.size(), key);
		keyOpsPool.put(keyOps);
		
		//wakeup
		if(wakenUp.compareAndSet(false,true)){
			selector.wakeup();
		}
		
	}
	
	
}
