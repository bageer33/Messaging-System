import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
/**
 * CS180 Project 5
 *
 * This program holds the ServerMessageReceiver and Client classes for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 30 2023
 *
 */
class ServerMessageReceiver extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private String message = "init";
    public ServerMessageReceiver(Socket socket) throws IOException {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            System.out.println("ServerMessageReceiver running ");
            while (message.equals("endsocket") != true) {
                System.out.println("ServerMessageReceiver readObject ing ");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                message = (String) ois.readObject();
                System.out.println("ServerMessageReceiver Received message: " + message);
                String[] tokens = message.split(";");
                String from = tokens[0];
                String to = tokens[1];
                if (from.equals(to) == false) {
                    JOptionPane.showMessageDialog(null, message, "ReceivedMessage",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
public class Client {

    public static void main(String[] args) throws IOException {

        ArrayList<Sellers> sellersList = new ArrayList<>();
        ArrayList<Customers> customersList = new ArrayList<>();
        ArrayList<String> storeNames = new ArrayList<>();
        ArrayList<String> customerNames = new ArrayList<>();
        ArrayList<String> sellerNames = new ArrayList<>();
        String filePathForSellers = "sellers.txt";
        String filePathForCustomers = "customers.txt";
        String filePathForBlockList = "block_list.txt";
        String filePathForInvisibleList = "invisible_list.txt";
        String filePathForMessages = "messages.txt";

        File sell = new File(filePathForSellers);
        if (!sell.exists()) {
            sell.createNewFile();
        }
        File cust = new File(filePathForCustomers);
        if (!cust.exists()) {
            cust.createNewFile();
        }
        File block = new File(filePathForBlockList);
        if (!block.exists()) {
            block.createNewFile();
        }
        File invis = new File(filePathForInvisibleList);
        if (!invis.exists()) {
            invis.createNewFile();
        }
        File messageFile = new File(filePathForMessages);
        if (!messageFile.exists()) {
            messageFile.createNewFile();
        }

        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePathForSellers));
            String strLine;
            while ( (strLine = buffer.readLine()) != null ) {
                strLine = strLine.strip();
                String[] tokens = strLine.split(";");
                if (tokens.length == 4) {
                    Sellers seller = new Sellers(tokens[0], tokens[1], tokens[2], tokens[3]);
                    sellersList.add(seller);
                    sellerNames.add(tokens[1]); // this is so customers can search for the name of a seller
                    storeNames.add(tokens[0]); // this is so customers can search for the name of a store
                }
            }
            buffer.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error with reading sellers file.",
                    "Messages", JOptionPane.ERROR_MESSAGE);
        }

        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePathForCustomers));
            String line;
            while ((line = buffer.readLine()) != null) {
                line = line.strip();
                String[] tokens = line.split(";");
                if (tokens.length == 3) {
                    Customers customer = new Customers(tokens[0], tokens[1], tokens[2]);
                    customersList.add(customer);
                    customerNames.add(tokens[0]); // this is so sellers can search for the name of a customer
                }
            }
            buffer.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error with reading customers file.",
                    "Messages", JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(null, "Welcome!", "Messages",
                JOptionPane.INFORMATION_MESSAGE);

        String[] custOrSellOptions = {"Customer", "Seller"};
        String custOrSell = (String) JOptionPane.showInputDialog(null,
                "Are you a customer or seller?", "Messages",
                JOptionPane.QUESTION_MESSAGE, null, custOrSellOptions, custOrSellOptions[0]);

        String[] logOrSignOptions = {"Login", "Signup"};
        String logOrSign = (String) JOptionPane.showInputDialog(null,
                "Are you logging in or signing up?", "Messages",
                JOptionPane.QUESTION_MESSAGE, null, logOrSignOptions, logOrSignOptions[0]);

        String name = JOptionPane.showInputDialog(null, "What is your name?",
                "Messages", JOptionPane.QUESTION_MESSAGE);
        String email = JOptionPane.showInputDialog(null, "What is your email?",
                "Messages", JOptionPane.QUESTION_MESSAGE);
        String password = JOptionPane.showInputDialog(null, "What is your password?",
                "Messages", JOptionPane.QUESTION_MESSAGE);


        P5Client client = new P5Client("localhost", 4242);
        Socket receiveSocket = client.connectAndGetRcvSocket(name);
        System.out.println("receiveSocket=" + receiveSocket);

        Thread t1 = new Thread(new  ServerMessageReceiver(receiveSocket) );
        t1.start();

        boolean exist = true;
        if (custOrSell.equals("Customer")) { // User is a customer
            Customers customer = new Customers(name, email, password);

            if (logOrSign.equals("Signup")) {  // Customer is Signing up
                customersList.add(customer);
                customerNames.add(customer.getName());
                customer.addCustomerToFile(customer);
                JOptionPane.showMessageDialog(null, "Customer added to the directory!",
                        "Messages", JOptionPane.INFORMATION_MESSAGE);
            }
            else {   // Customer is logging in
                exist = false;

                for (int i = 0; i < customersList.size(); i++) {
                    if (customer.getName().equals(customersList.get(i).getName())) {
                        if (customer.getEmail().equals(customersList.get(i).getEmail())) {
                            if (customer.getPassword().equals(customersList.get(i).getPassword())) {
                                exist = true;
                            }
                        }
                    }
                }

                if (exist) {
                    JOptionPane.showMessageDialog(null, "You are logged in!", "Messages",
                            JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(null, "Account does not exist!", "Messages",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            customer.showAllMessagesDialog(name);
            boolean continueMessages = true;
            boolean blocked = false;
            while (exist && continueMessages) {

                String [] primaryChoiceOptions = {"View a list of stores", "Search for a store",
                        "View a list of sellers", "Search for a seller", "Block a user",
                        "Become invisible from a user", "View Statistics", "Refresh Dashboard", "Exit"};
                String primaryChoice = (String) JOptionPane.showInputDialog(null,
                        "Please make a selection", "Messages",
                        JOptionPane.QUESTION_MESSAGE, null, primaryChoiceOptions, primaryChoiceOptions[0]);

                customer.loadSellers();
                customer.loadCustomers();

                ArrayList<String> invisibleUsers = new ArrayList<>();
                BufferedReader buffer = new BufferedReader(new FileReader(filePathForInvisibleList));
                String line;
                while ((line = buffer.readLine()) != null) {
                    if (line.contains(";" + name)) {
                        String user = line.replace(";" + name, "");
                        invisibleUsers.add(user);
                    }
                }
                buffer.close();

                String storeName = "";
                String sellerName = "";

                switch(primaryChoice) {
                    case "View a list of stores":

                        do {
                            ArrayList<String> listOfStores = new ArrayList<>();
                            for (int i = 0; i < storeNames.size(); i++) {
                                String tempStoreName = storeNames.get(i);
                                String tempSellerName = sellerNames.get(i);
                                if (!invisibleUsers.contains(tempSellerName)) {
                                    listOfStores.add(tempStoreName);
                                }
                            }

                            String[] arrayOfStores = listOfStores.toArray(new String[0]);
                            storeName = (String) JOptionPane.showInputDialog(null,
                                    "Choose a store to message:", "Messages",
                                    JOptionPane.QUESTION_MESSAGE, null, arrayOfStores, arrayOfStores[0]);
                            sellerName = customer.getSellerNameByStoreName(storeName);

                            buffer = new BufferedReader(new FileReader(filePathForBlockList));
                            blocked = false;
                            while ((line = buffer.readLine()) != null) {
                                if (line.contains(";" + name)) {
                                    if (line.contains(sellerName)) {
                                        blocked = true;
                                        JOptionPane.showMessageDialog(null,
                                                "You are blocked by this seller.", "Messages",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                            buffer.close();

                        } while (blocked);

                        break;

                    case "Search for a store":

                        boolean storeExists = false;
                        do {
                            storeName = JOptionPane.showInputDialog(null, "What store " +
                                    "would you like to message?", "Messages", JOptionPane.QUESTION_MESSAGE);
                            for (int i = 0; i < storeNames.size(); i++) {
                                if (storeNames.get(i).contains(storeName)) {
                                    sellerName = customer.getSellerNameByStoreName(storeName);
                                    if (!invisibleUsers.contains(sellerName)) {
                                        storeExists = true;
                                    }
                                }
                            }
                            buffer = new BufferedReader(new FileReader(filePathForBlockList));
                            blocked = false;
                            while ((line = buffer.readLine()) != null) {
                                if (line.contains(";" + name)) {
                                    if (line.contains(sellerName)) {
                                        blocked = true;
                                    }
                                }
                            }
                            buffer.close();

                            if (!storeExists) {
                                JOptionPane.showMessageDialog(null, "Store does not exist!",
                                        "Messages", JOptionPane.ERROR_MESSAGE);
                            } else if (blocked) {
                                JOptionPane.showMessageDialog(null, "You are blocked by this seller.",
                                        "Messages", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (!storeExists || blocked);
                        break;

                    case "View a list of sellers":

                        do {
                            ArrayList<String> listOfSellers = new ArrayList<>();
                            for (int i = 0; i < sellerNames.size(); i++) {
                                String temp = sellerNames.get(i);
                                if (!invisibleUsers.contains(temp)) {
                                    listOfSellers.add(temp);
                                }
                            }

                            String[] arrayOfSellers = listOfSellers.toArray(new String[0]);
                            sellerName = (String) JOptionPane.showInputDialog(null,
                                    "Choose a seller to message:", "Messages",
                                    JOptionPane.QUESTION_MESSAGE, null, arrayOfSellers, arrayOfSellers[0]);

                            buffer = new BufferedReader(new FileReader(filePathForBlockList));
                            blocked = false;
                            while ((line = buffer.readLine()) != null) {
                                if (line.contains(";" + name)) {
                                    if (line.contains(sellerName)) {
                                        blocked = true;
                                        JOptionPane.showMessageDialog(null,
                                                "You are blocked by this seller.", "Messages",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                            buffer.close();

                        } while (blocked);

                        break;

                    case "Search for a seller":

                        boolean sellerExists = false;
                        do {
                            sellerName = JOptionPane.showInputDialog(null, "What seller " +
                                    "would you like to message?", "Messages", JOptionPane.QUESTION_MESSAGE);
                            for (int i = 0; i < sellerNames.size(); i++) {
                                if (sellerNames.get(i).contains(sellerName) && !invisibleUsers.contains(sellerName)) {
                                    sellerExists = true;
                                }
                            }
                            buffer = new BufferedReader(new FileReader(filePathForBlockList));
                            blocked = false;
                            while ((line = buffer.readLine()) != null) {
                                if (line.contains(";" + name)) {
                                    if (line.contains(sellerName)) {
                                        blocked = true;
                                    }
                                }
                            }
                            buffer.close();

                            if (!sellerExists) {
                                JOptionPane.showMessageDialog(null, "Seller does not exist!",
                                        "Messages", JOptionPane.ERROR_MESSAGE);
                            } else if (blocked) {
                                JOptionPane.showMessageDialog(null, "You are blocked by this seller.",
                                        "Messages", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (!sellerExists || blocked);
                        break;

                    case "Block a user":
                        String blockedUser = JOptionPane.showInputDialog(null,
                                "Enter the seller you'd like to block:", "Messages", JOptionPane.QUESTION_MESSAGE);
                        customer.setBlockOthers(blockedUser);
                        JOptionPane.showMessageDialog(null, "User has been blocked!", "Messages",
                                JOptionPane.INFORMATION_MESSAGE);

                        break;

                    case "Become invisible from a user":
                        String invisibleUser = JOptionPane.showInputDialog(null,
                                "Enter the seller you'd like to turn invisible to:", "Messages",
                                JOptionPane.QUESTION_MESSAGE);
                        customer.setInvisibleOthers(invisibleUser);
                        JOptionPane.showMessageDialog(null, "You are now invisible to this user!", "Messages",
                                JOptionPane.INFORMATION_MESSAGE);

                        break;

                    case "View Statistics":
                        String[] alphabetically = {"Yes", "No"};
                        String alphabet = (String) JOptionPane.showInputDialog(null,
                                "Would you like to sort the sellers alphabetically?", "Messages",
                                JOptionPane.QUESTION_MESSAGE, null, alphabetically, alphabetically[0]);
                        if (alphabet.equals("Yes")) {
                            ArrayList<String> sellerNamesSorted = new ArrayList<>();
                            for (int i = 0; i < sellerNames.size(); i++) {
                                sellerNamesSorted.add(sellerNames.get(i));
                            }
                            Collections.sort(sellerNamesSorted);
                            customer.showStatsDialog(name, sellerNamesSorted);
                        } else {
                            customer.showStatsDialog(name, sellerNames);
                        }
                        break;
                    case "Refresh Dashboard":
                        customer.showAllMessagesDialog(name);
                        break;
                    case "Exit":
                        JOptionPane.showMessageDialog(null, "Signing out...", "Messages",
                                JOptionPane.INFORMATION_MESSAGE);
                        continueMessages = false;
                        break;
                }

                if (primaryChoice.contains("store") || primaryChoice.contains("seller")) {

                    customer.showMessageDialog(sellerName, name);
                    String[] messageOptions = {"Send new message", "Edit a message", "Delete a message", "Export all messages", "Exit"};
                    String messageChoice = (String) JOptionPane.showInputDialog(null, "Enter your message",
                            "Messages", JOptionPane.QUESTION_MESSAGE, null, messageOptions, messageOptions[0]);

                    switch (messageChoice) {
                        case "Send new message":
                            String msg = "";
                            String[] typeOfMessageOptions = {"1. Type a message.", "2. Import a txt file."};
                            String typeOfMessage = (String) JOptionPane.showInputDialog(null,
                                    "Would you like to:", "Messages", JOptionPane.QUESTION_MESSAGE,
                                    null, typeOfMessageOptions, typeOfMessageOptions[0]);

                            if (typeOfMessage.contains("1")) { // type message
                                msg = JOptionPane.showInputDialog(null, "Type your message:",
                                        "Messages", JOptionPane.QUESTION_MESSAGE);

                            } else if (typeOfMessage.contains("2")) { // import txt file
                                boolean go;
                                do {
                                    String msgFilePath = JOptionPane.showInputDialog(null,
                                            "Please enter the filepath of the txt file:", "Messages",
                                            JOptionPane.QUESTION_MESSAGE);
                                    try {
                                        buffer = new BufferedReader(new FileReader(msgFilePath));
                                        msg = "";
                                        while ((line = buffer.readLine()) != null) {
                                            msg += line;
                                        }
                                        go = true;
                                        buffer.close();
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, "Issue with inputted " +
                                                "filepath. Please try again", "Messages", JOptionPane.ERROR_MESSAGE);
                                        go = false;
                                    }
                                } while (!go);
                            }

                            msg = customer.createMessage(sellerName, msg);
                                customer.addNewMessageToFile(msg);
                                customer.showMessageDialog(sellerName, name);

                                JOptionPane.showMessageDialog(null, "Message has been sent.", "Messages",
                                        JOptionPane.INFORMATION_MESSAGE);

                                client.sendMsg(name, sellerName, msg);

                            break;

                        case "Edit a message":
                            ArrayList<String> allMsgsArray = new ArrayList<>();
                            allMsgsArray = customer.getMyMessages(name, sellerName);
                            String[] allMsgs = allMsgsArray.toArray(new String[0]);
                            String oldMsg = (String) JOptionPane.showInputDialog(null,
                                    "Pick a message to edit:", "Messages", JOptionPane.QUESTION_MESSAGE,
                                    null, allMsgs, allMsgs[0]);
                            String newMsg = JOptionPane.showInputDialog(null, "Type your new message:",
                                    "Messages", JOptionPane.QUESTION_MESSAGE);
                            try {
                                newMsg = customer.createMessage(sellerName, newMsg);
                                customer.editMessage(oldMsg, newMsg);
                                customer.showMessageDialog(sellerName, name);
                                JOptionPane.showMessageDialog(null, "Message has been edited.", "Messages",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error. Message was not edited.", "Messages",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            break;

                        case "Delete a message":
                            ArrayList<String> allMsgsArray2 = new ArrayList<>();
                            allMsgsArray2 = customer.getMyMessages(name, sellerName);
                            String[] allMsgs2 = allMsgsArray2.toArray(new String[0]);
                            String whichMsg = (String) JOptionPane.showInputDialog(null,
                                    "Pick a message to delete:", "Messages", JOptionPane.QUESTION_MESSAGE,
                                    null, allMsgs2, allMsgs2[0]);

                            try {
                                customer.deleteMessage(whichMsg);
                                customer.showMessageDialog(sellerName, name);
                                JOptionPane.showMessageDialog(null, "Message has been deleted.", "Messages",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error. Message was not deleted.", "Messages",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            break;

                        case "Export all messages":
                            String filepath = (String) JOptionPane.showInputDialog(null, "Please enter the filepath name",
                                    "Messages", JOptionPane.QUESTION_MESSAGE);
                            ArrayList<String> messages = customer.getMessages(name, sellerName);
                            customer.exportMessages(messages, filepath, sellerName, name);

                            break;

                        case "Exit":
                            JOptionPane.showMessageDialog(null, "Signing out...", "Messages",
                                    JOptionPane.INFORMATION_MESSAGE);
                            continueMessages = false;
                            t1.interrupt();
                            break;
                    }
                }
            }
        }

        else {  // User is a seller
            String storeName = JOptionPane.showInputDialog(null, "What is your store name?",
                    "Messages", JOptionPane.QUESTION_MESSAGE);
            Sellers seller = new Sellers(storeName, name, email, password);
            if (logOrSign.equals("Signup")) {  // Seller is Signing up
                sellersList.add(seller);
                storeNames.add(storeName);
                sellerNames.add(name);
                seller.addSellerToFile(seller);
                JOptionPane.showMessageDialog(null, "Account created!", "Messages",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {   // Seller is logging in
                exist = false;
                for (int i = 0; i < sellersList.size(); i++) {
                    if (seller.getStoreName().equals(sellersList.get(i).getStoreName())) {
                        if (seller.getName().equals(sellersList.get(i).getName())) {
                            if (seller.getEmail().equals(sellersList.get(i).getEmail())) {
                                if (seller.getPassword().equals(sellersList.get(i).getPassword())) {
                                    exist = true;
                                }
                            }
                        }
                    }
                }
                if (exist) {
                    JOptionPane.showMessageDialog(null, "You are logged in!", "Messages",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Account does not exist!", "Messages",
                            JOptionPane.ERROR_MESSAGE); // should go back to CoS question
                }
            }
            if (exist) {
                seller.showAllMessagesDialog(name);
                String another;
                do {
                    String[] sellerOptions = {"1. View a list of customers to message.", "2. Search for a customer to message.",
                            "3. View Store Statistics.", "4. Block a Customer", "5. Turn invisible to a customer.",
                            "6. See Dashboard (Refreshed).", "7. Exit"};
                    String sellerOption = (String) JOptionPane.showInputDialog(null,
                            "Please pick from a list of options:", "Messages",
                            JOptionPane.QUESTION_MESSAGE, null, sellerOptions, sellerOptions[0]);
                    String messageChoice = "";

                    if (sellerOption.contains("1") || sellerOption.contains("2")) { // Message a Customer
                        String customerName = "";
                        String msg = "";
                        Boolean blocked;
                        ArrayList<String> invisibleUsers = new ArrayList<>();
                        BufferedReader buffer = new BufferedReader(new FileReader(filePathForInvisibleList));
                        String line;
                        while ((line = buffer.readLine()) != null) {
                            if (line.contains(";" + name)) {
                                String user = line.replace(";" + name, "");
                                invisibleUsers.add(user);
                            }
                        }
                        buffer.close();
                        if (sellerOption.contains("1")) { // View Customers
                            do {
                                ArrayList<String> custOptionsArray = new ArrayList<>();
                                for (int i = 0; i < customerNames.size(); i++) {
                                    String temp = customerNames.get(i);
                                    if (!invisibleUsers.contains(temp)) {
                                        custOptionsArray.add(temp);
                                    }
                                }
                                String[] custOptions = custOptionsArray.toArray(new String[0]);
                                customerName = (String) JOptionPane.showInputDialog(null,
                                        "Choose a customer to message:", "Messages",
                                        JOptionPane.QUESTION_MESSAGE, null, custOptions, custOptions[0]);

                                buffer = new BufferedReader(new FileReader(filePathForBlockList));
                                blocked = false;
                                while ((line = buffer.readLine()) != null) {
                                    if (line.contains(";" + name)) {
                                        if (line.contains(customerName)) {
                                            blocked = true;
                                            JOptionPane.showMessageDialog(null, "You are blocked by this customer.",
                                                    "Messages", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                }
                                buffer.close();
                            } while (blocked);

                        } else if (sellerOption.contains("2")) { // Search for Customer
                            boolean customerExists = false;
                            do {
                                customerName = JOptionPane.showInputDialog(null, "What customer " +
                                        "would you like to message?", "Messages", JOptionPane.QUESTION_MESSAGE);
                                for (int i = 0; i < customerNames.size(); i++) {
                                    if (customerNames.get(i).contains(customerName) && !invisibleUsers.contains(customerName)) {
                                        customerExists = true;
                                    }
                                }
                                buffer = new BufferedReader(new FileReader(filePathForBlockList));
                                blocked = false;
                                while ((line = buffer.readLine()) != null) {
                                    if (line.contains(";" + name)) {
                                        if (line.contains(customerName)) {
                                            blocked = true;
                                        }
                                    }
                                }
                                buffer.close();

                                if (!customerExists) {
                                    JOptionPane.showMessageDialog(null, "Customer does not exist!",
                                            "Messages", JOptionPane.ERROR_MESSAGE);
                                } else if (blocked) {
                                    JOptionPane.showMessageDialog(null, "You are blocked by this customer.",
                                            "Messages", JOptionPane.ERROR_MESSAGE);
                                }
                            } while (!customerExists || blocked);
                        }
                        seller.showMessageDialog(name, customerName);
                        String[] messageOptions = {"1. Send a new message.", "2. Edit a  message.", "3. Delete a message.",
                                "4. Export all messages.", "5. Exit"};
                        messageChoice = (String) JOptionPane.showInputDialog(null,
                                "What would you like to do:", "Messages",
                                JOptionPane.QUESTION_MESSAGE, null, messageOptions, messageOptions[0]);

                        if (messageChoice.contains("1")) { // send new message
                            String[] typeOfMessageOptions = {"1. Type a message.", "2. Import a txt file."};
                            String typeOfMessage = (String) JOptionPane.showInputDialog(null,
                                    "Would you like to:", "Messages", JOptionPane.QUESTION_MESSAGE,
                                    null, typeOfMessageOptions, typeOfMessageOptions[0]);

                            if (typeOfMessage.contains("1")) { // type message
                                msg = JOptionPane.showInputDialog(null, "Type your message:",
                                        "Messages", JOptionPane.QUESTION_MESSAGE);

                            } else if (typeOfMessage.contains("2")) { // import txt file
                                boolean go;
                                do {
                                    String msgFilePath = JOptionPane.showInputDialog(null,
                                            "Please enter the filepath of the txt file:", "Messages",
                                            JOptionPane.QUESTION_MESSAGE);
                                    try {
                                        buffer = new BufferedReader(new FileReader(msgFilePath));
                                        msg = "";
                                        while ((line = buffer.readLine()) != null) {
                                            msg += line;
                                        }
                                        go = true;
                                        buffer.close();
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, "Issue with inputted " +
                                                "filepath. Please try again", "Messages", JOptionPane.ERROR_MESSAGE);
                                        go = false;
                                    }
                                } while (!go);
                            }
                            String fullMessage = seller.createMessage(customerName, msg);
                            seller.addNewMessageToFile(fullMessage);
                            seller.showMessageDialog(name, customerName);
                            JOptionPane.showMessageDialog(null, "Message has been sent.", "Messages",
                                    JOptionPane.INFORMATION_MESSAGE);

                        } else if (messageChoice.contains("2")) { // edit message
                            ArrayList<String> allMsgsArray = new ArrayList<>();
                            allMsgsArray = seller.getMyMessages(customerName, name);
                            String[] allMsgs = allMsgsArray.toArray(new String[0]);
                            String oldMsg = (String) JOptionPane.showInputDialog(null,
                                    "Pick a message to edit:", "Messages", JOptionPane.QUESTION_MESSAGE,
                                    null, allMsgs, allMsgs[0]);
                            String newMsg = JOptionPane.showInputDialog(null, "Type your new message:",
                                    "Messages", JOptionPane.QUESTION_MESSAGE);
                            newMsg = seller.createMessage(customerName, newMsg);
                            try {
                                seller.editMessage(oldMsg, newMsg);
                                seller.showMessageDialog(name, customerName);
                                JOptionPane.showMessageDialog(null, "Message has been edited.", "Messages",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error. Message was not edited.", "Messages",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        } else if (messageChoice.contains("3")) { // delete message
                            ArrayList<String> allMsgsArray = new ArrayList<>();
                            allMsgsArray = seller.getMyMessages(customerName, name);
                            String[] allMsgs = allMsgsArray.toArray(new String[0]);
                            String whichMsg = (String) JOptionPane.showInputDialog(null,
                                    "Pick a message to delete:", "Messages", JOptionPane.QUESTION_MESSAGE,
                                    null, allMsgs, allMsgs[0]);
                            try {
                                seller.deleteMessage(whichMsg);
                                seller.showMessageDialog(name, customerName);
                                JOptionPane.showMessageDialog(null, "Message has been deleted.", "Messages",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error. Message was not deleted.", "Messages",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        } else if (messageChoice.contains("4")) { // export all messages
                            ArrayList<String> allMsgs = new ArrayList<>();
                            allMsgs = seller.getMessages(customerName, name);
                            String filepath = (String) JOptionPane.showInputDialog(null, "Please enter the filepath name",
                                    "Messages", JOptionPane.QUESTION_MESSAGE);
                            seller.exportMessages(allMsgs, filepath, name, customerName);

                        }

                    } else if (sellerOption.contains("3")) { // View Store Statistics
                        String[] alphabetically = {"Yes", "No"};
                        String alphabet = (String) JOptionPane.showInputDialog(null,
                                "Would you like to sort the customers alphabetically?", "Messages",
                                JOptionPane.QUESTION_MESSAGE, null, alphabetically, alphabetically[0]);
                        if (alphabet.equals("Yes")) {
                            ArrayList<String> customerNamesSorted = new ArrayList<>();
                            for (int i = 0; i < customerNames.size(); i++) {
                                customerNamesSorted.add(customerNames.get(i));
                            }
                            Collections.sort(customerNamesSorted);
                            seller.showStatsDialog(name, customerNamesSorted);
                        } else {
                            seller.showStatsDialog(name, customerNames);
                        }

                    } else if (sellerOption.contains("4")) { // Block a customer
                        String blockedCust = JOptionPane.showInputDialog(null,
                                "Enter the customer you'd like to block:", "Messages", JOptionPane.QUESTION_MESSAGE);
                        seller.blockCustomer(name, blockedCust);
                        JOptionPane.showMessageDialog(null, "User has been blocked!", "Messages",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else if (sellerOption.contains("5")) { // Turn invisible to a customer
                        String invisibleCust = JOptionPane.showInputDialog(null,
                                "Enter the customer you'd like to turn invisible to:", "Messages",
                                JOptionPane.QUESTION_MESSAGE);
                        seller.makeInvisibleToCustomer(name, invisibleCust);
                        JOptionPane.showMessageDialog(null, "You are now invisible to this user!", "Messages",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else if (sellerOption.contains("6")) { // See refreshed dashboard
                        seller.showAllMessagesDialog(name);
                    }

                    if (sellerOption.contains("Exit") || messageChoice.contains("Exit")) {
                        another = "No";
                    } else {
                        String[] anotherSelection = {"Yes", "No"};
                        another = (String) JOptionPane.showInputDialog(null,
                                "Would you like to make another selection?", "Messages",
                                JOptionPane.QUESTION_MESSAGE, null, anotherSelection, anotherSelection[0]);
                    }

                } while (another.equals("Yes"));

                JOptionPane.showMessageDialog(null, "Signing Out . . .", "Messages",
                        JOptionPane.INFORMATION_MESSAGE);
                client.sendMsg(name, name, "endSocket");
            }
        }
    }
}
