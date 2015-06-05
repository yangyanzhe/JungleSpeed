package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class JungleSpeedServer {
	public static void main(String[] args) {
		serve();
	}
	
	public static void serve() {
		Server.serve();
		Server.shutdown();
	}
}

class SOCKET {
	Socket socket;
	BufferedReader is;
	PrintWriter os;
	String ID;//用户名
	int Grade;//用户积分
	int Rank;//用户排名
	int No;	//桌号
	int seatInTable;//在桌子中坐的位置
	String avatar = "";
	
	SOCKET(Socket socket) {
		this.socket=socket;
		No = -1;
		seatInTable = -1;
		ID = null;
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
				Information info = new Information(_socket, content);
				messenger.mq.put(info);
			}
			catch(IOException e) {
				System.out.println("一个用户断线了"+_socket.socket);
				if (_socket.ID != null) {
					//若该用户还没有登录，就不把他的消息给其他所有客户端了
					Information info = new Information(_socket, "offline");
					messenger.mq.put(info);
				}
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
	
	Desk(UserManager um) {
		super(um);
		
		int i;
		for (i = 0; i < 8; i++) {
			isUserReady[i] = false;
		}
	}
	
	public boolean join(SOCKET _socket) {
		if (isReady) {
			//游戏已经开始
			return false;
		}
		
		for (int i = 0; i < 8; i++) {
			if (_socket.No != -1) { 
				//同一个socket不能加入多个桌子或多个位置
				return false;
			}
			if (_sockets[i] == null) {
				_sockets[i] = _socket;
				return true;
			}
		}
		return false;
	}
	
	public void remove(SOCKET _socket) {
		//TODO 要处理有人在游戏过程当中强退游戏后把他的牌都放在图腾下面，游戏继续。进入桌子里面退出还需要测试
		// 游戏中有人强退游戏 或是 游戏还没开始时有人退出桌子

		int id = 0;
		int i;
		for (i = 0; i < 8; i++) {
			if (_sockets[i] != null && _sockets[i].equals(_socket)) {
				id = i;
				int j;
				for (j = i; j < 7; j++) {
					_sockets[j] = _sockets[j + 1];
					if(_sockets[j] == null){
						break;
					}
				}
				_sockets[j] = _socket;
			}
		}
		if (isReady) {
			_socket.Grade -= 10;
			exceptionLeave(id);
		}
	}
}

class User {
	private String username;
	private String password;
	private int score = 0;
	public boolean isLogIn = false;
	private String avatar = "";
	
	public User(String username, String password, int score, String avatar) {
		this.username = username;
		this.password = password;
		this.score = score;
		this.isLogIn = false;
		this.avatar = avatar;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public String toString(){
		return username + " " + password + " " + score + " " + avatar + "\n";
	}
	
	public void setScore(int s) {
		this.score = s;
	}
	
	public String getAvatar() {
		return this.avatar;
	}
	
	public void setAvatar(String a) {
		this.avatar = a;
	}
	
	public User copy() {
		User r = new User(username, password, score, avatar);
		r.isLogIn = this.isLogIn;
		return r;
	}
}



class Messenger extends Thread {
	MessageQueue mq;
	Desk[] desks;
	Vector<SOCKET> SOCKETList;     //所有在线的人
	UserManager userManager;
	final int maxTable = 12;
	
