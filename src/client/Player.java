package client;

import javax.swing.ImageIcon;

public class Player {
	Client client;
	private ImageIcon icon;
	private String name;
	private Integer score;
	
	public Player(Client client, ImageIcon icon, String name, Integer score) {
		this.client = client;
		this.icon = icon;
		this.name = name;
		this.score = score;
	}
	
	public void setIcon(ImageIcon icon) {this.icon = icon; client.infoPanel.init();}
	public ImageIcon getIcon() {return this.icon;}
	public void setName(String name) {this.name = name; client.infoPanel.init();}
	public String getName() {return this.name;}
	public void setScore(Integer score) {this.score = score; client.infoPanel.init();}
	public Integer getScore() {return this.score;}
}
