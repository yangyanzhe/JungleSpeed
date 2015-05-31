package server;

import java.util.Timer;
import java.util.TimerTask;

/************************************************************
* Game (Rule Control)
* Author: Yang Yanzhe
* Time:   2015/05/30
*************************************************************/

public class Game {
	public SOCKET[] _sockets;
	public int tableID;
	
	int gamerNumber;						// total number of gamers
	int currentCard;						// new shown card
	
	int currentGamer;						// record the gamer who turn cards
	int nextGamer;
	
	int currentMode;						// mode=0��������mode=1, һ���ƣ�
											// mode=2��һ������mode=3����ɫ; mode=4, һ������Ϊgamer�����һ�£�����
	int nextMode;
	
	int   totemCardsNumber;					// number of cards under totem
	int[] totemCards;						// cards under totem
	
	int	  winnerNumber;
	int[] winnerList;
	
	boolean robFlag = false;				// actionFlag
	boolean startFromTimer1 = false;
	boolean startFromTimer2 = false;
	boolean end = false;
	
	Gamer[] gamers;							// player
	Timer timer1;
	Timer timer2;
	int countDefine = 3;	// 5
	volatile int count = 3;					// delay for 5s
	
	int punishedGuy;						// player who will be punished after a rob action
	
	public Game(){
		gamers = new Gamer[8];	
		for(int i = 0; i<8; i++){
			gamers[i] = new Gamer();
		}
		totemCards = new int[80];
		winnerList = new int[8];
		_sockets = new SOCKET[8];
		for (int i = 0; i < 8; i++) {
			_sockets[i] = null;
		}
		tableID = -1;
		init();
	}
	
	public Game(int number){
		gamers = new Gamer[8];
		for(int i = 0; i<8; i++){
			gamers[i] = new Gamer();
		}
		totemCards = new int[80];
		winnerList = new int[8];
		_sockets = new SOCKET[8];
		for (int i = 0; i < 8; i++) {
			_sockets[i] = null;
		}
		tableID = -1;
		init(number);
	}
	
	public void init(){						// initiate the value
		gamerNumber = 0;
		currentMode = 0;
		nextMode = 0;
		currentGamer = 0;
		nextGamer = 0;
		totemCardsNumber = 0;
		winnerNumber = 0;
		
		punishedGuy = -1;
		for(int i = 0; i<8; i++){
			gamers[i].init();
			winnerList[i] = 0;
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
			while(winnerList[currentGamer] > 0){
				currentGamer = (currentGamer + 1) % gamerNumber;
			}
			currentCard = gamers[currentGamer].turnCard();
			int k = gamerNumber-1-winnerNumber;
			while(currentCard ==-1){
				currentGamer = (currentGamer+1) % gamerNumber;
				currentCard = gamers[currentGamer].turnCard();
				k--;
				if(k == 0){
					System.out.println("�;�");
					end = true;
					
					for (int i = 0; i < gamerNumber; i++) {
						_sockets[i].os.println("tie");
						_sockets[i].os.flush();
					}
					
					return;
				}
			}
			
			System.out.println("���"+currentGamer+"��������Ϊ��"+currentCard);
			nextGamer = (currentGamer+1) % gamerNumber;
			
			if(currentCard < 3){
				nextMode = 1;					// ���ִ��һ����
			}
			else if(currentCard < 6){
				if(gamers[currentGamer].downHead == gamers[currentGamer].downTail){
					currentMode = 4;
				}
				else{
					currentMode = 2;				// ���ִ��һ����
				}
			}
			else if(currentCard < 8){
				nextMode = 3;					// ���ֿ�ʼ����ɫȡͬ
			}
			else {
				currentMode = 0;				// ����
			}
			
			if((currentCard < 3 || (5 < currentCard && currentCard < 8)) &&
			   gamers[currentGamer].downHead == gamers[currentGamer].downTail){
				// if the last one is 0-2 or 6-7, he wins
				winnerNumber++;
				winnerList[currentGamer] = winnerNumber;
			}
			
			//��ͻ���˵���˷����ƣ���ʽΪ turncard~���Ӻ�~��ұ��~�Ƶı��
			for (int i = 0; i < gamerNumber; i++) {
				_sockets[i].os.println("turncard~" + currentGamer + "~" + currentCard);
				_sockets[i].os.flush();
			}
		}
		else{
			boolean outerArrow = false;
			boolean functionCard = false;
			boolean empty = true;
			for(int i = 0; i<gamerNumber; i++){
				if(winnerList[i] > 0){	
					continue;	
				}
				
				currentCard = gamers[currentGamer].turnCard();
				if(currentCard == -1){
					continue;
				}
				
				empty = false;
				System.out.println("���"+i+"��������Ϊ��"+currentCard);
				//��ͻ���˵���˷����ƣ���ʽΪ turncard~���Ӻ�~��ұ��~�Ƶı��
				for (int j = 0; j < gamerNumber; j++) {
					_sockets[j].os.println("turncard~" + i + "~" + currentCard);
					_sockets[j].os.flush();
				}
				
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
			} // end for
			
			if(empty){					// end game
				end = true;
				System.out.println("�;�");
			}
			
			if(!functionCard && !outerArrow){
				currentMode = 0;
			}
		}
		
		
	}
	
