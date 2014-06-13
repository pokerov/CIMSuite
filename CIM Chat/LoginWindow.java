import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class LoginWindow implements SelectionListener{

	public Shell login_window;
	private Label lAddress, lPort, lName, lChannel;
	private Text tAddress, tName;
	private Button bLogin, bDefault;
	private Monitor monitor;
	private boolean def;
	private Spinner sPort;
	private Combo cChannel;
	
	public LoginWindow(Display dsp){
		login_window = new Shell(dsp, SWT.TITLE | SWT.CLOSE);
		monitor = dsp.getPrimaryMonitor();
		
		InitializeComponents();
				
		login_window.pack();
		login_window.setLocation((monitor.getBounds().width - login_window.getBounds().width) / 2, (monitor.getBounds().height - login_window.getBounds().height) / 2);
		login_window.open();
		while(!login_window.isDisposed())if(!dsp.readAndDispatch())dsp.sleep();
	}
	
	private void InitializeComponents(){
		login_window.setText("Credentials");
		
		GridLayout gl = new GridLayout(3, true);
		login_window.setLayout(gl);
		
		lAddress = new Label(login_window, SWT.NONE);
		lAddress.setText("Address:");
		lAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		tAddress = new Text(login_window, SWT.BORDER);
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = SWT.FILL;
		gd1.verticalAlignment = SWT.CENTER;
		gd1.minimumWidth = 100;
		gd1.widthHint = 100;
		gd1.horizontalSpan = 2;
		tAddress.setLayoutData(gd1);
		tAddress.setText("127.0.0.1");
		tAddress.addListener(SWT.Modify, new Listener(){
			public void handleEvent(Event ev){
				char[] chars = tAddress.getText().toCharArray();
				try{
					if((tAddress.getText().length() > 15) || (chars[3] != '.')){
						MessageBox msg = new MessageBox(login_window, SWT.ICON_WARNING | SWT.OK);
						msg.setMessage("You can use only IP address in the following format: XXX.XXX.XXX.XXX");
						msg.setText("Error");
						msg.open();
						tAddress.setText("");
					}
				}
				catch(Exception e){}
			}
		});
		
		lPort = new Label(login_window, SWT.NONE);
		lPort.setText("Port:");
		lPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		sPort = new Spinner(login_window, SWT.BORDER);
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = SWT.FILL;
		gd2.verticalAlignment = SWT.CENTER;
		gd2.horizontalSpan = 2;
		sPort.setLayoutData(gd2);
		sPort.setMinimum(3000);
		sPort.setMaximum(7000);
		sPort.setSelection(6000);
		sPort.addListener(SWT.Modify, new Listener(){
			public void handleEvent(Event ev){
				if(sPort.getSelection() < 3000 || sPort.getSelection() > 7000){
					MessageBox msg = new MessageBox(login_window, SWT.ICON_WARNING | SWT.OK);
					msg.setMessage("Please choose port between 3000 and 7000. Others are not allowed.");
					msg.setText("Error");
					msg.open();
					sPort.setSelection(6000);
				}
			}
		});
		
		lName = new Label(login_window, SWT.NONE);
		lName.setText("Name:");
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		tName = new Text(login_window, SWT.BORDER);
		GridData gd3 = new GridData();
		gd3.horizontalAlignment = SWT.FILL;
		gd3.verticalAlignment = SWT.CENTER;
		gd3.minimumWidth = 100;
		gd3.widthHint = 100;
		gd3.horizontalSpan = 2;
		tName.setLayoutData(gd3);
		tName.setText("User");
		tName.addListener(SWT.Modify, new Listener(){
			public void handleEvent(Event ev){
				if(tName.getText().equals("Admin"))tName.setText("");
				else if(tName.getText().equals("@"))tName.setText("");
			}
		});
		
		lChannel = new Label(login_window, SWT.NONE);
		lChannel.setText("Channel:");
		lChannel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		cChannel = new Combo(login_window, SWT.DROP_DOWN);
		cChannel.setItems(new String[]{"#Technology", "#Games", "#Movies", "#Music", "#Entertainment", "#Social"});
		GridData gd4 = new GridData();
		gd4.horizontalAlignment = SWT.FILL;
		gd4.verticalAlignment = SWT.CENTER;
		gd4.minimumWidth = 100;
		gd4.widthHint = 100;
		gd4.horizontalSpan = 2;
		cChannel.setLayoutData(gd4);
		cChannel.setText("#Default");
		cChannel.addListener(SWT.Modify, new Listener(){
			public void handleEvent(Event ev){
				if(!cChannel.getText().startsWith("#")){
					MessageBox msg = new MessageBox(login_window, SWT.ICON_WARNING | SWT.OK);
					msg.setMessage("Every channel should start with \"#\" symbol, otherwise is not channel!");
					msg.setText("Error");
					msg.open();
					cChannel.setText("#Default");
				}
			}
		});
		
		bDefault = new Button(login_window, SWT.PUSH);
		bDefault.setText("Default");
		bDefault.setToolTipText("Log in to default chat room");
		bDefault.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		bDefault.addSelectionListener(this);
		
		bLogin = new Button(login_window, SWT.PUSH);
		bLogin.setText("Log In");
		bLogin.setToolTipText("Log in to the server with given address");
		GridData gd5 = new GridData();
		gd5.horizontalAlignment = SWT.RIGHT;
		gd5.verticalAlignment = SWT.CENTER;
		gd5.horizontalSpan = 2;
		bLogin.setLayoutData(gd5);
		bLogin.addSelectionListener(this);
		
		login_window.addListener(SWT.Traverse, new Listener(){
			public void handleEvent(Event ev){
				if(ev.detail == SWT.TRAVERSE_ESCAPE)login_window.dispose();
			}
		});
	}
	
	public void widgetDefaultSelected(SelectionEvent ev){}
	public void widgetSelected(SelectionEvent ev){
		Object e = ev.getSource();
		if(e == bLogin){
			if(tAddress.getCharCount() == 0){
				MessageBox msg = new MessageBox(login_window, SWT.ICON_WARNING | SWT.OK);
				msg.setMessage("There is no address typed. You can use only IP address in the following format: XXX.XXX.XXX.XXX");
				msg.setText("Error");
				msg.open();
			}
			else if(tName.getCharCount() == 0){
				MessageBox msg = new MessageBox(login_window, SWT.ICON_WARNING | SWT.OK);
				msg.setMessage("Please enter your name.");
				msg.setText("Error");
				msg.open();
			}
			else {
				login_window.setVisible(false);
				setGoToDefaultRoom(false);
				new MainWindow(this);
			}
		}
		else if(e == bDefault){
			login_window.setVisible(false);
			setGoToDefaultRoom(true);
			new MainWindow(this);
		}
	}
	
	public String getAddress(){ return tAddress.getText(); }
	public String getPort(){ return sPort.getText(); }
	public String getChannel(){ return cChannel.getText(); }
	public String getName(){ return tName.getText(); }
	public boolean goToDefaultRoom(){ return def; }
	private void setGoToDefaultRoom(boolean n){ def = n; }
}
