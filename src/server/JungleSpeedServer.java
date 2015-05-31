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

//-------------------SOCKET类，封装用户Socket、其输入输出流、相关信息-----------
class SOCKET {
	Socket socket;		//Socket
	BufferedReader is;	//输入流
	PrintWriter os;		//输出流
	String ID;			//用户名，是关键码
	int Head;			//用户头像
	int Grade;			//用户积分
	int Rank;			//用户排名
	int No;				//桌号
	
	SOCKET(Socket socket) {
		this.socket=socket;
		No = -1;		//表示还没进桌子
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
	//每一个ClientListener线程负责监控一个客户Socket
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
				System.out.println("一个用户断线了"+_socket.socket);
				Information info = new Information(_socket,"offli:");
				messenger.mq.put(info);
				this.stop();
			}
		}
	}
}

class SocketListener extends Thread {
	//监听socket连接的线程，负责不断接入socket
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
				//接入一个socket连接
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
	//消息队列
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
					SOCKET _socket = info._socket;	//发出该消息的SOCKET
					String content = info.content;	//消息内容
					String[] splitStrings = content.split("~");
					
					if(splitStrings[0].equals("login")) {
						System.out.println("server get login!!(client sent)");
						_socket.os.println("loginreveived");
						_socket.os.flush();
					}
					else if (splitStrings[0].equals("jointable")) {
						//加入桌子，命令格式为 jointable~桌子编号
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
						//用户点击了准备按钮，进入准备状态，格式为 userready~桌子编号
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
						//抢夺图腾，格式为：grab~桌子编号
						System.out.println("grabing...");
						int tableNum = Integer.parseInt(splitStrings[1]);
						int len = desks[tableNum].gamerNumber;
						for (int i = 0; i < len; i++) {
							if (desks[tableNum]._sockets[i] == _socket) {
								//得到抢夺判定结果的函数
								desks[tableNum].actionRob(i);
								break;
							}
						}
						//把所有结果发给所有客户端
						//发给客户端的结果格式为：grabresult~结果
						//结果类型有getall(包括图腾下的) rejecttototem rejecttoother
						//grabresult~getall~接收牌的玩家
						//grabresult~rejecttototem~抛弃牌的玩家
						//grabresult~rejecttoother~抛弃牌的玩家~接收牌的玩家
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