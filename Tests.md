To run the program, first run P5Server.java, and then run Client.java.

Sellers.java, Customers.java, and P5Client.java must be downloaded.

Other files were used for sharing code and personal testing, and are not used in the final product.

These test cases are to be done in order, keeping the data in the text files!

Test 1: Signing up

Steps:

  1. User launches application, "Welcome" message pops up. User clicks ok.
  2. User selects that they are a seller and clicks ok.
  3. User selects signup and clicks ok.
  4. User enters their name "John" and clicks ok.
  5. User enters their email "JohnDoe@gmail.com" and clicks ok.
  6. User enters their password "123" and clicks ok.
  7. User enters their store name "Store" and clicks ok.
  8. "Account created" appears, click ok and choose exit in the drop down. User is signed out.

Expected result: Should be saved in the sellers.txt file as "Store;John;JohnDoe@gmail.com;123".  

Test status: Passed

Test 2: Login

Steps:

  1. User launches application, "Welcome" message pops up. User clicks ok.
  2. User selects that they are a seller and clicks ok.
  3. User selects login and clicks ok.
  4. User enters their name "John" and clicks ok.
  5. User enters their email "JohnDoe@gmail.com" and clicks ok.
  6. User enters their password "123" and clicks ok.
  7. User enters their store name "Store" and clicks ok.

Expected result: User is logged in, press ok and the next dropdown appears. 

Test status: Passed

Test 3: Sending Messages

Steps:

  1. Create an account for a customer named "Amy".
  2. After the account has been created popup, click ok and the choice dropdown will appear.
  3. Choose "View a list of stores" and click ok.
  4. A dropdown should appear with all of the stores. If John is the only seller that has been created, then "Store" will be the only choice.
  5. Choose "Store" and click ok. The message dialogue will pop up between Amy and John (will be empty if no messages have been sent).
  6. Choose "Send new message" and click ok.
  7. Choose "Type a message" and click ok.
  8. Type any message you would like to send to John.

Expected result: "Message has been sent." The messages between Amy and John now have the message sent in the format of "sender;receiver;time;message"

Test status: Passed

Test 4: Editing Messages

Steps:

  1. Login to the customer account "Amy".
  2. When the first choice dropdown pops up, choose "Search for a seller" and press ok.
  3. Search for the seller "John".
  4. There should be a message that Amy has sent to John if Test 3 has been done. Choose "Edit a message" and click ok.
  5. Choose a message to edit and click ok.
  6. Type the new message.

Expected result: "Message has been edited." The messages will appear between Amy and John and the message that was edited should be updated.

Test status: Passed

Test 5: Deleting Messages

Steps:

  1. Login to the customer account "Amy".
  2. When the first choice dropdown pops up, choose "Search for a store" and press ok.
  3. Search for the store "Store".
  4. There should be a message that Amy sent to John if previous tests has been done. Choose "Delete a message" and click ok.
  5. Choose a message to delete and click ok.

Expected result: "Message has been deleted." The messages will appear and the deleted message should no longer be there.

Test status: Passed

Test 6: Exporting Messages

Steps:

  1. Login to customer account "Amy".
  2. Ensure that there are existing messages between Amy and John.
  3. View list of stores and choose Store to open messages with John.
  4. Choose "Export all Messages" and click ok.
  5. Enter a filepath name such as "AmyJohnMsgs" and click ok.

Expected result: "Messages have been exported." There should be a file that is named the entered filepath and contains all the messages between Amy and John.

Test status: Passed

Test 7: Statistics

Steps:

  1. Make sure there is a seller account that has been messaged many times by many different customers.
  2. Sign into this seller account, and choose "3. View store statistics" from the dropdown and click ok.
  3. Choose "Yes" to sorting customers alphabetically and click ok.

Expected result: A frame that displays statistics. Should say the most common word and the number of messages sent with each customer, with the names in alphabetical order.

Test status: Passed

Test 8: Blocking

Steps:

  1. Login to the John seller account.
  2. From the dropdown, choose "4. Block a Customer" and click ok.
  3. Enter "Amy" as the customer you would like to block, and click ok.
  4. "User has been blocked!" will appear. Click ok and choose "No" to making another selection, which will sign John out.
  5.  Run the program again and login to the Amy customer account.
  6.  Search for a store and search for "Store" (John's store).

Expected result: A popup that says "You are blocked by this seller."

Test status: Passed

Test 9: Invisible

Steps:

  1. Login to the John seller account (make sure that John does not have Amy blocked, can edit the block_list txt file.)
  2. From the dropdown, choose "5. Turn invisible to a customer." and click ok.
  3. Type "Amy" as the customer you'd like to turn invisible to, and click ok.
  4. "You are now invisible to this user!" will appear. Click ok and choose "No" to making another selection, signing out.
  5. Run the program again and login to the Amy customer account.
  6. Choose "View a list of sellers" and click ok.

Expected result: In the seller dropdown, John will not appear, because he is invisible to Amy.

Test status: Passed
