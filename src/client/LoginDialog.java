package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog {
	Client client;
	
	JPanel panelContainer;
	JPanel welcomePanel;
	JPanel usernamePanel;
	JTextField usernameField;
	JPanel passwordPanel;
	JPasswordField passwordField;
	JPanel infoPanel;
	JLabel infoLabel;
	JPanel buttonPanel;
	JButton loginButton;
	JButton registerButton;
	
	public LoginDialog(Client client) {
		super(client, "登陆", true);
		this.client = client;
		init();
	}
	
	public void init() {
		panelContainer = new JPanel();
		panelContainer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		// 设置布局方式
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
		
		// 添加组件
		welcomePanel = new JPanel();
		welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.X_AXIS));
		welcomePanel.add(new JLabel("欢迎来到图腾快手游戏"));
		welcomePanel.add(Box.createGlue());
		panelContainer.add(welcomePanel);
		
		usernamePanel = new JPanel();
		usernamePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		usernamePanel.add(new JLabel("昵称"));
		usernamePanel.add(Box.createHorizontalStrut(5));
		usernameField = new JTextField(10);
		usernamePanel.add(usernameField);
		panelContainer.add(usernamePanel);
		
		passwordPanel = new JPanel();
		passwordPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.add(new JLabel("密码"));
		passwordPanel.add(Box.createHorizontalStrut(5));
		passwordField = new JPasswordField(10);
		passwordPanel.add(passwordField);
		panelContainer.add(passwordPanel);
		
		infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoLabel = new JLabel("");
		infoPanel.add(infoLabel);
		infoPanel.add(Box.createGlue());
		panelContainer.add(infoPanel);
		//infoLabel.setText("haha");
		//infoLabel.setForeground(Color.RED);
		
		buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createGlue());
		loginButton = new JButton("登陆");
		loginButton.addActionListener(new LoginListener());
		buttonPanel.add(loginButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		registerButton = new JButton("注册");
		registerButton.addActionListener(new RegisterListener());
		buttonPanel.add(registerButton);
		panelContainer.add(buttonPanel);
		
		Container c = getContentPane();
		c.add(panelContainer);
		pack();
		Dimension loginDimension = getPreferredSize();
		Dimension clientDimension = client.getPreferredSize();
		setLocation((clientDimension.width-loginDimension.width)/2, 
					(clientDimension.height-loginDimension.height)/2);
		setResizable(false);
	}
	
	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			client.pClient.os.println("login~" + username + "~" + password);
			client.pClient.os.flush();
			passwordField.setText("");
			//new Player的操作放在了JungleSpeedClient中，因为只有在登录成功消息接收后才能构造该类
			/*ImageIcon icon = new ImageIcon("/Users/liutongtong/Pictures/logo.png");
			client.player = new Player(icon, "tt", 100);*/
		}
	}
	
	class RegisterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			usernameField.setText("");
			passwordField.setText("");
			setVisible(false);
			client.registerDialog.setVisible(true);
		}
	}
}