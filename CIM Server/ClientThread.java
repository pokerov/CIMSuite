import java.io.*;
import java.net.*;
import javax.net.ssl.*;

import org.eclipse.swt.SWT;

public class ClientThread extends Thread{
	
	private Socket userSocket;
	private InetAddress sa;
	private InetSocketAddress isa;
	private String comm;
	private int command = 0;
	private volatile boolean stop = false;
	private InputStream is;
	private OutputStream os;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private MyPacket my_packet, temp;
	private ServerThread server_thread;
	private MainWindow main_window;
	public Program program;
	private String message = "";
	private String nickName;
	private String channelName;
	
	public ClientThread(Socket socket, ServerThread ST, MainWindow MW){
		userSocket = socket;
		server_thread = ST;
		main_window = MW;
		comm = "";
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String msg){
		message = msg;
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public void setNickName(String nick){
		nickName = nick;
	}
	
	public String getChannelName(){
		return channelName;
	}
	
	public void setChannelName(String channel){
		channelName = channel;
	}
	
	public void sendMessage(MyPacket mp){
		try{
			oos.writeObject(mp);
			oos.flush();
		}
		catch(SocketException e){ halt(); }
		catch(IOException e){ halt(); }
	}
	
	private void obtainUserData(){
		try{
			my_packet = (MyPacket)ois.readObject();
		}
		catch(ClassNotFoundException e){ halt();	}
		catch(IOException e){ halt(); }
		if(my_packet.getServerData()){
			setNickName(my_packet.getNickName());
			setChannelName(my_packet.getChannelName());
		}
		else halt();
	}
	
	@SuppressWarnings("static-access")
	public void run(){
		try{
			isa = (InetSocketAddress)userSocket.getRemoteSocketAddress();
			sa = isa.getAddress();
			is = userSocket.getInputStream();
			os = userSocket.getOutputStream();
			ois = new ObjectInputStream(is);
			oos = new ObjectOutputStream(os);
			
			if(server_thread.getUsersNumber() >= server_thread.getMaxUsers())halt();
			else server_thread.addUser(this);
			
			obtainUserData();
			
			try{
				program.dsp.asyncExec(new Runnable(){
					public void run(){
						main_window.addMessage("User " + getNickName() +" connected from: " + sa + ":" + server_thread.getPort(), SWT.COLOR_BLACK);
						main_window.addUser(getNickName());
					}
				});
			}
			catch(Exception e){ }
			
			try{
				my_packet = new MyPacket();
				my_packet.setServerData(true);
				my_packet.setMessage("Welcome, type /info for more information");
				oos.writeObject((Object)my_packet);
				oos.flush();
			}
			catch(Exception e){ }
			
			while(!stop){
				try{
					try{
						temp = (MyPacket)ois.readObject();
					}
					catch(ClassNotFoundException e){ halt(); continue; }
					catch(SocketException e){ halt(); continue; }
					catch(EOFException e){ halt(); continue; }
					
					comm = temp.getMessage();
					if(comm == null)comm = "";
					else if(comm.equals("/info"))command = 1;
					else if(comm.equals("/online"))command = 2;
					else if(comm.equals("/whoami"))command = 3;
					else if(comm.equals("/whereami"))command = 4;
					else server_thread.sendMessage(temp, this);
					
					switch(command){
						case 1:{
							my_packet = new MyPacket();
							my_packet.setServerData(true);
							my_packet.setMessage("/whoami - shows my nick name\n/whereami - shows my room\n/online - shows the number of online users");
							oos.writeObject((Object)my_packet);
							oos.flush();
							break;
						}
						case 2:{
							my_packet = new MyPacket();
							my_packet.setServerData(true);
							my_packet.setMessage("Online users: " + server_thread.getUsersNumber());
							oos.writeObject(my_packet);
							oos.flush();
							break;
						}
						case 3:{
							my_packet = new MyPacket();
							my_packet.setServerData(true);
							my_packet.setMessage("Name: " + getNickName());
							oos.writeObject(my_packet);
							oos.flush();
							break;
						}
						case 4:{
							my_packet = new MyPacket();
							my_packet.setServerData(true);
							my_packet.setMessage("Room: " + getChannelName());
							oos.writeObject(my_packet);
							oos.flush();
							break;
						}
					}
				}
				catch(SocketException e){ halt(); }
				catch(SSLException e){ halt(); }
				catch(IOException e){ halt(); }
			}
			
			try{
				program.dsp.asyncExec(new Runnable(){
					public void run(){
						main_window.addMessage("User " + getNickName() + " disconnected from " + sa + ":" + server_thread.getPort(), SWT.COLOR_RED);
						main_window.removeUser(getNickName());
					}
				});
			}
			catch(Exception e){ }
			
		}
		catch(StreamCorruptedException e){ halt(); }
		catch(SSLHandshakeException e){ halt(); }
		catch(SSLException e){ halt(); }
		catch(IOException e){ halt(); }
	}
	
	public void halt(){
		try{
			stop = true;
			is.close();
			os.close();
			if(oos != null)ois.close();
			if(oos != null)oos.close();
			server_thread.removeUser(this);
		}
		catch(SocketException e){ }
		catch(IOException e){ }
	}
	
	public void disconnect(String msg){
		try{
			my_packet = new MyPacket();
			my_packet.setMessage("You are disconnected, reason: " + msg);
			my_packet.setServerData(true);
			oos.writeObject(my_packet);
			oos.flush();
			
			stop = true;
			is.close();
			os.close();
			if(oos != null)ois.close();
			if(oos != null)oos.close();
			server_thread.removeUser(this);
		}
		catch(SocketException e){ }
		catch(IOException e){ }
	}
	
	public void sendMessageToUser(String msg){
		try{
			my_packet = new MyPacket();
			my_packet.setServerData(true);
			my_packet.setMessage("Server: " + msg);
			oos.writeObject(my_packet);
			oos.flush();
		}
		catch(IOException e){ }
	}
}
