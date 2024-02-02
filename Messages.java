import java.util.Scanner;
import java.util.*;
import java.io.*;
/**
 * CS180 Project 4
 *
 * This program holds the Messages Class for Project 4
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */
public class Messages {
    private Customers customer;
    private Sellers seller;
    private static final String WELCOME = "Welcome to the marketplace!";
    private static final String CUSTOMER_OR_SELLER = "Are you a customer or seller?";
    private static final String CUSTOMER_2_SELLER = "1.Customer\n2.Seller";
    private static final String LOGIN_OR_SIGNUP_WITH_A_NEW_ACCOUNT
        = "Would you like to login or signup with a new account";
    private static final String LOGIN_OR_SIGNUP = "1.Login\n2.Signup";
    private static final String FILE_PATH_FOR_SELLERS = "sellers.txt";
    private static final String FILE_PATH_FOR_CUSTOMERS = "customers.txt";
    private static final String FILE_PATH_FOR_MESSAGES = "messages.txt";
    private static final String FILE_PATH_FOR_BLOCK_LIST = "block_list.txt";
    private static final String FILE_PATH_FOR_INVISIBLE_LIST = "invisible_list.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Sellers> sellersList = new ArrayList<>();
        ArrayList<Customers> customersList = new ArrayList<>();
        ArrayList<String> storeNames = new ArrayList<>();
        ArrayList<String> customerNames = new ArrayList<>();
        ArrayList<String> sellerNames = new ArrayList<>();

