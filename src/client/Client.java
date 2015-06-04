package client;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Client extends JFrame {
	Player player;
	
	JPanel panelContainer;
	InfoPanel infoPanel;
	HallPanel hallPanel;
	GamePanel gamePanel;
	PlayersPanel playersPanel;
	ChatPanel chatPanel;
	
	LoginDialog loginDialog;
	RegisterDialog registerDialog;
	
	JungleSpeedClient pClient;	
	GameHall gamehall_panel;

	public Client(JungleSpeedClient pClient) {
		super("Jungle Speed");
		
		panelContainer = new JPanel();
		panelContainer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panelContainer.setLayout(new GridBagLayout());
		
		GridBagConstraints cInfoPanel = new GridBagConstraints();
		cInfoPanel.gridx = 0;
		cInfoPanel.gridy = 0;
		cInfoPanel.gridwidth = GridBagConstraints.REMAINDER;
		cInfoPanel.gridheight = 1;
		cInfoPanel.weightx = 1;
		cInfoPanel.weighty = 0;
		cInfoPanel.fill = GridBagConstraints.BOTH;
		infoPanel = new InfoPanel(this);
		panelContainer.add(infoPanel, cInfoPanel);
		
		GridBagConstraints cHallPanel = new GridBagConstraints();
		cHallPanel.gridx = 0;
		cHallPanel.gridy = 1;
		cHallPanel.gridwidth = 3;
		cHallPanel.gridheight = 5;
		cHallPanel.weightx = 1;
		cHallPanel.weighty = 1;
		cHallPanel.fill = GridBagConstraints.BOTH;
//		hallPanel = new HallPanel(this);
		
		gamehall_panel = new GameHall(this);
		gamehall_panel.setSize(800,700);
//		gamehall_panel.setLocation(0,0);
		panelContainer.add(gamehall_panel, cHallPanel);
		
		gamePanel = new GamePanel(this);
//		panelContainer.add(gamePanel, cHallPanel);
		
		GridBagConstraints cPlayerListPanel = new GridBagConstraints();
		cPlayerListPanel.gridx = 3;
		cPlayerListPanel.gridy = 1;
		cPlayerListPanel.gridwidth = 1;
		cPlayerListPanel.gridheight = 2;
		cPlayerListPanel.weightx = 0;
		cPlayerListPanel.weighty = 0;
		cPlayerListPanel.fill = GridBagConstraints.BOTH;
		playersPanel = new PlayersPanel(this);
		panelContainer.add(playersPanel, cPlayerListPanel);
		
		GridBagConstraints cChatPanel = new GridBagConstraints();
		cChatPanel.gridx = 3;
		cChatPanel.gridy = 3;
		cChatPanel.gridwidth = 1;
		cChatPanel.gridheight = 3;
		cChatPanel.weightx = 0;
		cChatPanel.weighty = 1;
		cChatPanel.fill = GridBagConstraints.BOTH;
		chatPanel = new ChatPanel(this);
		panelContainer.add(chatPanel, cChatPanel);
		
		Container c = getContentPane();
		c.add(panelContainer);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(false);
		
		loginDialog = new LoginDialog(this);
		registerDialog = new RegisterDialog(this);
		
		loginDialog = new LoginDialog(this);
		registerDialog = new RegisterDialog(this);
		
		this.pClient = pClient;
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
	
	public void run() {
		setVisible(true);
		loginDialog.setVisible(true);
	}
	
	/*public static void main(String[] args) {
		Client app = new Client();
		app.run();
	}*/
}
