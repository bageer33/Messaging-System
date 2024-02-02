import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * CS180 Project 5
 *
 * This program holds the CustomersGUI Class for Project 4
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */

public class CustomersGUI {

    private static  JFrame frameMain=null;
    private static JFrame frameLogin=null;
    private static JFrame frameRegister=null;

    private static String  userName = "";
    private static JComboBox<String> cbContacts=null;
    private static P5Client client = new P5Client("localhost", 4242);
    public static void mainUI()
    {
        if(frameLogin!=null) {
            frameLogin.setVisible(false);
        }

        if(frameRegister!=null)
        {
            frameRegister.setVisible(false);
        }
        frameMain = new JFrame();
        frameMain.setTitle("Main Xinyan Tang");
        frameMain.setBounds(400, 200, 800, 600);
        frameMain.setResizable(false);
        frameMain.invalidate();
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = frameMain.getContentPane();
        con.setLayout(new GridLayout(7,1));
        JLabel welcomeLabel = new JLabel("system console");

        JPanel p1 = new JPanel();
        p1.add(welcomeLabel);
        con.add(p1);

        JPanel p2 = new JPanel();
        JButton getContactBtn = new JButton("Get Contact");
        JLabel contactLabel = new JLabel("Your Contacts: ");
        String[] choices = {  };
        cbContacts = new JComboBox<String>(choices);
        cbContacts.setMaximumSize(cbContacts.getPreferredSize()); // added code
        cbContacts.setAlignmentX(Component.CENTER_ALIGNMENT);// added code

        getContactBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> r2 = client.getContacts(userName);
                for(int i=0;i<r2.size();i++) {
                    boolean isDup = false;
                    for(int j=0;j<cbContacts.getItemCount();j++)
                    {
                        String item = cbContacts.getItemAt(j);
                        if(r2.get(i).equals(item)==true)
                        {
                            isDup = true;
                        }
                    }
                    if(isDup==false) {
                        cbContacts.addItem(r2.get(i));
                    }
                }
            }
        });

        p2.add(getContactBtn);
        p2.add(contactLabel);
        p2.add(cbContacts);
        con.add(p2);


        frameMain.setVisible(true);
    }

    public static void loginUI()
    {
        frameLogin = new JFrame();
        frameLogin.setTitle("Login Xinyan Tang");
        frameLogin.setBounds(400, 200, 400, 270);
        frameLogin.setResizable(false);
        frameLogin.invalidate();
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = frameLogin.getContentPane();
        con.setLayout(new GridLayout(7,1));

        JLabel welcomeLabel = new JLabel("welcome to the system");
        JLabel username = new JLabel();
        JLabel password = new JLabel();
 
        JTextField text1 = new JTextField(12);
        JTextField text2 = new JTextField(12);
        JButton login = new JButton("login");
        JButton regist = new JButton("register");

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions

                String u = text1.getText();
                String p = text2.getText();
                userName = u;
                boolean r = client.isUsernameAndPasswordCorrect(u, p);

                System.out.println("isAuthSuccess="+r);
                if(r==true) {
                    mainUI();
                }
                else
                {
                    welcomeLabel.setText("username or password incorrect");
                }
            }
        });



        regist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions
                registerUI();
            }
        });

        String[] choices = { "Customer", "Seller" };
        final JComboBox<String> cb = new JComboBox<String>(choices);
        cb.setMaximumSize(cb.getPreferredSize()); // added code
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);// added code

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();

        p1.add(welcomeLabel);
        con.add(p1);

        username.setText("username:");
        password.setText("password:");
        p2.add(username);
        p2.add(text1);
        con.add(p2);

        p3.add(password);
        p3.add(text2);
        con.add(p3);

        p4.add(cb);
        con.add(p4);

        p5.add(login);
        p5.add(regist);
        con.add(p5);

        frameLogin.setVisible(true);

    }

    public static void registerUI()
    {
        frameRegister = new JFrame();
        frameRegister.setTitle("Register Xinyan Tang");
        frameRegister.setBounds(400, 200, 400, 270);
        frameRegister.setResizable(false);
        frameRegister.invalidate();
        frameRegister.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = frameRegister.getContentPane();
        con.setLayout(new GridLayout(7,1));

        JLabel welcomeLabel = new JLabel("welcome to register");
        JLabel username = new JLabel();
        JLabel password = new JLabel();

        JTextField text1 = new JTextField(12);
        JPasswordField text2 = new JPasswordField(12);
        JButton regist = new JButton("register");

        regist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions
                mainUI();
            }
        });

        String[] choices = { "Customer", "Seller" };
        final JComboBox<String> cb = new JComboBox<String>(choices);
        cb.setMaximumSize(cb.getPreferredSize()); // added code
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);// added code


        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();

        p1.add(welcomeLabel);
        con.add(p1);

        username.setText("username:");
        password.setText("password:");
        p2.add(username);
        p2.add(text1);
        con.add(p2);

        p3.add(password);
        p3.add(text2);
        con.add(p3);

        p4.add(cb);
        con.add(p4);

        p5.add(regist);
        con.add(p5);

        frameRegister.setVisible(true);

    }


    public static void main(String[] args)
    {

        loginUI();

    }
}
