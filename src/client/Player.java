package client;

import javax.swing.ImageIcon;

public class Player {
	private ImageIcon icon;
	private String name;
	private Integer score;
	
	public Player(ImageIcon icon, String name, Integer score) {
		this.icon = icon;
		this.name = name;
		this.score = score;
	}
	
	public void setIcon(ImageIcon icon) {this.icon = icon;}
	public ImageIcon getIcon() {return this.icon;}
	public void setName(String name) {this.name = name;}
	public String getName() {return this.name;}
	public void setScore(Integer score) {this.score = score;}
	public Integer getScore() {return this.score;}
}
