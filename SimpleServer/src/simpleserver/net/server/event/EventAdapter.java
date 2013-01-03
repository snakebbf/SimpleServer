package simpleserver.net.server.event;

import simpleserver.net.server.Request;
import simpleserver.net.server.Response;



/**
 * <p>Title: 事件适配器</p>
 * @author snakebbf;starboy
 * @version 1.1
 */

//抽象类做适配器
//默认调用

public abstract class EventAdapter implements ServerListener {
    public EventAdapter() {
    }
    public void onError(String error) {
    }
    public void onAccept() throws Exception {
    }
    public void onAccepted(Request request)  throws Exception {
    }
    public void onRead(Request request)  throws Exception {
    }
    public void onWrite(Request request, Response response)  throws Exception {
    }
    public void onClosed(Request request)  throws Exception{
    }
}
