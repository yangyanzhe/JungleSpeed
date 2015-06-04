package client;

public class GameControl {
	final static int MAX_PLAYER_NUM = 8;
	final static int CARD_NUM = 80;
	
	int playersNum;
	String[] playersName = new String[MAX_PLAYER_NUM - 1];
	
	int state;			// 0:未准备，1:准备，2:开始
	boolean isColor;
	String caption;
	
	int middleNum;
	int myCard;
	int myFrontNum;
	int myBackNum;
	int[] otherCard = new int[MAX_PLAYER_NUM - 1];
	int[] otherFrontNum = new int[MAX_PLAYER_NUM - 1];
	int[] otherBackNum = new int[MAX_PLAYER_NUM - 1];
	
	public GameControl() {
		playersNum = 0;
		
		state = 0;
		isColor = false;
		caption = new String("欢迎来到图腾快手游戏!");
		
		middleNum = 0;
		myCard = CARD_NUM;
		myFrontNum = 0;
		myBackNum = 0;
		
		for (int i = 0; i < MAX_PLAYER_NUM - 1; i++) {
			otherCard[i] = CARD_NUM;
			otherFrontNum[i] = 0;
			otherBackNum[i] = 0;
		}
	}
}
