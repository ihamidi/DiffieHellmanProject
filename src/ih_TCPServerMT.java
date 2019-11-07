// Multi-threaded Server program
// File name: ih_TCPServerMT.java
// Programmer: Izhak Hamidi E01533340

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ih_TCPServerMT {
    private static ServerSocket servSock;
    public static ArrayList < ClientHandler > slist = new ArrayList < ClientHandler > ();
    public static File file = new File("ih_chat.txt");
    public static int g=33,n=128;
    
    
    /**
     * Main checks the comand line to see if any arguments are passed.
     * Then it assigns those arguments to variables and starts a server socket. with those arguments
     * It will indefinetly call the RUN METHOD.
     * @param args
     */
    public static void main(String[] args) {
        //HARDCODED PORTNUM
        int portnum = 20450;
        
        
        //Checking for the portnum argument
        if (args.length != 0) {
        	//for loop to check for values
            for (int i = 0; i < args.length; i++) {
                //checking for g
            	if (args[i].equals("-g")) {
                	g=Integer.parseInt(args[i + 1]);
                    System.out.println("g recieved");
                }
            	//checking for port
                if (args[i].equals("-p")) {
                    portnum = Integer.parseInt(args[i+1]);
                
                System.out.println("portnum recieved");
                }
                //checking for n
                if (args[i].equals("-n")) {
	                //-h will allow next arg to be hostnum
	                 n= Integer.parseInt(args[i + 1]);
	                System.out.println("n recieved");
                }

            }
        }
        System.out.println("Opening port...\n");
        try {
            // Create a server object
            servSock = new ServerSocket(portnum);
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        //creating a client for the first instance

        do {
            if (slist.isEmpty()) {
                file.delete();
            }
            run();
            //cecking the queu if empty,then deletes the file


        } while (true);
    }
    /**
     * Run will wait for a connection.
     * Once a connection is found it outputs a message
     * and hands it off to a new ClientHandler thread, which is started at the end.
     * It will also check the arraylist to check if a user has disconnected.
     */
    private static void run() {

        Socket link = null;
        try {

            // Put the server into a waiting state
            link = servSock.accept();


            // print local host name
            String host = InetAddress.getLocalHost().getHostName();
            System.out.println("Client has estabished a connection to " + host);

            // Create a thread to handle this connection
            ClientHandler handler = new ClientHandler(link);
            // adding client handler  to an arraylist
            slist.add(handler);

            //checking to ee if slist has one element, meaning a new file must be created
            if (!file.exists())
                file.createNewFile();

            //going through the list of clients and removing disconnected clients
            for (int i = 0; i < slist.size(); i++)
                if (slist.get(i).isClientClosed())
                    slist.remove(i);

            // start serving this connection
            handler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ClientHandler extends Thread {
    private Socket client;
    private static BufferedReader in ;
    private static PrintWriter out;
    public static int g=ih_TCPServerMT.g,n=ih_TCPServerMT.n,clientkey;
    public ClientHandler(Socket s) {

        // set up the socket
        client = s;

        try {

            // Set up input and output streams for socket
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 
     * @return the current socket
     */
    public boolean isClientClosed() {
        return client.isClosed();
    }

    // overwrite the method 'run' of the Runnable interface

    // this method is called automatically when a client thread starts.
    /**
     * Run will execute when the ClientHandler Thread is started
     * It will deal with the incoming connection and broadcasts messages to other clienthandlers
     */
    public void run() {
        
    	
    	try {
            int sharedkey=Handshake();
            
            System.out.println("shared key woop woop "+sharedkey);
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	
    	
    	
    	
    	// Receive and process the incoming data 
        int numMessages = 0;
        //starting timer for session time
        long starttime = System.currentTimeMillis();
        try {
            Scanner frdr= new Scanner(ih_TCPServerMT.file);
            String currline = "";

            PrintWriter tofile = new PrintWriter(new FileWriter((ih_TCPServerMT.file.getName()), true));
          
            //using message to get the username
        	String user=in.readLine();
        	
            //printing out the contents of the final
            while(frdr.hasNextLine()){
                currline += frdr.nextLine()+"\n";
            }
            out.println(currline+ "\nEnter message:");
        	//broadcasting arrival
            for (int i = 0; i < ih_TCPServerMT.slist.size(); i++) {
                if (ih_TCPServerMT.slist.get(i) != this)
                    ih_TCPServerMT.slist.get(i).out.println("\n" + user + " has joined the room.\nEnter message:");
            }
        	
            //printing arrival to console
            System.out.println(user + " has joined the room.");
                        
        	
            //p[rinting arrival to file
        	tofile.println(user + " has joined the room.");
        	
            //read in the necxt sent message
            String message = in .readLine();            
            

            //printing the message to everyone
            while (!message.substring(message.indexOf(":") + 2).equals("DONE")) {
                System.out.println(message);
                numMessages++;
                //broadcast message to all active clients
                for (int i = 0; i < ih_TCPServerMT.slist.size(); i++) {
                    if (ih_TCPServerMT.slist.get(i) != this)
                        ih_TCPServerMT.slist.get(i).out.println("\n" + message + "\nEnter message:");
                }
                //printing message to file
                synchronized(this) {
                    tofile.println(message);
                    tofile.flush();
                }
                message = in .readLine();
            }
            
            //broadcasting departure
            for (int i = 0; i < ih_TCPServerMT.slist.size(); i++) {
                if (ih_TCPServerMT.slist.get(i) != this)
                    ih_TCPServerMT.slist.get(i).out.println("\n" + user+ " has left the room."+"\nEnter message:");
            }
            
            //printing departure to console
        	System.out.println(user + " has left the room.");
        	
        	//printing departure to file
        	tofile.println(user + " has left the room.");
        	
            // getting end time for session
            long elapsed=System.currentTimeMillis()-starttime;
            String time=convertToTime(elapsed);

            // Send a report back and close the connection
            out.println("Session Time: "+time);
            out.println("Server received " + numMessages + " messages");
            out.flush();
            out.close();
            frdr.close();
            tofile.close();
            //removing from the list when disconnecting
            ih_TCPServerMT.slist.remove(this);
            //checking to see if anyone is connected, otherwise delete file
            if (ih_TCPServerMT.slist.size() == 0)
            	ih_TCPServerMT.file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                System.out.println("!!!!! Closing connection... !!!!!");
                System.out.println();
                client.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }

    }
    public static byte Handshake() throws NumberFormatException, IOException {
    	System.out.println("Clientthread running"+ih_TCPServerMT.g);
    	Random rand= new Random();
    	int x=rand.nextInt();
    	out.println(g);
    	out.flush();
    	out.println(n);
    	out.flush();
    	
	   	BigInteger mod = new BigInteger("" + g).modPow(new BigInteger("" + x), new BigInteger("" + n));
	   	System.out.println(mod+" this the value");
	   	
	   	clientkey=Integer.parseInt(in.readLine());
	   	
	   	
 
	   	
		int privkey=mod.intValue();
		out.println(privkey);
    	out.flush();
    	
	    
	   	BigInteger sharedkey= new BigInteger("" + clientkey).modPow(new BigInteger("" + x), new BigInteger("" + n));
	   			
	   	byte lowByte = (byte)(sharedkey.intValue() & 0xFF);
		
		
		
	   	return lowByte;
    }
    
    
    /**
     * Method to convert milliseconds to a readable format
     * returns hh:mm:ss:milli
     */
    public static String convertToTime(long x)
    {
    	//using integer dis=vision and modulus to return readable value
    	String s="";
    	long hours = (x / 1000) / (60*60);
        long minutes = (x / 1000) / 60;
        long seconds = (x / 1000) % 60;
    	long milliseconds= (x%1000);
    	s= hours+":"+minutes+":"+seconds+":"+milliseconds;
    	return s;
    }

}