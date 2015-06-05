package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UserManager{
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
			
		User user = new User(username, password, 0, avatar);
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
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF-8"));
			
			String line="";
	        
			while ((line=bufferedReader.readLine())!=null) {
	            String[] strList = line.split(" ");
	            
	            //debug 
	            //System.out.println(strList[0] + "~" + strList[1] + "~" + strList[2]);
	            
	            User user = new User(strList[0], strList[1], Integer.parseInt(strList[2]), strList[3]);
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
