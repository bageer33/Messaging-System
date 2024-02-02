import java.net.*;
import java.io.*;
import java.util.HashMap;

/**
 * CS180 Project 5
 *
 * This program holds the P5Server Class for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */
 
public class P5Server
{

    private static HashMap<String, Socket> userNameToSocketRcv = new HashMap<>();
    public static class ServerRunnerForOneClient implements Runnable
    {
        private Socket clientSocketObj;
        public ServerRunnerForOneClient(Socket clientSocketObj)
        {
            this.clientSocketObj = clientSocketObj;
        }

        public String printLog(String msg)
        {
            String clientAddr = clientSocketObj.getInetAddress().getHostAddress();
            String outMst = "LOG: "+msg+", client addr=" + clientAddr + "clientSocketObj="+clientSocketObj;
            System.out.println(outMst);
            return outMst;
        }

        public String processCMDLoginConsumer(String content)
        {
            // not auth currently, just return success
            // server add logic to check username and password by comparing registered username and password at files
            // return "CMDLoginConsumerFail;" if auth fail
            String[] tokens = content.split(";");
            String userName = tokens[0];
            String password = tokens[1];
            if (userName.equals("aaa") && password.equals("123")) {
                return "CMDLoginConsumerSuccess;";
            }
            else
            {
                return "CMDLoginConsumerFail;";
            }
        }

        public String processCMDGetContacts(String content)
        {
            // not read from files currently, since register not completed

            String[] tokens = content.split(";");
            String userName = tokens[0];

            return "CMDGetContacts;aaaa;bbbb;cccccccccccc;dddd;eeeeeeeee;ffff;ggggg";

        }


        //
        public String processCMDSendMsg(String content)
        {
            // not read from files currently, since register not completed

            String[] tokens = content.split(";");
            String from = tokens[0];
            String to = tokens[1];
            String msg = tokens[2];

            if(userNameToSocketRcv.containsKey(to)==true)
            {
                Socket sock = userNameToSocketRcv.get(to);
                System.out.println("send message to "+sock);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                    oos.writeObject( "CMDSendMsg;"+content  );
                    oos.flush(); // ensure data is sent to the client
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


            return "CMDSendMsg;success";

        }

        public String processCMDRegistRcv(String content)
        {
            // not read from files currently, since register not completed

            String[] tokens = content.split(";");
            String username = tokens[0];

            userNameToSocketRcv.put(username, clientSocketObj);

            return "CMDRegistRcv;success";

        }


        public void run()
        {
            try {

                printLog("working for one client at handler");
                // all networkI/O message processing here
                ObjectOutputStream oos = new ObjectOutputStream(clientSocketObj.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocketObj.getInputStream());

                String msg;
                while(  (msg = (String) ois.readObject())!=null  )
                {
                    printLog("server received msg = "+msg);

                    String msgSend = "CMDBadMsgReceived;";
                    try
                    {
                        String[] tokens = msg.split(";");
                        String cmd = tokens[0];
                        String content = msg.substring(cmd.length()+1);
                        printLog("server received cmd="+cmd+", content="+content);

                        if (cmd.equals("CMDExit")==true)
                        {
                            msgSend = "CMDExit;";
                        }
                        else if(cmd.equals("CMDLoginConsumer")==true)
                        {
                            msgSend = this.processCMDLoginConsumer(content);
                        }
                        else if(cmd.equals("CMDGetContacts")==true)
                        {
                            msgSend = this.processCMDGetContacts(content);
                        }
                        else if(cmd.equals("CMDSendMsg")==true)
                        {
                            msgSend = this.processCMDSendMsg(content);
                        }
                        else if(cmd.equals("CMDRegistRcv")==true)
                        {
                            msgSend = this.processCMDRegistRcv(content);
                        }
                        //CMDRegistRcv
                        else
                        {
                            msgSend = "CMDReceivedUnknownCmd;"+content;
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    oos.writeObject( msgSend  );
                    oos.flush(); // ensure data is sent to the client
                    printLog("server send client: "+msgSend);
                }

                // close
                try {
                    oos.close();
                    ois.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            catch (Exception  e)
            {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args)
    {
        ServerSocket serverSocketObj = null;
        try
        {
            serverSocketObj = new ServerSocket(4242);
            serverSocketObj.setReuseAddress(true);
            while(true)
            {
                Socket clientSocketObj = serverSocketObj.accept();
                String clientAddr = clientSocketObj.getInetAddress().getHostAddress();

                System.out.println("received one client connect, client addr="+clientAddr+ "clientSocketObj="+clientSocketObj);
                ServerRunnerForOneClient r = new ServerRunnerForOneClient(clientSocketObj);
                Thread th = new Thread(r);
                th.start();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
