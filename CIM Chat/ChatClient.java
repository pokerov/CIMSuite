import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;

public class ChatClient implements Runnable{

	private String host = "localhost";
	private int userPort = 6000;
	private String channelName = "#Default";
	private String nickName = "User";
	private SocketFactory socketFactory;
	private Socket userSocket; 
	private InputStream is;
	private OutputStream os;
	private ObjectOutputStream oos;
	private BufferedReader br;
	private MyPacket my_packet;
	private ClientThread client_thread;
	private MainWindow main_window;
	
	public String getNickName(){
		return nickName;
	}
	
	public void setNickName(String name){
		nickName = name;
	}
	
	public String getChannelName(){
		return channelName;
	}
	
	public void setChannelName(String channel){
		channelName = channel;
	}
	
	public void setPort(String portNumber){
		userPort = Integer.parseInt(portNumber);
	}
	
	public void setHost(String hostName){
		host = hostName;
	}
	
	public ChatClient(String HOST, String PORT, String CHANNEL, String NAME, MainWindow MW){
		main_window = MW;
		host = HOST;
		userPort = Integer.parseInt(PORT);
		channelName = CHANNEL;
		nickName = NAME;
		
		try{
			socketFactory = SSLSocketFactory.getDefault();
			userSocket = socketFactory.createSocket(host, userPort);
			is = userSocket.getInputStream();
			os = userSocket.getOutputStream();
			oos = new ObjectOutputStream(os);
			br = new BufferedReader(new InputStreamReader(System.in));
		}
		catch(IOException e){ halt(); }
		catch(Exception e){ halt(); }
	}
	
	private void sendUserData(){
		try{
			my_packet = new MyPacket();
			my_packet.setServerData(true);
			my_packet.setNickName(nickName);
			my_packet.setChannelName(channelName);
			oos.writeObject(my_packet);
			oos.flush();
		}
		catch(IOException e){ halt(); }
		catch(Exception e){ halt(); }
	}
	
	public void sendMessage(String text){
		try{
			my_packet = new MyPacket();
			my_packet.setNickName(nickName);
			my_packet.setChannelName(channelName);
			my_packet.setServerData(false);
			my_packet.setMessage(text);
			oos.writeObject(my_packet);
			oos.flush();
		}
		catch(IOException e){ }
		catch(Exception e){ }
	}
	
	public void run(){
		client_thread = new ClientThread(is, this, main_window);
		new Thread(client_thread).start();
		sendUserData();
	}
	
	public void halt(){
		try{
			if(oos != null)oos.close();
			if(br != null)br.close();
			if(is != null)is.close();
			if(os != null)os.close();
		}
		catch(IOException e){ }
		catch(Exception e){ }
	}
}
