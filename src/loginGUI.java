/*
 * Author: Enrique Posada Lozano
 * ITESM Campus QRO
 * A01700711
 * */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
/**
 * Login GUI
 * This class takes care of the initial GUI presented to the user after a connection is established with the Server
 * The GUI consists of an input and a button, where a user enters its username and if not empty, proceeds to take the user to the Chat-Room
 * */
public class loginGUI extends JFrame {
    private JPanel loginPanel;
    private JLabel title;
    private JTextField userName;
    private JLabel userName_Label;
    private JButton enterRoomButton;
    private JPanel subPanel;

    public loginGUI(String s) {
        super(s);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(loginPanel);
        this.pack(); // Packs the contents

        // Button Action
        enterRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!userName.getText().isEmpty()) {
                    System.out.println("Hello '" + userName.getText() + "'");
                    Client.enterChat(userName.getText());
                    enterRoomButton.setEnabled(false); // Disables the button from further requests
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill the text-field");
                }
            }
        });

        // For exit on close
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Sending Signal to Close...");
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

}
