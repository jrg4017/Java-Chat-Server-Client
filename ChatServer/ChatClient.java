/**
*	Name: Julianna Gabler	
*	Course:	ISTE 200-01 2014	
*	Homework:	#13, MCTS Chat 	
*	Date:	12/1/2014	
*
*	Class:	ChatClient	
*	Purpose:	to create the gui shell
*  as well as the connection to the server.
*  the client also displays messages from everyone
*  and sends messages to the server. the 
*  shell also accepts a chat name for identification
*  purposes in the chat <BR>
*
*	@author julianna gabler
*	@version 1.0
*	@see	ChatServer
*/

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* gui shell and the connection to, read/write to server
*/
public class ChatClient{
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////attributes//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private JTextArea jtaRecieved;      //the text area that displays all recieved messages in the chat
   private JTextField jtaSend;         //the text field for sending messages
   private JTextField jta;             //the inital field for getting the ip address
   private JTextField jta2;            //the intial field for getting the chat name
   private JFrame frame;               //the first frame
   private JFrame jFrame;              //the second frame
   
   private BufferedReader reader;      //for reading all input
   private PrintWriter writer;         //for sending all output
   private Socket sock;                //the socket connection
   private String IPAdd;               //the ipaddress
   private String chatName;            //the client's chat name
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////functions//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
   /**
   * initializes the client's constructor and connects to the socket <BR>
   * @param args - accepts command line arguements in string array
   */
   public static void main(String[] args){
      ChatClient cc = new ChatClient();
      cc.clientIntitalGUI();
   }
   
   /**
   * the intial frame that accepts an ip address and chat name	<BR>
   * <BR>
   */
   public void clientIntitalGUI(){
      //set up main frame and panel
      frame = new JFrame("MTCS CHAT CLIENT");
      JPanel iMain  = new JPanel();
      iMain.setLayout(new GridLayout(0, 2));
      
      //for formatting the panels
      JPanel container = new JPanel();
      container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
      container.setLayout(new BorderLayout());
      
      //set the properties of the welcome label
      JLabel welcome = new JLabel("Welcome to MTCS Chat", SwingConstants.CENTER);
      welcome.setFont(new Font("Serif", Font.BOLD, 30));
      welcome.setForeground(Color.blue);
      frame.add(welcome, BorderLayout.NORTH);
      
      //the ip label & field set up and add
      JLabel ip = new JLabel("Please enter an IP address:");
      iMain.add(ip);
 
      jta = new JTextField(20);
      iMain.add(jta);
      
      //set up chat name label & field
      JLabel name = new JLabel("Please enter a chat name:");
      iMain.add(name);
      
      jta2 = new JTextField(20);
      iMain.add(jta2);
      
      //add the main to container for formatting
      container.add(iMain);
      frame.add(container, BorderLayout.CENTER);
      
      //create a ok button and listener
      JButton jbOK = new JButton("OK");
      jbOK.addActionListener(new OKListener());
      
      //create a panel so button doesn't take up entire south field
      JPanel iSouth = new JPanel();
      iSouth.setLayout(new FlowLayout());
      iSouth.add(jbOK);
      frame.add(iSouth, BorderLayout.SOUTH);
      
      //set properties of the frame
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      //set it to the center of the screen by getting screen size
      frame.setLocationRelativeTo(null);
      
      frame.pack();
      frame.setVisible(true);
   }
   /**
   * the client chat gui shell for communicating with the server <BR>
   * <BR>
   */
   public void clientGUI(){
      //set up the frame and panels
      jFrame = new JFrame("MTCS CHAT Client");
      JPanel main = new JPanel();
      main.setLayout(new FlowLayout());
      
      //add a menu
      menu();
      
      //add the title and center
      JLabel title = new JLabel("Welcome to MTCS Chat", SwingConstants.CENTER);
      title.setFont(new Font("Serif", Font.BOLD, 30));
      title.setForeground(Color.blue);
      
      jFrame.add(title, BorderLayout.NORTH);
      
      //set up the recieved messages
      jtaRecieved = new JTextArea(15,50);
      jtaRecieved.setFont(new Font("Serif", Font.PLAIN, 18));
      jtaRecieved.setLineWrap(true);
      jtaRecieved.setWrapStyleWord(true);
      jtaRecieved.setEditable(false);
      
      //allow the recieved messages to be scrollable
      JScrollPane scroll = new JScrollPane(jtaRecieved);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      jFrame.add(scroll, BorderLayout.CENTER);
      
      //set up the field for sending text
      jtaSend = new JTextField(50);
      main.add(jtaSend);
      
      //add a send button and a listener
      JButton jbSend = new JButton("Send"); 
      jbSend.addActionListener(new SendListener());
      main.add(jbSend);
      
      //connect to the server
      connect();
      
     //add to the main frame and set properties
      jFrame.add(main, BorderLayout.SOUTH);
      jFrame.setResizable(false);
      jFrame.setLocationRelativeTo(null);
      jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jFrame.pack();
      jFrame.setVisible(true);
  } 
  /**
  * sets up the menu for the chat and adds a listener to the menu items
  * <BR>
  */
  private void menu(){
      
      JMenuBar jmb = new JMenuBar();
      jFrame.setJMenuBar(jmb);
      
      //set up menu
      JMenu jmFile = new JMenu("File");
      JMenuItem jmiExit = new JMenuItem("Exit");
      jmFile.add(jmiExit);
      jmb.add(jmFile);
      
      jmiExit.addActionListener(new ExitListener());
  }
  
  
  /**
  * connects to the server and creates threads. also sets up the reader / writer 
  * <BR>
  */
  private void connect(){
    //keeps going until a connection goes through
    while(true){
    try{ 
      //connect to address
         sock = new Socket(IPAdd, 5000);
            //if the connection goes through aka not null, do below
            if(sock != null){
            
               //set up the ability to send and write messages sent/recieved  
               InputStreamReader inputRead = new InputStreamReader(sock.getInputStream());
               reader = new BufferedReader(inputRead);
               writer = new PrintWriter(sock.getOutputStream());
      
               //prints out a user friendly message to the client saying you have connected
               jtaRecieved.append("You have connected to the Chat Room\n---------------------------------------------------\n");
               
               //set up a thread
               Thread th = new Thread(new Incoming());
               th.start();
      
               th.sleep(1000);
               //breaks out of loop
               break;
            }
              
            
       }catch(IOException ie){
         //prints a user friendly message stating failure to connect
         jtaRecieved.append("\nClient did not connect to the Chat Room. Please try again\n");
      }catch(InterruptedException e){
         //prints a user friendly message stating failure to connect
         jtaRecieved.append("\nClient did not connect to the Chat Room. Please try again\n");
      }
     }//end while loop
  }//end connect function
  
