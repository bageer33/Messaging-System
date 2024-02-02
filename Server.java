import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * CS180 Project 5
 *
 * This program holds the (unused) Server Class for Project 5
 *
 * @author Nithish Thatchinamoorthy, Xinyan Tang, Brittany Geer, Kinjal Goel, Hari Meka
 * @version April 10 2023
 *
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242);

        Socket socket = serverSocket.accept();
//        JOptionPane.showMessageDialog(null, "Client connected!",
//                "Search Engine", JOptionPane.INFORMATION_MESSAGE);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

    }
}

