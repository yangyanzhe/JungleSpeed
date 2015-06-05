package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class InfoPanel extends JPanel {
	final static int ICON_WIDTH = 50, ICON_HEIGHT = 50;
	final static int INTEVAL = 10;
	
	Client client;
	
	UserPanel userPanel;
	JPanel systemPanel;
	JButton networkButton;
	
	public InfoPanel(Client client) {
		this.client = client;
		init();
	}
	
	public void init() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		setPreferredSize(new Dimension(
				GamePanel.GAME_WIDTH + ChatPanel.CHAT_WIDTH + getInsets().left + getInsets().right, 
				ICON_HEIGHT + getInsets().top + getInsets().bottom));
		
		userPanel = new UserPanel();
		add(userPanel);
		
		add(Box.createGlue());
		
		systemPanel = new JPanel();
		systemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		systemPanel.setLayout(new BoxLayout(systemPanel, BoxLayout.Y_AXIS));
		networkButton = new JButton("网络设置");
		networkButton.addActionListener(new networkListener());
		systemPanel.add(networkButton);
		add(systemPanel);
	}
	
	public void updateInfo() {
		userPanel.repaint();
	}

	class UserPanel extends JPanel {		
		public void paintComponent(Graphics g) {
			drawIcon(g);
			drawName(g);
			drawScore(g);
		}
		
		public void drawIcon(Graphics g) {
			if (client.player != null && client.player.getIcon() != null) {
				ImageIcon iconResized = new ImageIcon();
				iconResized.setImage(client.player.getIcon().getImage().getScaledInstance(
						ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT));
				g.drawImage(iconResized.getImage(), 0, 0, ICON_WIDTH, ICON_HEIGHT, null);
			}
		}
		
		public void drawName(Graphics g) {
			if (client.player != null && client.player.getName() != null) {
				g.drawString(client.player.getName(), ICON_WIDTH + INTEVAL, 
						g.getFontMetrics().getFont().getSize());
			}
		}
		
		public void drawScore(Graphics g) {
			if (client.player != null && client.player.getScore() != null) {
				g.drawString(client.player.getScore().toString(), ICON_WIDTH + INTEVAL, ICON_HEIGHT);
			}
		}
	}

	class networkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			NetworkDialog networkDialog = new NetworkDialog(client);
			networkDialog.setVisible(true);
		}
	}
}
