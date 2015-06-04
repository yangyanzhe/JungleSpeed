package client;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	Client client;
	
	public GamePanel(Client client) {
		this.client = client;
		init();
	}
	
	public void init() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setPreferredSize(new Dimension(800,600));
		
		
	}
}
