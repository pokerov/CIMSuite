import java.io.*;
import java.net.*;

@SuppressWarnings("serial")
public class MyPacket implements Serializable{

	private String message, nickName, channelName;
	private InetSocketAddress address;
	private boolean serverData;

	public MyPacket(){
		message = "";
		nickName = "";
		channelName = "";
		serverData = false;
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

	public void setNickName(String name){
		nickName = name;
	}

	public String getChannelName(){
		return channelName;
	}

	public void setChannelName(String ch){
		channelName = ch;
	}

	public InetSocketAddress getAddress(){
		return address;
	}

	public void setAddress(InetSocketAddress sa){
		address = sa;
	}

	public boolean getServerData(){
		return serverData;
	}

	public void setServerData(boolean b){
		serverData = b;
	}
}