package client;

import java.util.Timer;
import java.util.TimerTask;

/************************************************************
* Game (Rule Control)
* Author: Yang Yanzhe
* Time:   2015/05/30
*************************************************************/

public class Game {
	int gamerNumber;
	int currentCard;
	int currentGamer;
	int currentMode;						// mode=0，正常；mode=1, 一起翻牌；mode=2，一起抢；mode=3，变色
	int   totemCardsNumber;					// number of cards under totem
	int[] totemCards;						// cards under totem
	Gamer[] gamers;							// player
	Timer timer1;
	Timer timer2;
	volatile int count = 5;
	
	int punishedGuy;						// player who will be punished after a rob action
	
	public Game(){
		gamers = new Gamer[8];	
		for(int i = 0; i<8; i++){
			gamers[i] = new Gamer();
		}
		totemCards = new int[80];
		init();
	}
	
	public Game(int number){
		gamers = new Gamer[8];
		for(int i = 0; i<8; i++){
			gamers[i] = new Gamer();
		}
		totemCards = new int[80];
		init(number);
	}
	
	public void init(){						// initiate the value
		gamerNumber = 0;
		currentMode = 0;
		currentGamer = 0;
		totemCardsNumber = 0;
		punishedGuy = -1;
		for(int i = 0; i<8; i++){
			gamers[i].init();
		}
		for(int i = 0; i<80; i++){
			totemCards[i] = -1;
		}
	}
	
	public void init(int number){
		init();
		gamerNumber = number;
	}
	
	public void deliverCard(){				// Get number [0, 79] randomly and unique
		int[] set = new int[80];
		for(int j = 0; j<80; j++){	
			set[j] = 0;
		}
		
		for(int j = 0; j<80; j++){
			int i = (int)(Math.random() * 80);	
			if(set[i] == 1){
				while(true){
					i = (i+1) % 80;
					if(set[i]==0)
					{
						set[i] = 1;
						gamers[j % gamerNumber].cardDown[j / gamerNumber] = i;
						gamers[j % gamerNumber].downTail++;
						break;
					}
				}
			}
			else{
				set[i] = 1;
				gamers[j % gamerNumber].cardDown[j / gamerNumber] = i;
				gamers[j % gamerNumber].downTail++;
			}		
		}
	}

	public void turnCard(){
		currentCard = gamers[currentGamer].turnCard();
		if(currentCard < 3){
			currentMode = 1;				// 下轮大家一起翻牌
		}
		else if(currentCard < 6){
			currentMode = 2;				// 这轮大家一起抢
		}
		else if(currentCard < 8){
			currentMode = 3;				// 下轮开始按颜色取同
		}
		else {
			currentMode = 0;				// 正常
		}
	}
	
	public boolean judgeRob(int gamerId){
		if(currentMode == 2){
			return true;		
		}
		else if(currentMode > 0){
			return false;		// robs are not allowed 
		}
		
		if(currentGamer == gamerId){
			// find the punished guy
			for(int i = 0; i<gamerNumber; i++){
				if(i == gamerId){
					continue;
				}
				if(currentCard == gamers[i].cardShown){
					punishedGuy = i;
					return true;
				}
			}
		}
		
		else if(gamers[gamerId].cardShown == currentCard){
				return true;
		}
		
		return false;
	}
	
	public void actionRob(int gamerId){
		boolean result = judgeRob(gamerId);
		
		if(result == true){
			// put cards under totem
			if(currentMode == 2){		
				totemCardsNumber = gamers[gamerId].upCount;
				for(int i = 0; i < totemCardsNumber; i++){
					totemCards[i] = gamers[gamerId].cardUp[i];
				}
				gamers[gamerId].dropUpCards();
			}
			
			// give cards to the guy being punished
			else{
				gamers[punishedGuy].addCards(gamers[gamerId].cardUp, gamers[gamerId].upCount);
				gamers[gamerId].dropUpCards();
			}
		} // end the true condition
		
		else{	
			// give all cards to the wrong action player
			for(int i = 0; i < gamerNumber; i++){
				if(i == gamerId){
					continue;
				}
				
				gamers[gamerId].addCards(gamers[i].cardUp, gamers[i].upCount);
				gamers[i].dropUpCards();
			}
			
			
			// if there are cards under totem, give it to the loser
			if(totemCardsNumber > 0){
				gamers[gamerId].addCards(totemCards, totemCardsNumber);
				
				// clear totem variables
				for(int i = 0; i < totemCardsNumber; i++){
					totemCards[i] = -1;
				}
				totemCardsNumber = 0;
			}
		}
	}
	
    class Task1 extends TimerTask {
        public void run() {
        	System.out.println("倒计时： " + count);
    		count--;
            if (count == 0) {
            	timer1.cancel();
            	
            	turnCard();
    			System.out.println("玩家"+currentGamer+"翻出的牌为："+currentCard);
    			currentGamer = (currentGamer+1) % gamerNumber;
            	
            	timer2 = new Timer(); 
            	count = 5;
            	timer2.scheduleAtFixedRate(new Task2(), 0, 1000);
            }
        }
    }

    class Task2 extends TimerTask{
        public void run() {
        	System.out.println("倒计时： " + count);
    		count--;
            if (count == 0) {
            	timer2.cancel();
            	
            	turnCard();
    			System.out.println("玩家"+currentGamer+"翻出的牌为："+currentCard);
    			currentGamer = (currentGamer+1) % gamerNumber;
    			
    			count = 5;
            	timer1 = new Timer(); 
            	timer1.scheduleAtFixedRate(new Task1(), 0, 1000);
            }
        }
    }
	
	public void start(){
		deliverCard();
		for(int i = 0; i<8; i++){
			for(int j = 0; j<10; j++){
				System.out.print(gamers[i].cardDown[j] + " ");
			}
			System.out.print("\n");
		}
		
		System.out.println("牌已经分到玩家手中，开始游戏！");
		
		// 开始轮流发牌
		timer1 = new Timer();
	    timer1.scheduleAtFixedRate(new Task1(), 0, 1000);
	}	
}

/************************************************************
* Gamer (Player Recordings)
* Author: Yang Yanzhe
* Time:   2015/05/30
*************************************************************/

class Gamer{
	int[] cardUp = new int[80];
	int[] cardDown = new int[80];
	int cardShown;		// cardShown = -1, no cards in up set
	int upCount;
	int downHead;
	int downTail;
	
	public Gamer(){
		init();
	}
	
	public void init(){
		upCount = 0;
		downHead = 0;
		downTail = 0;
		cardShown = -1;
		
		for(int i = 0; i < 80; i++){
			cardUp[i] = -1;
			cardDown[i] = -1;
		}
	}
	
	public void dropUpCards(){						// 在抢图腾中胜利后清空翻开的牌
		cardShown = -1;
		for(int i = 0; i<upCount; i++){
			cardUp[i] = -1;
		}
		upCount = 0;
	}

	public void addCards(int[] addSet, int num){	// 输牌后加牌，将给定的牌堆加在栈底
		for(int i = 0; i < num; i++){
			downHead = (downHead - 1) % 80;
			cardDown[downHead] = addSet[i];
		}
	}
	
	public int turnCard(){							// 翻牌
		downTail = (downTail-1) % 80;
		cardShown = cardDown[downTail]; 
		cardUp[upCount] = cardShown;
		upCount++;
		cardDown[downTail] = -1;
		return cardShown;
	}
}