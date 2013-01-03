package simpleserver.net.server;

import simpleserver.net.server.event.EventAdapter;



/**
 * <p>Title: 服务端事件处理器</p>
 * <p>Description: 处理服务器端受理客户请示的各类事件, 在这里实现具体应用</p>
 * @author snakebbf;starboy
 * @version 1.1
 */

public class ServerHandler extends EventAdapter {
    public ServerHandler() {
    }

    public void onAccept() throws Exception {
        System.out.println("#onAccept()");
    }

    public void onAccepted(Request request) throws Exception {
        System.out.println("#onAccepted()");
    }

    public void onRead(Request request) throws Exception {
        //byte[] rspData = data;
        //if (new String (data).equalsIgnoreCase("query")) {
        //    rspData = new java.util.Date().toString().getBytes();
        //}
        //request.attach(rspData);
        //System.out.println("#onRead()");
    }

    public void onWrite(Request request, Response response) throws Exception {
        //System.out.println("#onWrite()");
        //response.send((byte[])request.attachment());
        //response.send("OK".getBytes());
    }

    public void onClosed(Request request) throws Exception {
        //System.out.println("#onClosed()");
    }

    public void onError(String error) {
        System.out.println("#onAError(): " + error);
    }
}
