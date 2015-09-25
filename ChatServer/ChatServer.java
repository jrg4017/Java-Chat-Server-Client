/**
*	Name: Julianna Gabler	
*	Course:	ISTE 200-01 2014	
*	Homework:	#13, MCTS Chat 	
*	Date:	12/1/2014	
*
*	Class:	ChatServer	
*	Purpose:	 <BR>
*
*	@author julianna gabler
*	@version 1.0
*	@see	ChatClient
*/


import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer{
   private ArrayList clientOutputStream;
   private int clientCount = 0;
   
   public class ClientHandler implements Runnable{
      BufferedReader reader;
      Socket sock;
      
      
      public ClientHandler(Socket cSock){
         try{
            sock = cSock;
            InputStreamReader isr = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(isr);
            
         }catch(Exception ex){}
      }
      
      public void run(){
         String msg;
         
         try{
            while((msg = reader.readLine()) != null){
               System.out.println("RECVD: " + msg);
               sendToAll(msg);
            }
         }catch(Exception ex){}
         
         clientCount--;
         System.out.println("\nCLIENT HAS DISCONNECTED || TOTAL CLIENTS: " + clientCount +" \n");
      }
   }//end inner class ClientHandler
   
   public void connect(){
      clientOutputStream = new ArrayList();
      
      try{
         ServerSocket serverSock = new ServerSocket(5000);
         System.out.println("\n\nSERVER HAS STARTED");
         System.out.println("----------------------------------------------------\n");
         
         while(true){
            Socket clientSock = serverSock.accept();
            PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
            clientOutputStream.add(writer);
            
            Thread t = new Thread(new ClientHandler(clientSock));
            t.start();
            
            clientCount++;
            System.out.println("\nCLIENT CONNECTED || TOTAL CLIENTS: " + clientCount + "\n");
         }
      }catch(Exception ex){
         //System.out.println("Client has disconnected");
      }
   }
   
   public void sendToAll(String msg){
      Iterator it = clientOutputStream.iterator();
      
      while(it.hasNext()){
         try{
            PrintWriter writer = (PrintWriter) it.next();
            writer.println(msg);
            writer.flush();
            
         }catch(Exception ex){ }
      }
   }//end send ToAll
   
   public static void main(String[] args){
      new ChatServer().connect();
   }
}