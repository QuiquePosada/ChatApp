/*
 * Author: Enrique Posada Lozano
 * ITESM Campus QRO
 * A01700711
 * */

import java.util.Scanner;
import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.DataOutputStream;
import java.io.DataInputStream;


/**
 * Client handles everything from connection
 * Basically, it takes care of the socket (input/output stream) and Message Listener
 * */
public class Client {
    private static Socket socket = null;
    private static DataInputStream input = null;
    private static DataOutputStream output = null;
    private static InetAddress inetAddress;

    private static ClientRead readMessage;
    private static Thread readThread;
    private static String userName;

    public static void main(String[] args) throws Exception{
        System.out.println("Welcome!!");
        inetAddress = InetAddress.getLocalHost();
        System.out.println("Your ip is : " + inetAddress.getHostAddress());
        System.out.println("Host Name : " + inetAddress.getHostName());
        System.out.println("Address Info : " + inetAddress);

        Scanner scanner = new Scanner(System.in);
        String ip = null;

        System.out.println(">>> Please NOTE : We are running in port 8080");

        System.out.println("Insert the IP or HOSTNAME of the server to attempt connection ('localhost' is valid if done on same computer) : ");
        ip = scanner.nextLine();

        System.out.print("Checking");
        for(int i = 0; i < 3; i++){
            Thread.sleep(500);
            System.out.print(".");
        }
        System.out.println(); // Just prints a new line

        try {
            inetAddress = InetAddress.getByName(ip);
        }catch (IOException e){
            System.out.println("An error occurred, probably the Address given is misspelled. Try Again...");
            System.exit(1);
        }

        // Waits until a connection is established
        while (true) {
            try {
                socket = new Socket(inetAddress, 8080);
                if (socket != null) { // Socket and connection was successfully established
                    input = new DataInputStream(socket.getInputStream());
                    output = new DataOutputStream(socket.getOutputStream());
                    break;
                }
            }
            catch (IOException e) {
                Thread.sleep(2000);
                System.out.println("Error : Connection not established. Check the ip given or for the server to be running...");
            }
        }

        // Initialize Read and Write
        readMessage = new ClientRead(input);
//        writeMessage = new ClientWrite(output);

        // Initialize threads
        readThread = new Thread(readMessage);

//        writeThread.start();

        System.out.println("Great!  Connected to Server. Enjoy :D");
        loginGUI loginGUI = new loginGUI("Welcome!!");
        JFrame frame = loginGUI; // Opens the Login GUI
//        ClientRead.setLoginGUI(loginGUI);
        // NOTE : The rest of Client execution and handling is now done via calling methods for the other GUI's
    }

    /*
    * Enter chat
    * Provides the user with the Chat-Room GUI while starting the Message Listener (ClientRead) thread
    * * userName -> User given Nickname
    * */
    public static void enterChat(String userName){ // Personal Note : static method's can be called without creating an instance of the class
        System.out.println("Nickname is -> " + userName);
        setUserName(userName);
//        writeThread = new Thread(writeMessage);

        // Execute Threads
        readThread.start();
        try {
            output.writeUTF(userName); // Sends username to server
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatGUI chatGUI = new chatGUI(userName + " in Chat-Room"); // Opens the Chat GUI
        ClientRead.setChatGUI(chatGUI);
        JFrame frame = chatGUI;
    }

    /*
    * Disconnect from Server
    * Closes the socket and sets the exit condition for the Message Listener to end its execution
    * */
    public static void disconnect(){
        System.out.println("Disconnecting from Server...");

        try {
            output.writeUTF("Hello everybody, this is my time to disconnect. Goodbye Everybody...");
            output.flush();
            ClientRead.setAlive(false); // Stops the Thread byb Breaking loop condition
//            readThread.interrupt();
//            output.close();
//            input.close();
            socket.close();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("See ya! :D");
    }

    /*
    * Send Message
    * Given a message, sends it to the server, otherwise shows an error message
    * * message -> the String to be sent to the server
    * */
    public static void sendMessage(String message){
        System.out.println("Sending -> " + message);
        try{
            output.writeUTF(message);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("There was an error sending a Message. Try Again");
            // note : this could be changed to show a dialog in the GUI
        }
    }

    /*
    * Setter for the clients username
    * */
    public static void setUserName(String userName) {
        Client.userName = userName;
    }

    /*
    * Getter for the clients username
    * */
    public static String getUserName() {
        return userName;
    }

}
