import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import netgame.common.Hub;


public class GameServer extends Hub{
	
	
	//GameState game;
	int playersRestart = 0;
	

	

	
	public GameServer(int port) throws IOException {
		super(port);
		//game = new GameState(4);
	}
	
	
	
	
	
	public void playerConnected(int playerID) {
		System.out.println("player: " + playerID);
		if (playerID == 4) {
			//shutDownHub();
			sendToAll("start");
			
		}
		
		
	}
	
	public void playerDisconnected(int playerID)  {
		shutDownHub();
	}
	
	protected void messageReceived(int playerID, Object message) {
		
		if (message.toString().startsWith("Player")) {
			sendToAll(message.toString());
			
		}
		if (message.toString().equals("restart")) {
			playersRestart++;
			if (playersRestart == 4) {
				playersRestart = 0;
				sendToAll("start");
			}
		}
	}
	
	
	
	
	
	
}
