package test.timeserver;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import simpleserver.net.server.Request;
import simpleserver.net.server.Response;
import simpleserver.net.server.event.EventAdapter;



public class TimeHandler extends EventAdapter {
	
    public TimeHandler() {
    
    }
    
    public void onWrite(Request request, Response response) throws Exception {
        String command = new String(request.getDataInput());
        String time = null;
        Date date = new Date();

      
        if (command.equals("GB")) {
            DateFormat cnDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, Locale.CHINA);
            time = cnDate.format(date);
        }
        else {
            DateFormat enDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, Locale.US);
            time = enDate.format(date);
        }

        response.send(time.getBytes("UTF-8"));
    }
}
