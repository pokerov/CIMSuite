import java.io.*;

import org.eclipse.swt.SWT;

public class ClientThread implements Runnable{

	private ObjectInputStream ois;
	private volatile boolean stop = false;
	private MyPacket my_packet;
	private ChatClient chat_client;
	private MainWindow main_window;
	private Program program;
	
	public ClientThread(InputStream input, ChatClient CC, MainWindow MW){
		chat_client = CC;
		main_window = MW;
		try{
			ois = new ObjectInputStream(input);
		}
		catch(IOException e){ System.exit(1); }
		catch(Exception e){ System.exit(1); }
	}
	
	@SuppressWarnings("static-access")
	public void run(){
		while(!stop){
			try{
				my_packet = (MyPacket)ois.readObject();
			}
			catch(IOException e){ halt(); return; }
			catch(ClassNotFoundException e){ halt(); return; }
			catch(Exception e){ halt(); return; }
			
			if(my_packet == null)halt();
			else {
				if(my_packet.getServerData()){
					program.returnDisplay().asyncExec(new Runnable(){
						public void run(){
							main_window.addMessage(my_packet.getMessage(), SWT.COLOR_RED);
						}
					});
				}
				else {
					program.returnDisplay().asyncExec(new Runnable(){
						public void run(){
							if(chat_client.getChannelName().equals(my_packet.getChannelName()))main_window.addMessage(my_packet.getNickName() + ": " + my_packet.getMessage(), SWT.COLOR_WHITE);
						}
					});
				}
			}
		}
	}
	
	public void halt(){
		stop = true;
		try{
			if(ois != null)ois.close();
		}
		catch(IOException e){ }
		catch(Exception e){ }
	}
}
