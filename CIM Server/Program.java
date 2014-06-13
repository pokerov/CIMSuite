import org.eclipse.swt.widgets.*;

public class Program {
	
	public static Display dsp;
	
	public static void main(String[] args){
		dsp = new Display();
		new MainWindow(dsp);
		dsp.dispose();
		System.exit(0);
	}
}
