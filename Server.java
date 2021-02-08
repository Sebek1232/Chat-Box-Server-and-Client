
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	HashMap<Integer, ClientThread> clients = new HashMap<Integer, ClientThread>();
	//ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<Integer> clientList = new ArrayList<Integer> ();
	
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread{
		
		public synchronized void run() {
		
			//Server launched and waiting 
			try(ServerSocket mysocket = new ServerSocket(5555);)
			{
				System.out.println("Server is waiting for a client!");
		    	while(true) 
		    	{
		    	
		    		//When client connects add to client List 
		    		ClientThread c = new ClientThread(mysocket.accept(), count);
		    		clients.put(count, c);
		    		clientList.add(count);
				
		    		//Display server message that client has connected
		    		ChatInfo info = new ChatInfo("client has connected to server: " + "client #" + count, count);
		    		callback.accept(info);
				
		    		c.start();
		    		count++;
			    	}	
			 }
				//Server did not launch
				catch(Exception e) 
				{
					//Display server message that server did not launch
					ChatInfo info = new ChatInfo("Server socket did not launch",count);
					callback.accept(info);
				}
			}
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			//Update all Clients
			public synchronized void updateClients(ChatInfo message)  
			{
				
				
				for(ClientThread x : clients.values()) 
				{
					try 
					{
					 x.out.reset();
					 x.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}
			
			// Update One client
			// The client to update is in message.who-1
			public synchronized void updateOneClient(ChatInfo message)
			{
				ClientThread t = clients.get(message.getWho());
				try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {}
			}
				
			// Update clients in group
			// The clients are listed in message.groupChat
			// The array holds the client number.
			public synchronized void updateGroupClients(ChatInfo message)
			{
				for(Integer client : message.getGroupChat())
				{
					ClientThread t = clients.get(client);
					try {
						 t.out.writeObject(message);
						}
						catch(Exception e) {}
				}
			}
			public synchronized void run()
			{
				
				// open streams
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				ChatInfo info = new ChatInfo("new client # " + count + " on server", count, clientList);
				// Display message on server that new client connected.
				callback.accept(info);
				//Tell all clients that new client connected.
				updateClients(info);
					
				 while(true) 
				 {
					    try {
					    	//Read in data.
					    	ChatInfo data = (ChatInfo) in.readObject();
					    	
					    	//Global chat
					    	if(data.getWho() == -1)
					    	{
					    		data.setMessage("(Global) Client #" + count + " sent: " + data.getMessage());
					    		callback.accept(data);
						    	updateClients(data);
					    	}
					    	//Group chat
					    	else if(data.getWho() == -2)
					    	{
					    		data.setMessage("(Group) Client #" + count + " sent: " + data.getMessage());
					    		callback.accept(data);
						    	updateGroupClients(data);
					    	}
					    	//Private Chat
					    	else 
					    	{
					    		data.setMessage("(Private) Client #" + count + " sent: " + data.getMessage());
					    		callback.accept(data);
					    		updateOneClient(data);
					    	}
					    }
					    
					    catch(Exception e) {
					    	//Client Dropped.
					    	clientList.remove((Object)count);
							ChatInfo data = new ChatInfo("Client #"+count+" has left the server!",count, clientList);
							//Display to server that client dropped
							callback.accept(data);
							
							//Tell all clients that client dropped
					    	updateClients(data);
					    	
					    	clients.remove(count);
					    	break;
					    }
					}
				}
			
			
		}
}


	
	

	
