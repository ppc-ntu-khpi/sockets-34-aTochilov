/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Andrei
 */
public class ChatClient {
    
    private TextArea output;
    private TextField input;
    private Button send;
    private Button quit;
    private Button confirmusernameButton;
    private Frame frame;
    private TextField usernames;

    private Socket connection = null;
    private BufferedReader serverIn = null;
    private PrintStream serverOut = null;

    private void doConnect() {
      String serverIP = System.getProperty("serverIP", "127.0.0.1");
      String serverPort = System.getProperty("serverPort", "2000");

      try {
      connection = new Socket(serverIP, Integer.parseInt(serverPort));
      InputStream is = connection.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      serverIn = new BufferedReader(isr);
      serverOut = new PrintStream(connection.getOutputStream());
      Thread t = new Thread(new RemoteReader());
       t.start();} catch (Exception e) {
          System.err.println("Unable to connect to server!");
          e.printStackTrace();
        }
      } // закінчення методу doConnect

    private void launchFrame() {
        output = new TextArea(10,50);
        input = new TextField(40);
        send = new Button("Send");
        quit = new Button("Quit");
        usernames = new TextField(20);
        confirmusernameButton = new Button("Confirm Username");
        
        frame = new Frame("ChatClient");

        // Create the user panel
        Panel userPanel = new Panel(); 
        userPanel.setLayout(new FlowLayout());
        userPanel.add(usernames);
        userPanel.add(confirmusernameButton);
        userPanel.add(quit);
        
        Panel sendPanel = new Panel();
        sendPanel.setLayout(new FlowLayout());
        sendPanel.add(input);
        sendPanel.add(send);

        // Use the Border Layout for the frame
        frame.setLayout(new BorderLayout());

        frame.add(output, BorderLayout.WEST);
        frame.add(sendPanel, BorderLayout.SOUTH);   
        frame.add(userPanel, BorderLayout.NORTH);
        
        send.setEnabled(false);
        // Create menu bar
        MenuBar menuBar = new MenuBar();
        frame.setMenuBar(menuBar);

        // Add Help menu to menu bar
        Menu help = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(frame, "The ChatClient is a tool that allows you to talk to other users");
            }
        });
        help.add(aboutMenuItem);
        menuBar.setHelpMenu(help);

        send.addActionListener(new SendHandler());
        input.addActionListener(new SendHandler());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
        });
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              System.exit(0);
            }
        });

        confirmusernameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (usernames.getText().compareTo("") == 0) {
                    JOptionPane.showMessageDialog(frame, "Enter your username!");
                    return;
                }
                else{
                    send.setEnabled(true);
                    confirmusernameButton.setEnabled(false);
                    usernames.setEnabled(false);
                    doConnect();

                }
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        }

    private class SendHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
          String text = input.getText();
          text = usernames.getText() + ": " + text + "\n";
          serverOut.print(text);
          input.setText("");
        } // закінчення методу actionPerformed 
  } // закінчення опису внутрішнього класу SendHandler

    private class RemoteReader implements Runnable {
        public void run() {
          try {
            while ( true ) {
              String nextLine = serverIn.readLine();
              output.append(nextLine + "\n");
            }
          } catch (Exception e) {
              System.err.println("Error while reading from server.");
              e.printStackTrace();
        }
    } // закінчення методу run 
  } // закінчення опису внутрішнього класу RemoteReader 

    public static void main(String[] args) {
        ChatClient cc = new ChatClient();
        cc.launchFrame();
    }
}


