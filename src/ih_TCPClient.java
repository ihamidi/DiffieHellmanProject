// Programmer: Izhak Hamidi E01533340 
// Client program
// File name: ih_TCPClient.java

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;
public class ih_TCPClient extends Thread {
    private static InetAddress host;
    public static int g=-1,n=-1;
    public static volatile boolean flag=false;
	public static int clientkey,serverkey;
	
    /**
     * Main will check for arguments passed at command line.
     * Then will try to establish a connection with a Server using those arguments.
     * @param args
     */
    public static void main(String[] args) {
        //creating variables to store in arguments, and setting them to default values
        //hostnum adn username are set to defalt values
        String hostnum = "localhost";
        String user="";
        int portnum = 20450;
        boolean ureceived = false, preceived = false, hreceived = false;
        //checking if any argyment was passed
        if (args.length != 0) {
            //for every argument, check for -u,-p,-h
            for (int i = 0; i < args.length; i++)
                if (args[i].equals("-u") && !ureceived) {
                	user=args[i + 1];
                    System.out.println("username recieved");
                    ureceived = true;
                }
            else if (args[i].equals("-p") && !preceived) {
                //-p will alow next arg to be portnum
                portnum = Integer.parseInt(args[i + 1]);
                System.out.println("portnum recieved");
                preceived = true;
            } else if (args[i].equals("-h") && !hreceived) {
                //-h will allow next arg to be hostnum
                hostnum = args[i + 1];
                System.out.println("hosttnum recieved");
                hreceived = true;
            }
        }

        try {
            // Get server IP-address
            host = InetAddress.getByName(hostnum);
        } catch (UnknownHostException e) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        Socket link = null;
        try {
            // Establish a connection to the server
            link = new Socket(host, portnum);
            
            //diffieHellman

            //g=Integer.parseInt(in.readLine());
           // n=Integer.parseInt(in.readLine());
            
            PrintWriter out = new PrintWriter(
                    link.getOutputStream(), true);
 	       	BufferedReader in = new BufferedReader(
			       new InputStreamReader(link.getInputStream()));
            
            int sharedkey=Handshake(in,out);
            
            System.out.println("shared key woop woop "+sharedkey);
            
            
            while(!flag) {}
            
            
            //starting a send thread to manage client sending
            SendThread send = new SendThread(ureceived, user, link,out);
            //starting a get thread to manage client recieving.
            GetThread get = new GetThread(link,in);
            get.start();
            send.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static int Handshake(BufferedReader in, PrintWriter out) throws NumberFormatException, IOException {
    	Random rand= new Random();
    	int y=rand.nextInt(20);
	    g=Integer.parseInt(in.readLine());
	   	n=Integer.parseInt(in.readLine());
	   	System.out.println(g+" g ; n "+n);
	   	

	   	BigInteger mod = new BigInteger("" + g).modPow(new BigInteger("" + y), new BigInteger("" + n));
	   	System.out.println(mod+" this the value");
	   	clientkey=mod.intValue();
	    out.println(clientkey);
	   	out.flush();
	   	
	   	
		/*
	   	//BigInteger modby=new BigInteger(n+"");
		//modcalc=modcalc.mod(modby);
		//int privkey=modcalc.intValue();
		//out.println(privkey);
    	//out.flush();
    	*/
	    serverkey=Integer.parseInt(in.readLine());

	   	BigInteger sharedkey= new BigInteger("" + serverkey).modPow(new BigInteger("" + y), new BigInteger("" + n));
		flag=true;
	   	return sharedkey.intValue();
    }

}
/**
 * SendThread is a class that has a thread which controls the messages the client sends.
 * @author hizha
 *
 */
class SendThread extends Thread {
    private boolean ureceived;
    private String us;
    private Socket link;
    private PrintWriter out;
    /**
     * Constructor for sendtrhead
     * @param ureceived if user was received
     * @param us username
     * @param link the socket linked
     */
    SendThread(boolean ureceived, String us, Socket link,PrintWriter out) {
    	this.out=out;
        this.ureceived = ureceived;
        this.us = us;
        this.link = link;
    }
    /**
     * Run will start a couple of writers adn connect them to the console
     * and SocketStream.
     * It will send messages to the server.
     */
    public void run() {
        try {
        	//initializing username to nothing
            String user = "";
            //starting a printwriter to send things to the server
            
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            String message;
            
//            while(!ih_TCPClient.flag){}
//            System.out.println("key="+ih_TCPClient.clientkey);
//            System.out.println("meaninggul message");
//
//
//            out.println(ih_TCPClient.clientkey);
//        	out.flush();
//        	
//            ih_TCPClient.flag=false;

            //bufferedreader to read in user input
            //Prompting user for username
            if (!ureceived) {
                System.out.println("Username?:");
                user = userEntry.readLine();
                
            } else {
                user = us;
            }
            //sending username
            out.println(user);
        	out.flush();

            
            // Get data from the user and send it to the server
            do {
            	
            	//prompting user to enter a message, then sending it to the server
                System.out.print("Enter message:\n ");
                message = userEntry.readLine();
                out.println(user + ": " + message);
                //delaying the thread for 200 milliseconds
                Thread.sleep(200);

            } while (!message.equals("DONE"));
            out.println(user + " has left the room");
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }



}
/**
 * Get Thread is a class that extends thread which controls all incoming messages to the client.
 * @author hizha
 *
 */
class GetThread extends Thread {
    private Socket link;
    private BufferedReader in;
    /**
     * GetThread constructer takes only the socket that is linked to the server
     * @param link
     */
    GetThread(Socket link,BufferedReader in) {
    	this.in=in;
        this.link = link;
        
    }
    /**
     * Run will wait until a message is recieved, then output it to the clients console
     */
    public void run() {
	   	 Random rand = new Random();
	   	 int x=rand.nextInt(10);
	       // Set up input streams for the connection

		try {

			
			
			
			

		       //while(!in.ready()) { 

			//creating BigInteger Objects to tstore tehe key results
//			String num=Math.pow(ih_TCPClient.n,x)+"";
//		   	BigInteger modcalc=new BigInteger(num);
//		   	BigInteger modby=new BigInteger(ih_TCPClient.n+"");
//			modcalc=modcalc.mod(modby);
//			ih_TCPClient.clientkey=modcalc.intValue();
//		    //ih_TCPClient.clientkey=(int)((Math.pow(ih_TCPClient.g,x)) % ih_TCPClient.n);
//		    ih_TCPClient.flag=true;
//		    System.out.println("flag="+ih_TCPClient.flag);


        while (!link.isClosed()) {



                
                
                String response;
                String currline;

                // Receive the final report
                response = in.readLine();
                System.out.println(response);
                //reading in the final report
                while ((currline = in .readLine()) != null) {
                    System.out.println(currline);
                }
        }
		}  
        catch (IOException e) {
            e.printStackTrace();
        } finally {
        	//closing the connection.
            try {
                System.out.println("\n!!!!! Closing connection... !!!!!");
                link.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }

        }
    }
}