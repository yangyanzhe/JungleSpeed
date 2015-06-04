package client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.ImageIcon;

public class JungleSpeedClient {
	int port;
	String ip;
	Socket socket;
	PrintWriter os;
	BufferedReader is;
	
	Client app = null;
	
	public JungleSpeedClient() {
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
		ClientOperation clientOperation = new ClientOperation(this);
		clientOperation.start();
		//this.app = new Client(this);
		//this.app.run();
	}
	
	public static void main(String[] args) {
		new JungleSpeedClient();
	}
}

class ClientThread extends Thread {
	//客户机线程，用来接收服务机发送过来的消息
	JungleSpeedClient pClient;
	
	public ClientThread(JungleSpeedClient pClient) {
		this.pClient = pClient;
	}
	
	public void run() {
		while(true) {
			try {
				String s = pClient.is.readLine();
				String[] splitStrings = s.split("~");
				
				if (splitStrings[0].equals("loginreveived")) {
					//返回用户名，积分，头像给这里，构造一个player对象(Client类中的)
					//loginreveived~用户名~积分~头像路径
					System.out.println("登录成功！");
					pClient.app.loginDialog.infoLabel.setText("");
					ImageIcon icon = new ImageIcon(splitStrings[3]);
					int score = Integer.parseInt(splitStrings[2]);
					pClient.app.player = new Player(icon, splitStrings[1], score);
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
				else if (splitStrings[0].equals("gameover")) {
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
					pClient.app.playersPanel.addPlayer(null, newUser, score);
				}
				else if (splitStrings[0].equals("loginrejected")) {
					System.out.println("登录失败！用户名或密码错误！");
					pClient.app.loginDialog.infoLabel.setText("用户名或密码错误");
					pClient.app.loginDialog.infoLabel.setForeground(Color.RED);
				}
				else if (splitStrings[0].equals("registersuccess")) {
					//registersuccess~用户名~密码
					System.out.println("注册成功");
					/*pClient.app.gamehall_panel.setVisible(false);
					pClient.app.loginDialog.setVisible(false);
					pClient.app.registerDialog.setVisible(true);*/
					pClient.app.registerDialog.infoLabel.setText("注册成功！请登录");
					pClient.app.registerDialog.infoLabel.setForeground(Color.GREEN);
					pClient.os.println("login~" + splitStrings[1] + "~" + splitStrings[2]);
					pClient.os.flush();
				}
				else if (splitStrings[0].equals("registerfail")) {
					System.out.println("注册失败");
					pClient.app.gamehall_panel.setVisible(false);
					pClient.app.loginDialog.setVisible(false);
					pClient.app.registerDialog.setVisible(true);
					pClient.app.registerDialog.infoLabel.setText("用户名已存在！");
					pClient.app.registerDialog.infoLabel.setForeground(Color.RED);
				}
				else if (splitStrings[0].equals("tellseatinfo")) {
					//命令格式是tellseatinfo~用户名~桌子号~座位号
					System.out.println("用户" + splitStrings[1] + "坐在" + splitStrings[2]
							+ "号桌子的" + splitStrings[3] + "号位置上");
					
					int table = Integer.parseInt(splitStrings[2]);
					int id = Integer.parseInt(splitStrings[3]);
					id += table * 8;
					pClient.app.gamehall_panel.drawingPanel.addchairs(id, 1);
				}
				else if (splitStrings[0].equals("tablegamestart")) {
					//tablegamestart~桌子号
					System.out.println(splitStrings[1] + "号桌子已经开始了游戏，不能加入!");
				}
				else if (splitStrings[0].equals("clientoffline")) {
					//用户下线信息，发给客户端的格式为：clientoffline~username
					System.out.println("用户" + splitStrings[1] + "下线了！");
					//TODO 根据新的接口来删用户
				}
				else if (splitStrings[0].equals("loginagain")) {
					System.out.println("重复登录，登录失败！");
					pClient.app.loginDialog.infoLabel.setText("该用户已登录！");
					pClient.app.loginDialog.infoLabel.setForeground(Color.RED);
				}
				else if (splitStrings[0].equals("chattoclient")) {
					//chattoclient~username~avatar~msg
					ImageIcon icon = new ImageIcon(splitStrings[2]);
					String msg = s.substring(0);
					for (int i = 0; i < 3; i++) {
						msg = msg.substring(splitStrings[i].length() + 1);
					}
					pClient.app.chatPanel.addPlayerMessage(icon, splitStrings[1], msg);
				}
				//TODO 是否添加登出功能？
			} catch (Exception e) {
			}
		}
	}
}

class ClientOperation extends Thread {
	JungleSpeedClient pClient;
	
	public ClientOperation(JungleSpeedClient pClient) {
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
