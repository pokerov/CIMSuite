import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import org.eclipse.swt.SWT;
import java.util.*;

public class ServerThread extends Thread {

	private Socket userSocket;
	private ServerSocket plainSocket;
	private ServerSocket sslSocket;
	private ServerSocketFactory sslFactory;
	private int socketPort = 6000;
	private final int maxUsers = 10;
	private volatile boolean stop = false;
	private boolean SSL = true;
	private Vector<ClientThread> users;
	private MainWindow main_window;
	private ClientThread client_thread;
	private String errMessage;
	
	public ServerThread(MainWindow mw){
		users = new Vector<ClientThread>();
		main_window = mw;
	}
	
	public int getMaxUsers(){
		return maxUsers;
	}
	
	public int getPort(){
		return socketPort;
	}
	
	public void setPort(int value){
		socketPort = value;
	}
	
	public String getErrorMessage(){
		return errMessage;
	}
	
	public void setErrorMessage(String message){
		errMessage = message;
	}
	
	private void SSLConnection(){
		try{
			sslFactory = SSLServerSocketFactory.getDefault();
			sslSocket = sslFactory.createServerSocket(getPort());
		}
		catch(BindException e){
			setErrorMessage("The specified port is in use. You have to restart the program or choose different port.");
			main_window.returnInstance().addMessage(getErrorMessage(), SWT.COLOR_BLACK);
		}
		catch(IOException e){ System.exit(1); }
		
		while(!stop){
			userSocket = null;
			try{
				userSocket = sslSocket.accept();
			}
			catch(IOException e){ }
			client_thread = new ClientThread(userSocket, this, main_window);
			client_thread.start();
		}
	}
	
	private void PlainConnection(){
		try{
			plainSocket = new ServerSocket(getPort());
		}
		catch(BindException e){
			setErrorMessage("The specified port is in use. You have to restart the program or use different port.");
			main_window.returnInstance().addMessage(getErrorMessage(), SWT.COLOR_BLACK);
		}
		catch(IOException e){ System.exit(1); }
		
		while(!stop){
			userSocket = null;
			try{
				userSocket = plainSocket.accept();
			}
			catch(IOException e){ }
			client_thread = new ClientThread(userSocket, this, main_window);
			client_thread.start();
		}
	}
	
	public void requestStop(){
		stop = false;
	}
	
	public synchronized void addUser(ClientThread client){
		users.addElement(client);
	}
	
	public synchronized void removeUser(ClientThread client){
		users.removeElement(client);
	}
	
	public synchronized int getUsersNumber(){
		return users.size();
	}
	
	public synchronized boolean isNickNameFree(String nick, ClientThread client){
		ClientThread current = null;
		if(users.size() == 0)return true;
		else{
			for(int i=0; i<users.size(); i++){
				current = users.get(i);
				if(current.getNickName().equals(nick) && (current != client))return false;
			}
		return true;
		}
	}
	
	public synchronized void sendMessage(MyPacket mp, ClientThread client){
		for(int i=0; i<users.size(); i++){
			if(users.get(i).getChannelName().equals(mp.getChannelName())){
				users.get(i).sendMessage(mp);
			}
		}
	}
	
	public void run(){
		try{
			if(SSL)SSLConnection();
			else PlainConnection();
		}
		catch(Exception e){ }
	}
	
	public ClientThread getClientThread(int id){
		return users.get(id);
	}
}
