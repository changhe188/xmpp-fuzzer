package xmpp_fuzzer;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class AboutFuzzer extends Dialog{

	Object result;
	public AboutFuzzer(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}
	
	public Object open() {
		final Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("����XMPP Fuzzer");
		GridLayout abt_layout = new GridLayout();
		abt_layout.numColumns = 2;
		abt_layout.horizontalSpacing = 15;
		shell.setLayout(abt_layout);
		Image icon = new Image(Display.getCurrent(), "image\\icon.jpg");
		shell.setSize(360,138);
		
		Label blank = new Label(shell,SWT.NONE);
		blank.setImage(icon);
		Label about = new Label(shell, SWT.NONE);
		about.setText("XMPP Fuzzer , V1.0   2009\r\n�̹�С�ֶ�\r\n\r\nUI��   �ƣ�����· \r\nFuzz���ģ�jf lj\r\n�ĵ�׫д��ls");
		
		shell.open();
		final Display display = parent.getDisplay();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
				display.sleep();
		}
		icon.dispose();
		return result;
	}

}
