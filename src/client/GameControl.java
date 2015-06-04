package client;

public class GameControl {
	final static int MAX_PLAYER_NUM = 8;
	final static int CARD_NUM = 80;
	
	int playersNum;
	String[] playersName = new String[MAX_PLAYER_NUM - 1];
	
	int state;			// 0:未准备，1:准备，2:开始
	boolean isColor;
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
		middleNum = 0;
		myCard = CARD_NUM;
		myFrontNum = 0;
		myBackNum = 0;
		
		for (int i = 0; i < MAX_PLAYER_NUM - 1; i++) {
			otherCard[i] = CARD_NUM;
			otherFrontNum[i] = 0;
			otherBackNum[i] = 0;
		}
		
		playersNum = MAX_PLAYER_NUM - 1;
		playersName[0] = "杨妍喆";
		playersName[1] = "杨妍喆";
		playersName[2] = "杨妍喆";
		playersName[3] = "杨妍喆";
		playersName[4] = "杨妍喆";
		playersName[5] = "杨妍喆";
		playersName[6] = "杨妍喆";
	}
}
