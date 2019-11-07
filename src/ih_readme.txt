#####################################
Izhak Hamidi E01533340
CSOC439 Computer Networking Principles
Project 2
#####################################
Project Description:
This is a multithreadd Server chat room project. It will allow multiple clients to connect to the server by creating a thread for each client to join. Each client has their own 2 threads, one manages sending messages while the other manages receiveing messages. It will broadcast the departure and arrival for all clients. For each cient that leaves ti will send them a final report of how long they were connected to the server and how many messages they received.

To Run this program:
Run the server java file on GCP or locally using java ih_TCPServerMT -p 20450	(-p and 20450 are an optional portnumber argument)
Once the server is started, you will need to run the client java file on command line
You may connect using multiple clients to the same server.
This file takes three optional arguments eachwith a prefix of -p,-h, or -u. (port, host, username)
If you are connecting to the server that is not local, you will need to use -h "hostname"
Four example of a valid command: java ih_TCPClient.java -p 20450
java ih_TCPClient.java -u iham -p 20450
java ih_TCPClient.java
java ih_TCPClient.java -h 127.0.0.1 -p 20450 -u iham
After one of these commands are entered, you will get a message in the server saying that "Client has connected to", which confirms that the connection is succesful

Conclusion:
I did learn a lot about multithreading adn how it is used in server chat rooms.
This project took around 8 hours to complete.
I struggled in a couple of areas.
-Trying to implement a datastructure to hold all connections that can be accessed through differen classes.
-Ceating the two thread classes for the clients
-Couple of ConcurrentModification Exceptions.