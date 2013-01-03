package test.timeserver;


import java.net.Socket;
import java.io.*;


public class Client {
    public Client() {
    }

    public static void main(String[] args) {
        Socket client = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            client = new Socket("localhost", 5100);

            out = new DataOutputStream( (client.getOutputStream()));
           
            String query = "GB";
            byte[] request = query.getBytes();
            out.write(request);
            out.flush();
            client.shutdownOutput();
            
            in = new DataInputStream(client.getInputStream());
            byte[] reply = new byte[100];
            in.read(reply);
            System.out.println("Time: " + new String(reply, "UTF-8"));
            
            in.close();
            out.close();
            client.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
