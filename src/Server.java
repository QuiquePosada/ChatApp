/*
* Author: Enrique Posada Lozano
* ITESM Campus QRO
* A01700711
* */

/*
* Server Class:
* This class is the Server and it takes care of receiving connections from clients and handling communication between them.
* */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

/**
 * */
public class Server {
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static DataInputStream clientInput = null;
    private static DataOutputStream clientOutput = null;
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/YY HH:mm");
    private static LocalDateTime actualTime;

    // NOTE : Vectors are now used instead of an ArrayList because access to a Vector is synchronized whereas an ArrayList is not
    private static Vector<ConnectedClient> clientList = new Vector<ConnectedClient>();

    public static void main(String[] args){
        System.out.println("Server started!");

        // Initialize the Socket and assign port 8080 to it
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("Server initialized!");
            System.out.println("" + serverSocket); // Listens to all interfaces / ip addresses
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Waiting for Clients...");

        while(true){
            clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                clientInput = new DataInputStream(clientSocket.getInputStream());
                clientOutput = new DataOutputStream(clientSocket.getOutputStream());

                // Creates a client object, containing the Clients socket, input, output, and id (size of Vector)
                    // clientList.size will always be 'i + 1' as for Client Id
                ConnectedClient client = new ConnectedClient(clientSocket, clientInput, clientOutput, clientList.size());
                Thread clientThread = new Thread(client);
                if(!client.getClientSocket().isClosed()){
                    clientList.add(client);
                    clientThread.start(); // Executes Thread
                    System.out.println("Socket info " + clientSocket.getLocalAddress() + " Socket address " + clientSocket.getRemoteSocketAddress());
                    System.out.println(">>>USERS : " + clientList);
                }
                else{
                    System.out.println("The client socket was closed during login...");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to accept client connection... Closing everything...");
                try {
                    serverSocket.close();
                    //due to error, close connections of all connected clients
                    for(int i = 0; i < clientList.size(); i++){
                        ConnectedClient client = clientList.get(i);
                        Socket socketClose = client.getClientSocket();
                        socketClose.getOutputStream().close();
                        socketClose.getInputStream().close();
                        socketClose.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*
    * Broadcast Message
    * Sends a message to all connected users
    *
    * message -> the message to broadcast (sends to the whole group including sender in order to count that the message was received by server)
    * synchronized in order to have each user sending message in order and avoid race conditions
    * */
    public static synchronized void broadCastMessage(String senderName, String message){
        // insert a switch that allows a message type for entering, leaving or broadcasting a message (Note, all of these are in the end of type broadcast)
        for (int i = 0; i < clientList.size(); i++) {
            ConnectedClient client = clientList.get(i);
            System.out.println("\nClient broadcast turn is : " + client.getClientName());
            actualTime = LocalDateTime.now();
            if(client.getClientName().equals(senderName)){ // Checks if user is the sender
                try {
                    client.clientOutput.writeUTF( "<strong><u>You</u></strong> <i>[" + dateTimeFormatter.format(actualTime)  + "]</i> > " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{ // Broadcast message for everyone else
                try {
                    client.clientOutput.writeUTF( "<strong>"+ senderName + "</strong> <i>[" + dateTimeFormatter.format(actualTime)  + "]</i> > " + message);
//                    client.clientOutput.writeUTF( senderName + " > " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    * Sends a Private Message
    *
    * * senderName -> The name of the Client sending the message
    * * senderId -> The id of the client sending the message
    * * receiverName -> The name of the client to receive the message
    * * message -> The message to send
    * */
    public static synchronized void sendPrivateMessage(String senderName, int senderId, String receiverName, String message){
        boolean userExists = false;
        ConnectedClient sender = clientList.get(senderId);
        // Check is username exists in Chat-Room
        for (int i = 0; i < clientList.size(); i++) {
            ConnectedClient client = clientList.get(i);
            if(client.getClientName().equals(receiverName)){
//                System.out.println("User found for private message, Sending...");
                userExists = true;
                try {
                    client.clientOutput.writeUTF("<strong>(" + senderName + " to you)</strong>" + " > " + message);
                    sender.clientOutput.writeUTF("<strong>(<u>You</u> to " + receiverName + ")</strong>" + " > " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (userExists == false) { // User not found or does not exist
            try {
                sender.clientOutput.writeUTF(receiverName + " username not found or doesn't exist. Try again :)\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * Disconnect from Server
    * Removes a Client from the active Client list and closes its socket
    *
    * This method is synchronized in case that 2 or more threads disconnect at the same time
    *
    * * clientId -> The id of the client trying to disconnect
    * * socketClose -> The socket from the client trying to disconnect
    * */
    public static synchronized void disconnectClient(int clientId, Socket socketClose){
        ConnectedClient client = clientList.get(clientId); // Gets the Client Id of List
        String exitMessage = "<strong><i>" + client.getClientName() + "</i> has exit the room</strong>";
        // Broadcast message that a user just exited the room
        broadCastMessage(client.getClientName(), exitMessage);
        System.out.println("Array size then -> " + clientList.size());
        clientList.remove(client);
        System.out.println("Array size now -> " + clientList.size());
        System.out.println(client.getClientName() + " has exited the room with id " + clientId);
        // Disconnect
        try {
            socketClose.close();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error. Unknown error...");
        }
        // Update user Id's to vector size
        // Basically, it iterates the vector and updates each Client's id to match the vector
//        System.out.println("ARRAY SIZE -> " + clientList.size());
        for (int id = 0; id < clientList.size(); id++){
            client = clientList.get(id); // Reuse variable to update other clients in vector
            client.setClientId(id);
//            System.out.println(client.getClientName() + " New id" + client.getClientId());
        }
    }

//    /*
//    * Getter for the Connect Client List
//    * This is intended to be used in Connected Client class for getting information from the connected users, of which it was not implemented on this version*/
//    public static Vector getClientList(){
//        return clientList;
//    }

}
