import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{
	
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	Client(Consumer<Serializable> call){
	
		callback = call;
	}
	
	public void run() {
		
		//Connect socket and open streams
		try {
			socketClient= new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		while(true) {
			 
			try {
				//Read in message
				ChatInfo message = (ChatInfo) in.readObject();
				//Display to client gui
				callback.accept(message);
			}
			catch(Exception e) {}
		}
	
    }
	
	//Send data to server
	public void send(ChatInfo data) {
		
		try {
			System.out.println(data.getMessage());
			out.reset();
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
