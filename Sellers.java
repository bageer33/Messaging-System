import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
/**
 * CS180 Project 5
 *
 * This program holds the Sellers Class for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */
public class Sellers {
    private String email;
    private String password;
    private String name;
    private String storeName;
    private String filePathForMessages; // storage the messages (format is: sender;receiver;timestamp;status;content)
    private String filePathForSellers;
    private String filePathForCustomers;
    private String filePathForBlockList;
    private String filePathForInvisibleList;
    private ArrayList<Sellers> sellerList;
    private ArrayList<Customers> customerList;

    public Sellers(String storeName, String name, String email, String password) {
        this.storeName = storeName;
        this.name = name;
        this.email = email;
        this.password = password;

        this.filePathForMessages = "messages.txt";
        this.filePathForSellers = "sellers.txt";
        this.filePathForCustomers = "customers.txt";
        this.filePathForBlockList = "block_list.txt";
        this.filePathForInvisibleList = "invisible_list.txt";

        this.sellerList = new ArrayList<>();
        this.customerList = new ArrayList<>();
        //this.storeNames = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public String getStoreName() {
        return this.storeName;
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

    public void showStatsDialog(String sName, ArrayList<String> customers) {
        ArrayList<String> messageHistory = new ArrayList<>();
        String word;
        JFrame frame = new JFrame("Project 5");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        JPanel upperPanel = new JPanel();

        JLabel stats = new JLabel("Customer Statistics");
        upperPanel.add(stats);
        content.add(upperPanel, BorderLayout.NORTH);

        JTextArea customerList = new JTextArea(10,35);
        JScrollPane scrollPane = new JScrollPane(customerList);
        customerList.setEditable(false);

        word = CommonWord(sName, customers);
        customerList.append(word + "\n");

        for (int i = 0; i < customers.size(); i++) {
            messageHistory = getMessages(sName, customers.get(i));
            customerList.append(messageHistory.size() + " messages sent with " + customers.get(i) + ".\n");
        }
        bottomPanel.add(scrollPane);
        content.add(bottomPanel, BorderLayout.CENTER);


        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public String CommonWord(String sName, ArrayList<String> customers) {
        ArrayList<String> messageHistory = new ArrayList<>();
        ArrayList<String> wordList = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {
            messageHistory = getMessages(sName, customers.get(i));
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
    public void showMessageDialog(String sName, String cName) {
        ArrayList<String> anomalies = getMessages(sName, cName);
        JFrame frame = new JFrame("Project 5");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        JPanel upperPanel = new JPanel();

        JLabel label = new JLabel("Chat between " + sName + " and " + cName);
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
    public void showAllMessagesDialog(String sName) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForMessages);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = bffr.readLine()) != null) {
                if (currentLine.split(";")[0].equalsIgnoreCase(sName) || currentLine.split(";")[1].equalsIgnoreCase(sName)) {
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

        JLabel label = new JLabel("All messages between " + sName + " and customers");
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
    public ArrayList getMyMessages(String cName, String sName) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForMessages);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = bffr.readLine()) != null) {
                if (currentLine.split(";")[0].equalsIgnoreCase(sName)) {
                    if (currentLine.split(";")[1].equalsIgnoreCase(cName)) {
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

    public String createMessage(String receiver, String content)
    {
        String sender = this.name;
        String timestamp = this.getCurrentDateTimeStr();
        String result = String.format("%s;%s;%s;%s", sender,receiver,timestamp,content);
        return result;
    }
    public static String getCurrentDateTimeStr()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strDateTime = formatter.format(date);
        return strDateTime;
    }
    public boolean isNameInCustomers(String name)
    {
        for(int i = 0; i < customerList.size(); i++)
        {
            if (customerList.get(i).getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
    public boolean sendMessageToCustomer(String receiverCustomerName, String content)
    {
        if (this.isNameInCustomers(receiverCustomerName) == true)
        {
            String msg = createMessage(receiverCustomerName, content);
            addNewMessageToFile(msg);
            return true;
        }
        return false;
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
    public void addSellerToFile(Sellers s) {
        String allContentBeforeAdd = getAllFileContent(this.filePathForSellers);
        File file = new File(this.filePathForSellers);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write( allContentBeforeAdd );
            String sellerLine = s.getStoreName() + ";" + s.getName() + ";" + s.getEmail() + ";" + s.getPassword();
            bw.write(sellerLine);
            bw.close();
        } catch (IOException e) {
            System.out.println("invalid");
        }
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
                result+=strLine+"\n";
            }
        } catch (Exception e) {
            result = "";
            System.out.println(e);
        }
        return result;

    }

    public void printList(ArrayList<Sellers> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getName());
        }
    }
    public boolean searchCustomer(ArrayList list) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Customers> customList = new ArrayList<>();
        System.out.println("Here is the list of available customers: ");
        printList(list);
        String customName = sc.nextLine();
        if (customList.contains(customName)) {
            return true;
        } else {
            return false;
        }
    }

    // Use Dashboard sort list in main method
    public void sortList(String ans) {
        // This is the logic for this method
        // The variable ans is assuming the user wants to sort dashboard alphabetically
        if (ans.equalsIgnoreCase("Yes")) {
            sellerList.sort((o1, o2)
                    -> o1.getEmail().compareTo(
                    o2.getEmail()));
        }
        printList(sellerList);
    }



    // Print this data in the main method after getting messageHistory from the Customer
    public void displayDashBoard(boolean isSort)
    {
        String allContent = getAllFileContent(this.filePathForMessages);
        System.out.println("allContent for dash board = " + allContent);
        // store,msg received sort
        ArrayList<Integer> sellerNameListDB = new ArrayList<>();
        ArrayList<Integer> storeNameListDB = new ArrayList<>();
        ArrayList<Integer> storeReceivedMessageCountListDB = new ArrayList<>();

        String[] msgs = allContent.split("\n");
        System.out.println("msgs=" + msgs.length);

        // customer, send msg sort
    }

    public ArrayList<String> getBlockedCustomers() {
        ArrayList<String> blockedCustomers = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForBlockList);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while((currentLine = bffr.readLine()) != null) {
                blockedCustomers.add(currentLine);
            }
            bffr.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return blockedCustomers;
    }

