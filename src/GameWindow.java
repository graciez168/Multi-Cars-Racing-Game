//import java.util.Timer;

import java.awt.Color;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


import netgame.common.Client;



public class GameWindow extends JFrame implements MouseListener, ActionListener{


	private Track track;
	private GameClient connection;
	private JLabel messageFromServer;
	int[] playerPositions;
	int playerID;
	static final int FINISH_LINE = 2000;
	Player player;
	Timer timer;
	JButton newGame = new JButton("New Game");
	JButton quit = new JButton("Quit");
	final JPanel contentPane = new JPanel();
	int[] places;
	boolean endGame = false;
	JLabel first = new JLabel("First Place: ");
	JLabel second = new JLabel("Second Place: ");
	JLabel third = new JLabel("Third Place: ");
	JLabel fourth = new JLabel("Fourth Place: ");
	JLabel purple = new JLabel("Purple Player");
	JLabel red = new JLabel("Red Player");
	JLabel blue = new JLabel("Blue Player");
	JLabel green = new JLabel("Green Player");


	public GameWindow(final String hubHostName, final int hubPort) {
		//setLayout(null);

		this.player = new Player(10);
		messageFromServer = makeLabel(100,205,500,25,16,Color.BLACK);
		places = new int[5];
		this.addKeyListener(new ArrowListener());

		timer = new Timer(10, this);
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setTitle("The Race");
		setLocation(100,100);
		setSize(520, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);



		try {

			final GameClient c = new GameClient(hubHostName,hubPort);
			playerID = c.getID();
			track = new Track(5, 4, 10, 1);
			track.setLayout(null);
			track.setSize(520, 500);
			track.setLocation(0, 0);

			System.out.println("into Runnable");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					connection = c;
					if (c.getID() < 4) { 
						// This is Player #1.  Still have to wait for second player to
						// connect.  Change the message display to reflect that fact.
						messageFromServer.setText("Waiting for opponents to connect...");
					}
					else {
						remove(messageFromServer);
						contentPane.add(track);
						//startGame();
						//System.out.println("content pane set");
					}
				}
			});
		}
		catch (final IOException e) {
			// Error while trying to connect to the server.  Tell the
			// user, and end the program.  Use SwingUtilties.invokeLater()
			// because this happens in a thread other than the GUI event thread.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dispose();
					JOptionPane.showMessageDialog(null,"Could not connect to "
							+ hubHostName +".\nError:  " + e);
					System.exit(0);
				}
			});
		}
		first.setSize(150, 40);
		first.setLocation(50, 10);
		first.setVisible(false);
		track.add(first);



		second.setSize(150, 40);
		second.setLocation(50, 110);
		second.setVisible(false);
		track.add(second);


		third.setSize(150, 40);
		third.setLocation(50, 210);
		third.setVisible(false);
		track.add(third);


		fourth.setSize(150, 40);
		fourth.setLocation(50, 310);
		fourth.setVisible(false);
		track.add(fourth);

		purple.setSize(150, 40);
		purple.setVisible(false);
		track.add(purple);
		
		red.setSize(150, 40);
		red.setVisible(false);
		track.add(red);
		
		blue.setSize(150, 40);
		blue.setVisible(false);
		track.add(blue);
		
		green.setSize(150, 40);
		green.setVisible(false);
		track.add(green);


		track.add(newGame);
		newGame.setSize(100, 40);
		newGame.setLocation(50, 410);
		newGame.setVisible(false);
		newGame.addActionListener(this);

		track.add(quit);
		quit.setSize(100, 40);
		quit.setLocation(200, 410);
		quit.setVisible(false);
		quit.addActionListener(this);

	}

	JLabel makeLabel(int x, int y, int width, int height, int fontSize, Color color) {
		JLabel  label = new JLabel();

		//System.out.println("label created");
		label.setBounds(x,y,width,height);
		label.setOpaque(false);
		label.setForeground(color);
		label.setFont(new Font("Serif", Font.BOLD, fontSize));
		label.setText("WAITING FOR CONNECTION");
		contentPane.add(label);
		return label;
	}



	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {
		requestFocusInWindow();
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(quit)) {
			System.exit(0);
		}
		if (evt.getSource().equals(newGame)) {
			connection.send("restart");
			newGame.setEnabled(false);
		}
		track.currentPosition++;
		for (int i = 0; i < playerPositions.length; i++) {
			playerPositions[i]++;
		}
		track.topDashUnit+= 6;
		if (track.topDashUnit > track.DASH_LENGTH + 50) {
			track.topDashUnit = 0;
		}
		
			repaint();
		

	}

	public void startGame() {
		//System.out.println("width: " + width);
		
		for (int i = 1; i < places.length; i++) {
			places[i] = 0;
		}
		
		endGame = false;
		newGame.setEnabled(true);
		contentPane.remove(messageFromServer);

		first.setVisible(false);
		second.setVisible(false);
		third.setVisible(false);
		fourth.setVisible(false);
		newGame.setVisible(false);
		quit.setVisible(false);
		purple.setVisible(false);
		red.setVisible(false);
		blue.setVisible(false);
		green.setVisible(false);

		contentPane.add(track);
		for (int i = 0; i < playerPositions.length; i++) {
			playerPositions[i] = 100;
		}
		track.topDashUnit = 0;
		track.currentPosition = 0;
		player = new Player(10);
		timer.setDelay(40); // use this to set speed
		setContentPane(contentPane);
		setFocusable(true);
		requestFocus();
		timer.start();



	}

	public void endGame() {
		
		first.setVisible(true);
		second.setVisible(true);
		third.setVisible(true);
		fourth.setVisible(true);
		newGame.setVisible(true);
		quit.setVisible(true);
		

		//System.out.println("labels visible");


		for (int i = 1; i < places.length; i++) {
			if (places[i] == 0)
				break;
			//JLabel label = new JLabel("");
			switch (places[i]) {
			case 1: {
				purple.setLocation(70, 100 * i - 60);
				purple.setVisible(true);
				break;
			}
			case 2: {
				red.setLocation(70, 100 * i - 60);
				red.setVisible(true);
				break;
			}
			case 3: {
				blue.setLocation(70, 100 * i - 60);
				blue.setVisible(true);
				break;
			}
			case 4: {
				green.setLocation(70, 100 * i - 60);
				green.setVisible(true);
				break;
			}
			}
			
			
		}
		//setContentPane(contentPane);
		//this.removeKeyListener(this);
	}

	public class ArrowListener implements KeyListener {
		public void keyPressed(KeyEvent e) {

		}

		public void keyReleased(KeyEvent e) {
			//System.out.println(e.getKeyCode());
			if (e.getKeyCode() == 39) {
				player.changeLane(1);
			}

			if (e.getKeyCode() == 37) {
				player.changeLane(-1);
			}
		}

		public void keyTyped(KeyEvent e) {

		}
	}
	public class GameClient extends Client {

		public GameClient(String hubHostName, int hubPort) throws IOException {
			super(hubHostName, hubPort);
			//System.out.println("finished client");
		}

		public void messageReceived(final Object message) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String code = message.toString();
					if (code.equals("start")) {
						startGame();
					}
					else if (code.startsWith("Player")) {
						if (code.endsWith("end")) {
							for (int i = 1; i < places.length; i++) {
								//System.out.println("places value: " + places[i]);
								if (places[i] == 0) {
									places[i] = Integer.parseInt(code.substring(7,8));
									System.out.println(places[i] + " in " + i);

									if (endGame) {
										switch (places[i]) {

										case 1: {
											purple.setLocation(70, 100 * i - 60);
											purple.setVisible(true);
											break;
										}
										case 2: {
											red.setLocation(70, 100 * i - 60);
											red.setVisible(true);
											break;
										}
										case 3: {
											blue.setLocation(70, 100 * i - 60);
											blue.setVisible(true);
											break;
										}
										case 4: {
											green.setLocation(70, 100 * i - 60);
											green.setVisible(true);
											break;
										}

										}

									}
									break;
								}
							}
						}
						else {
							int player = Integer.parseInt(code.substring(7,8));
							int position = Integer.parseInt(code.substring(9));
							playerPositions[player] = position;
						}
					}

				}
			});
		}

		protected void serverShutdown(String message) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(GameWindow.this,
							"Your opponent has quit.\nThe game is over.");
					System.exit(0);
				}
			});
		}


	}




	public class Track extends JPanel{
		int lanes;

		final static int RENDER_POSITIONS = 100;
		final static int DASH_LENGTH = 25;

		int width;
		int height;
		int topDashUnit; // dahs unit = dahs plus space in front of it
		int currentPosition;

		int numCheckpoints;



		//int playerID;
		int panelHeight = 500;


		public Track(int lanes, int numPlayers, int numCheckpoints, int playerID) {

			this.lanes = lanes;
			playerPositions = new int[numPlayers + 1];
			width = lanes * 70 + (lanes - 1) * 5;
			height = 520;

			this.numCheckpoints = numCheckpoints;
			currentPosition = 1500;

			setFocusable(true);
			requestFocusInWindow();

		}



		public void paintComponent(Graphics g) {
			currentPosition = track.currentPosition;

			if (!endGame && currentPosition == FINISH_LINE) {
				endGame();
				endGame = true;
				connection.send("Player " + playerID + " " + "end");
				System.out.println("winner sent");
				g.setColor(player.color);
				g.fillRect(0, 0, width, height);

			}

			else if (!endGame) {
				topDashUnit = track.topDashUnit;
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				int i = 0;
				int xNum = 69;//144
				for (int j = 0; j < lanes - 1; j++) {
					i = 0;
					while (i < panelHeight) {
						g.setColor(Color.white);
						if (i == 0) {
							if (topDashUnit <= DASH_LENGTH) {
								g.fillRect(xNum, i, 5, topDashUnit);
							}
							else {
								g.fillRect(xNum, topDashUnit - DASH_LENGTH, 5, DASH_LENGTH);
							}
							i+= topDashUnit;
						}
						else {
							g.fillRect(xNum, i, 5, DASH_LENGTH);
							i += DASH_LENGTH;
						}
						i += 50;

					}
					xNum += 75;
				}

			

			for(int c = 0; c < player.checkpoints.length; c++) {
				player.checkpoints[c].draw(g, currentPosition, player);
			}

			for(int o = 0; o < player.obstacles.size(); o++) {
				player.obstacles.get(o).draw(g, this, currentPosition);
			}

			player.draw(g, this);

			g.setColor(Color.BLACK);
			g.fillRect(width + 1, 0, 100, height);
			
			}

			int ratio = FINISH_LINE / 480;
			g.setColor(new Color(153,0,153));
			g.fillRect(width + 11, 480 - (playerPositions[1]/ratio), 5, 5);
			g.setColor(Color.RED);
			g.fillRect(width + 36, 480 - (playerPositions[2]/ratio), 5, 5);
			g.setColor(Color.BLUE);
			g.fillRect(width + 61, 480 - (playerPositions[3]/ratio), 5, 5);
			g.setColor(Color.GREEN);
			g.fillRect(width + 86, 480 - (playerPositions[4]/ratio), 5, 5);

			if (endGame) {
				g.setColor(player.color);
				g.fillRect(0, 0, width, height);
			}


		}



	}



	public class Player {

		private Checkpoint lastCheckpoint;
		private int currentPosition;
		private int currentLane;
		ArrayList<Obstacle> obstacles;
		int yValueOnTrack = 400;
		int numObstacles = 100;
		Color color;


		Checkpoint[] checkpoints;



		public Player(int numCheckpoints) {
			lastCheckpoint = new Checkpoint(100);
			currentPosition = 100;
			currentLane = 0;
			obstacles = new ArrayList<Obstacle>();
			checkpoints = new Checkpoint[numCheckpoints];
			for (int i = 0; i < numCheckpoints; i++) {
				checkpoints[i] = new Checkpoint(FINISH_LINE/(numCheckpoints) * (i));
			}
			for (int o = 0; o < numObstacles; o++) {
				obstacles.add(new Obstacle(FINISH_LINE/(numObstacles) * (o))); 
			}

			switch (playerID) {
			case 1: {
				color = new Color(153,0,153);
				break;
			}
			case 2: {
				color = Color.RED;
				break;
			}
			case 3: {
				color = Color.blue;
				break;
			}
			case 4: {
				color = Color.green;
				break;
			}
			default: {
				color = Color.black;

			}
			}

		}

		public void hitObstacle() {

			currentPosition = lastCheckpoint.position;
			currentLane = lastCheckpoint.lane;
			connection.send("Player " + connection.getID() + " " + currentPosition);

		}

		public void updateCheckpoint(Checkpoint point) {
			lastCheckpoint = point;


		}

		public void changeLane(int direction) {
			int newLane = currentLane + direction;
			if (newLane >= 0 && newLane <  track.lanes) {
				currentLane = newLane;
			}

		}

		public void draw(Graphics g, Component c) {
			//ImageIcon car = new ImageIcon("greencar.png");
			int x = 10 + player.currentLane * 75;
			//System.out.println(player.currentLane + " " + currentLane);
			//car.paintIcon(c, g, 100, 100);
			g.setColor(color);
			g.fillRect(x, yValueOnTrack, 50, 50);
			currentPosition++;
			//System.out.println(currentPosition + " " + player.currentPosition);
		}
	}

	private class Checkpoint {

		private static final int SIZE = 50;
		private int position;
		private int lane; 
		private Color blockColor = Color.GREEN;
		private Color circleColor = Color.BLUE;
		int x;
		boolean hit = false;

		public Checkpoint(int position) {
			this.position = position;
			//System.out.println(position);
			lane = (int)(Math.random() * 5);
			//System.out.println(lane);
			x =  10 + lane * 75;
		}



		public void hitCheckpoint(Player player) {
			player.updateCheckpoint(this);

		}

		public void draw(Graphics g, int trackPosition, Player player) {
			if (hit) {
				circleColor = Color.ORANGE;
				hitCheckpoint(player);
				hit = false;
			}
			if (position >= trackPosition && position < trackPosition + track.RENDER_POSITIONS) {
				int y = (100 - (position - trackPosition)) * 5;
				//System.out.println("Checkpoint: " + position + " " + trackPosition + " " + y);
				g.setColor(blockColor);
				g.fillRect(x, y, SIZE, SIZE);
				g.setColor(circleColor);
				g.fillOval(x + 5, y + 5, SIZE - 10, SIZE - 10);

				//System.out.println(y + " " + playerYOnTrack);
				if (lane == player.currentLane && y == player.yValueOnTrack) {
					//System.out.println(lane + " " + player.currentLane);
					hit = true;
				}

				//System.out.println(y + " " + trackPosition + " " + position);

			}
		}

	}

	private class Obstacle {

		private int position;
		private int lane;
		int imageSize = 50;
		int x;
		int hit = 0;


		public Obstacle(int position) {
			this.position = position;
			lane = (int)(Math.random() * 5);
			x = 10 + lane * 75;
		}



		public void draw(Graphics g, Component c, int trackPosition) {
			if (position >= trackPosition && position < trackPosition + Track.RENDER_POSITIONS) {
				int y = (100 - (position - trackPosition)) * 5 ;
				//ImageIcon trafficCone = new ImageIcon("trafficcone.png");
				//trafficCone.paintIcon(c, g, x, y);
				g.setColor(Color.YELLOW);
				g.fillRect(x, y, imageSize, imageSize);
				//((y > player.yValueOnTrack + 5 && y < player.yValueOnTrack + 45)) ||
				if ((lane == player.currentLane) && ((y > player.yValueOnTrack + 5 && y < player.yValueOnTrack + 45) || (player.yValueOnTrack > y && player.yValueOnTrack < y + 50))) 
					//track.timer.setDelay(100);
					hit++;


				if (hit == 3) {
					hit = 0;
					player.hitObstacle();
					track.currentPosition = player.currentPosition - 20;
					//System.out.println("player position: " + player.currentPosition);
				}

			}

		}


	}




}
