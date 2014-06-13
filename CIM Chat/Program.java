import org.eclipse.swt.widgets.*;

public class Program {

	private static Display dsp;
	
	public static void main(String[] args){
		dsp = new Display();
		new LoginWindow(dsp);
		exitApp();
	}
	
	public static void exitApp(){
		dsp.dispose();
		System.exit(0);
	}
	
	public static Display returnDisplay(){
		return dsp;
	}
}
