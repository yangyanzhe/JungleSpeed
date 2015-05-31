package client;

import java.util.Scanner;

public class test {
	Game testGame;
	
	public static void main(String[] args){
		test game = new test();
		game.testRobAction();
	}
	
	public void testDelivery(){
		testGame = new Game();
		testGame.gamerNumber = 8;
		
		long pre = System.currentTimeMillis();
		for(int i = 0; i<1000000; i++){
			testGame.deliverCard();
		}
		long post = System.currentTimeMillis();
		System.out.println("Time cost in delivery cards is:" + (post-pre) + " ms");
		for(int i = 0; i<8; i++){
			for(int j = 0; j<10; j++){
				System.out.print(testGame.gamers[i].cardDown[j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	public void testGame(){
		testGame = new Game(8);
		testGame.start();
	}
	
	public void testRobAction(){
		testGame = new Game(8);
		testGame.start();
		ClientOperation operation = new ClientOperation();
		operation.start();
	}
	
	class ClientOperation extends Thread {
		Game test;
		
		public ClientOperation() {
		}
		
		public void run() {
			Scanner sc = new Scanner(System.in);
			while(true) {
				int player = sc.nextInt();
				testGame.robFlag = true;
				testGame.actionRob(player);
				// testGame.timerResume();
			}
		}
	}

	
	public void testEnd(){
		
	}
}


