package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class JungleSpeedClient {
	int port;
	String ip;
	Socket socket;
	PrintWriter os;
	BufferedReader is;
	
	Client app = null;
	
	public JungleSpeedClient() {
		// TODO Auto-generated constructor stub
		ip = "127.0.0.1";
		port = 4700;
		
		try {
			socket = new Socket(ip, port);
			os = new PrintWriter(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
		}
		catch(Exception e) {
			System.out.println("新建Socket失败！");
		}
		
		ClientThread clientThread = new ClientThread(this);
		clientThread.start();
		//ClientOperation clientOperation = new ClientOperation(this);
		//clientOperation.start();
		this.app = new Client(this);
		this.app.run();
	}
	
	public static void main(String[] args) {
		new JungleSpeedClient();
	}
}

class ClientThread extends Thread {
	//客户机线程，用来接收服务机发送过来的消息
	JungleSpeedClient pClient;
	
	public ClientThread(JungleSpeedClient pClient) {
		// TODO Auto-generated constructor stub
		this.pClient = pClient;
	}
	
	public void run() {
		while(true) {
			try {
				String s = pClient.is.readLine();
				String[] splitStrings = s.split("~");
				
				if (splitStrings[0].equals("loginreveived")) {
					System.out.println("登录成功！");
					pClient.app.loginDialog.setVisible(false);
					pClient.app.registerDialog.setVisible(false);
					pClient.app.gamehall_panel.setVisible(true);
				}
				else if (splitStrings[0].equals("jointablesuccess")){
					System.out.println("Join Table Success!");
				}
				else if (splitStrings[0].equals("jointablefail")) {
					System.out.println("Join Table Fail!");
				}
				else if (splitStrings[0].equals("setreadysuccess")) {
					System.out.println("Set ready success!");
				}
				else if (splitStrings[0].equals("setreadyfail")) {
					System.out.println("Set ready fail!");
				}
				else if (splitStrings[0].equals("gamestart")) {
					System.out.println("Game Start!");
				}
				else if (splitStrings[0].equals("turncard")) {
					//翻牌，格式为： turncard~玩家编号~牌的编号
					int playerNum = Integer.parseInt(splitStrings[1]);
					int cardNum = Integer.parseInt(splitStrings[2]);
					System.out.println("player " + playerNum + " turned card " + cardNum);
				}
				else if (splitStrings[0].equals("grabresult")) {
					//grabresult~getall~接收牌的玩家
					//grabresult~rejecttototem~抛弃牌的玩家
					//grabresult~rejecttoother~抛弃牌的玩家~接收牌的玩家
					if (splitStrings[1].equals("getall")) {
						int receiver = Integer.parseInt(splitStrings[2]);
						System.out.println("玩家" + receiver + "得到了所有的牌！");
					}
					else if (splitStrings[1].equals("rejecttototem")) {
						int giver = Integer.parseInt(splitStrings[2]);
						System.out.println("玩家" + giver + "把自己翻开的牌放在了图腾下面！");
					}
					else if (splitStrings[1].equals("rejecttoother")) {
						int giver = Integer.parseInt(splitStrings[2]);
						int receiver = Integer.parseInt(splitStrings[3]);
						System.out.println("玩家" + giver + "把自己翻开的牌给了玩家" + receiver + "!");
					}
				}
				else if (splitStrings[0].equals("grabresult")) {
					System.out.println("Game Over!");
				}
				else if (splitStrings[0].equals("tie")) {
					System.out.println("Tie!!");
				}
				else if (splitStrings[0].equals("newgamer")) {
					//new gamer log in
					String newUser = splitStrings[1];
					int score = Integer.parseInt(splitStrings[2]);
					System.out.println("用户" + newUser + "进入游戏大厅，分数为" + score);
				}
				else if (splitStrings[0].equals("loginrejected")) {
					System.out.println("登录失败！用户名或密码错误！");
				}
				else if (splitStrings[0].equals("registersuccess")) {
					System.out.println("注册成功");
					pClient.app.gamehall_panel.setVisible(false);
					pClient.app.loginDialog.setVisible(false);
					pClient.app.registerDialog.setVisible(true);
				}
				else if (splitStrings[0].equals("registerfail")) {
					System.out.println("注册失败");
					pClient.app.gamehall_panel.setVisible(false);
					pClient.app.loginDialog.setVisible(false);
					pClient.app.registerDialog.setVisible(true);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}

class ClientOperation extends Thread {
	JungleSpeedClient pClient;
	
	public ClientOperation(JungleSpeedClient pClient) {
		// TODO Auto-generated constructor stub
		this.pClient = pClient;
	}
	
	public void run() {
		Scanner sc = new Scanner(System.in);
		while(true) {
			String s = sc.nextLine();
			pClient.os.println(s);
			pClient.os.flush();
		}
	}
	
}
