import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import java.util.*;

public class MainWindow implements SelectionListener{

	private Shell main_window;
	private Monitor monitor;
	private StyledText tMessages;
	private Text tCommands;
	private Program program;
	private LoginWindow login_window;
	private ChatClient chat_client;
	private String defName;
	
	@SuppressWarnings("static-access")
	public MainWindow(LoginWindow LW){
		login_window = LW;
		main_window = new Shell(program.returnDisplay(), SWT.MIN | SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.SYSTEM_MODAL);	
		monitor = program.returnDisplay().getPrimaryMonitor();
		
		Calendar cal = new GregorianCalendar();
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		int mseconds = cal.get(Calendar.MILLISECOND);
		defName = "User_" + hour + minute + seconds + mseconds;
		
		try{
			if(login_window.goToDefaultRoom()){
				chat_client = new ChatClient("localhost", "6000", "#Default", defName, this);
				new Thread(chat_client).start();
			}
			else if(!login_window.goToDefaultRoom()){
				chat_client = new ChatClient(login_window.getAddress(), login_window.getPort(), login_window.getChannel(), login_window.getName(), this);
				new Thread(chat_client).start();
			}
		}
		catch(Exception e) { }
		
		InitializeComponents();
		main_window.pack();
		main_window.setLocation((monitor.getBounds().width - main_window.getBounds().width)/2, (monitor.getBounds().height - main_window.getBounds().height)/2);
		main_window.open();
	}

	@SuppressWarnings("static-access")
	private void InitializeComponents(){
		if(login_window.goToDefaultRoom())main_window.setText("CIM Chat - " + defName);
		else main_window.setText("CIM Chat - " + login_window.getName());
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		main_window.setLayout(gl);
		main_window.setMinimumSize(300, 500);
		main_window.addShellListener(new ShellListener(){
			public void shellActivated(ShellEvent e){}
			public void shellDeactivated(ShellEvent e){}
			public void shellClosed(ShellEvent e){
				program.exitApp();
			}
			public void shellDeiconified(ShellEvent e) {}
			public void shellIconified(ShellEvent e) {}
		});
		
		tMessages = new StyledText(main_window, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		GridData gd1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd1.minimumHeight = 400;
		gd1.minimumWidth = 200;
		gd1.heightHint = 400;
		gd1.widthHint = 200;
		tMessages.setLayoutData(gd1);
		
		tCommands = new Text(main_window, SWT.BORDER);
		GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		tCommands.setLayoutData(gd2);
		tCommands.addSelectionListener(this);
		tCommands.setFocus();
	}
	
	@SuppressWarnings("static-access")
	public void addMessage(String message, int colour){
		int ptext = tMessages.getCharCount();
		StyleRange range = new StyleRange();
		if(tMessages.getCharCount() == 0)tMessages.append(message);
		else tMessages.append("\r" + message);
		range.start = ptext;
		range.length = tMessages.getCharCount() - ptext;
		range.background = program.returnDisplay().getSystemColor(colour);
		tMessages.setStyleRange(range);
		tMessages.setSelection(tMessages.getCharCount());
	}
	
	public void widgetDefaultSelected(SelectionEvent ev){
		Object e = ev.getSource();
		if(e == tCommands){
			chat_client.sendMessage(tCommands.getText());
			tCommands.setText("");
		}
	}
	public void widgetSelected(SelectionEvent ev){}
}
