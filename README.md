# project05

Run P5Server.java and then Client.java to run the application.
Sellers.java, Customers.java, and P5Client.java must also be downloaded.
Other files are scratch code, don't need to be saved.

Client.java: 
 - Has the flow of communication.
 
 Sellers.java:
  - Can create a seller object, has many seller functions that are used by Client.java
  
 Customers.java:
  - Can create a customer object, has many customer functions that are used by Client.java

## 1. design

1. P5Server
- Server should be multiple threads to support multiple clients communication
- Since assignment require : multiple users must be able to access the application at once.

2. P5Client
- GUI + Socket
- 2 threads: one for GUI I/O, another for receiving messages from server



## 2. network communication format

1. format: CMD + Message

e.g: 
- client send "CMDSendMsgFromConsumerToSeller;C1;S1;2023-04-07 15:37:32;Yes. How much dose it cost?" to server
- server parse cmd, and send "CMDSendMsgFromConsumerToSeller;C1;S1;2023-04-07 15:37:32;Yes. How much dose it cost?" to Seller S1

2. CMDs (will add more latter)
- SendMsg
   - CMDSendMsgFromConsumerToSeller;C1;S1;2023-04-07 15:37:32;Yes. How much dose it cost?
   - CMDSendMsgFromSellerToConsumer;C1;S1;2023-04-07 15:37:32;Yes. How much dose it cost?
- Register
   - CMDRegisterConsumer;C1
   - CMDRegisterSeller;S1
- Login
   - CMDLoginConsumer;C1;PasswordEncrypt
- exit
   - CMDExit;




## 3. import GUIs

1. Core GUIs should be implemented at client side.
- Input server host name and port
- Login & register
- Send message to other
- Edit Message
- Delete Message
- Export Message
- Set Block
- Set Invisible
- Display dashboard (data statistic)


## 4. example for whole workflow

1. client method for verification user name and password
- public boolean isUsernameAndPasswordCorrect(String userName, String password) at P5Client.java

2. server process the verification command
- public String processCMDLoginConsumer(String content) at P5Server.java






# project04

## 1. init design 

1. all messages should be stored at `messages.txt`, example as below

file format is: sender;receiver;timestamp;status;content

file content example is 

```
S1;C1;2023-04-07 15:35:27;Do you want to buy this book?
C1;S1;2023-04-07 15:37:32;Yes. How much dose it cost?
S1;C1;2023-04-07 15:42:41;256$
C1;S1;2023-04-07 15:47:23;Yes. I will pay you latter.
C2;S2;2023-04-07 15:48:32;How much?
S2;C2;2023-04-07 15:51:41;512$
C2;S2;2023-04-07 15:53:23;OK.
```

2. file `sellers.txt` and `customers.txt` store all the user informations for seller and customer, example as below

`sellers.txt` file format is: store name;seller name;email;password

one store related to one seller for simple.

- `sellers.txt`

```
Store1;S1;111@seller.com;111
Store2;S2;222@seller.com;222
Store3;S3;333@seller.com;333
```

`customers.txt` file format is: customer name;email;password


- `customers.txt`

```
C1;111@customer.com;111
C2;222@customer.com;222
```


3. the data to block another user or become invisible is stored at file `block_list.txt` and `invisible_list.txt`


- `block_list.txt`

```
C1;S1
C1;S2
C1;S3
```

This means C1 want to block S1, S2 and S3


- `invisible_list.txt`

```
C2;S2
C2;S3
```

This means C2 set himself invisible to S2 and S3. 


## 2. implementation

1. messages is transfered by files (File I/O) at above files.

2. Seller and Customer should read,parse and write the files for their behaviors.


## 3. how to use class Customers

Customers examples as below

```java
// create a Customer
Customers c1 = new Customers("C1", "ccc111@aaa.com", "123");
c1.loadSellers();
c1.loadCustomers();
System.out.println( "Sellers="+c1.getSellers() );
System.out.println( "Customers"+c1.getCustomers() );
System.out.println( "StoreNames="+c1.getStoreNames() );
System.out.println( "createMessage="+c1.createMessage("S1", "hello S1.") );
System.out.println( "sendMessageToSeller="+c1.sendMessageToSeller("S1", "hello S1.") );
System.out.println( "sendMessageToStore="+c1.sendMessageToStore("Store2", "hello Store2.") );
c1.displayDashBoard(true);
// block and invisible usages
c1.setBlockOthers("S1");
c1.setInvisibleOthers("S1");
// edit, delete and export messages
c1.editMessage("hello Store2.","hello Store22.");
c1.deleteMessage("new msg for test edit");
c1.exportCustomerRelatedMsgs("customerMsgs.csv");

```
**List of who submitted which parts of the assignment on Brightspace and Vocareum. 
1. Hari Meka - Submitted Report on Brightspace. 
2. Hari Meka - Submitted Presentation on Brightspace.
3. Brittany Geer - Submitted Vocareum workspace.
