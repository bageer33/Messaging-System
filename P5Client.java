import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CS180 Project 5
 *
 * This program holds the P5Client Class for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */

public class P5Client
{
    private String hostName;
    private int portNumber;
    public Socket receiveSocket;
    public P5Client(String hostName, int portNumber)
    {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public boolean isUsernameAndPasswordCorrect(String userName, String password)
    {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {

            // connect to server
            Socket socket = new Socket(this.hostName, this.portNumber);
            //  flush
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush(); // ensure data is sent to the client

            // construct whold message
            String msgSend = String.format( "%s;%s;%s", "CMDLoginConsumer", userName, password);

            // send data to server
            oos.writeObject(msgSend);
            oos.flush();
            System.out.println("client send to server: " + msgSend);

            // read content from server
            Object r = ois.readObject();
            String msgRcv = (String)r;
            System.out.println("received from server: " + msgRcv);
            String[] tokens = msgRcv.split(";");
            String cmd = tokens[0];
            String content = msgRcv.substring(cmd.length()+1);
            System.out.println("client received cmd = "+cmd+", content="+content);

            if(cmd.equals("CMDLoginConsumerSuccess")==true) {
                return true;
            }

        } catch (Exception e)
        {


        }
    return false;
    }



    public ArrayList<String> getContacts(String userName)
    {
        ArrayList<String> contactNames = new ArrayList<>();
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            // connect to server
            Socket socket = new Socket(this.hostName, this.portNumber);
            //  flush
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush(); // ensure data is sent to the client

            // construct whold message
            String msgSend = String.format( "%s;%s", "CMDGetContacts", userName);

            // send data to server
            oos.writeObject(msgSend);
            oos.flush();
            System.out.println("client send to server: " + msgSend);

            // read content from server
            Object r = ois.readObject();
            String msgRcv = (String)r;
            System.out.println("received from server: " + msgRcv);
            String[] tokens = msgRcv.split(";");
            String cmd = tokens[0];
            String content = msgRcv.substring(cmd.length()+1);
            System.out.println("client received cmd = "+cmd+", content="+content);

            String[] tokens2 = content.split(";");
            for(int i=0;i<tokens2.length;i++)
            {
                contactNames.add( tokens2[i] );
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return contactNames;
    }

    public Socket connectAndGetRcvSocket(String username)
    {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        // connect to server
        try {
            Socket socket = new Socket(this.hostName, this.portNumber);
            this.receiveSocket = socket;
            //  flush
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush(); // ensure data is sent to the client

            // construct whole message
            String msgSend = String.format( "%s;%s", "CMDRegistRcv", username);

            // send data to server
            oos.writeObject(msgSend);
            oos.flush();
            System.out.println("client send to server: " + msgSend);
            // read content from server
            Object r = ois.readObject();
            String msgRcv = (String)r;
            System.out.println("received from server: " + msgRcv);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.receiveSocket;
    }

    public String sendMsg(String from, String to, String msg)
    {
        ArrayList<String> contactNames = new ArrayList<>();
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {

            // connect to server
            Socket socket = new Socket(this.hostName, this.portNumber);
            //  flush
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush(); // ensure data is sent to the client

            // construct whold message
            String msgSend = String.format( "%s;%s;%s;%s", "CMDSendMsg", from, to, msg);

            // send data to server
            oos.writeObject(msgSend);
            oos.flush();
            System.out.println("client send to server: " + msgSend);

            // read content from server
            Object r = ois.readObject();
            String msgRcv = (String)r;
            System.out.println("received from server: " + msgRcv);
            String[] tokens = msgRcv.split(";");
            String cmd = tokens[0];
            String content = msgRcv.substring(cmd.length()+1);
            System.out.println("client received cmd = "+cmd+", content="+content);

            return "success";

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "fail";
    }



    public static void main(String[] args)
    {
        P5Client client = new P5Client("localhost", 4242);
        // test auth
        boolean r = client.isUsernameAndPasswordCorrect("aaa", "123");
        System.out.println("isAuthSuccess="+r);
        // test get contacts
        ArrayList<String> r2 = client.getContacts("aaa");
        System.out.println("getContacts="+r2);
        // test send message
        String r3 = client.sendMsg("C1", "S2", "hello aaa bbb ccc");

    }


    public static void mainTest(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            String hostName = "localhost";
            int portNumber = 4242;

            // connect to server
            Socket socket = new Socket(hostName, portNumber);

            //  flush
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush(); // ensure data is sent to the client


            while (true)
            {
                System.out.println("input message for send to server, 'exit' for exit");
                String msg = scanner.next();

                // construct whold message
                String msgSend = "CMDmsg;"+msg;
                if (msg.equals("exit")==true)
                {
                    msgSend = "CMDExit;"+msg;
                }

                // send data to server
                oos.writeObject(msgSend);
                oos.flush();
                System.out.println("client send to server: " + msgSend);

                // read content from server
                Object r = ois.readObject();
                String msgRcv = (String)r;
                System.out.println("received from server: " + msgRcv);
                String[] tokens = msgRcv.split(";");
                String cmd = tokens[0];
                String content = msgRcv.substring(cmd.length()+1);
                System.out.println("client received cmd = "+cmd+", content="+content);

                if(cmd.equals("CMDExit")==true)
                {
                    System.out.println("farewell");
                    break;
                }

            }

        } catch (Exception e)
        {


        }
    }
}
