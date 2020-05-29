/*
 * Author: Enrique Posada Lozano
 * ITESM Campus QRO
 * A01700711
 * */

import java.io.*;
import java.net.Socket;

/**
 * Connected Client
 * This class takes care of being the connection between the server and each client. The server assigns a thread to each client that connects in order to listen to multiple clients.
 * */
public class ConnectedClient implements Runnable{
    private Socket clientSocket;
    private DataInputStream clientInput;
    public DataOutputStream clientOutput;

    private int clientId;
    private String clientName;

    private String exitMessage = "Hello everybody, this is my time to disconnect. Goodbye Everybody...";
    private String entryMessage = " has entered the room</strong> :)";

    // Constructor
    public ConnectedClient(Socket clientSocket, DataInputStream clientInput, DataOutputStream clientOutput, int clientId) {
        this.clientSocket = clientSocket;
        this.clientInput = clientInput;
        this.clientOutput = clientOutput;
        this.clientId = clientId;
//        this.clientName = clientName;
        String userName = null; // Username is null, because it will wait for client to send username
        try {
            userName = clientInput.readUTF(); // Waits for Client response
            this.clientName = userName;
            if(userName.equals(exitMessage)){ // Handles exits during login
                System.out.println("EXIT CONDITION REACHED...");
                // Remove User
//                Server.disconnectClient(clientId, clientSocket);
                this.clientSocket.close(); // In contrast to the disconnect method, the socket is closed and the server therefore never creates and runs a thread on the object
            }else{
                System.out.println("The new username is : " + userName);
                entryMessage = "<strong><i>" + clientName + "</i> has entered the room</strong> :)";
                Server.broadCastMessage(this.clientName, entryMessage);// Sends a message to all connected users that a new user connected
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        try {
//            boolean isAvailable = true;
//            do{
//                Vector clientList = Server.getClientList();
//                userName = clientInput.readUTF(); // Waits for Client response
//                if(userName.equals(exitMessage)){ // Covers exiting during login
//                    System.out.println("EXIT CONDITION REACHED...");
//                    // Disconnect User
////                    Server.disconnectClient(clientId, clientSocket);
////                    this.clientSocket.close();
//                    break;
//                }
//                System.out.println(clientList);
//
//                for(int i = 0; i < clientList.size(); i++){
//                    ConnectedClient client = (ConnectedClient) clientList.get(i);
//                    System.out.println("USERNAME CHECKING -> " + client.getClientName());
//                    if(client.getClientName() == userName){
//                        isAvailable = false;
//                        System.out.println("Username_Unavailable");
//                        clientOutput.writeUTF("Username_Unavailable");
//                        break; // Avoids looping since a match was found
//                    }
//                }
//            }while(isAvailable == false);
//            clientOutput.writeUTF("Username_Available");
//            this.clientName = userName;
//            System.out.println("The new username is : " + userName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void run() {
        System.out.println("Server has connected Client...");
        String message = null;
//        Server.broadCastMessage(clientName, entryMessage);

        while(!clientSocket.isClosed()){ // Takes care of not entering if the user exited during login (now made on Server but still breaks the loop whenever it is time to disconnect)
            try{
                try {
                    message = clientInput.readUTF(); // Reads message from client
                    System.out.println("Message : " + message);
                }catch (EOFException e){
                    System.out.println("Disconnected Socket " + clientName);
                    Server.disconnectClient(clientId,clientSocket);
                    break;
                }
//                System.out.println("OK " + (message.equals(exitMessage))); // Just used for checking exit condition
                if(message.equals(exitMessage)){
                    System.out.println("EXIT CONDITION REACHED...");
                    break; // Breaks the loop
                }

                // Checks for individual/private messages
                if(message.charAt(0) == '@' && message.contains(" ")){ // Checks for @ and for 1 space for the receiver and message to be distinguished
                    int receiverEndIndex = message.indexOf(' ');
                    String receiver = message.substring(1,receiverEndIndex);
                    String privateMessage = message.substring(receiverEndIndex + 1);
                    System.out.println("The message will be sent to --> '" + receiver + "'");
                    System.out.println("The message sent is --> '" + message.substring(receiverEndIndex + 1) + "'");
                    Server.sendPrivateMessage(clientName, clientId, receiver, privateMessage);
                }else{
                    Server.broadCastMessage(clientName, message);
                }
            } catch (IOException e) {
                System.out.println("Error reading message for " + clientName);
                e.printStackTrace();
                break;
            }
        }
        System.out.println(clientName + " socket has disconnected...");
        Server.disconnectClient(clientId, clientSocket);// Removes and Disconnects User
    }

    // Getter that obtains the Clients username
    public String getClientName() {
        return clientName;
    }

    // Getter that obtains the Clients Socket
    public Socket getClientSocket() {
        return this.clientSocket;
    }

    // Setter that sets the Clients ID
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

}
