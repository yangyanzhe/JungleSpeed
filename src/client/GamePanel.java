package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

//TODO gamecontrol中的iscolor是否为颜色模式(测试)
@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	static int GAME_WIDTH = 810;
	static int GAME_HEIGHT = 610;
	final static int INTEVAL = 40;
	final static int TEXT_INTEVAL = 2;
	final static int CAPTION_INTEVAL = 14;
	final static int BUTTON_INTEVAL = 12;
	final static int BUTTON_BORDER = 3;
	
	final static Color BACKGROUND_COLOR = new Color(46,110,163);
	final static Color TEXT_COLOR = new Color(255, 255, 255);
	final static Color BUTTON_BACKGROUND_COLOR = new Color(255, 255, 255);
	final static Color BUTTON_BORDER_COLOR = new Color(0, 0, 0);
	final static Color BUTTON_TEXT_COLOR = new Color(0, 0, 0);
	final static Color CAPTION_COLOR = new Color(255, 255, 255);
	
	final static Font NAME_FONT = new Font("Microsoft YaHei", Font.BOLD, 14);
	final static Font NUM_FONT = new Font("Microsoft YaHei", Font.PLAIN, 12);
	final static Font BUTTON_FONT = new Font("Microsoft YaHei", Font.BOLD, 30);
	final static Font CAPTION_FONT = new Font("Microsoft YaHei", Font.BOLD, 20);
	
	Client client;
	GameControl game;
	
	MediaTracker gameTracker;
	Image logo0, logo1;
	final static int cardNum = 80;
	Image[] cards = new Image[cardNum + 3];
	
	public GamePanel(Client client) {
		this.client = client;
		game = new GameControl();
		init();
		addKeyListener(new keyListenser());
		addMouseListener(new mouseListener());
	}
	
	public void init() {
		setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
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
		for (int i = 0; i < cardNum + 3; i++) {
			cards[i] = getToolkit().getImage(cardFolder + i + ".jpg");
			gameTracker.addImage(cards[i], 0);
		}
		
		try {
			gameTracker.waitForAll();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void paint(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawBackground(g);
		drawMyPlayer(g);
		drawOtherPlayers(g);
		drawMiddle(g);
		drawInfo(g);
		drawButtons(g);
		drawCaption(g);
	}
	
	public void drawBackground(Graphics g) {
		int height, width;
		width = logo0.getWidth(null);
		height = logo0.getHeight(null);
		Color oldColor = g.getColor();
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(5, 5, GAME_WIDTH - 10, GAME_HEIGHT - 10);
		g.setColor(oldColor);
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
		g.drawImage(logo1, x, y, width, height, null);
		Color oldColor = g.getColor();
		g.setColor(TEXT_COLOR);
		Font oldFont = g.getFont();
		g.setFont(NUM_FONT);
		drawCenterString(g, "牌数: " + game.middleNum, 
				x + width/2, y + height + TEXT_INTEVAL 
							   + g.getFontMetrics().getFont().getSize());
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	
	public void drawMyPlayer(Graphics g) {
		int height, width;
		int x, y;
		width = cards[0].getWidth(null);
		height = cards[0].getHeight(null);
		x = GAME_WIDTH/2 - INTEVAL/2 - width;
		y = GAME_HEIGHT - INTEVAL - height;
		
		int card = game.myCard;
		if (card < 0) {
			card = cardNum;
		}
		g.drawImage(cards[card], x, y, width, height, null);
		Color oldColor = g.getColor();
		g.setColor(TEXT_COLOR);
		Font oldFont = g.getFont();
		g.setFont(NUM_FONT);
		drawCenterString(g, "正面: " + game.myFrontNum, x + width/2, y + height 
				+ TEXT_INTEVAL + g.getFontMetrics().getFont().getSize());
		x = GAME_WIDTH/2 + INTEVAL/2;
		y = GAME_HEIGHT - INTEVAL - height;
		g.drawImage(cards[cardNum], x, y, width, height, null);
		drawCenterString(g, "反面: " + game.myBackNum, x + width/2, y + height 
				+ TEXT_INTEVAL + g.getFontMetrics().getFont().getSize());
		g.setColor(oldColor);
		g.setFont(oldFont);
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
		int card = game.otherCard[player];
		if (card < 0) {
			card = cardNum;
		}
		g.drawImage(cards[card], x, y, width, height, null);
		Color oldColor = g.getColor();
		g.setColor(TEXT_COLOR);
		Font oldFont = g.getFont();
		g.setFont(NUM_FONT);
		drawCenterString(g, "正" + game.otherFrontNum[player] + " 反" + game.otherBackNum[player],
			x + width/2, y + height + TEXT_INTEVAL + g.getFontMetrics().getFont().getSize());
		g.setFont(NAME_FONT);
		drawCenterString(g, game.playersName[player],
				x + width/2, y - TEXT_INTEVAL);
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	
	public void drawInfo(Graphics g) {
		int height, width;
		int x = 0, y = 0;
		
		width = cards[cardNum + 1].getWidth(null);
		height = cards[cardNum + 1].getHeight(null);
		x = INTEVAL;
		y = GAME_HEIGHT - INTEVAL - height;
		if (!game.isColor) {
			g.drawImage(cards[cardNum + 1], x, y, width, height, null);
		}
		else {
			g.drawImage(cards[cardNum + 2], x, y, width, height, null);
		}
	}
	
	static int LEAVE_BUTTON_X, LEAVE_BUTTON_Y;
	static int READY_BUTTON_X, READY_BUTTON_Y;
	static int CANCEL_BUTTON_X, CANCEL_BUTTON_Y;
	public void drawButtons(Graphics g) {
		String label = "离开";
		Font oldFont = g.getFont();
		g.setFont(BUTTON_FONT);
		LEAVE_BUTTON_X = GAME_WIDTH - INTEVAL - BUTTON_BORDER*4 - g.getFontMetrics().stringWidth(label);
		LEAVE_BUTTON_Y = GAME_HEIGHT - INTEVAL - BUTTON_BORDER*4 - g.getFontMetrics().getHeight();
		drawSingleButton(g, label, LEAVE_BUTTON_X, LEAVE_BUTTON_Y);
		if (game.state == 0) {
			label = "准备";
			READY_BUTTON_X = LEAVE_BUTTON_X - BUTTON_INTEVAL - BUTTON_BORDER*4 - g.getFontMetrics().stringWidth(label);
			READY_BUTTON_Y = LEAVE_BUTTON_Y;
			drawSingleButton(g, label, READY_BUTTON_X, READY_BUTTON_Y);
		} else if (game.state == 1) {
			label = "取消";
			CANCEL_BUTTON_X = LEAVE_BUTTON_X - BUTTON_INTEVAL - BUTTON_BORDER*4 - g.getFontMetrics().stringWidth(label);
			CANCEL_BUTTON_Y = LEAVE_BUTTON_Y;
			drawSingleButton(g, label, CANCEL_BUTTON_X, CANCEL_BUTTON_Y);
		} else {	
		}
		g.setFont(oldFont);
	}
	
	public void drawSingleButton(Graphics g, String label, int x, int y) {
		Color oldColor = g.getColor();
		g.setColor(BUTTON_BORDER_COLOR);
		g.fillRoundRect(x, y, g.getFontMetrics().stringWidth(label) + BUTTON_BORDER*4, 
				g.getFontMetrics().getHeight() + BUTTON_BORDER*4, 10, 10);
		g.setColor(BUTTON_BACKGROUND_COLOR);
		g.fillRoundRect(x + BUTTON_BORDER, y + BUTTON_BORDER, 
				g.getFontMetrics().stringWidth(label) + BUTTON_BORDER*2, 
				g.getFontMetrics().getHeight() + BUTTON_BORDER*2, 10, 10);
		g.setColor(BUTTON_TEXT_COLOR);
		g.drawString(label, x + BUTTON_BORDER*2, 
				y + BUTTON_BORDER*2 + g.getFontMetrics().getFont().getSize());
		g.setColor(oldColor);
	}
	
	public void drawCaption(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(CAPTION_COLOR);
		Font oldFont = g.getFont();
		g.setFont(CAPTION_FONT);
		if (game.caption.length() > 0) {
			drawCenterString(g, game.caption, GAME_WIDTH/2, 
					GAME_HEIGHT/2 - logo1.getHeight(null)/2 - CAPTION_INTEVAL);
		}
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	
	class keyListenser implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				//按下空格键的事件处理
				client.pClient.os.println("grab");
				client.pClient.os.flush();
				//System.out.println("yes");
			}
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
	
	class mouseListener implements MouseListener {
		public boolean isClicked(int xPos, int yPos, int x, int y, int width, int height) {
			if (x <= xPos && xPos <= x + width
					&& y <= yPos && yPos <= y + height) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			int xPos = e.getX();
			int yPos = e.getY();
			
			requestFocus();
			if (isClicked(xPos, yPos, LEAVE_BUTTON_X, LEAVE_BUTTON_Y, 72, 48)) {
				// 点击离开按钮
				//System.out.println("yes");
				client.pClient.os.println("leavetable");
				game.init();
				client.pClient.os.flush();
			} else if (game.state == 0 && 
					isClicked(xPos, yPos, READY_BUTTON_X, READY_BUTTON_Y, 72, 48)) {
				// 点击准备按钮
				//System.out.println("yes");
				client.pClient.os.println("userready");
				client.pClient.os.flush();
			} else if (game.state == 1 && 
					isClicked(xPos, yPos, CANCEL_BUTTON_X, CANCEL_BUTTON_Y, 72, 48)) {
				// 点击取消按钮
				client.pClient.os.println("cancelready");
				client.pClient.os.flush();
			}
		}

		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}
}
