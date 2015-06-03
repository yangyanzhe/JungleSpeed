package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sun.net.www.content.audio.x_aiff;

/***************************************************************************************
 * Game Hall Layout
 * Author: Yanzhe Yang, Bao Yu
 * Time:   2015/06/02
 * reference: https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html
 *****************************************************************************************/

public class GameHall extends JPanel implements MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	// private JPanel drawingPanel;
	private HallWindow drawingPanel;
	private Client p;
	
	public GameHall(Client p) {
		super(new BorderLayout());
		
		new Dimension(0, 0);
		
		// set up the hall window
		drawingPanel = new HallWindow(p);
		drawingPanel.setBackground(Color.white);
		drawingPanel.setPreferredSize(new Dimension(680, 800));
		drawingPanel.addMouseListener(this);
		
		// put the drawing area in a scroll pane
		JScrollPane scroller = new JScrollPane(drawingPanel);
		scroller.setPreferredSize(new Dimension(680, 540));
		
		// layout
		add(scroller, BorderLayout.CENTER);
		
		this.p = p;
	}

 	public class HallWindow extends JPanel{
		
		private static final long serialVersionUID = 1L;
		Client p;
		int maxPlayersNumber;
		int totalPlayersNumber;
		int row;
		int line;
		int[] chairs;
		int[] tables;
		
		public HallWindow(Client p)
		{
			this.p = p;
		
			totalPlayersNumber = 108;
			
			row = 3;
			line = 4;
			
			chairs = new int[totalPlayersNumber];
			tables = new int[12];
			for (int i = 0; i < totalPlayersNumber; i++){
				chairs[i] = -1;
			}
			for (int i = 0; i < 12; i++){
				tables[i] = -1;
			}
			tables[0] = 1;
		} // end hallWindow(client p)
		
		public void paintComponent(Graphics g)
		{
			// draw background
			g.drawImage(p.hallBack, 0, 0, 680, 800, null);

			for (int i = 0; i < row; i++){
				for (int j = 0; j < line; j++){
					int X = 25 + 200 * i;
					int Y = 180 * j;
					g.drawImage(p.table, X+40, Y+40, 150, 150, null);
				}
			}
			
			drawPlayers(g);
			drawTableStatus(g);
			
		} // end paintComponent
		
		public void addchairs(int no)
		{
			chairs[no] = 1;
			this.repaint();
		}
		
		public void removechairs(int no)
		{
			chairs[no] = -1;
			this.repaint();
		}	

		public void drawPlayers(Graphics g){
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < line; j++) {
					
					int X = 40 + 25 + 200 * i;
					int Y = 40 + 180 * j;
					
					for(int k = 0; k <= 8; k++) {
						if(k == 4){
							continue;
						}
						
						int id = i * 9 + k + 27 * j;
						if(chairs[id] != -1){
							int x1 = X + (k % 3) * 50 + 3;
							int y1 = Y + (k / 3) * 50 + 3;
							g.drawImage(p.player, x1, y1, 43, 43, null);
						}
					} // end loop k
				} // end loop j
			} // end loop i
		} // end draw player
		
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
		}
		
	} // end hallWindow
	
	// handle mouse event
	public void mouseClicked(MouseEvent e) {
		
		int currentX = e.getX();
		int currentY = e.getY(); 
		int tablex = 0;
		int tabley = 0;
		int playerPos = 0;
		int id = 0;
		
		if(25 <= currentX && currentX <= 575 && 40 <= currentY && currentY <= 870){
			tablex = (currentX - 65) / 200;
			tabley = currentY / 180;
			
			// the table has begun the game, cannot join
			if(drawingPanel.tables[tablex + tabley * 3] != -1){
				return;
			}
			
			int x = (currentX - 25) % 200;
			int y = currentY % 180;
			
			x -= 40;
			y -= 40;
			playerPos = (x / 50) + 3 * (y / 50);
			
			id = (tablex + tabley * 3) * 9 + playerPos;
			if(drawingPanel.chairs[id] != -1)	return;
			
			drawingPanel.addchairs(id);
			System.out.println("new player added in Pos: (" + tablex + ", " + tabley + "), in " + playerPos);
			
			int tableNoInServer = (tablex + 1) * (tabley + 1) - 1;
			p.pClient.os.println("jointable~" + tableNoInServer + "~" + playerPos);
			p.pClient.os.flush();
		}
	}

	public void mouseEntered(MouseEvent arg0) { }

	public void mouseExited(MouseEvent arg0) { }

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) { }
}