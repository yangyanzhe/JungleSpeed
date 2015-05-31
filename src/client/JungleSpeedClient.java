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
			System.out.println("�½�Socketʧ�ܣ�");
		}
		
		ClientThread clientThread = new ClientThread(this);
		clientThread.start();
		ClientOperation clientOperation = new ClientOperation(this);
		clientOperation.start();
	}
	
	public static void main(String[] args) {
		new JungleSpeedClient();
	}
}

class ClientThread extends Thread {
	//�ͻ����̣߳��������շ�������͹�������Ϣ
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
					System.out.println("Server has received login info!!");
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
					//���ƣ���ʽΪ�� turncard~��ұ��~�Ƶı��
					int playerNum = Integer.parseInt(splitStrings[1]);
					int cardNum = Integer.parseInt(splitStrings[2]);
					System.out.println("player " + playerNum + " turned card " + cardNum);
				}
				else if (splitStrings[0].equals("grabresult")) {
					//grabresult~getall~�����Ƶ����
					//grabresult~rejecttototem~�����Ƶ����
					//grabresult~rejecttoother~�����Ƶ����~�����Ƶ����
					if (splitStrings[1].equals("getall")) {
						int receiver = Integer.parseInt(splitStrings[2]);
						System.out.println("���" + receiver + "�õ������е��ƣ�");
					}
					else if (splitStrings[1].equals("rejecttototem")) {
						int giver = Integer.parseInt(splitStrings[2]);
						System.out.println("���" + giver + "���Լ��������Ʒ�����ͼ�����棡");
					}
					else if (splitStrings[1].equals("rejecttoother")) {
						int giver = Integer.parseInt(splitStrings[2]);
						int receiver = Integer.parseInt(splitStrings[3]);
						System.out.println("���" + giver + "���Լ��������Ƹ������" + receiver + "!");
					}
				}
				else if (splitStrings[0].equals("grabresult")) {
					System.out.println("Game Over!");
				}
				else if (splitStrings[0].equals("tie")) {
					System.out.println("Tie!!");
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