	public Messenger() {
		mq = new MessageQueue();
		desks = new Desk[maxTable];
		userManager = new UserManager();
		userManager.loadFromFile();
		SOCKETList = new Vector<SOCKET>();
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
						//login~username~password
						if (splitStrings.length == 3){
							System.out.println("server get login info!");
							User currentUser = userManager.verify(splitStrings[1], splitStrings[2]);
							if (currentUser != null) {
								if (currentUser.isLogIn) {
									_socket.os.println("loginagain");
									_socket.os.flush();
									System.out.println("重复登录");
								}
								else {
									//loginreveived~用户名~积分~头像路径
									_socket.os.println("loginreveived~" + currentUser.getUsername() + "~"
											 + currentUser.getScore() + "~" + currentUser.getAvatar());
									_socket.os.flush();
									
									currentUser.isLogIn = true;
									
									System.out.println("通过验证,登录成功");
									SOCKETList.add(_socket);
									_socket.ID = currentUser.getUsername();
									_socket.No = -1;
									_socket.seatInTable = -1;
									_socket.Grade = currentUser.getScore();
									_socket.avatar = currentUser.getAvatar();
									
									int n = SOCKETList.size();
	
									//把游戏大厅其他已加入桌子的人的位置信息告诉客户端让他渲染
									for (int i = 0; i < n; i++) {
										//命令格式是tellseatinfo~用户名~桌子号~座位号
										SOCKET temp = (SOCKET)SOCKETList.get(i);
										if (!_socket.equals(temp)){
											_socket.os.println("newgamer~" + temp.ID + "~" + temp.Grade);
											_socket.os.flush();
										}
										if (temp.No >= 0 && temp.seatInTable >= 0) {
											_socket.os.println("tellseatinfo~" + temp.ID + "~" + temp.No + "~" + temp.seatInTable);
											_socket.os.flush();
										}
									}
									
									for (int i = 0; i < n; i++) {
										SOCKET temp = (SOCKET)SOCKETList.get(i);
										temp.os.println("newgamer~" + currentUser.getUsername() + "~" + currentUser.getScore());
										temp.os.flush();
									}
									
									//告诉客户端哪些桌子已经开始了游戏
									//tablegamestart~桌子号
									for (int i = 0; i < maxTable; i++) {
										if (desks[i].isReady) {
											_socket.os.println("tablegamestart~" + i);
											_socket.os.flush();
										}
									}
								}
							}
							else {
								_socket.os.println("loginrejected");
								_socket.os.flush();
								System.out.println("拒绝通过");
							}
						}
						else {
							System.out.println("login指令参数数目不对！");
						}
					}
					else if (splitStrings[0].equals("jointable")) {
						//TODO 一个一个自然加入
						//加入桌子，命令格式为 jointable~桌子编号~玩家座位位置
						if (splitStrings.length == 3) {
							System.out.println("joining table...");
							int tableNum = Integer.parseInt(splitStrings[1]);
							int seatPos = Integer.parseInt(splitStrings[2]);
							if (desks[tableNum] == null) {
								desks[tableNum] = new Desk(userManager);
							}
							boolean isSucceed = desks[tableNum].join(_socket);
							if (isSucceed) {
								_socket.No = tableNum;
								_socket.seatInTable = seatPos;
								_socket.os.println("jointablesuccess");
								_socket.os.flush();
								
								//可以把新加入的信息告诉所有客户端让他渲染，不过若加入的动作太过频繁还是不传好
								int n = SOCKETList.size();
								for (int i = 0; i < n; i++) {
									//命令格式是tellseatinfo~用户名~桌子号~座位号
									SOCKET temp = (SOCKET)SOCKETList.get(i);
									temp.os.println("tellseatinfo~" + _socket.ID + "~" + tableNum + "~" + seatPos);
									temp.os.flush();
								}
							}
							else {
								_socket.os.println("jointablefail");
								_socket.os.flush();
							}
						}
						else {
							System.out.println("jointable命令参数错误！");
						}
					}
					else if (splitStrings[0].equals("userready")) {
						//用户点击了准备按钮，进入准备状态，格式为 userready
						int tableNum = _socket.No;
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
							desks[tableNum].init(len);
							System.out.println("We are ready!!");
							desks[tableNum].gamerNumber = len;
							desks[tableNum].tableID = tableNum;
							desks[tableNum].start();
							transferGameControl(tableNum);
							for (int i = 0; i < len; i++) {
								desks[tableNum]._sockets[i].os.println("gamestart");
								desks[tableNum]._sockets[i].os.flush();
							}
						}
					}
					else if (splitStrings[0].equals("grab")) {
						//抢夺图腾，格式为：grab
						System.out.println("grabing...");
						int tableNum = _socket.No;
						int len = desks[tableNum].gamerNumber;
						for (int i = 0; i < len; i++) {
							if (desks[tableNum]._sockets[i] == _socket) {
								//得到抢夺判定结果的函数
								desks[tableNum].actionRob(i);
								break;
							}
						}
						transferGameControl(tableNum);
						//把所有结果发给所有客户端
						//发给客户端的结果格式为：grabresult~结果
						//结果类型有getall(包括图腾下的) rejecttototem rejecttoother
						//grabresult~getall~接收牌的玩家
						//grabresult~rejecttototem~抛弃牌的玩家
						//grabresult~rejecttoother~抛弃牌的玩家~接收牌的玩家
					}
					else if (splitStrings[0].equals("register")) {
						//注册命令格式为register~用户名~密码
						boolean flag = false;
						flag = userManager.add(splitStrings[1], splitStrings[2], "res/a.gif");
						if (flag) {
							System.out.println("新用户" + splitStrings[1] + "注册成功！");
							_socket.os.println("registersuccess~" + splitStrings[1] + "~" + splitStrings[2]);
							_socket.os.flush();
							userManager.outputToFile();
						}
						else {
							System.out.println("用户" + splitStrings[1] + "注册失败！");
							_socket.os.println("registerfail");
							_socket.os.flush();
						}
					}
					else if (splitStrings[0].equals("offline")) {
						//用户下线信息，发给客户端的格式为：clientoffline~username
						SOCKETList.remove(_socket);
						int n = SOCKETList.size();
						for (int i = 0; i < n; i++) {
							SOCKET temp = (SOCKET)SOCKETList.get(i);
							temp.os.println("clientoffline~" + _socket.ID);
							temp.os.flush();
						}
						if (_socket.No != -1) {
							desks[_socket.No].remove(_socket);
						}
						User t = userManager.findByUsername(_socket.ID);
						t.isLogIn = false;
					}
					else if (splitStrings[0].equals("chattoserver")) {
						//chattoserver~message
						String msg = content.substring(13);
						int n = SOCKETList.size();
						for (int i = 0; i < n; i++) {
							SOCKET temp = (SOCKET)SOCKETList.get(i);
							if (!_socket.equals(temp)) {
								temp.os.println("chattoclient~" + _socket.ID + "~" + _socket.avatar + "~" + msg);
								temp.os.flush();
							}
						}
					}
					else if (splitStrings[0].equals("leavetable")) {
						System.out.println("用户" + _socket.ID + "离开" + _socket.No + "号桌子");
						int tableNum = _socket.No;
						int seatNum = _socket.seatInTable;
						desks[tableNum].remove(_socket);
						System.out.println(desks[tableNum].gamerNumber);
						_socket.os.println("leavetablesuccess~" + tableNum + "~" + seatNum);
						_socket.os.flush();
						
						int n = SOCKETList.size();
						for (int i = 0; i < n; i++) {
							SOCKET temp = (SOCKET)SOCKETList.get(i);
							if (!_socket.equals(temp)) {
								temp.os.println("leavetablebroadcast~" + tableNum + "~" + seatNum);
								temp.os.flush();
							}
						}

						_socket.No = -1;
						_socket.seatInTable = -1;
					}
					else if (splitStrings[0].equals("cancelready")) {
						int tableNum = _socket.No;
						System.out.println("Cancel ready");
						for (int i = 0; i < 8; i++) {
							if (desks[tableNum]._sockets[i] == _socket) {
								desks[tableNum].isUserReady[i] = false;
								_socket.os.println("cancelreadysuccess");
								_socket.os.flush();
								break;
							}
						}
					}
					