	public void selectWinner(){
		for(int i = 0; i<gamerNumber; i++){
			if(winnerList[i] > 0){
				continue;
			}
			
			if(gamers[i].upCount == 0 && gamers[i].downTail == gamers[i].downHead){
				winnerNumber++;
				winnerList[i] = winnerNumber;
			}
		}
	}
	
	public boolean judgeRob(int gamerId){
		/// �������  ///
		if(currentMode == 2){
			return true;		
		}
		else if(currentMode == 4){
			if(gamerId == currentGamer){
				winnerNumber++;
				winnerList[gamerId] = winnerNumber;
				return true;
			}
			else{	
				// current gamer is punished in the way of wrong action
				gamerId = currentGamer;		
				return false;
			}
		}
		else if(currentCard < 8){
			return false;		// robs are not allowed 
		}
		
		/// ��������   ///
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
			
			if(currentMode == 0 &&  ((gamers[gamerId].cardShown / 4) == (currentCard / 4))){
				punishedGuy = gamerId;
				return true;
			}
			
			else if(currentMode == 3 && ((gamers[gamerId].cardShown % 4) == (currentCard % 4))){
				punishedGuy = gamerId;
				return true;
			}
		}
		
		return false;
	}
	
	public void actionRob(int gamerId){
		//grabresult~getall~�����Ƶ����
		//grabresult~rejecttototem~�����Ƶ����
		//grabresult~rejecttoother~�����Ƶ����~�����Ƶ����
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
				
				for (int i = 0; i < gamerNumber; i++) {
					_sockets[i].os.println("grabresult~rejecttototem~" + gamerId);
					_sockets[i].os.flush();
				}
			}
			
			// give cards to the guy being punished
			else{
				gamers[punishedGuy].addCards(gamers[gamerId].cardUp, gamers[gamerId].upCount);
				gamers[gamerId].dropUpCards();
				
				for (int i = 0; i < gamerNumber; i++) {
					_sockets[i].os.println("grabresult~rejecttoother~" + gamerId + "~" + punishedGuy);
					_sockets[i].os.flush();
				}
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
			
			// turn own up cards to down
			if(gamers[gamerId].upCount > 0){
				gamers[gamerId].addCards(gamers[gamerId].cardUp, gamers[gamerId].upCount);
				gamers[gamerId].dropUpCards();
			}
			
			for (int i = 0; i < gamerNumber; i++) {
				_sockets[i].os.println("grabresult~getall~" + gamerId);
				_sockets[i].os.flush();
			}
		}
		
		selectWinner();
		if(isEnd()){
    		System.out.println("��Ϸ����");
    	}
		else{
			timerResume();
		}
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
            	if(isEnd() || end){
            		System.out.println("��Ϸ����");
            	}
            	else{
	            	timer2 = new Timer(); 
	            	count = countDefine;
	            	timer2.scheduleAtFixedRate(new Task2(), 0, 1000);
            	}
            	
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
            	
            	if(isEnd() || end){
            		System.out.println("��Ϸ����");
            	}
            	else{
            		count = countDefine;
                	timer1 = new Timer(); 
                	timer1.scheduleAtFixedRate(new Task1(), 0, 1000);
            	}
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
    	System.out.println("winnerNumber: " + winnerNumber);
    	
    	if(winnerNumber >= (gamerNumber-2)) {
    		for (int i = 0; i < gamerNumber; i++) {
    			_sockets[i].os.println("gameover");
    			_sockets[i].os.flush();
    		}
    		return true;
    	}
    	else return false;
    }
    
	public void start(){
		deliverCard();
		for(int i = 0; i<gamerNumber; i++){
			for(int j = 0; j<(80 / gamerNumber); j++){
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
		if(downTail == downHead) return -1;			// �Ѿ�û������
		
		downTail = (downTail - 1 + 80) % 80;
		cardShown = cardDown[downTail]; 
		cardUp[upCount] = cardShown;
		upCount++;
		cardDown[downTail] = -1;
		return cardShown;
	}
}