        try {
            BufferedReader buffer = new BufferedReader(new FileReader(FILE_PATH_FOR_SELLERS));
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
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            BufferedReader buffer = new BufferedReader(new FileReader(FILE_PATH_FOR_CUSTOMERS));
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
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println(WELCOME);

        boolean exist = true;
        do {
            System.out.println(CUSTOMER_OR_SELLER);
            System.out.println(CUSTOMER_2_SELLER);
            int cos = scanner.nextInt();
            scanner.nextLine();

            System.out.println(LOGIN_OR_SIGNUP_WITH_A_NEW_ACCOUNT);
            System.out.println(LOGIN_OR_SIGNUP);
            int los = scanner.nextInt();
            scanner.nextLine();

            System.out.println("What is your name?");
            String name = scanner.nextLine();
            System.out.println("What is your email?");
            String email = scanner.nextLine();
            System.out.println("What is your password?");
            String password = scanner.nextLine();

            if (cos == 1) {   //if user is a Customer
                Customers customer = new Customers(name, email, password);

                if (los == 1) {  //if logging in
                    // check if the customer exists
                    if (!(customersList.contains(customer))) {
                        System.out.println("This user does not exist!");
                        exist = false;   //back to cos question
                    }
                } else {   //customer is signing up
                    customersList.add(customer);
                    System.out.println("Customer created!");
                }

                // there needs to be a login or signup mechanism here,
                // check if it exists in customersList if login
                // create new if signup

                if (exist) {
                    int selection;
                    do {
                        System.out.println("Please make a selection");
                        System.out.println("1.Search for a store");
                        System.out.println("2.Search for a seller");
                        System.out.println("3.Block a user");
                        System.out.println("4. Become invisible from a user");
                        System.out.println("5.View Dashboard");
                        System.out.println("6.Exit");
                        selection = scanner.nextInt();
                        scanner.nextLine();

                        boolean sendMessage;
                        String chosenStore;
                        String chosenSeller;
                        String message;

                        boolean continueMessages = true;
                        do {
                            switch (selection) {
                                case 1:
                                    // there needs to also be choices here for editing, deleting, and exporting msgs

                                    System.out.println("Please enter the name of the store");

                                    //implement if the customer is blocked or invisible

                                    String storeName = scanner.nextLine();

                                    chosenStore = customer.getSellerNameByStoreName(storeName);

                                    System.out.println("Please make a selection:");
                                    System.out.println("1. Send new message");
                                    System.out.println("2. Edit a message");
                                    System.out.println("3. Delete a message");
                                    System.out.println("4. Export messages to a csv file");
                                    System.out.println("5. Exit");
                                    int messageSelection = scanner.nextInt();
                                    scanner.nextLine();

                                    switch (messageSelection) {
                                        case 1:
                                            System.out.println("What message would you like to send");
                                            message = scanner.nextLine();
                                            sendMessage = customer.sendMessageToSeller(chosenStore, message);
                                            break;
                                        case 2:
                                            System.out.println("Which message would you like to edit?");
                                            String oldMessage = scanner.nextLine();
                                            System.out.println("What would you like to edit it to?");
                                            String newMessage = scanner.nextLine();

                                            customer.editMessage(oldMessage, newMessage);
                                            break;
                                        case 3:
                                            System.out.println("What message would you like to delete");
                                            String deleteMessage = scanner.nextLine();

                                            customer.deleteMessage(deleteMessage);
                                            break;
                                        case 4:
                                            System.out.println("Please enter the file path name");
                                            String filepath = scanner.nextLine();

                                            customer.exportCustomerRelatedMsgs(filepath);
                                            break;
                                        case 5:
                                            System.out.println("Signing out...");
                                            continueMessages = true;
                                            break;
                                        default:
                                            System.out.println("Please make a valid selection!");
                                            continueMessages = false;
                                            break;
                                    }

                                    break;
                                case 2:
                                    //implement if the customer is blocked or invisible
                                    System.out.println("Please enter the name of the seller");
                                    for (int i = 0; i < sellersList.size(); i++) {
                                        System.out.println(sellersList.get(i));
                                    }
                                    chosenSeller = scanner.nextLine();
                                    System.out.println("What message would you like to send?");
                                    message = scanner.nextLine();
                                    sendMessage = customer.sendMessageToSeller(chosenSeller, message);
                                    break;
                                case 3:
                                    System.out.println("What is the name of the user that you would like to block?");
                                    String blockUser = scanner.nextLine();
                                    customer.setBlockOthers(blockUser);
                                    System.out.printf("%s is now blocked!\n", blockUser);
                                    break;
                                case 4:
                                    System.out.println("Who would you like to become invisible to?");
                                    String invisibleUser = scanner.nextLine();
                                    customer.setInvisibleOthers(invisibleUser);
                                    System.out.printf("You are now invisible to %s\n", invisibleUser);
                                    break;
                                case 5:
                                    boolean sort;
                                    boolean valid = true;
                                    do {
                                        System.out.println("Would you like to sort the dashboard?");
                                        System.out.println("1.Yes\n2.No");
                                        int yOrN = scanner.nextInt();
                                        if (yOrN == 1) {
                                            sort = true;
                                        } else {
                                            sort = false;
                                        }
                                        if (yOrN > 2 || yOrN < 1) {
                                            System.out.println("Please make a valid selection!");
                                            valid = false;
                                        }
                                    } while (valid == false);

                                    customer.displayDashBoard(sort);
                                    break;
                                case 6:
                                    System.out.println("Signing out...");
                                    continueMessages = true;
                                    break;
                                default:
                                    System.out.println("Please make a valid selection!");
                                    continueMessages = false;
                                    break;
                            }
                        } while (continueMessages == false);

                    } while (selection != 6);
                }

            } else { //user is a seller
                System.out.println("What is your store name?");
                String storeName = scanner.nextLine();
                Sellers seller = new Sellers(storeName, name, email, password);

                if (los == 1) { // user wants to login

                    if (sellersList.contains(seller)) {
                        exist = true;
                        System.out.println("You are logged in!");
                    } else {
                        exist = false;
                        System.out.println("Account does not exist"); // should go back to cos question
                    }

                } else if (los == 2) { // user wants to sign up
                    sellersList.add(seller);
                    storeNames.add(storeName);
                    sellerNames.add(name);
                    System.out.println("Account created!");
                }
                int another;
                if (exist) {
                    do {
                        int optionPick = 1;
                        do {
                            if (optionPick < 1 || optionPick > 5) {
                                System.out.println("Error. Invalid input. Try Again.");
                            }
                            System.out.println("Please pick from list of options:");
                            System.out.println("1. View a list of customers to message.");
                            System.out.println("2. Search for a specific customer to message");
                            System.out.println("3. View store statistics.");
                            System.out.println("4. Block a customer.");
                            System.out.println("5. Turn invisible to a customer.");
                            optionPick = scanner.nextInt();
                            scanner.nextLine();
                        } while (optionPick < 1 || optionPick > 5);

                        // message customer
                        if (optionPick == 1 || optionPick == 2) {
                            int whoToMsg = 0;
                            if (optionPick == 1) { // choosing customer from list to msg
                                System.out.println("Choose a customer from this list:");
                                for (int i = 0; i < customerNames.size(); i++) {
                                    System.out.print(i + 1);
                                    System.out.print(". ");
                                    System.out.println(customerNames.get(i));
                                }
                                whoToMsg = scanner.nextInt() - 1; // index of person in customerNames and customersList
                                scanner.nextLine();

                            } else if (optionPick == 2) { // searching for customer to msg
                                do {
                                    whoToMsg = -1;
                                    System.out.println("Please enter a customer to search for:");
                                    String searchCust = scanner.nextLine();
                                    for (int i = 0; i < customerNames.size(); i++) {
                                        if (customerNames.get(i).contains(searchCust)) {
                                            whoToMsg = i;
                                        }
                                    }
                                    if (whoToMsg == -1) {
                                        System.out.println("Cannot find this user. Please try again.");
                                    }
                                } while (whoToMsg == -1);
                            }
                            int typeOfMessage = 1;
                            do {
                                if (typeOfMessage < 1 || typeOfMessage > 4) {
                                    System.out.println("Invalid input. Please try again");
                                }
                                System.out.println("Would you like to:");
                                System.out.println("1. Send a new message.");
                                System.out.println("2. Edit a message.");
                                System.out.println("3. Delete a message.");
                                System.out.println("4. Export all messages.");
                                typeOfMessage = scanner.nextInt();
                                scanner.nextLine();
                            } while (typeOfMessage < 1 || typeOfMessage > 4);

                            if (typeOfMessage == 1) { // sending new message
                                int typeOrImport;
                                String msg = "";
                                do {
                                    System.out.println("Would you like to:");
                                    System.out.println("1. Type a message.");
                                    System.out.println("2. Import a txt file.");
                                    typeOrImport = scanner.nextInt();
                                    if (typeOrImport != 1 && typeOrImport != 2) {
                                        System.out.println("Invalid input. Please try again.");
                                    }
                                } while (typeOrImport != 1 && typeOrImport != 2);
                                if (typeOrImport == 1) { // type msg
                                    System.out.println("Please type your message:");
                                    msg = scanner.nextLine();
                                } else if (typeOrImport == 2) { // import msg
                                    boolean go;
                                    do {
                                        System.out.println("Please enter a txt filepath to import");
                                        String msgFilePath = scanner.nextLine();
                                        try {
                                            BufferedReader buffer = new BufferedReader(new FileReader(msgFilePath));
                                            String line;
                                            msg = "";
                                            while ((line = buffer.readLine()) != null) {
                                                msg += line;
                                            }
                                            go = true;
                                        } catch (Exception e) {
                                            System.out.println("Issue with inputted filepath. Please try again");
                                            go = false;
                                        }
                                    } while (!go);
                                }
                                String fullMessage = seller.createMessage(customerNames.get(whoToMsg), msg);
                                seller.addNewMessageToFile(fullMessage);

                            } else if (typeOfMessage == 2) { // editing message
                                ArrayList<String> allMsgs = new ArrayList<>();
                                allMsgs = seller.getMessages(name, customerNames.get(whoToMsg));
                                System.out.println("Pick a message to edit.");
                                for (int i = 0; i < allMsgs.size(); i++) {
                                    System.out.print(i + 1);
                                    System.out.print(". ");
                                    System.out.println(allMsgs.get(i));
                                }
                                int whichMsg = scanner.nextInt() - 1;
                                scanner.nextLine();
                                System.out.println("What will the new message say");
                                String newMsg = scanner.nextLine();
                                newMsg = seller.createMessage(customerNames.get(whoToMsg), newMsg);
                                try {
                                    seller.editMessage(allMsgs.get(whichMsg), newMsg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Message has been edited!");

                            } else if (typeOfMessage == 3) { // deleting message
                                ArrayList<String> allMsgs = new ArrayList<>();
                                allMsgs = seller.getMessages(name, customerNames.get(whoToMsg));
                                System.out.println("Pick a message to delete.");
                                for (int i = 0; i < allMsgs.size(); i++) {
                                    System.out.print(i + 1);
                                    System.out.print(". ");
                                    System.out.println(allMsgs.get(i));
                                }
                                int whichMsg = scanner.nextInt() - 1;
                                scanner.nextLine();
                                // seller.deleteMessage(allMsgs.get(whichMsg));
                                System.out.println("Message has been deleted!");

                            } else if (typeOfMessage == 4) { // exporting all messages
                                ArrayList<String> allMsgs = new ArrayList<>();
                                allMsgs = seller.getMessages(name, customerNames.get(whoToMsg));
                                // seller.exportMessages(allMsgs);                                 // need this function
                                System.out.println("File has been exported. It is called ...");
                            }

                            // view store statistics
                        } else if (optionPick == 3) {
                            
                            /*
                            System.out.println("Would you like the statistics to be sorted?");
                            System.out.println("1. Yes");
                            System.out.println("2. No");
                            int sort = scanner.nextInt();
                            scanner.nextLine();
                            boolean isSort;
                            if (sort == 1) {
                                isSort = true;    
                            } else if (sort == 2) {
                                isSort = false;
                            }
                            seller.displayDashboard(
                            */

                            // block a customer
                        } else if (optionPick == 4) {
                            System.out.println("Which customer would you like to block?");
                            String blockedCust = scanner.nextLine();
                            String rule = storeName + ";" + blockedCust;
                            seller.blockCustomer(storeName, blockedCust);
                            System.out.println("Customer name has been added to blocklist");

                            // turn invisible to customer
                        } else if (optionPick == 5) {
                            System.out.println("Which customer would you like to be invisible to?");
                            String invisibleCust = scanner.nextLine();
                            String rule = storeName + ";" + invisibleCust;
                            seller.makeInvisibleToCustomer(storeName, invisibleCust);
                        }


                        another = 1;
                        do {
                            if (another != 1 && another != 2) {
                                System.out.println("Invalid input. Please try again.");
                            }
                            System.out.println("Make another action?");
                            System.out.println("1. Yes");
                            System.out.println("2. No");
                            another = scanner.nextInt();
                            scanner.nextLine();
                        } while (another != 1 && another != 2);
                    } while (another == 1);
                    System.out.println("Signing out...");
                }
            }

            if (cos > 2 || cos < 1 || los > 2  || los < 1) {
                exist = false;
            }

        } while (exist == false);
    }
}
