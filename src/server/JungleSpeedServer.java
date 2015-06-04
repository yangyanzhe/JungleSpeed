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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
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
				Information info = new Information(_socket, "offline");
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
		int i;
		for (i = 0; i < 8; i++) {
			if (_sockets[i].equals(_socket)) {
				int j;
				for (j = i; j < 7; j++) {
					_sockets[j] = _sockets[j + 1];
				}
				_sockets[j] = null;
			}
		}
		gamerNumber--;
	}
}

class User {
	private String username;
	private String password;
	private int score = 0;
	public boolean isLogIn = false;
	private String avatar = "";
	
	public User(String username, String password, String avatar) {
		this.username = username;
		this.password = password;
		this.score = 0;
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
		User r = new User(username, password, avatar);
		r.setScore(score);
		r.isLogIn = this.isLogIn;
		return r;
	}
}

class UserManager{
	private static final String fileName = "userdb.txt";
	
	private Set<User> userSet;
	public UserManager(){
		userSet = new TreeSet<User>(new Comparator<User>() {
			@Override
			public int compare(User a, User b) {
				if (a.getScore() == b.getScore()){
					return a.getUsername().compareTo(b.getUsername());
				}
				
				if (a.getScore() > b.getScore()){
					return 1;
				}
				return -1;
			}
		});
	}
	
	public void loadFromFile() {
		boolean ifSucceed = readFileByLines();
		if (! ifSucceed){
			System.out.println("Load user info failed, please check " + fileName);
		}
	}
	
	public void outputToFile(){
		boolean ifSucceed = writeFileByLines();
		if (! ifSucceed){
			System.out.println("Output user info failed, please check " + fileName);
		} else{
			System.out.println("Output user info successfully!");
		}
	}
	
	public boolean add(String username, String password, String avatar){
		User ifExist = findByUsername(username);
		if (ifExist != null){
			System.out.println("Add new user failed. Username has existed.");
			return false;
		}
			
		User user = new User(username, password, avatar);
		boolean ifSucceed = add(user);
		if (! ifSucceed){
			System.out.println("Add new user failed. Unknown reason.");
			return false;
		} else{
			System.out.println("Add new user successfully!");
			return true;
		}
	}
	public boolean remove(String username, String password){
		User match = verify(username, password);
		
		if (match == null){
			System.out.println("Remove user failed. Wrong username or password.");
			return false;
		}
			
		boolean ifSucceed = remove(match);
		if (ifSucceed){
			System.out.println("Remove user successfully!");
		}else{
			System.out.println("Remove user failed. Unknown reason.");
		}
		
		return ifSucceed;
	}

	public User verify(String username, String password){
		User user = findByUsername(username);
		
		if (user == null)
			return null;
		
		if (! user.getPassword().equals(password))
			return null;
				
		return user;
	}
	
	private boolean add(User user){
		return userSet.add(user);
	}
	
	private boolean remove(User user){
		return userSet.remove(user);
	}
	
	public User findByUsername(String username) {
		for (User user : userSet){
			if (user.getUsername().equals(username)){
				return user;
			}
		}
		return null;
	}
	
	public boolean updateScore(String username, int newScore){
		User match = findByUsername(username);
		
		if (match == null){
			System.out.println("Update score failed. No such user.");
			return false;
		}
		
		User substitute = match.copy();
		substitute.setScore(newScore);
		
		boolean ifSucceed = replace(match, substitute);
		if (ifSucceed){
			System.out.println("Update score successfully!");
		}else{
			System.out.println("Update score failed. Unknown reason.");
		}
		
		return ifSucceed;
	}
	
	public List<User> rtrRankList(){
		
		List<User> list = new ArrayList<User>();
		for (User user : userSet){
			list.add(user);
		}
		
		System.out.println("sort:");
		for (User user : list){
			System.out.println(user.toString());
		}
		return list;
	}
	
	public int getRanking(int score){
		int ranking = 0;
		for (User user : userSet){
			ranking ++;
			if (user.getScore() == score){
				return ranking;
			}
		}
		
		return -1;
	}
	
	private boolean replace(User old, User substitute){
		boolean ifSucceed = remove(old);
		
		if (! ifSucceed){
			System.out.println("Can't remove the original.");
			return false;
		}
		
		ifSucceed = userSet.add(substitute);
		
		if (! ifSucceed){
			System.out.println("Can't add the substitute.");
			return false;
		}
		
		return true;
	}
	
	private boolean readFileByLines(){
		File inFile = new File(fileName);
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF-8"));
			
			String line="";
	        
			while ((line=bufferedReader.readLine())!=null) {
	            String[] strList = line.split(" ");
	            
	            //debug 
	            //System.out.println(strList[0] + "~" + strList[1] + "~" + strList[2]);
	            
	            User user = new User(strList[0], strList[1], strList[3]);
	            userSet.add(user);
	        }
			
			bufferedReader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        
        return true;
    }

	
	private boolean writeFileByLines(){
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(fileName)), "UTF-8"));
			
			for (User user : userSet){
				String userInfo = user.toString();
				bufferedWriter.write(userInfo);
			}
			
			bufferedWriter.close();
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
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
						//加入桌子，命令格式为 jointable~桌子编号~玩家座位位置
						if (splitStrings.length == 3) {
							System.out.println("joining table...");
							int tableNum = Integer.parseInt(splitStrings[1]);
							int seatPos = Integer.parseInt(splitStrings[2]);
							if (desks[tableNum] == null) {
								desks[tableNum] = new Desk();
							}
							boolean isSucceed = desks[tableNum].join(_socket);
							if (isSucceed) {
								_socket.No = tableNum;
								_socket.seatInTable = seatPos;
								_socket.os.println("jointablesuccess");
								_socket.os.flush();
							}
							else {
								_socket.os.println("jointablefail");
								_socket.os.flush();
							}
							//TODO 可以把新加入的信息告诉所有客户端让他渲染，不过若加入的动作太过频繁还是不传好
						}
						else {
							System.out.println("jointable命令参数错误！");
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
					else if (splitStrings[0].equals("register")) {
						//注册命令格式为register~用户名~密码
						boolean flag = false;
						//TODO 添加头像的对话框？默认头像？
						flag = userManager.add(splitStrings[1], splitStrings[2], "res/a.jpg");
						if (flag) {
							System.out.println("新用户" + splitStrings[1] + "注册成功！");
							_socket.os.println("registersuccess");
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
				}
			} catch (Exception e) {
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