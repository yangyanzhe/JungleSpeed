package client;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Client extends JFrame {
	static int defaultW = 1024;
	static int defaultH = 768;
	LoginDialog loginDialog;
	RegisterDialog registerDialog;
	
	JungleSpeedClient pClient;
	
	public Client(JungleSpeedClient pClient) {
		super("Jungle Speed");
		Container c = getContentPane();
		c.setLayout(new FlowLayout(FlowLayout.LEFT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(defaultW, defaultH);
		
		this.pClient = pClient;
	}
	
	public void run() {
		setVisible(true);
		loginDialog = new LoginDialog(this);
		registerDialog = new RegisterDialog(this);
		loginDialog.setVisible(true);
	}
	
	/*public static void main(String[] args) {
		Client app = new Client();
		app.run();
	}*/
}
