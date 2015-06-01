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
public class RegisterDialog extends JDialog {
	Client client;
	static int defaultW = 200;
	static int defaultH = 210;
	
	JPanel welcomePanel;
	JPanel usernamePanel;
	JTextField usernameField;
	JPanel passwordPanel;
	JPasswordField passwordField;
	JPanel repasswordPanel;
	JPasswordField repasswordField;
	JPanel buttonPanel;
	JButton loginButton;
	JButton registerButton;
	
	LoginDialog loginDialog;
	
	public RegisterDialog(Client f) {
		super(f, "注册", true);
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
		
		repasswordPanel = new JPanel();
		repasswordPanel.setLayout(new BorderLayout());
		repasswordPanel.add(new JLabel("密码"), BorderLayout.WEST);
		repasswordField = new JPasswordField(10);
		repasswordPanel.add(repasswordField, BorderLayout.CENTER);
		c.add(repasswordPanel);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		registerButton = new JButton("注册");
		registerButton.addActionListener(new RegisterListener());
		buttonPanel.add(registerButton);
		loginButton = new JButton("登陆");
		loginButton.addActionListener(new LoginListener());
		buttonPanel.add(loginButton);
		c.add(buttonPanel);
		
		setSize(defaultW, defaultH);
		setLocation((Client.defaultW-RegisterDialog.defaultW)/2, 
					(Client.defaultH-RegisterDialog.defaultH)/2);
	}
	
	class RegisterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			String repassword = new String(repasswordField.getPassword());
			// TODO: 判断注册是否成功
			if (password.equals(repassword)) {
				client.pClient.os.println("register~" + username + "~" + password);
				client.pClient.os.flush();
			}
			else {
				System.out.println("两次输入的密码不一致!");
			}
		}
	}
	
	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			client.loginDialog.setVisible(true);
		}
	}
}