  /**
  * inner class that implements ActionListener and exits the program
  */
  public class ExitListener implements ActionListener{
      public void actionPerformed(ActionEvent ae){
         System.exit(1);                           //exits the program when clicked
      }
  }
 
  /**
  * inner class that implements ActionListener and sends the user's mesages to the Server
  */
  public class SendListener implements ActionListener{
      public void actionPerformed(ActionEvent ae){
         try{
            //add chat name in front of the text entered by the user
            String sendMsg = chatName + ": " + jtaSend.getText();
            writer.println(sendMsg);                              //send the message to the server
            writer.flush();                                        //flushes
            
         }catch(Exception ex){
            //pushes an user friendly disconnect message to the client
            jtaRecieved.append("\nClient is not connected. Please try restarting the client.\n");
            
            //pushes the scroll area to show the most recent text from others
            jtaRecieved.setCaretPosition(jtaRecieved.getDocument().getLength());
         
         }//end catch
         
         //clears the send text field and prepares it for recieving most user input
         jtaSend.setText("");
         jtaSend.requestFocus();
         
      }//end actionPerformed

   }//end inner class SendListener
   
  /**
   * inner class that implements ActionListener in which the OK button in the inital button launches the second frame
   */
   public class OKListener implements ActionListener{
      public void actionPerformed(ActionEvent ae){
           //save the entered values into appropiate attributes
           IPAdd = jta.getText();
           chatName = jta2.getText();
           
            frame.dispose(); //after above values are saved, the initial gui frame is disposed
            
            //call second client for actual chatting
            clientGUI();
      }//end actionperformed
   }//end inner class OKListener
   
   /**
   * inner class that implements runnable and gets all messages sent
   */
   public class Incoming implements Runnable{
      @Override
      public void run(){
         String msg;                         //for saving the message that gets sent to the client
         
         try{
 
            while((msg = reader.readLine()) != null ){
               //adds the Server's message to the recieved box for the client to see
               jtaRecieved.append(msg +"\n");
               
               //pushes the scroll area to show the most recent text from others
               jtaRecieved.setCaretPosition(jtaRecieved.getDocument().getLength());
               
            }
         }catch(Exception ex){
            //pushes an user friendly disconnect message to the client
            jtaRecieved.append("\nClient is not connected. Please try restarting the client.\n"); 
            
            

         }//end catch
         
      }//end run
      
   }//end inner class Incoming
   
}//end Chatclient

