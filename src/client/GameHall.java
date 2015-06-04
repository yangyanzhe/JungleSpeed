package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


/***************************************************************************************
 * Game Hall Layout
 * Author: Yanzhe Yang, Bao Yu
 * Time:   2015/06/02
 * reference: https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html
 *****************************************************************************************/

public class GameHall extends JPanel implements MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	// private JPanel drawingPanel;
	public HallWindow drawingPanel;
	private Client p;
	
	Image[] playersImg = new Image [16];
	Image[] chairsImg = new Image [6];
	Image tableImg;
	Image hallImg;
	
	MediaTracker mt;
	
	public GameHall(Client p) {
		super(new BorderLayout());
		
		new Dimension(0, 0);
		loadHallMedia();
		
		// set up the hall window
		drawingPanel = new HallWindow(p);
		drawingPanel.setBackground(Color.white);
		drawingPanel.setPreferredSize(new Dimension(800, 2000));
		drawingPanel.addMouseListener(this);
		
		// put the drawing area in a scroll pane
		JScrollPane scroller = new JScrollPane(drawingPanel);
		scroller.setPreferredSize(new Dimension(800, 600));
		
		// layout
		add(scroller, BorderLayout.CENTER);
		
		this.p = p;
	}
	
	public void loadHallMedia(){
		// chair 
		// down: 0; left: 1; up:2; right:3
		for(int i = 0; i<6; i++){
			String str_chair = "res/chair" + i + ".png";
			chairsImg[i] =  getToolkit().getImage(str_chair);
		}
		
		// player
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				String str_player = "res/player" + i + j + ".png";
				playersImg[i*4 + j] = getToolkit().getImage(str_player);
			}
		}
		
		// table
		String str_table = "res/table.png";
		tableImg = getToolkit().getImage(str_table);
		
		// background
		String str_back = "res/hall_background.jpg";
		hallImg = getToolkit().getImage(str_back);
		
		
		mt = new MediaTracker(this);
		for(int i = 0; i < 6; i++){
			mt.addImage(chairsImg[i], 0);
		}
		for(int i = 0; i < 16; i++){
			mt.addImage(playersImg[i], 0);
		}
		mt.addImage(tableImg, 0);
		mt.addImage(hallImg, 0);
		
		try
		{
			mt.waitForAll();
		}catch(InterruptedException mye){System.out.println(mye);}
	}
	
 	public class HallWindow extends JPanel{
		
		private static final long serialVersionUID = 1L;
		Client p;
		int maxPlayersNumber;
		int totalPlayersNumber;
		int row;
		int line;
		int[] tables;
		int[] chairs;
		
		public HallWindow(Client p)
		{
			this.p = p;
		
			totalPlayersNumber = 108;
			
			row = 2;
			line = 6;
			
			chairs = new int[totalPlayersNumber];
			tables = new int[12];
			
			for (int i = 0; i < totalPlayersNumber; i++){
				chairs[i] = -1;
			}
			for (int i = 0; i < 12; i++){
				tables[i] = -1;
			}
			//tables[0] = 1;
		} // end hallWindow(client p)
		
		public void paintComponent(Graphics g)
		{
			// draw background
			g.drawImage(hallImg, 0, 0, 800, 2000, null);

			drawChairsBehind(g);
			
			// row: 2, line: 6
			for (int i = 0; i < row; i++){
				for (int j = 0; j < line; j++){
					int X = 350 * i + 100;
					int Y = 320 * j + 100;
					g.drawImage(tableImg, X, Y, 200, 137, null);
				}
			}
			
			drawChairs(g);
			// drawPlayers(g);
			// drawTableStatus(g);
			
		} // end paintComponent
		
		public void addchairs(int no, int sex)
		{
			if(chairs[no] != -1)
				return;
			
			// 0-female 1-male
			chairs[no] = sex;
			this.repaint();
		}
		
		public void removechairs(int no)
		{
			chairs[no] = -1;
			this.repaint();
		}	
		
		
		public void drawChairsBehind(Graphics g){
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < line; j++) {
					
					int X = 350 * i;
					int Y = 320 * j;
					
					for(int k = 4; k < 8; k++) {
						int x1 = 0, y1 = 0;
						switch(k){
						case 4:
							x1 = X + 80;
							y1 = Y + 80;
							break;
						case 5:
							x1 = X + 120;
							y1 = Y + 60;
							break;
						case 6:
							x1 = X + 190;
							y1 = Y + 60;
							break;
						case 7:
							x1 = X + 230;
							y1 = Y + 80;
							break;
						default: 
							continue;
						}
						
						
						int id = 16 * j + 8 * i + k;
						g.drawImage(chairsImg[k/2], x1, y1, 83, 100, null);
						if(chairs[id] != -1){
							g.drawImage(playersImg[(k/2)*4+ (chairs[id] * 2)], x1, y1, 83, 100, null);
						}
					} // end loop k
				} // end loop j
			} // end loop i
		}
		
		public void drawChairs(Graphics g){
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < line; j++) {
					
					int X = 350 * i;
					int Y = 320 * j;
					
					for(int k = 0; k < 4; k++) {
						int x1 = 0, y1 = 0;
						switch(k){
						case 0:
							x1 = X + 240;
							y1 = Y + 130;
							break;
						case 1:
							x1 = X + 190;
							y1 = Y + 160;
							break;
						
						case 2:
							x1 = X + 70;
							y1 = Y + 125;
							break;
						case 3:
							x1 = X + 120;
							y1 = Y + 150;
							break;
						default: 
							continue;
						}
						
						int id = 16 * j + 8 * i + k;
						if(chairs[id] == -1){
							g.drawImage(chairsImg[k/2], x1, y1, 83, 100, null);
						}
						else{
							g.drawImage(playersImg[(k/2)*4 + (chairs[id]*2)], x1, y1, 83, 100, null);
							if(k/2 < 2){
								g.drawImage(chairsImg[k/2+4], x1, y1, 83, 100, null);
							}
							else{
								g.drawImage(chairsImg[k/2], x1, y1, 83, 100, null);
							}
						}
					} // end loop k
				} // end loop j
			} // end loop i
		}
		
		public void addtables(int no)
		{
			tables[no] = 1;
			this.repaint();
		}
		
		public void removetables(int no)
		{
			tables[no] = -1;
			this.repaint();
		}	
		
		/*
		public void drawTableStatus(Graphics g){
			for(int i = 0; i<3; i++){
				for(int j = 0; j<4; j++){
					if(tables[i+j*3] != -1){
						int x1 = 85 + 25 + 200 * i;
						int y1 = 85 + 180 * j;
						
						g.drawImage(p.logo, x1, y1, 63, 63, null);
					}
				}
			}
		}*/
		
	} // end hallWindow
	
	// handle mouse event
	public void mouseClicked(MouseEvent e) {
		
		int currentX = e.getX();
		int currentY = e.getY(); 
		int tablex = currentX / 350;
		int tabley = currentY / 320;
		
		int x = currentX - tablex * 350;
		int y = currentY - tabley * 320;
		System.out.println("mouse clicks at (" + currentX + ", " + currentY + " )");
		int id = 0;
		
		if(190 <= x && x <= 261 && 160 <= y && y <= 260){
			id = 1;
		}
		else if(240 <= x && x <= 310 && 150 <= y && y <= 230){
			id = 0;
		}
		else if(70 <= x && x <= 135 && 140 <= y && y <= 195){
			id = 2;
		}
		else if(135 <= x && x <= 180 && 150 <= y && y <= 250){
			id = 3;
		}
		else if(80 <= x && x <= 135 && 80 <= y && y <= 139){
			id = 4;
		}
		else if(135 <= x && x <= 183 && 60 <= y && y <= 130){
			id = 5;
		}
		else if(190 <= x && x <= 255 && 60 <= y && y <= 130){
			id = 6;
		}
		else if(255 <= x && x <= 299 && 80 <= y && y <= 150){
			id = 7;
		}
		else{
			return;
		}
			
		System.out.println("new player added in Pos: (" + tablex + ", " + tabley + "), in " + id);
		int number = tablex * 8 + tabley * 16 + id;
		int playerType = ((int)(Math.random() * 4)) % 2;
		drawingPanel.addchairs(number, playerType);
		
		int tableNoInServer = tablex + tabley * 2;
		p.pClient.os.println("jointable~" + tableNoInServer + "~" + id);
		p.pClient.os.flush();
	}

	public void mouseEntered(MouseEvent arg0) { }

	public void mouseExited(MouseEvent arg0) { }

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) { }
}