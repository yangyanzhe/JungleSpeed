package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import client.LoginDialog.LoginListener;
import client.LoginDialog.RegisterListener;

@SuppressWarnings("serial")
public class NetworkDialog extends JDialog {
	Client client;
	
	JPanel panelContainer;
	JPanel labelPanel;
	JPanel inputContainer;
	JTextField[] ipFields;
	JTextField portField;
	JPanel buttonPanel;
	JButton okButton;
	JButton cancelButton;
	
	public NetworkDialog(Client client) {
		super(client, "修改网络设置", true);
		this.client = client;
		init();
	}
	
	public void init() {
		panelContainer = new JPanel();
		panelContainer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
		
		labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(new JLabel("IP设置"));
		labelPanel.add(Box.createGlue());
		panelContainer.add(labelPanel);
		
		inputContainer = new JPanel();
		inputContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.X_AXIS));
		ipFields = new JTextField[4];
		for (int i = 0; i < 4; i++) {
			ipFields[i] = new JTextField();
		}
		portField = new JTextField();
		ipFields[0].setText("127");
		ipFields[1].setText("0");
		ipFields[2].setText("0");
		ipFields[3].setText("1");
		portField.setText("4700");
		Dimension ipDimension = ipFields[0].getPreferredSize();
		for (int i = 0; i < 4; i++) {
			ipFields[i].setPreferredSize(ipDimension);
			inputContainer.add(ipFields[i]);
			inputContainer.add(Box.createVerticalStrut(5));
		}
		inputContainer.add(new JLabel(":"));
		inputContainer.add(Box.createVerticalStrut(5));
		inputContainer.add(portField);		
		panelContainer.add(inputContainer);
		
		buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createGlue());
		okButton = new JButton("确认");
		okButton.addActionListener(new OkListener());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		cancelButton = new JButton("取消");
		cancelButton.addActionListener(new CancelListener());
		buttonPanel.add(cancelButton);
		panelContainer.add(buttonPanel);
		
		Container c = getContentPane();
		c.add(panelContainer);
		pack();
		Dimension loginDimension = getPreferredSize();
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDimension.width-loginDimension.width)/2, 
					(screenDimension.height-loginDimension.height)/2);
		setResizable(false);
	}
	
	class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try
			{
				client.pClient.ip = ipFields[0].getText() + "." + ipFields[1].getText() + "." 
									+ ipFields[2].getText() + "." + ipFields[3].getText();
				client.pClient.port = Integer.parseInt(portField.getText());
			} catch(Exception mye) {
				System.out.println(mye);
			}
			try
			{
				File chanfile = new File(client.pClient.iniFile);
				chanfile.delete();
				chanfile.createNewFile();
				BufferedWriter writebuf = new BufferedWriter(new FileWriter(client.pClient.iniFile));
				writebuf.write(client.pClient.ip + "\r\n");
				writebuf.write(client.pClient.port + "\r\n");
				writebuf.close();
			} catch (IOException e1) {
				System.out.println(e1);
			}		
			JOptionPane.showMessageDialog(null,"修改将在下次启动时生效","注意",JOptionPane.WARNING_MESSAGE);
			dispose();
		}
	}
	
	class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
}
