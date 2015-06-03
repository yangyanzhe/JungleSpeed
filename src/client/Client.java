package client;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Client extends JFrame {
	JPanel panelContainer;
	InfoPanel infoPanel;
//	HallPanel hallPanel;
	PlayerTablePanel playerListPanel;
	ChatPanel chatPanel;
	
	LoginDialog loginDialog;
	RegisterDialog registerDialog;
	
	JungleSpeedClient pClient;
	
	String str_player = "res/player.png";
	String str_table = "res/table.png";
	String str_hall_back = "res/hall_background.jpg";
	String str_logo = "res/logo.png";
	
	Image player;
	Image table;
	Image hallBack;
	Image logo;
	
	GameHall gamehall_panel;
	MediaTracker mt;
	
	public Client(JungleSpeedClient pClient) {
		super("Jungle Speed");
		
		panelContainer = new JPanel();
		panelContainer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panelContainer.setLayout(new GridBagLayout());
		
		GridBagConstraints cInfoPanel = new GridBagConstraints();
		cInfoPanel.gridx = 0;
		cInfoPanel.gridy = 0;
		cInfoPanel.gridwidth = 4;
		cInfoPanel.gridheight = 1;
//		cInfoPanel.weightx = 1;
//		cInfoPanel.weighty = 0;
		cInfoPanel.fill = GridBagConstraints.BOTH;
		infoPanel = new InfoPanel(this);
		panelContainer.add(infoPanel, cInfoPanel);
		
		GridBagConstraints cHallPanel = new GridBagConstraints();
		cHallPanel.gridx = 0;
		cHallPanel.gridy = 1;
		cHallPanel.gridwidth = 3;
		cHallPanel.gridheight = 6;
//		cHallPanel.weightx = 1;
//		cHallPanel.weighty = 1;
		cHallPanel.fill = GridBagConstraints.BOTH;
//		hallPanel = new HallPanel(this);
		
		player = getToolkit().getImage(str_player);
		table = getToolkit().getImage(str_table);
		logo = getToolkit().getImage(str_logo);
		hallBack = getToolkit().getImage(str_hall_back);
		
		mt = new MediaTracker(this);
		mt.addImage(hallBack, 0);
		mt.addImage(table, 0);
		mt.addImage(player, 0);
		mt.addImage(logo, 0);
		
		try
		{
			mt.waitForAll();
		}catch(InterruptedException mye){System.out.println(mye);}
		
		gamehall_panel = new GameHall(this);
		gamehall_panel.setSize(680,540);
//		gamehall_panel.setLocation(0,0);
		
		panelContainer.add(gamehall_panel, cHallPanel);

		
		GridBagConstraints cPlayerListPanel = new GridBagConstraints();
		cPlayerListPanel.gridx = 3;
		cPlayerListPanel.gridy = 1;
		cPlayerListPanel.gridwidth = 1;
		cPlayerListPanel.gridheight = 3;
//		cPlayerListPanel.weightx = 0;
//		cPlayerListPanel.weighty = 0;
		cPlayerListPanel.fill = GridBagConstraints.BOTH;
		playerListPanel = new PlayerTablePanel(this);
		panelContainer.add(playerListPanel, cPlayerListPanel);
		
		GridBagConstraints cChatPanel = new GridBagConstraints();
		cChatPanel.gridx = 3;
		cChatPanel.gridy = 4;
		cChatPanel.gridwidth = 1;
		cChatPanel.gridheight = 3;
//		cChatPanel.weightx = 0;
//		cChatPanel.weighty = 1;
		cChatPanel.fill = GridBagConstraints.BOTH;
		chatPanel = new ChatPanel(this);
		panelContainer.add(chatPanel, cChatPanel);
		
		Container c = getContentPane();
		c.add(panelContainer);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		
		loginDialog = new LoginDialog(this);
		registerDialog = new RegisterDialog(this);
		
		loginDialog = new LoginDialog(this);
		registerDialog = new RegisterDialog(this);
		
		this.pClient = pClient;
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