    public ArrayList<String> getInvisibleToCustomers() {
        ArrayList<String> invisibleList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(this.filePathForBlockList);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;
            while((currentLine = bffr.readLine()) != null) {
                invisibleList.add(currentLine);
            }
            bffr.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return invisibleList;
    }

    public void blockCustomer(String sName, String cName) {
        try {
            BufferedWriter output;
            output = new BufferedWriter(new FileWriter(this.filePathForBlockList));
            output.append(sName + ";" + cName);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void makeInvisibleToCustomer(String sName, String cName) {
        try {
            BufferedWriter output;
            output = new BufferedWriter(new FileWriter(this.filePathForInvisibleList));
            output.append(sName + ";" + cName);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // This tests if the seller has been blocked by someone
    public boolean isBlocked(String sName) {
        try {
            ArrayList<String> blockedList = new ArrayList<>();
            FileReader fr = new FileReader(this.filePathForBlockList);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;

            while ((currentLine = bffr.readLine()) != null) {
                blockedList.add(currentLine);
            }
            bffr.close();

            for (int i = 0; i < blockedList.size(); i++) {
                if (blockedList.get(i).split(";")[1].equals(sName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // This is to check if the seller has set themselves invisible to someone
    public boolean isInvisibleTo(String sName) {
        try {
            ArrayList<String> blockedList = new ArrayList<>();
            FileReader fr = new FileReader(this.filePathForInvisibleList);
            BufferedReader bffr = new BufferedReader(fr);
            String currentLine;

            while ((currentLine = bffr.readLine()) != null) {
                blockedList.add(currentLine);
            }
            bffr.close();

            for (int i = 0; i < blockedList.size(); i++) {
                if (blockedList.get(i).split(";")[0].equals(sName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
