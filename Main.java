
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application
{
	
	TextField messageField;
	Button serverChoice,clientChoice, refresh, sendAll, sendTo, addGroup, sendGroup, resetGroup;
	HashMap<String, Scene> sceneMap;
	HBox buttonBox, listBox, clientButtonBox;
	VBox clientBox, chatBox, clientListBox, groupBox, serverBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	
	Text chatLabel,clientLabel,groupLabel, instructions,serverLabel;
	
	ListView<String> serverItems, clientChat, clientList, groupMembers;
	ArrayList<Integer> groupChatClients = new ArrayList<Integer>();
	ArrayList<Integer> clientNumbers = new ArrayList<Integer>();
	int clientNum;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		guiInit();
		buttonHandlers();
		
		//Switch to server gui
		serverChoice.setOnAction(e->
		{
			primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("This is the Server");
			serverConnection = new Server(data -> 
			{
				//Call when new items are added to server list
				Platform.runLater(()->
				{
					serverItems.getItems().add(((ChatInfo) data).getMessage());
				});

			});
											
		});
		
		//Switch to client gui
		clientChoice.setOnAction(e-> 
		{
			primaryStage.setScene(sceneMap.get("client"));
			primaryStage.setTitle("This is a client");
			clientConnection = new Client(data->
			{
				//Call when data is sent 
				Platform.runLater(()->
				{
					//Display message
					clientChat.getItems().add(((ChatInfo) data).getMessage());
					
					//Get clientNum
					clientNum = ((ChatInfo) data).getClientNum();
					
					clientList.getItems().clear();
					clientNumbers.clear();
					clientNumbers = ((ChatInfo) data).getClientList();
					//Display all clients
					for(int i = 0; i < clientNumbers.size(); i++)
					{
						clientList.getItems().add("Client " + String.valueOf(clientNumbers.get(i)));
					}
					
					groupMembers.getItems().clear();
					for(int x : groupChatClients)
					{
						groupMembers.getItems().add("Client " + String.valueOf(x));
					}								
				});
			});
			clientConnection.start();
		});
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	public void guiInit()
	{
		//First Scene
		serverChoice = new Button("Server");
		serverChoice.setStyle("-fx-pref-width: 400px; -fx-pref-height: 250px; -fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
				
		//First Scene
		clientChoice = new Button("Client");
		clientChoice.setStyle("-fx-pref-width: 400px; -fx-pref-height: 250px; -fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
				
		//First Scene
		buttonBox = new HBox(100, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		startPane.setStyle("-fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #950740;");


		
		//First Scene
		startScene = new Scene(startPane, 950, 750);
		
		//server gui
		serverItems = new ListView<String>(); 
		
		//client gui
		clientChat = new ListView<String>();
		clientList = new ListView<String>();
		groupMembers = new ListView<String>();
		
		messageField = new TextField();
		messageField.setStyle("-fx-pref-height: 25px;");
		sendAll = new Button("Send Global Message");
		sendAll.setStyle("-fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		sendTo = new Button("Send Private Message");
		sendTo.setStyle("-fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		addGroup = new Button("Add To Group");
		addGroup.setStyle("-fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		sendGroup = new Button("Send Group Message");
		sendGroup.setStyle("-fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		resetGroup = new Button("Reset Group Members");
		resetGroup.setStyle("-fx-fill: #950740; -fx-font-size: 1em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
	
	}
	public void buttonHandlers()
	{
		//Send global message
		sendAll.setOnAction(e->
			{	ChatInfo info = new ChatInfo(messageField.getText(), clientNum, -1, clientNumbers);
				clientConnection.send(info); 
				messageField.clear();
			});
		
		//Send private message to client selected from client list
		sendTo.setOnAction(e->
			{
				int clientNumber = getSelectionIndex();
				ChatInfo info = new ChatInfo(messageField.getText(), clientNum,clientNumber, clientNumbers);
				clientConnection.send(info); 
				messageField.clear();
			});
		
		//Add selected client from client list to group
		addGroup.setOnAction(e->
			{ 
				int clientNumber = getSelectionIndex();
				
				if(checkForDuplicates(clientNumber))
					groupChatClients.add(clientNumber);
				groupMembers.getItems().clear();
				
				for(int x : groupChatClients)
				{
					groupMembers.getItems().add("Client " + String.valueOf(x));
				}		
			});
		
		//Send message to group
		sendGroup.setOnAction(e->
			{
				ChatInfo info = new ChatInfo(messageField.getText(), clientNum, -2, groupChatClients, clientNumbers);
				clientConnection.send(info); 
				messageField.clear();
			});
		
		//Reset this client's created group
		resetGroup.setOnAction(e->
			{
				groupChatClients.clear();
				groupMembers.getItems().clear();
			});
	}
	
	public Scene createServerGui() 
	{
		serverLabel = new Text("Server Information");
		serverLabel.setStyle("-fx-fill: #950740; -fx-font-size: 2em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		
		serverBox = new VBox(serverLabel, serverItems);
		serverBox.setAlignment(Pos.CENTER);
		serverBox.setStyle("-fx-background-color: #89CFF0 ");
		
		
	
		return new Scene(serverBox, 500, 400);
	}
	
	public Scene createClientGui() 
	{
		instructions = new Text("Chat Room Instructions:\n"
							   + "To send Global Message write in the textbox and click Send Global Message.\n"
							   + "To send a private message select the user from the user list and click Send Private Message.\n"
							   + "To create a group, select user from the user list and click Add To Group.\n"
							   + "When you click Send To Group, the message will be sent to all members of your group.\n"
							   + "Clicking Reset Group will reset your group.");
		instructions.setStyle("-fx-fill: #950740; -fx-font-size: 1.8em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		chatLabel = new Text("Chat");
		chatLabel.setStyle("-fx-fill: #950740; -fx-font-size: 2em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		chatBox = new VBox(10,chatLabel, clientChat);
		
		clientLabel = new Text("Users On Server");
		clientLabel.setStyle("-fx-fill: #950740; -fx-font-size: 2em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		clientListBox = new VBox(10,clientLabel, clientList, addGroup);
		
		groupLabel = new Text("Your Group");
		groupLabel.setStyle("-fx-fill: #950740; -fx-font-size: 2em; -fx-font-weight: bold; -fx-background-color: #89CFF0;");
		groupBox = new VBox(10,groupLabel, groupMembers, resetGroup);
		
		clientButtonBox = new HBox(10, sendAll, sendTo,sendGroup);
		
		listBox = new HBox(chatBox, clientListBox, groupBox);
		clientBox = new VBox(10, messageField, clientButtonBox, listBox, instructions);
		clientBox.setStyle("-fx-background-color: orange");
		return new Scene(clientBox, 950, 750);

		
	}
	
	public boolean checkForDuplicates(int newClient)
	{
		if(groupChatClients.contains(newClient))
			return false;
		else 
			return true;
	}
	
	public int getSelectionIndex()
	{
		String client = clientList.getSelectionModel().getSelectedItem();
		String[] split = client.split(" ");
		int clientNumber = Integer.parseInt(split[1]);
		return clientNumber;
	}
	
}
