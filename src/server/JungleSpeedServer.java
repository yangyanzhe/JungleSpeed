package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.stream.events.EndDocument;

public class JungleSpeedServer {
	public static void main(String[] args) {
		serve();
	}
	
	public static void serve() {
		Server.serve();
		Server.shutdown();
	}
}

//-------------------SOCKET�࣬��װ�û�Socket��������������������Ϣ-----------
class SOCKET {
	Socket socket;		//Socket
	BufferedReader is;	//������
	PrintWriter os;		//�����
	String ID;			//�û������ǹؼ���
	int Head;			//�û�ͷ��
	int Grade;			//�û�����
	int Rank;			//�û�����
	int No;				//����
	
	SOCKET(Socket socket) {
		this.socket=socket;
		No = -1;		//��ʾ��û������
		try {
			is=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
		}
		catch(IOException e) {
			System.out.println("Error");
		}
	}
}

class Information {
	SOCKET _socket;
	String content;
	
	Information(SOCKET _socket, String content) {
		this._socket = _socket;
		this.content = content;
	}
}

class ClientListener extends Thread	{
	//ÿһ��ClientListener�̸߳�����һ���ͻ�Socket
	SOCKET _socket;
	Messenger messenger;
	
	public ClientListener(SOCKET _socket,Messenger messenger) {
		this._socket=_socket;
		this.messenger = messenger;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);
			}
			catch(InterruptedException ie) {
			}
			try {
				String content = _socket.is.readLine();
				Information info = new Information(_socket,content);
				messenger.mq.put(info);
			}
			catch(IOException e) {
				System.out.println("һ���û�������"+_socket.socket);
				Information info = new Information(_socket,"offli:");
				messenger.mq.put(info);
				this.stop();
			}
		}
	}
}

class SocketListener extends Thread {
	//����socket���ӵ��̣߳����𲻶Ͻ���socket
	ServerSocket server;
	Messenger messenger;
	
	public SocketListener(ServerSocket server,Messenger messenger) {
		this.server=server;
		this.messenger = messenger;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);
			}
			catch(InterruptedException ie) {
			}
			try {
				//����һ��socket����
				Socket socket =server.accept();
				SOCKET _socket = new SOCKET(socket);
				(new ClientListener(_socket, messenger)).start();
			}
			catch(Exception e) {
				System.out.println("Error:"+e);
			}	
		}
	}	
}

class MessageQueue extends Vector<Information>{
	//��Ϣ����
	synchronized void put(Information info) {
		addElement(info);
		notify();
	}
	
	synchronized Information get() {
		if(this.size()==0) {
			try {
				wait();
			}
			catch(InterruptedException e) {
			}
		}
		Information info = (Information)this.firstElement();
		try {
			this.remove(this.firstElement());
		}
		catch (Exception e) {
			System.out.println("Error:"+e);
		}
		return info;
	}
}

class Desk extends Game {
	public boolean[] isUserReady = new boolean[8];
	public boolean isReady;
	
	Desk() {
		super();
		
		int i;
		for (i = 0; i < 8; i++) {
			isUserReady[i] = false;
		}
		isReady = false;
	}
	
	public boolean join(SOCKET _socket) {
		for (int i = 0; i < 8; i++) {
			if (_sockets[i] == null) {
				_sockets[i] = _socket;
				return true;
			}
		}
		return false;
	}
}

class UserManager {
	public void loadFromFile() {
		
	}
	
	public void outputToFile() {
		
	}
}

class Messenger extends Thread {
	MessageQueue mq;
	Desk[] desks;
	UserManager userManager;
	final int maxTable = 64;
	
	public Messenger() {
		mq = new MessageQueue();
		desks = new Desk[maxTable];
		userManager = new UserManager();
		userManager.loadFromFile();
		for (int i = 0; i < maxTable; i++) {
			desks[i] = null;
		}
	}
	
