package client;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Client extends JFrame {
	static int defaultW = 1024;
	static int defaultH = 768;
	
	public Client() {
		super("Jungle Speed");
		Container c = getContentPane();
		c.setLayout(new FlowLayout(FlowLayout.LEFT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(defaultW, defaultH);
	}
	
	public void run() {
		setVisible(true);
		LoginDialog loginDialog = new LoginDialog(this);
		RegisterDialog registerDialog = new RegisterDialog(this);
		loginDialog.setRegisterDialog(registerDialog);
		registerDialog.setLoginDialog(loginDialog);
		loginDialog.setVisible(true);
	}
	
	public static void main(String[] args) {
		Client app = new Client();
		app.run();
	}
}
