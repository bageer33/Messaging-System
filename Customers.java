import java.io.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
/**
 * CS180 Project 5
 *
 * This program holds the Customer Class for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */

public class Customers {
    private String name; // Customer name
    private String email; // email and password can be used for account auth
    private String password; // email and password can be used for account auth

    private String filePathForMessages; // storage the messages (format is: sender;receiver;timestamp;status;content)
    private String filePathForSellers;
    private String filePathForCustomers;
    private String filePathForBlockList;
    private String filePathForInvisibleList;
    private ArrayList<Sellers> sellersList;
    private ArrayList<Customers> customersList;

    private ArrayList<String> storeNames;

    private HashMap<String, String> sellerNameToStoreName;

    public Customers(String name, String email, String password)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        // default file data storage
        this.filePathForMessages = "messages.txt";
        this.filePathForSellers = "sellers.txt";
        this.filePathForCustomers = "customers.txt";
        this.filePathForBlockList = "block_list.txt";
        this.filePathForInvisibleList = "invisible_list.txt";
        // init sellers and customers
        this.sellersList = new ArrayList<>();
        this.customersList = new ArrayList<>();
        this.storeNames = new ArrayList<>();
        sellerNameToStoreName = new HashMap<>();

    }
    public void showStatsDialog(String cName, ArrayList<String> sellers) {
        ArrayList<String> messageHistory = new ArrayList<>();
        String word;
        JFrame frame = new JFrame("Project 5");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        JPanel upperPanel = new JPanel();

        JLabel stats = new JLabel("Seller Statistics");
        upperPanel.add(stats);
        content.add(upperPanel, BorderLayout.NORTH);

        JTextArea customerList = new JTextArea(10,35);
        JScrollPane scrollPane = new JScrollPane(customerList);
        customerList.setEditable(false);

        word = CommonWord(cName, sellers);
        customerList.append(word + "\n");

        for (int i = 0; i < sellers.size(); i++) {
            messageHistory = getMessages(cName, sellers.get(i));
            customerList.append(messageHistory.size() + " messages sent with " + sellers.get(i) + ".\n");
        }
        bottomPanel.add(scrollPane);
        content.add(bottomPanel, BorderLayout.CENTER);


        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public String CommonWord(String cName, ArrayList<String> sellers) {
        ArrayList<String> messageHistory = new ArrayList<>();
        ArrayList<String> wordList = new ArrayList<>();

        for (int i = 0; i < sellers.size(); i++) {
            messageHistory = getMessages(cName, sellers.get(i));
            for (int k = 0; k < messageHistory.size(); k++) {
                String[] messageWords = messageHistory.get(k).split(" ");
                for (int j = 2; j < messageWords.length; j++) {
                    wordList.add(messageWords[j]);
                }
            }
        }
        HashMap<String, Integer> map = new HashMap<>();
        for (String element : wordList) {
            if (map.containsKey(element)) {
                map.put(element, map.get(element) + 1);
            } else {
                map.put(element, 1);
            }
        }

        // find the most common element
        int maxCount = 0;
        String mostCommon = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommon = entry.getKey();
            }
        }
        return "The overall most common word is \"" + mostCommon + "\".";
    }
    public void addCustomerToFile(Customers c) {
        String allContentBeforeAdd = getAllFileContent(this.filePathForCustomers);
        File file = new File(this.filePathForCustomers);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write( allContentBeforeAdd );
            String customerLine = c.getName() + ";" + c.getEmail() + ";" + c.getPassword();
            bw.write(customerLine);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }
    }


    // Any customer can message any Seller, and any Seller may message any customer.
    // Customers cannot message another customer. Sellers cannot message another seller.
    public boolean sendMessageToSeller(String receiverSellerName, String content)
    {
        if (this.isNameInSellers(receiverSellerName) == true)
        {
            String msg = createMessage(receiverSellerName, content);
            addNewMessageToFile(msg);
            return true;
        }
        return false;
    }
    public boolean sendMessageToStore(String receiverStoreName, String content)
    {
        String sellerName = getSellerNameByStoreName(receiverStoreName);
        return sendMessageToSeller(sellerName, content);
    }
    // create, edit, and delete access to their personal conversation history.
    // Creating a message will create it for both the sender and the recipient.
    // Editing a message will update the contents for both the sender and the recipient.
    // Deleting a message will only delete it for the user who initiated the delete operation.

    // return format : sender;receiver;timestamp;status;content
    public String createMessage(String receiver, String content)
    {
        String sender = this.name;
        String timestamp = this.getCurrentDateTimeStr();
        String result = String.format("%s;%s;%s;%s", sender, receiver, timestamp, content);
        return result;
    }

    // edit message send by this Customer
    public void editMessage(String oldMessage, String newMessage) throws IOException {
        try {
            FileReader fileReader = new FileReader(filePathForMessages);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String newLine = line.replace(oldMessage, newMessage);
                sb.append(newLine);
                sb.append(System.lineSeparator());
            }
            bufferedReader.close();
            FileWriter fileWriter = new FileWriter(filePathForMessages);
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String removeLine) throws IOException {
        try {
            File inputFile = new File(filePathForMessages);
            File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(removeLine)) {
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            reader.close();
            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            } else {
                throw new IOException("Failed to delete the original file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportMessages(ArrayList<String> messages, String filePath, String seller, String customer) {
        try {
            FileWriter csvWriter = new FileWriter(filePath);
            csvWriter.append("Participants (sender first),");
            csvWriter.append("Time sent,");
            csvWriter.append("Content");
            csvWriter.append("\n");

            for (int i = 0; i < messages.size(); i++) {
                csvWriter.append(messages.get(i));
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
            JOptionPane.showMessageDialog(null, "Messages have been exported.", "Messages",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error with exporting " +
                    "messages.", "Messages", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ArrayList getMessages(String cName, String sName) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForMessages);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = bffr.readLine()) != null) {
                if (currentLine.split(";")[0].equalsIgnoreCase(cName) || currentLine.split(";")[0].equalsIgnoreCase(sName)) {
                    if (currentLine.split(";")[1].equalsIgnoreCase(cName) || currentLine.split(";")[1].equalsIgnoreCase(sName)) {
                        messages.add(currentLine);
                    }
                }

            }
            bffr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public ArrayList getMyMessages(String cName, String sName) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForMessages);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = bffr.readLine()) != null) {
                if (currentLine.split(";")[0].equalsIgnoreCase(cName)) {
                    if (currentLine.split(";")[1].equalsIgnoreCase(sName)) {
                        messages.add(currentLine);
                    }
                }
            }
            bffr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void showMessageDialog(String sName, String cName) {
        ArrayList<String> anomalies = getMessages(cName, sName);
        JFrame frame = new JFrame("Project 5");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        JPanel upperPanel = new JPanel();

        JLabel label = new JLabel("Chat between " + cName + " and " + sName);
        upperPanel.add(label);

        content.add(upperPanel, BorderLayout.NORTH);


        JTextArea chatHistory = new JTextArea(7,25);
        JScrollPane scrollPane = new JScrollPane(chatHistory);
        chatHistory.setEditable(false);
        for(String a : anomalies){
            chatHistory.append(a + "\n");
        }
        bottomPanel.add(scrollPane);
        content.add(bottomPanel, BorderLayout.CENTER);

        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void showAllMessagesDialog(String cName) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForMessages);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = bffr.readLine()) != null) {
                if (currentLine.split(";")[0].equalsIgnoreCase(cName) || currentLine.split(";")[1].equalsIgnoreCase(cName)) {
                    messages.add(currentLine);
                }

            }
            bffr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Project 5");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        JPanel upperPanel = new JPanel();

        JLabel label = new JLabel("All messages between " + cName + " and sellers");
        upperPanel.add(label);

        content.add(upperPanel, BorderLayout.NORTH);


        JTextArea chatHistory = new JTextArea(7,25);
        JScrollPane scrollPane = new JScrollPane(chatHistory);
        chatHistory.setEditable(false);
        for(String a : messages){
            chatHistory.append(a + "\n");
        }
        bottomPanel.add(scrollPane);
        content.add(bottomPanel, BorderLayout.CENTER);

        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static String getCurrentDateTimeStr()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strDateTime = formatter.format(date);
        return strDateTime;
    }


    // Customers should be able to view a list of stores to select a message recipient.
    // Selecting a store will send a message to the seller associated with that store.


    // TBD: Customers should be able to search for a specific seller to message.


    // Blocking (TBD)
    // Users may choose to block another user or become invisible to them.
    // An individual who has been blocked by another user may not send messages to the one who blocked them.
    // An individual who has become invisible to another user will not appear on the application
    //    when the other user searches for them.
    public void setBlockOthers(String sellerName)
    {
        String rule = String.format("%s;%s", this.name, sellerName);
        String allContentBeforeAdd = this.getAllFileContent(this.filePathForBlockList);

        String[] tokens = allContentBeforeAdd.split("\n");

        boolean isRuleExists = false;
        for (int i = 0; i < tokens.length; i++)
        {
            if (tokens[i].equals(rule) == true)
            {
                isRuleExists = true;
            }
        }
        if (isRuleExists == false)
        {
            addNewBlockRuleToFile(rule);
        }
    }
    public void setInvisibleOthers(String sellerName)
    {
        String rule = String.format("%s;%s", this.name, sellerName);
        String allContentBeforeAdd = this.getAllFileContent(this.filePathForInvisibleList);

        String[] tokens = allContentBeforeAdd.split("\n");

        boolean isRuleExists = false;
        for (int i = 0; i < tokens.length; i++)
        {
            if (tokens[i].equals(rule) == true)
            {
                isRuleExists = true;
            }
        }
        if (isRuleExists == false)
        {
            addNewInvisibleRuleToFile(rule);
        }
    }

    public void loadSellers()
    {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePathForSellers));
            String strLine;
            int line = 0;
            while ( (strLine = buffer.readLine()) != null )
            {
                line++;
                strLine = strLine.strip();
                String[] tokens = strLine.split(";");
                if (tokens.length == 4) {
                    Sellers seller = new Sellers(tokens[0], tokens[1], tokens[2], tokens[3]);
                    sellersList.add(seller);
                    storeNames.add(tokens[0]);
                    sellerNameToStoreName.put( tokens[1], tokens[0]);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public String getSellerNameByStoreName(String storeName)
    {
        for (int i = 0; i < this.sellersList.size(); i++)
        {
            if (this.sellersList.get(i).getStoreName().equals(storeName) == true)
            {
                return this.sellersList.get(i).getName();
            }
        }
        return null;
    }

    public void loadCustomers()
    {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(this.filePathForCustomers));
            String strLine;
            int line = 0;
            while ( (strLine = buffer.readLine()) != null )
            {
                line++;
                strLine = strLine.strip();
                String[] tokens = strLine.split(";");
                if (tokens.length == 3) {
                    Customers customer = new Customers(tokens[0], tokens[1], tokens[2]);
                    this.customersList.add(customer);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void addNewMessageToFile(String message)
    {
        String allContentBeforeAdd = getAllFileContent(this.filePathForMessages);
        File file = new File(this.filePathForMessages);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write( allContentBeforeAdd );

            bw.write(message);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }
    }

    public void writeMsgFileByNew(String messages)
    {
        String allContentBeforeAdd = getAllFileContent(this.filePathForMessages);
        File file = new File(this.filePathForMessages);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(messages);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }
    }
    public void writeContentToFile(String messages, String filePath)
    {
        File file = new File(filePath);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(messages);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }
    }

    public void addNewBlockRuleToFile(String rule)
    {
        String allContentBeforeAdd = getAllFileContent(this.filePathForBlockList);
        File file = new File(this.filePathForBlockList);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write( allContentBeforeAdd );

            bw.write(rule);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }

    }

    public void addNewInvisibleRuleToFile(String rule)
    {
        String allContentBeforeAdd = getAllFileContent(this.filePathForInvisibleList);
        File file = new File(this.filePathForInvisibleList);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write( allContentBeforeAdd );

            bw.write(rule);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }

    }


    // Statistics
    // Customers can view a dashboard with store and seller information.
    // Data will include a list of stores by number of messages received and
    //    storeName,RcvMsgNumber
    // a list of stores by the number of messages that particular customer has sent.
    //    storeName,RcvMsgNumber,CustomerSendTheMsg

    // Customers can choose to sort the dashboard.


    public void displayDashBoard(boolean isSort)
    {
        String allContent = getAllFileContent(this.filePathForMessages);
        // store,msg received sort
        HashMap<String, Integer> storeNameToRcvMsgCount = new HashMap<>();

        String[] msgs = allContent.split("\n");
        for (int i = 0; i < msgs.length; i++)
        {
            String[] tokens = msgs[i].split(";");
            String senderName = tokens[0];
            String receiverName = tokens[1];
            if (isNameInSellers(receiverName) == true)
            {
                String storeName = sellerNameToStoreName.get(receiverName);
                if (storeNameToRcvMsgCount.containsKey(storeName) == false)
                {
                    storeNameToRcvMsgCount.put(storeName, 0);
                }
                int newValue = storeNameToRcvMsgCount.get(storeName) + 1;
                storeNameToRcvMsgCount.replace(storeName, newValue);
            }

        }
        System.out.println("The dashboard for a list of stores by number of messages received ");
        if (isSort == true)
        {
            storeNameToRcvMsgCount = sortDictByValue(storeNameToRcvMsgCount);
        }
        System.out.println( String.format("%-15s%-15s", "StoreName", "ReceivedMsgNumber"));
        for (String key: storeNameToRcvMsgCount.keySet())
        {
            Integer value  = storeNameToRcvMsgCount.get(key);
            System.out.println(String.format("%-15s%-15d", key, value));
        }

        // customer, send msg sort
        HashMap<String, ArrayList<HashMap<String, Integer>>> customerNameToSendStoreMsgs =
                new HashMap<String, ArrayList<HashMap<String, Integer>>>();
        for (int i = 0; i < msgs.length; i++) {
            String[] tokens = msgs[i].split(";");
            String senderName = tokens[0];
            String receiverName = tokens[1];
            if (isNameInCustomers(senderName) == true)
            {
                String storeName = sellerNameToStoreName.get(receiverName);
                if (customerNameToSendStoreMsgs.containsKey(senderName) == false)
                {
                    ArrayList<HashMap<String, Integer>> vList = new ArrayList<HashMap<String, Integer>>();
                    HashMap<String, Integer> storeToMsgCount = new HashMap<>();
                    storeToMsgCount.put(storeName, 0);
                    vList.add(storeToMsgCount);
                    customerNameToSendStoreMsgs.put(senderName, vList);
                }
                boolean isStoreNameExist = false;
                for (HashMap<String, Integer> d: customerNameToSendStoreMsgs.get(senderName))
                {
                    if (d.containsKey(storeName) == true)
                    {
                        isStoreNameExist = true;

                    }
                }
                if (isStoreNameExist == false)
                {
                    ArrayList<HashMap<String, Integer>> vList = customerNameToSendStoreMsgs.get(senderName);
                    HashMap<String, Integer> storeToMsgCount = new HashMap<>();
                    storeToMsgCount.put(storeName, 0);
                    vList.add(storeToMsgCount);
                    customerNameToSendStoreMsgs.replace(senderName, vList);
                }
                for (int j = 0; j < customerNameToSendStoreMsgs.get(senderName).size(); j++)
                {
                    HashMap<String, Integer> d = customerNameToSendStoreMsgs.get(senderName).get(j);
                    if (isSort == true)
                    {
                        d = sortDictByValue(d);
                    }
                    if (d.containsKey(storeName) == true) {
                        int v = d.get(storeName);
                        v = v + 1;
                        customerNameToSendStoreMsgs.get(senderName).get(j).replace(storeName, v);
                    }
                }
            }
        }
        // a list of stores by the number of messages that particular customer has sent.
        System.out.println(
                "Dashboard for a list of stores by the number of messages that particular customer has sent.");
        System.out.println(  String.format("%-15s%-15s%-15s", "StoreName", "CustomerName", "SendMsgNumber")  );
        for (String customerNm : customerNameToSendStoreMsgs.keySet())
        {
            for (int i = 0; i < customerNameToSendStoreMsgs.get(customerNm).size(); i++)
            {
                HashMap<String, Integer> d = customerNameToSendStoreMsgs.get(customerNm).get(i);
                for (String storeNm : d.keySet())
                {
                    int sendNum = d.get(storeNm);
                    System.out.println(  String.format("%-15s%-15s%-15d", storeNm, customerNm, sendNum)  );
                }
            }
        }
    }


    // referenced from : https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
    public  HashMap<String, Integer> sortDictByValue(HashMap<String, Integer> hmp)
    {
        LinkedList<HashMap.Entry<String, Integer>> lst =
                new LinkedList<HashMap.Entry<String, Integer>>(hmp.entrySet());

        Collections.sort(lst, new Comparator<HashMap.Entry<String, Integer>>()
        {
            public int compare(HashMap.Entry<String, Integer> x1, HashMap.Entry<String, Integer> x2)
            {
                return (x1.getValue()).compareTo(x2.getValue());
            }
        });
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (HashMap.Entry<String, Integer> h : lst)
        {
            temp.put(h.getKey(), h.getValue());
        }
        return temp;
    }







    // search if name in Sellers
    public boolean isNameInSellers(String name3)
    {
        for (int i = 0; i < sellersList.size(); i++)
        {
            if (sellersList.get(i).getName().equals(name3))
            {
                return true;
            }
        }
        return false;
    }

    // search if name in Customers
    public boolean isNameInCustomers(String name2)
    {
        for (int i = 0; i < customersList.size(); i++)
        {
            if (customersList.get(i).getName().equals(name2))
            {
                return true;
            }
        }
        return false;
    }


    public static String getAllFileContent(String filePath)
    {
        String result = "";
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePath));
            String strLine;
            int line = 0;
            while ( (strLine = buffer.readLine()) != null )
            {
                line++;
                result += strLine + "\n";
            }
        } catch (Exception e) {
            result = "";
            System.out.println(e);
        }
        return result;

    }


    // common getters and setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFilePathForMessages() {
        return filePathForMessages;
    }

    public void setFilePathForMessages(String filePathForMessages) {
        this.filePathForMessages = filePathForMessages;
    }

    public String getFilePathForSellers() {
        return filePathForSellers;
    }

    public void setFilePathForSellers(String filePathForSellers) {
        this.filePathForSellers = filePathForSellers;
    }

    public String getFilePathForCustomers() {
        return filePathForCustomers;
    }

    public void setFilePathForCustomers(String filePathForCustomers) {
        this.filePathForCustomers = filePathForCustomers;
    }

    public String getFilePathForBlockList() {
        return filePathForBlockList;
    }

    public void setFilePathForBlockList(String filePathForBlockList) {
        this.filePathForBlockList = filePathForBlockList;
    }

    public String getFilePathForInvisibleList() {
        return filePathForInvisibleList;
    }

    public void setFilePathForInvisibleList(String filePathForInvisibleList) {
        this.filePathForInvisibleList = filePathForInvisibleList;
    }

    public ArrayList<Sellers> getSellers()
    {
        return this.sellersList;
    }

    public ArrayList<String> getStoreNames()
    {
        return this.storeNames;
    }
    public ArrayList<Customers> getCustomers()
    {
        return this.customersList;
    }


    public String toString()
    {
        return String.format("name=%s, email=%s, pasword=%s", this.name, this.email, this.password);
    }
}
