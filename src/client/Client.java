package client;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Client extends JFrame {
	static int defaultW = 1024;
	static int defaultH = 768;
	LoginDialog loginDialog;
	RegisterDialog registerDialog;
	
	JungleSpeedClient pClient;
	
	String str_player = "res\\player.png";
	String str_table = "res\\table.png";
	String str_hall_back = "res\\hall_background.jpg";
	String str_logo = "res\\logo.png";
	
	Image player;
	Image table;
	Image hallBack;
	Image logo;
	
	GameHall gamehall_panel;
	MediaTracker mt;
	
	public void init() {
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
		gamehall_panel.setLocation(0,0);
		
		Container cp = getContentPane();
		cp.add(gamehall_panel);
		cp.add(new JPanel());
		
		gamehall_panel.setVisible(false);
	}
	
	public Client(JungleSpeedClient pClient) {
		super("Jungle Speed");
		Container c = getContentPane();
//		c.setLayout(new FlowLayout(FlowLayout.LEFT));
		c.setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(defaultW, defaultH);
		
		loginDialog = new LoginDialog(this);
		registerDialog = new RegisterDialog(this);
		
		this.pClient = pClient;
		
		init();
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
