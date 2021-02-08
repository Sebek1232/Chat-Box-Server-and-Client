import java.util.ArrayList;

import javafx.scene.control.ListView;



// Used to send chat information between clients and server
public class ChatInfo implements java.io.Serializable
{
	private String message;
	private int clientNum;
	private ArrayList<Integer> groupChat;
	private ArrayList<Integer> clientList;
	private int who;
	

	// Used when just sending messages
	public ChatInfo(String message, int clientNum)
	{
		this.message = message;
		this.clientNum = clientNum;
	}
	
	// Used when sending global/private messages
	// who == -1 then its global 
	// who == int > 0 its private message to client #who
	public ChatInfo(String message, int clientNum, int who,ArrayList<Integer> clientList)
	{
		this.message = message;
		this.clientNum = clientNum;
		this.who = who;
		this.clientList = clientList;
	}
	
	// Used when sending group message
	// who must equal -2
	// groupChat holds the client numbers in group
	public ChatInfo(String message, int clientNum, int who, ArrayList<Integer> groupChat, ArrayList<Integer> clientList)
	{
		this.message = message;
		this.clientNum = clientNum;
		this.who = who;
		this.groupChat = groupChat;
		this.clientList = clientList;
	}
	
	// Used when sending messages with clientLists
	public ChatInfo(String message, int clientNum, ArrayList<Integer> clientList)
	{
		this.message = message;
		this.clientNum = clientNum;
		this.clientList = clientList;
	}
	
	//Getter/setters
	public void setMessage(String message)
	{
		this.message = message;
	}
	public String getMessage()
	{
		return message;
	}
	public int getClientNum()
	{
		return clientNum;
	}
	public ArrayList<Integer> getGroupChat(){
		return groupChat;
	}
	public int getWho() 
	{
		return who;
	}
	public ArrayList<Integer> getClientList()
	{
		return clientList;
	}
			
}
