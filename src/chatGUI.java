/*
 * Author: Enrique Posada Lozano
 * ITESM Campus QRO
 * A01700711
 * */

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Chat GUI
 */
public class chatGUI extends JFrame {
    private JButton sendButton;
    private JPanel chatPanel;
    private JPanel inputPanel;
    private JScrollPane messagesScroll;
    private JTextArea messageInput;
    private JScrollPane inputScroll;
    private JTextPane messagesContainer;
//    private JTextArea messageContainer;

    public chatGUI(String s) {
        super(s);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(chatPanel);
        this.pack(); // Packs the contents

        // Placeholder
        TextPrompt placeholder = new TextPrompt("Send your Message...", messageInput);
        placeholder.changeAlpha(0.5f); // Reduces the placeholder alpha to half opacity

        // Wraps words in order for the scroll to work
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);

        messagesContainer.setContentType("text/html"); // Enables document content for html
        messagesContainer.setEditable(false);

        // First code for JTextArea
//        messageContainer.setLineWrap(true);
//        messageContainer.setWrapStyleWord(true);
//        messageContainer.setEditable(false); // Make messages uneditable

        String userName = Client.getUserName();
        insertMessage("<strong>" + userName + "(<u>You</u>) has entered the chat-room! :D" + "</strong>");

        // Action Listeners

        // Sends messages
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!messageInput.getText().isEmpty()) { // Avoids sending Empty Messages
                    Client.sendMessage(messageInput.getText());
                    messageInput.setText(null); // Clears Text
                    messageInput.requestFocus();
                    messagesScroll.getVerticalScrollBar().setValue(messagesScroll.getVerticalScrollBar().getMaximum());
                }
            }
        });

        // For exit on close
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Sending Chat Signal to Close...");
                // Send Exit signal
                Client.disconnect();
                e.getWindow().dispose();
            }
        });

        // Window Settings
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    /*
     * Inserts message received to message Container
     *
     * * message -> The message to insert in the container
     * */
    public void insertMessage(String message) {
//        messageContainer.append(message + "\n");
        HTMLDocument document = (HTMLDocument) messagesContainer.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) messagesContainer.getEditorKit();

        // The editor inserts at the end of the container
        try {
            editorKit.insertHTML(document, document.getLength(), (message + "\n"), 0, 0, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
