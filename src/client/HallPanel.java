package client;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class HallPanel extends JPanel {
Client client;
	
	JPanel labelPanel;
	
	public HallPanel(Client client) {
		this.client = client;
		init();
	}
	
	public void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.blue, 1));
		add(new JLabel("hall"));
	}
}
