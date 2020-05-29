/*
 * Author: Enrique Posada Lozano
 * ITESM Campus QRO
 * A01700711
 * */

import java.io.DataOutputStream;

/**
 * Client Write takes care of writing and sending all input to the server
 * This class acts on its own thread.
 * !!!THIS IS A DEPRECATED CLASS!!!
 * */

/*
* THIS CLASS IS NOT NEEDED, THE GUI TAKES CARE OF THIS, SO ONLY LISTENING SHOULD BE ESTABLISHED
* THE GUI CALLS A METHOD FOR EACH MESSAGE SENT, SO A THREAD IS NOT NEEDED FOR THIS CLASS
* PLEASE REMOVE AS SOON AS THE GUI SENDS MESSAGES*/
public class ClientWrite implements Runnable{
    private static DataOutputStream dataSent;

    public ClientWrite(DataOutputStream output) {
        this.dataSent = output;
    }

    /*
    * Sends the message to the server
    * */
    public static void sendMessage(String message){

    }

    @Override
    public void run() {
        System.out.println("Output Stream Established");
//        while(true){
//
//        }
    }
}
