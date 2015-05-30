package client;

import java.util.Scanner;

public class test {
	
	public static void testDelivery(){
		Game testGame = new Game();
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
	
	public static void testGame(){
		/*
		System.out.println("Input the room size(player number):");
		Scanner sc = new Scanner(System.in);
		while(true){
			String s = sc.nextLine();
			int x = sc.nextInt();
			
		}*/
		Game test = new Game(8);
		test.start();
	}
	
	public static void main(String[] args){
		//testDelivery();
		testGame();
	}
}
