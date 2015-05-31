package client;

import java.util.Timer;
import java.util.TimerTask;

/************************************************************
* Game (Rule Control)
* Author: Yang Yanzhe
* Time:   2015/05/30
*************************************************************/

public class Game {
	int gamerNumber;						// total number of gamers
	int currentCard;						// new shown card
	
	int currentGamer;						// record the gamer who turn cards
	int nextGamer;
	
	int currentMode;						// mode=0��������mode=1, һ���ƣ�mode=2��һ������mode=3����ɫ
	int nextMode;
	
	int   totemCardsNumber;					// number of cards under totem
	int[] totemCards;						// cards under totem
	
	boolean robFlag = false;				// actionFlag
	boolean startFromTimer1 = false;
	boolean startFromTimer2 = false;
	
	Gamer[] gamers;							// player
	Timer timer1;
	Timer timer2;
	volatile int count = 5;					// delay for 5s
	
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
		nextMode = 0;
		currentGamer = 0;
		nextGamer = 0;
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
		currentGamer = nextGamer;
		currentMode = nextMode;
		nextMode = 0;
		
		if(currentMode != 1){
			currentCard = gamers[currentGamer].turnCard();
			System.out.println("���"+currentGamer+"��������Ϊ��"+currentCard);
			nextGamer = (currentGamer+1) % gamerNumber;
			
			if(currentCard < 3){
				nextMode = 1;					// ���ִ��һ����
			}
			else if(currentCard < 6){
				currentMode = 2;				// ���ִ��һ����
			}
			else if(currentCard < 8){
				nextMode = 3;					// ���ֿ�ʼ����ɫȡͬ
			}
			else {
				currentMode = 0;				// ����
			}
			
		}
		else{
			boolean outerArrow = false;
			boolean functionCard = false;
			for(int i = 0; i<gamerNumber; i++){
				currentCard = gamers[i].turnCard();
				System.out.println("���"+i+"��������Ϊ��"+currentCard);
				
				if(currentCard < 3){
					nextMode = 1;					// ���ִ��һ����
					outerArrow = true;
					functionCard = true;
				}
				else if(currentCard < 6){
					currentMode = 2;				// ���ִ��һ����
					functionCard = true;
				}
				else if((!outerArrow) && currentCard < 8){
					nextMode = 3;
					functionCard = true;
				}
			}
			
			if(!functionCard && !outerArrow){
				currentMode = 0;
			}
		}
	}
	
	public boolean judgeRob(int gamerId){
		if(currentMode == 2){
			return true;		
		}
		else if(currentCard < 8){
			return false;		// robs are not allowed 
		}
		
		if(currentGamer == gamerId){
			// find the punished guy
			for(int i = 0; i<gamerNumber; i++){
				if(i == gamerId){
					continue;
				}
				if(gamers[i].upCount == 0){
					continue;
				}
				
				if(currentMode == 0){
					if((currentCard / 4) == (gamers[i].cardShown / 4)){
						punishedGuy = i;
						return true;
					}
				}
				else if(currentMode == 3){
					if((currentCard % 4) == (gamers[i].cardShown) % 4){
						punishedGuy = i;
						return true;
					}
				}
			}
		}
		
		else{
			if(gamers[gamerId].upCount == 0){
				return false;
			}
			
			if(currentMode == 0 &&  ((gamers[gamerId].cardShown/4) == (currentCard/4))){
				punishedGuy = gamerId;
				return true;
			}
			
			else if(currentMode == 3 && ((gamers[gamerId].cardShown%4) == (currentCard%4))){
				punishedGuy = gamerId;
				return true;
			}
		}
		
		return false;
	}
	
	public void actionRob(int gamerId){
		nextGamer = gamerId;
		System.out.println("���"+gamerId+"������ͼ��");
		if(startFromTimer1){
			timer2.cancel();
		}
		else if(startFromTimer2){
			timer1.cancel();
		}
		
		boolean success = judgeRob(gamerId);
		
		if(success){
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
				if(i == gamerId || gamers[i].upCount == 0){
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
		
		timerResume();
	}
	
    class Task1 extends TimerTask {
        public void run() {
        	startFromTimer1 = false;
        	startFromTimer2 = true;
        	System.out.println("����ʱ�� " + count);
    		count--;
    		
    		if (count == 0) {
            	timer1.cancel();
            	
            	turnCard();
            	
            	timer2 = new Timer(); 
            	count = 5;
            	timer2.scheduleAtFixedRate(new Task2(), 0, 1000);
            }
        }
    }

    class Task2 extends TimerTask{
        public void run() {
        	System.out.println("����ʱ�� " + count);
    		count--;
    		startFromTimer2 = false;
    		startFromTimer1 = true;
    		
    		if (count == 0) {
            	timer2.cancel();
            	startFromTimer1 = true;
            	
            	turnCard();
    			
    			count = 5;
            	timer1 = new Timer(); 
            	timer1.scheduleAtFixedRate(new Task1(), 0, 1000);
            }
        }
    }
	
    public void timerResume(){
    	if(startFromTimer1){
    		count = 2;
    		timer1 = new Timer(); 
        	timer1.scheduleAtFixedRate(new Task1(), 0, 1000);
        	startFromTimer1 = false;
    	}
    	else if(startFromTimer2){
    		count = 2;
    		timer2 = new Timer(); 
        	timer2.scheduleAtFixedRate(new Task2(), 0, 1000);
        	startFromTimer2 = false;
    	}
    }
    
    public boolean isEnd(){
    	return true;
    }
    
	public void start(){
		deliverCard();
		for(int i = 0; i<8; i++){
			for(int j = 0; j<10; j++){
				System.out.print(gamers[i].cardDown[j] + " ");
			}
			System.out.print("\n");
		}
		
		System.out.println("���Ѿ��ֵ�������У���ʼ��Ϸ��");
		
		// ��ʼ��������
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
	
	public void dropUpCards(){						// ����ͼ����ʤ������շ�������
		cardShown = -1;
		for(int i = 0; i<upCount; i++){
			cardUp[i] = -1;
		}
		upCount = 0;
	}

	public void addCards(int[] addSet, int num){	// ���ƺ���ƣ����������ƶѼ���ջ��
		for(int i = 0; i < num; i++){
			downHead = (downHead - 1 + 80) % 80;
			cardDown[downHead] = addSet[i];
		}
	}
	
	public int turnCard(){							// ����
		downTail = (downTail-1) % 80;
		cardShown = cardDown[downTail]; 
		cardUp[upCount] = cardShown;
		upCount++;
		cardDown[downTail] = -1;
		return cardShown;
	}
}