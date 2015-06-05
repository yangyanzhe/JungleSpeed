package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel {
	final static int CHAT_WIDTH = PlayersPanel.TABLE_WIDTH;
	final static int CHAT_HEIGHT = GamePanel.GAME_HEIGHT - PlayersPanel.TABLE_HEIGHT;
	final static int ICON_WIDTH = 20, ICON_HEIGHT = 20;
	
	Client client;
	
	JPanel labelPanel;
	JTextPane messagePane;
	StyledDocument messageStyle;
	SimpleAttributeSet nameSet;
	SimpleAttributeSet contentSet;
	SimpleAttributeSet systemSet;
	JTextField inputField;
	JScrollPane scrollPane;
	
	public ChatPanel(Client client) {
		this.client = client;
		init();
	}
	
	public void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(new JLabel("聊天窗口"));
		labelPanel.add(Box.createGlue());
		this.add(labelPanel);
		
		messagePane = new JTextPane();
		messageStyle = messagePane.getStyledDocument();
		nameSet = new SimpleAttributeSet();
		StyleConstants.setForeground(nameSet, Color.blue);
		contentSet = new SimpleAttributeSet();
		systemSet = new SimpleAttributeSet();
		StyleConstants.setForeground(systemSet, Color.red);
		
		messagePane.setEditable(false);
		addSystemMessage("欢迎来到图腾快手游戏!");
		
		scrollPane = new JScrollPane(messagePane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		inputField = new JTextField();
		inputField.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		inputField.addActionListener(new inputListener());
		
		scrollPane.setPreferredSize(new Dimension(CHAT_WIDTH, 
				CHAT_HEIGHT - labelPanel.getPreferredSize().height
							- inputField.getPreferredSize().height
							- getInsets().top - getInsets().bottom));
		this.add(scrollPane);
		this.add(inputField);
	}
	
	public void addPlayerMessage(ImageIcon icon, String name, String content) {
		ImageIcon iconResized = new ImageIcon();
		int offset;
		
		messagePane.setEditable(true);
		offset = messagePane.getText().length();
		messagePane.select(offset, offset);
		iconResized.setImage(icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT));
		messagePane.insertIcon(iconResized);
		offset = messagePane.getText().length();
		messagePane.select(offset, offset);
		messagePane.replaceSelection(name + ": ");
		messageStyle.setCharacterAttributes(offset, name.length()+1, nameSet, true);
		offset = messagePane.getText().length();
		messagePane.select(offset, offset);
		messagePane.replaceSelection(content + "\n");
		messageStyle.setCharacterAttributes(offset, content.length(), contentSet, true);
		messagePane.setEditable(false);
	}
	
	public void addSystemMessage(String content) {
		int offset = messagePane.getText().length();
		
		messagePane.setEditable(true);
		messagePane.select(offset, offset);
		messagePane.replaceSelection(content + "\n");
		messageStyle.setCharacterAttributes(offset, content.length(), systemSet, true);
		messagePane.setEditable(false);
	}
	
	class inputListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String message = inputField.getText();
			if (message.length() > 0) {
				addPlayerMessage(client.player.getIcon(), client.player.getName(), message);
				inputField.setText("");
				
				client.pClient.os.println("chattoserver~" + message);
				client.pClient.os.flush();
			}
		}
	}
}
