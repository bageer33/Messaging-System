import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * CS180 Project 5
 *
 * This program holds the TestCustomers Class for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */

public class TestCustomers {

    @Test
    public void testCreateCustomerForName() throws Exception {
        String nameTrue = "C10";
        Customers c1 = new Customers(nameTrue, "aaa@bbb.com", "123");
        String name = c1.getName();
        assertEquals(nameTrue, name);
    }
    @Test
    public void testCreateCustomerForPassword() throws Exception {
        String passwordTrue = "123";
        Customers c1 = new Customers("C10", "aaa@bbb.com", passwordTrue);
        String password = c1.getPassword();
        assertEquals(passwordTrue, password);
    }
    @Test
    public void testCreateCustomerForEmail() throws Exception {
        String emailTrue = "aaa@bbb.com";
        Customers c1 = new Customers("C10", emailTrue, "123");
        String email = c1.getEmail();
        assertEquals(emailTrue, email);
    }
    @Test
    public void testCreateCustomerForSetName() throws Exception {
        String nameTrue = "C12";
        Customers c1 = new Customers("C10", "aaa@bbb.com", "123");
        c1.setName(nameTrue);
        String name = c1.getName();
        assertEquals(nameTrue, name);
    }
    @Test
    public void testCreateCustomerForSetPassword() throws Exception {
        String passwordTrue = "123";
        Customers c1 = new Customers("C10", "aaa@bbb.com", "000");
        c1.setPassword(passwordTrue);
        String password = c1.getPassword();
        assertEquals(passwordTrue, password);
    }
    @Test
    public void testCreateCustomerForSetEmail() throws Exception {
        String emailTrue = "aaa@bbb.com";
        Customers c1 = new Customers("C10", "xxx", "123");
        c1.setEmail(emailTrue);
        String email = c1.getEmail();
        assertEquals(emailTrue, email);
    }
    @Test
    public void testCreateCustomerForGetConsumersFromFile() throws Exception {
        Customers c1 = new Customers("C10", "xxx", "123");
        c1.loadCustomers();
        ArrayList<Customers> r = c1.getCustomers();
        assertEquals("C1", r.get(0).getName());
        assertEquals("C2", r.get(1).getName());
        assertEquals("C3", r.get(2).getName());
    }
    @Test
    public void testCreateCustomerCreateMsg() throws Exception {
        Customers c1 = new Customers("C10", "xxx", "123");
        String msgOrigin = "Yes. I will pay you latter";
        String msgFull = c1.createMessage("SSS", msgOrigin);
        String[] tokens = msgFull.split(";");
        String sender = tokens[0];
        String receiver = tokens[1];
        String msg = tokens[3];
        assertEquals("C10", sender);
        assertEquals("SSS", receiver);
        assertEquals("Yes. I will pay you latter", msg);
    }

    @Test
    public void testCreateCustomerForEditMsg() throws Exception {
        Customers c1 = new Customers("C1","111@customer.com", "111");
        String m2 = "hello S1......";
        c1.editMessage("hello S1.", m2);

        boolean isEdited = false;
        String filePath = c1.getFilePathForMessages();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePath));
            String strLine;
            int line = 0;
            while ( (strLine = buffer.readLine()) != null )
            {
                line++;
                strLine = strLine.strip();
                String[] tokens = strLine.split(";");
                if (tokens.length == 4) {
                    String msg = tokens[3];
                    if (msg.equals(m2) == true)
                    {
                        isEdited = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }


        assertEquals(isEdited, true);
    }


    @Test
    public void testCreateCustomerForBlockOthers() throws Exception {
        Customers c1 = new Customers("C10", "xxx", "123");
        c1.loadCustomers();
        c1.setBlockOthers("S10");
        boolean isBlockRuleCreated = false;
        String filePathForBlockList = c1.getFilePathForBlockList();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePathForBlockList));
            String strLine;
            int line = 0;
            while ( (strLine = buffer.readLine()) != null )
            {
                line++;
                strLine = strLine.strip();
                String[] tokens = strLine.split(";");
                if (tokens.length == 2) {
                    String block1 = tokens[0];
                    String block2 = tokens[1];
                    if (block1.equals("C10") == true)
                    {
                        if (block2.equals("S10") == true)
                        {
                            isBlockRuleCreated = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        assertEquals( true, isBlockRuleCreated);
    }
    @Test
    public void testCreateCustomerForInvisible() throws Exception {
        Customers c1 = new Customers("C10", "xxx", "123");
        c1.loadCustomers();
        c1.setInvisibleOthers("S10");
        boolean isBlockRuleCreated = false;
        String filePathForInvisibleList = c1.getFilePathForInvisibleList();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(filePathForInvisibleList));
            String strLine;
            int line = 0;
            while ( (strLine = buffer.readLine()) != null )
            {
                line++;
                strLine = strLine.strip();
                String[] tokens = strLine.split(";");
                if (tokens.length == 2) {
                    String block1 = tokens[0];
                    String block2 = tokens[1];
                    if (block1.equals("C10") == true)
                    {
                        if (block2.equals("S10") == true)
                        {
                            isBlockRuleCreated = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        assertEquals( true, isBlockRuleCreated);
    }


}