	public void run() {
		while(true) {
			try {
				try {
					Thread.sleep(100);
				}
				catch(InterruptedException ie) {
				}
				
				Information info = mq.get();
				if (info != null) {
					SOCKET _socket = info._socket;	//��������Ϣ��SOCKET
					String content = info.content;	//��Ϣ����
					String[] splitStrings = content.split("~");
					
					if(splitStrings[0].equals("login")) {
						System.out.println("server get login!!(client sent)");
						_socket.os.println("loginreveived");
						_socket.os.flush();
					}
					else if (splitStrings[0].equals("jointable")) {
						//�������ӣ������ʽΪ jointable~���ӱ��
						System.out.println("joining table...");
						int tableNum = Integer.parseInt(splitStrings[1]);
						if (desks[tableNum] == null) {
							desks[tableNum] = new Desk();
						}
						boolean isSucceed = desks[tableNum].join(_socket);
						if (isSucceed) {
							_socket.os.println("jointablesuccess");
							_socket.os.flush();
						}
						else {
							_socket.os.println("jointablefail");
							_socket.os.flush();
						}
					}
					else if (splitStrings[0].equals("userready")) {
						//�û������׼����ť������׼��״̬����ʽΪ userready~���ӱ��
						int tableNum = Integer.parseInt(splitStrings[1]);
						System.out.println("Set ready");
						boolean flag = false;
						for (int i = 0; i < 8; i++) {
							if (desks[tableNum]._sockets[i] == _socket) {
								desks[tableNum].isUserReady[i] = true;
								_socket.os.println("setreadysuccess");
								_socket.os.flush();
								flag = true;
								break;
							}
						}
						if (flag == false) {
							_socket.os.println("setreadyfail");
							_socket.os.flush();
						}
						
						int len = 0;
						for (len = 0; len < 8; len++) {
							if (desks[tableNum]._sockets[len] == null) {
								break;
							}
						}
						flag = true;
						for (int i = 0; i < len; i++) {
							if (desks[tableNum].isUserReady[i] == false) {
								flag = false;
							}
						}
						
						if (len >= 3 && flag == true) {
							desks[tableNum].isReady = true;
							for (int i = 0; i < len; i++) {
								desks[tableNum]._sockets[i].os.println("gamestart");
								desks[tableNum]._sockets[i].os.flush();
							}
							System.out.println("We are ready!!");
							desks[tableNum].gamerNumber = len;
							desks[tableNum].tableID = tableNum;
							desks[tableNum].start();
						}
					}
					else if (splitStrings[0].equals("grab")) {
						//����ͼ�ڣ���ʽΪ��grab~���ӱ��
						System.out.println("grabing...");
						int tableNum = Integer.parseInt(splitStrings[1]);
						int len = desks[tableNum].gamerNumber;
						for (int i = 0; i < len; i++) {
							if (desks[tableNum]._sockets[i] == _socket) {
								//�õ������ж�����ĺ���
								desks[tableNum].actionRob(i);
								break;
							}
						}
						//�����н���������пͻ���
						//�����ͻ��˵Ľ����ʽΪ��grabresult~���
						//���������getall(����ͼ���µ�) rejecttototem rejecttoother
						//grabresult~getall~�����Ƶ����
						//grabresult~rejecttototem~�����Ƶ����
						//grabresult~rejecttoother~�����Ƶ����~�����Ƶ����
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	public void end() {
		userManager.outputToFile();
	}
}

class Server{
	private static ServerSocket server;
	private static Messenger messenger;
	
	public Server(){
		server = null;
		messenger = null;
	}
	
	public static void serve(){	
		try {
			server=new ServerSocket(4700);
			messenger = new Messenger();
			messenger.start();
			SocketListener Listener = new SocketListener(server,messenger);
			Listener.start();
		}
		catch (Exception e) {
			System.out.println("can not listen to:" + e);
		}
	}
	
	public static void shutdown(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override  
            public void run() {  
                messenger.end();
            }  
		});
	}
}