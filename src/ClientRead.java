/*
 * Author: Enrique Posada Lozano
 * ITESM Campus QRO
 * A01700711
 * */

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client Read takes care of reading and inserting all output into the chat GUI.
 * Basically this is the Message Listener
 * This class acts on its own thread as it takes care of listening all of the messages that the server sends to its respective user.
 * */
public class ClientRead implements Runnable{
    private static DataInputStream receivedData;
    private static boolean alive = true;
    private static boolean userNameAccepted = false;

    private static loginGUI loginGUI;
    private static chatGUI chatGUI;

    /*
    * Constructor
    * Receives the input stream of the client
    * */
    public ClientRead(DataInputStream input) {
        this.receivedData = input;

    }

    /*
    * Getter for the alive status of the thread
    * */
    public static boolean isAlive() {
        return alive;
    }

    /*
    * Setter for the alive status of the thread
    * */
    public static void setAlive(boolean alive) {
        ClientRead.alive = alive;
    }

    /*
    * Sets the Chat GUI in order to insert the received messages to the users Chat GUI
    * */
    public static void setChatGUI(chatGUI chatGUI) {
        ClientRead.chatGUI = chatGUI;
    }

    //This was going to be used for interacting with the server during the login but was ruled out due to errors and failing to communicate between server and client
//    public static void setLoginGUI(loginGUI loginGUI) {
//        ClientRead.loginGUI = loginGUI;
//    }

    @Override
    public void run() {
        System.out.println("Input Stream Established");
        while(isAlive()){
            try {
                String message = receivedData.readUTF();
//                System.out.println(message);
                chatGUI.insertMessage(message); // Inserts it in the Clients Chat-Room GUI
            } catch (IOException e) {
                if(e.getMessage().equals("Socket closed")){
                    // Handles exceptions for when the client disconnects
                    System.out.println("Socket has closed...");
                }else{
                    e.printStackTrace();
                    // This occurs only when a message was unable to be read by the input stream
                    System.out.println("Unable to read incoming message...");
                }
            }
        }
    }

}
