package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog {
	Client client;
	static int defaultW = 200;
	static int defaultH = 170;
	
	JPanel welcomePanel;
	JPanel usernamePanel;
	JTextField usernameField;
	JPanel passwordPanel;
	JPasswordField passwordField;
	JPanel buttonPanel;
	JButton loginButton;
	JButton registerButton;
	
	RegisterDialog registerDialog;
	
	public LoginDialog(Client f) {
		super(f, "登陆", true);
		client = f;
		init();
	}
	
	public void init() {
		// 设置布局方式
		Container c = getContentPane();
		c.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		// 添加组件
		welcomePanel = new JPanel();
		welcomePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		welcomePanel.add(new JLabel("欢迎来到图腾快手游戏"));
		c.add(welcomePanel);
		
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BorderLayout());
		usernamePanel.add(new JLabel("账号"), BorderLayout.WEST);
		usernameField = new JTextField(10);
		usernamePanel.add(usernameField, BorderLayout.CENTER);
		c.add(usernamePanel);
		
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new BorderLayout());
		passwordPanel.add(new JLabel("密码"), BorderLayout.WEST);
		passwordField = new JPasswordField(10);
		passwordPanel.add(passwordField, BorderLayout.CENTER);
		c.add(passwordPanel);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		loginButton = new JButton("登陆");
		loginButton.addActionListener(new LoginListener());
		buttonPanel.add(loginButton);
		registerButton = new JButton("注册");
		registerButton.addActionListener(new RegisterListener());
		buttonPanel.add(registerButton);
		c.add(buttonPanel);
		
		setSize(defaultW, defaultH);
		setLocation((Client.defaultW-LoginDialog.defaultW)/2, 
					(Client.defaultH-LoginDialog.defaultH)/2);
	}
	
	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			// TODO: 判断登陆是否成功
			
		}
	}
	
	class RegisterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			client.registerDialog.setVisible(true);
		}
	}
}