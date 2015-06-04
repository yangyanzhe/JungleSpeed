package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.MediaTracker;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.sun.security.auth.NTDomainPrincipal;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	static final int GAME_WIDTH = 800;
	static final int GAME_HEIGHT = 600;
	static final int INTEVAL = 40;
	static final int TEXT_INTEVAL = 5;
	static final Color BACKGROUND_COLOR = new Color(46,110,163);
	
	Client client;
	GameControl game;
	
	MediaTracker gameTracker;
	Image logo0, logo1;
	final static int cardNum = 80;
	Image[] cards = new Image[cardNum + 1];
	
	public GamePanel(Client client) {
		this.client = client;
		game = new GameControl();
		init();
	}
	
	public void init() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
		setBackground(BACKGROUND_COLOR);
		loadResources();
	}
	
	public void loadResources() {
		String resFolder = "res/";
		String cardFolder = resFolder + "cards/";
		gameTracker = new MediaTracker(this);
		logo0 = getToolkit().getImage(resFolder + "logo0.png");
		logo1 = getToolkit().getImage(resFolder + "logo1.png");
		gameTracker.addImage(logo0, 0);
		gameTracker.addImage(logo1, 0);
		for (int i = 0; i <= cardNum; i++) {
			cards[i] = getToolkit().getImage(resFolder + i + ".jpg");
			gameTracker.addImage(cards[i], 0);
		}
		
		try {
			gameTracker.waitForAll();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void paintComponent(Graphics g) {
		drawBackground(g);
		drawMyPlayer(g);
		drawOtherPlayers(g);
		drawMiddle(g);
		drawInfo(g);
	}
	
	public void drawBackground(Graphics g) {
		int height, width;
		width = logo0.getWidth(null);
		height = logo0.getHeight(null);
		g.drawImage(logo0, (GAME_WIDTH - width) / 2, (GAME_HEIGHT - height) / 2, 
				width, height, null);
	}
	
	public void drawCenterString(Graphics g, String str, int x, int y) {
		int strWidth = g.getFontMetrics().stringWidth(str);
        g.drawString(str, x - strWidth / 2, y);
	}
	
	public void drawMiddle(Graphics g) {
		int height, width;
		int x, y;
		width = logo1.getWidth(null);
		height = logo1.getHeight(null);
		x = (GAME_WIDTH - width) / 2;
		y = (GAME_HEIGHT - height) / 2;
		g.drawImage(logo0, x, y, width, height, null);
		drawCenterString(g, "牌数: " + game.middleNum, 
				x + width/2, y + height + TEXT_INTEVAL);
	}
	
	public void drawMyPlayer(Graphics g) {
		int height, width;
		int x, y;
		width = cards[0].getWidth(null);
		height = cards[0].getHeight(null);
		x = GAME_WIDTH/2 - INTEVAL/2 - width;
		y = GAME_HEIGHT - INTEVAL - height;
		g.drawImage(cards[game.myCard], x, y, width, height, null);
		drawCenterString(g, "正面: ", x + width/2, y + height + TEXT_INTEVAL);
		x = GAME_WIDTH/2 + INTEVAL/2;
		y = GAME_HEIGHT - INTEVAL - height;
		g.drawImage(cards[cardNum], x, y, width, height, null);
		drawCenterString(g, "正面: ", x + width/2, y + height + TEXT_INTEVAL);
	}
	
	final static int[][] PLAYER_POSITION = {
		{},
		{4},
		{3, 5},
		{2, 4, 6},
		{1, 3, 5, 7},
		{2, 3 ,4 ,5 ,6},
		{1, 2, 3, 5, 6, 7},
		{1, 2, 3, 4 ,5 ,6, 7}
		};
	public void drawOtherPlayers(Graphics g) {
		int height, width;
		width = cards[0].getWidth(null);
		height = cards[0].getHeight(null);
		int[] pos = PLAYER_POSITION[game.playersNum];
		for (int i = 0; i < game.playersNum; i++) {
			drawSinglePlayer(g, i, pos[i]);
		}
	}
	
	public void drawSinglePlayer(Graphics g, int player, int pos) {
		int height, width;
		int x = 0, y = 0;
		width = cards[0].getWidth(null);
		height = cards[0].getHeight(null);
		switch (pos) {
		case 1:
			x = GAME_WIDTH - INTEVAL - width;
			y = GAME_HEIGHT/2 + INTEVAL/2;
			break;
		
		case 2:
			x = GAME_WIDTH - INTEVAL - width;
			y = GAME_HEIGHT/2 - INTEVAL/2 - height;
			break;
			
		case 3:
			x = GAME_WIDTH/2 + width/2 + INTEVAL;
			y = INTEVAL;
			break;
			
		case 4:
			x = GAME_WIDTH/2 - width/2;
			y = INTEVAL;
			break;
			
		case 5:
			x = GAME_WIDTH/2 - width*3/2 - INTEVAL;
			y = INTEVAL;
			break;
			
		case 6:
			x = INTEVAL;
			y = GAME_HEIGHT/2 - INTEVAL/2 - height;
			break;
			
		case 7:
			x = INTEVAL;
			y = GAME_HEIGHT/2 + INTEVAL/2;
			break;
			
		default:
			break;
		}
		g.drawImage(cards[game.otherCard[player]], x, y, width, height, null);
		drawCenterString(g, "正" + game.otherFrontNum[player] + "反" + game.otherBackNum[player],
			x + width/2, y + height + TEXT_INTEVAL);
		drawCenterString(g, game.playersName[player],
				x + width/2, y - g.getFontMetrics().getHeight());
	}
	
	public void drawInfo(Graphics g) {
		
	}
}
