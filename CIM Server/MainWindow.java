import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import java.util.Vector;
import java.net.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainWindow extends Thread implements SelectionListener{

	private Shell Window;
	private ToolBar MainToolBar;
	private ToolItem iStart, iChangePort, iKick, iInfo, iExit, iAbout, iHelp;
	private StyledText tStatus;
	private List lUsers;
	private Text tCommands;
	private GridLayout gridLayout;
	public Program program;
	private ServerThread server_thread;
	private Calendar cal;
	private String am_pm, curr_time;
	private int hour, minute, second;
		
	public MainWindow(Display dsp){
		Window = new Shell(dsp);
		Window.setSize(700, 480);
		Window.setMinimumSize(700,  480);
		Window.setText("CIM Server");
		
		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		Window.setLayout(gridLayout);
		
		AddToolBar();
		AddInterface();
		
		iStart.setEnabled(true);
		iChangePort.setEnabled(true);
		iKick.setEnabled(false);
		iInfo.setEnabled(false);
		iExit.setEnabled(true);
		tStatus.setEditable(false);
		tCommands.setEnabled(false);
		
		server_thread = new ServerThread(this);
		Window.open();
		while(!Window.isDisposed()){
			if(!dsp.readAndDispatch())dsp.sleep();
		}
	}
	
	private void AddToolBar(){
		try{
			MainToolBar = new ToolBar(Window, SWT.RIGHT | SWT.FLAT);
			
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			gd.horizontalAlignment = SWT.FILL;
			gd.grabExcessHorizontalSpace = true;
			MainToolBar.setLayoutData(gd);
			
			iStart = new ToolItem(MainToolBar, SWT.PUSH);
			iStart.setText("Start");
			iStart.setToolTipText("Start server");
			iStart.setImage(new Image(Window.getDisplay(), "icons/start.png"));
			iStart.addSelectionListener(this);
			
			new ToolItem(MainToolBar, SWT.SEPARATOR);
			
			iChangePort = new ToolItem(MainToolBar, SWT.PUSH);
			iChangePort.setText("Change Port");
			iChangePort.setToolTipText("Change working port");
			iChangePort.setImage(new Image(Window.getDisplay(), "icons/port.png"));
			iChangePort.addSelectionListener(this);
			
			iKick = new ToolItem(MainToolBar, SWT.PUSH);
			iKick.setText("Kick");
			iKick.setToolTipText("Kick selected user");
			iKick.setImage(new Image(Window.getDisplay(), "icons/kick.png"));
			iKick.addSelectionListener(this);
			
			iInfo = new ToolItem(MainToolBar, SWT.PUSH);
			iInfo.setText("Information");
			iInfo.setToolTipText("Show info about selected user");
			iInfo.setImage(new Image(Window.getDisplay(), "icons/user.png"));
			iInfo.addSelectionListener(this);
			
			new ToolItem(MainToolBar, SWT.SEPARATOR);
			
			iHelp = new ToolItem(MainToolBar, SWT.PUSH);
			iHelp.setText("Help");
			iHelp.setToolTipText("Show additional help");
			iHelp.setImage(new Image(Window.getDisplay(), "icons/help.png"));
			iHelp.addSelectionListener(this);
			
			iAbout = new ToolItem(MainToolBar, SWT.PUSH);
			iAbout.setText("About");
			iAbout.setToolTipText("Information about creator");
			iAbout.addSelectionListener(this);
			
			new ToolItem(MainToolBar, SWT.SEPARATOR);
			
			iExit = new ToolItem(MainToolBar, SWT.PUSH);
			iExit.setText("Exit");
			iExit.setToolTipText("Exit application");
			iExit.setImage(new Image(Window.getDisplay(), "icons/exit.png"));
			iExit.addSelectionListener(this);
			
			MainToolBar.pack();
		}
		catch(Exception e){ }
	}
	
	private void AddInterface(){
		tStatus = new StyledText(Window, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		GridData gd0 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd0.minimumWidth = 500;
		gd0.widthHint = 500;
		tStatus.setLayoutData(gd0);
		
		lUsers = new List(Window, SWT.BORDER | SWT.V_SCROLL);
		GridData gd1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd1.minimumWidth = 120;
		gd1.widthHint = 120;
		lUsers.setLayoutData(gd1);
				
		tCommands = new Text(Window, SWT.BORDER | SWT.SEARCH);
		GridData gd2 = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd2.horizontalSpan = 2;
		tCommands.setLayoutData(gd2);
		tCommands.addSelectionListener(this);
	}
	
	public void widgetDefaultSelected(SelectionEvent ev){
		Object e = ev.getSource();
		if(e == tCommands){
			String[] on_users = lUsers.getItems();
			Vector<String> online_users = new Vector<String>();
			for(int i=0; i<on_users.length; i++)online_users.add(on_users[i]);
			try{
				if(!online_users.isEmpty()){
					server_thread.getClientThread(lUsers.getSelectionIndex()).sendMessageToUser(tCommands.getText());
					tCommands.setText("");
					addMessage("Message to user " + server_thread.getClientThread(lUsers.getSelectionIndex()).getNickName() + " was sent.", SWT.COLOR_BLACK);
				}
				else {
					MessageBox msg = new MessageBox(Window, SWT.ICON_WARNING | SWT.OK);
					msg.setMessage("There are no users online.");
					msg.setText("Information");
					msg.open();
					tCommands.setText("");
				}
			}
			catch(ArrayIndexOutOfBoundsException e1){ 
				addMessage("Choose a user who you need to contact.", SWT.COLOR_BLACK);
				tCommands.setText("");
			}
			catch(Exception e2){ }
		}
	}
	public void widgetSelected(SelectionEvent ev){
		Object e = ev.getSource();
		if (e == iChangePort){			
			final Shell port_window = new Shell(Window, SWT.DIALOG_TRIM);
			port_window.setText("Choose port");
			
			GridLayout gridLayout = new GridLayout(2, false);
			gridLayout.marginHeight = 10;
			gridLayout.marginWidth = 10;
			gridLayout.horizontalSpacing = 10;
			gridLayout.verticalSpacing = 10;
			port_window.setLayout(gridLayout);
			
			final Label lInfo = new Label(port_window, SWT.NONE);
			lInfo.setText("Port: ");
			GridData gd0 = new GridData();
			gd0.minimumWidth = 50;
			gd0.minimumHeight = 20;
			gd0.horizontalAlignment = SWT.RIGHT;
			gd0.verticalAlignment = SWT.CENTER;
			lInfo.setLayoutData(gd0);
			
			final Spinner sPort = new Spinner(port_window, SWT.BORDER);
			sPort.setMinimum(3000);
			sPort.setMaximum(7000);
			sPort.setSelection(6000);
			GridData gd1 = new GridData();
			gd1.horizontalAlignment = SWT.FILL;
			gd1.verticalAlignment = SWT.FILL;
			sPort.setLayoutData(gd1);
			
			final Button bSet = new Button(port_window, SWT.PUSH);
			bSet.setText("Set");
			bSet.setToolTipText("Choose working port");
			GridData gd2 = new GridData();
			gd2.minimumHeight = 20;
			gd2.minimumWidth = 100;
			gd2.horizontalSpan = 2;
			gd2.verticalAlignment = SWT.CENTER;
			gd2.horizontalAlignment = SWT.RIGHT;
			bSet.setLayoutData(gd2);
			bSet.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					try{
						server_thread.setPort(sPort.getSelection());
					}
					catch(Exception e1){
						MessageBox msg = new MessageBox(port_window, SWT.ICON_WARNING | SWT.OK);
						msg.setMessage("Error occurred: " + e1.getMessage());
						msg.setText("Error");
						msg.open();
						server_thread.setPort(6000);
					}
					port_window.dispose();
				}
			});
			
			sPort.addListener(SWT.Modify, new Listener(){
				public void handleEvent(Event e){
					if(sPort.getSelection() < 3000)sPort.setSelection(3000);
					else if (sPort.getSelection() > 7000)sPort.setSelection(7000);
				}
			});
			
			port_window.pack();
			port_window.open();
		}
		else if (e == iExit){
			System.exit(0);
		}
		else if (e == iStart){
			InetAddress localAddress;
			try{
				localAddress = InetAddress.getLocalHost();
				addMessage("Server is running", SWT.COLOR_BLACK);
				addMessage("Address for local users: " + localAddress.getHostAddress() + ":" + server_thread.getPort(), SWT.COLOR_BLACK);
				iStart.setEnabled(false);
				iChangePort.setEnabled(false);
				iKick.setEnabled(true);
				iInfo.setEnabled(true);
				tCommands.setEnabled(true);
				server_thread.start();
			}
			catch(IllegalThreadStateException e0){ }
			catch(UnknownHostException e1){ }
			catch(Exception e4){ }
		}
		else if (e == iKick){
			String[] arr_avusers = lUsers.getItems();
			Vector<String> vec_avusers = new Vector<String>();
			for(int j=0; j<arr_avusers.length; j++)vec_avusers.add(arr_avusers[j]);
			try{
				if(vec_avusers.isEmpty()){
					MessageBox msg = new MessageBox(Window, SWT.ICON_WARNING | SWT.OK);
					msg.setMessage("There are no users online.");
					msg.setText("Information");
					msg.open();
				}
				else if(lUsers.getSelectionCount() != 0){
					final Shell kick_window = new Shell(Window, SWT.DIALOG_TRIM);
					kick_window.setText("Reason");
					kick_window.setLayout(new GridLayout(2, true));
					
					final Label label = new Label(kick_window, SWT.NONE);
					label.setText("Reason: ");
					
					final Text tReason = new Text(kick_window, SWT.BORDER | SWT.SINGLE);
					tReason.setText("");
					
					final Button bOK = new Button(kick_window, SWT.PUSH);
					bOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
					bOK.setText("OK");
					
					final Button bCancel = new Button(kick_window, SWT.PUSH);
					bCancel.setText("Cancel");
					
					bOK.addListener(SWT.Selection, new Listener(){
						public void handleEvent(Event e){
							try{
								String[] arr_users = lUsers.getSelection();
								for(int i=0; i<arr_users.length; i++)addMessage("User " + arr_users[i] + " was kicked from the server. Reason: " + tReason.getText(), SWT.COLOR_RED);
								server_thread.getClientThread(lUsers.getSelectionIndex()).disconnect(tReason.getText());
							}
							catch(Exception e1) { }
							kick_window.dispose();
						}
					});
					
					bCancel.addListener(SWT.Selection, new Listener(){
						public void handleEvent(Event e){
							kick_window.dispose();
						}
					});
					
					tReason.addListener(SWT.DefaultSelection, new Listener(){
						public void handleEvent(Event e){
							try{
								String[] arr_users = lUsers.getSelection();
								for(int i=0; i<arr_users.length; i++)addMessage("User " + arr_users[i] + " was kicked from the server. Reason: " + tReason.getText(), SWT.COLOR_RED); //shows the name of the kicked user
								server_thread.getClientThread(lUsers.getSelectionIndex()).disconnect(tReason.getText());
							}
							catch(Exception e1) { }
							kick_window.dispose();
						}
					});
					
					kick_window.pack();
					kick_window.open();
				}
				else{
					MessageBox msg = new MessageBox(Window, SWT.ICON_WARNING | SWT.OK);
					msg.setMessage("Please select user from the list.");
					msg.setText("Information");
					msg.open();
				}
			}
			catch(ArrayIndexOutOfBoundsException e1){ addMessage("Choose user from the list to kick.", SWT.COLOR_BLACK); }
			catch(Exception ev1){ }
		}
		else if (e == iInfo){
			String[] arr_avusers = lUsers.getItems();
			Vector<String> vec_avusers = new Vector<String>();
			for(int j=0; j<arr_avusers.length; j++)vec_avusers.add(arr_avusers[j]);
			try{
				if(vec_avusers.isEmpty()){
					MessageBox msg = new MessageBox(Window, SWT.ICON_WARNING | SWT.OK);
					msg.setMessage("There are no users online.");
					msg.setText("Information");
					msg.open();
				}
				else{
					addMessage("User " + server_thread.getClientThread(lUsers.getSelectionIndex()).getNickName() + " chats in room " + server_thread.getClientThread(lUsers.getSelectionIndex()).getChannelName() + ".", SWT.COLOR_BLACK);
				}
			}
			catch(ArrayIndexOutOfBoundsException e1){ addMessage("Choose user from the list to get more information about him.", SWT.COLOR_BLACK); } //this shows when info button is pressed without selected user from lUsers
			catch(Exception ev1){ }
		}
		else if (e == iHelp){
			MessageBox msg = new MessageBox(Window, SWT.ICON_INFORMATION | SWT.OK);
			msg.setMessage("You can find more help in the folder where the program is installed. There is a file called 'Help' which you can read for more information.");
			msg.setText("Information");
			msg.open();
		}
		else if (e == iAbout){
			MessageBox msg = new MessageBox(Window, SWT.NONE);
			msg.setMessage("CIM Server, version 1.0.0\n(c) Kristiyan Dimov, 2012");
			msg.setText("About");
			msg.open();
		}
	}
	
	@SuppressWarnings("static-access")
	public void addMessage(String msg, int color){
		cal = new GregorianCalendar();
		
		hour = cal.get(Calendar.HOUR);
		minute = cal.get(Calendar.MINUTE);
		second = cal.get(Calendar.SECOND);
		
		if(cal.get(Calendar.AM_PM) == 0)am_pm = "AM";
		else am_pm = "PM";
		
		if(hour == 0)hour = 12;
		curr_time = "[" + hour + ":" + minute + ":" + second + " " + am_pm + "] ";
		
		int ptext = tStatus.getCharCount();
		StyleRange range = new StyleRange();
		if(tStatus.getCharCount() == 0)tStatus.append(curr_time + msg);
		else tStatus.append("\r" + curr_time + msg);
		range.start = ptext;
		range.length = tStatus.getCharCount() - ptext;
		range.foreground = program.dsp.getSystemColor(color);
		tStatus.setStyleRange(range);
		tStatus.setSelection(tStatus.getCharCount());
	}
	
	public void addUser(String s){
		lUsers.add(s);
	}
	
	public void removeUser(String s){
		lUsers.remove(s);
	}
	
	public MainWindow returnInstance(){
		return this;
	}
	
	@SuppressWarnings("unused")
	private void demoMode(){
		addMessage("User Chris connected from: 192.168.0.105:6000", SWT.COLOR_BLACK);
		addUser("Chris");
		addMessage("User Chris chats in room #Technology", SWT.COLOR_BLACK);
		addMessage("User Peter connected from: 192.168.0.108:6000", SWT.COLOR_BLACK);
		addUser("Peter");
		addMessage("User Jack connected from: 192.168.0.127:6000", SWT.COLOR_BLACK);
		addMessage("Message to user Jack was sent.", SWT.COLOR_BLACK);
		addUser("Jack");
		addMessage("User John connected from: 192.168.0.115:6000", SWT.COLOR_BLACK);
		addUser("John");
		addMessage("User Christen connected from: 192.168.0.112:6000", SWT.COLOR_BLACK);
		addUser("Christen");
		addMessage("User Christen was kicked from the server.", SWT.COLOR_RED);
		addMessage("User Christen disconnected from: 192.168.0.112:6000", SWT.COLOR_RED);
		removeUser("Christen");
		addMessage("User Louis connected from: 192.168.0.113:6000", SWT.COLOR_BLACK);
		addUser("Louis");
		addMessage("User Steeve connected from: 192.168.0.120:6000", SWT.COLOR_BLACK);
		addUser("Steeve");
		addMessage("User Jack disconnected from: 129.168.0.127:6000", SWT.COLOR_RED);
		removeUser("Jack");
		addMessage("User Kevin connected from: 192.168.0.121:6000", SWT.COLOR_BLACK);
		addUser("Kevin");
		addMessage("User Mark Connected from: 192.168.0.130:6000", SWT.COLOR_BLACK);
		addUser("Mark");
	}
}