					if (desks[_socket.No].isReady) {
						//颜色模式
						if (desks[_socket.No].currentMode == 3){
							for (int i = 0; i < desks[_socket.No].gamerNumber; i++) {
								desks[_socket.No]._sockets[i].os.println("setcolormodetrue");
								desks[_socket.No]._sockets[i].os.flush();
							}
						}
						else {
							for (int i = 0; i < desks[_socket.No].gamerNumber; i++) {
								desks[_socket.No]._sockets[i].os.println("setcolormodefalse");
								desks[_socket.No]._sockets[i].os.flush();
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	public void end() {
		userManager.outputToFile();
	}
	
	private void transferGameControl(int tableNum) {
		for (int i = 0; i < desks[tableNum].gamerNumber; i++) {
			desks[tableNum]._sockets[i].os.println("setplayersNum~" + (desks[tableNum].gamerNumber - 1));
			desks[tableNum]._sockets[i].os.flush();
			
			String playersNameSet = "";
			String otherCardSet = "";
			String otherFrontNumSet = "";
			String otherBackNumSet = "";
			for (int j = 0; j < desks[tableNum].gamerNumber; j++) {
				if (j != i) {
					playersNameSet = playersNameSet + desks[tableNum]._sockets[j].ID + "~";
					otherCardSet = otherCardSet + desks[tableNum].gamers[j].cardShown + "~";
					otherFrontNumSet = otherFrontNumSet + desks[tableNum].gamers[j].upCount + "~";
					otherBackNumSet = otherBackNumSet + 
							((desks[tableNum].gamers[j].downTail - desks[tableNum].gamers[j].downHead + 80) % 80) + "~";
				}
			}
			playersNameSet = playersNameSet.substring(0, playersNameSet.length()-1);
			otherCardSet = otherCardSet.substring(0, otherCardSet.length()-1);
			otherFrontNumSet = otherFrontNumSet.substring(0, otherFrontNumSet.length()-1);
			otherBackNumSet = otherBackNumSet.substring(0, otherBackNumSet.length()-1);
			
			desks[tableNum]._sockets[i].os.println("setplayersName~" + playersNameSet);
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setmiddleNum~" + desks[tableNum].totemCardsNumber);
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setmyCard~" + desks[tableNum].gamers[i].cardShown);
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setmyFrontNum~" + desks[tableNum].gamers[i].upCount);
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setmyBackNum~" + 
					((desks[tableNum].gamers[i].downTail - desks[tableNum].gamers[i].downHead + 80) % 80));
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setotherCard~" + otherCardSet);
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setotherFrontNum~" + otherFrontNumSet);
			desks[tableNum]._sockets[i].os.flush();
			
			desks[tableNum]._sockets[i].os.println("setotherBackNum~" + otherBackNumSet);
			desks[tableNum]._sockets[i].os.flush();
		}
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