package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
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
public class RegisterDialog extends JDialog {
	Client client;
	static int maxNameLen = 10;
	
	JPanel panelContainer;
	JPanel welcomePanel;
	JPanel usernamePanel;
	JTextField usernameField;
	JPanel passwordPanel;
	JPasswordField passwordField;
	JPanel repasswordPanel;
	JPasswordField repasswordField;
	JPanel infoPanel;
	JLabel infoLabel;
	JPanel buttonPanel;
	JButton loginButton;
	JButton registerButton;
	
	private int state = 0;
	
	public RegisterDialog(Client client) {
		super(client, "注册", true);
		this.client = client;
		init();
	}
	
	public void init() {
		// 设置布局方式
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
		
		repasswordPanel = new JPanel();
		repasswordPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		repasswordPanel.setLayout(new BoxLayout(repasswordPanel, BoxLayout.X_AXIS));
		repasswordPanel.add(new JLabel("确认"));
		repasswordField = new JPasswordField(10);
		repasswordPanel.add(Box.createHorizontalStrut(5));
		repasswordPanel.add(repasswordField);
		panelContainer.add(repasswordPanel);
		
		infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoLabel = new JLabel("");
		infoPanel.add(infoLabel);
		infoPanel.add(Box.createGlue());
		panelContainer.add(infoPanel);
		
		buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createGlue());
		registerButton = new JButton("注册");
		registerButton.addActionListener(new RegisterListener());
		buttonPanel.add(registerButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		loginButton = new JButton("登陆");
		loginButton.addActionListener(new LoginListener());
		buttonPanel.add(loginButton);
		panelContainer.add(buttonPanel);
		
		Container c = getContentPane();
		c.add(panelContainer);
		pack();
		Dimension registerDimension = getPreferredSize();
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDimension.width-registerDimension.width)/2, 
					(screenDimension.height-registerDimension.height)/2);
		setResizable(false);
	}
	
	class RegisterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			String repassword = new String(repasswordField.getPassword());
			
			if (username.equals("")) {
				infoLabel.setText("用户名不能为空！");
				infoLabel.setForeground(Color.RED);
			}
			else if (password.equals("")) {
				infoLabel.setText("密码不能为空！");
				infoLabel.setForeground(Color.RED);
			}
			else if (repassword.equals("")) {
				infoLabel.setText("密码不能为空！");
				infoLabel.setForeground(Color.RED);
			}
			else if (username.contains("~")) {
				infoLabel.setText("用户名中不能包含~");
				infoLabel.setForeground(Color.RED);
			}
			else if (password.equals(repassword)) {
				client.pClient.os.println("register~" + username + "~" + password);
				client.pClient.os.flush();
			}
			else {
				//System.out.println("两次输入的密码不一致!");
				infoLabel.setText("两次输入的密码不一致!");
				infoLabel.setForeground(Color.RED);
			}
			usernameField.setText("");
			passwordField.setText("");
			repasswordField.setText("");
		}
	}
	
	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			state = 2;
			setVisible(false);
		}
	}
	
	public void display() {
		state = 0;
		setVisible(true);
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int v) {
		this.state = v;
	}
}
