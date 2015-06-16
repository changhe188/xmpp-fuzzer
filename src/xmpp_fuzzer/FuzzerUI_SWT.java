package xmpp_fuzzer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import org.jivesoftware.smack.*;

public class FuzzerUI_SWT {
	
	private Connection conn;
	private static Text readArea;
	private Text writeArea;
	private Combo buddyList;
	private Combo fuzzList;
	private Button messageButton;
	private Button iqButton;
	private Button presenceButton;
	private Text fuzzStr;
	private Label light;
	private Label tips;
	
	FuzzerUI_SWT() throws SWTException{
		final Display display = new Display();
		Color white = display.getSystemColor(SWT.COLOR_WHITE);	
        final Image yellow = new Image(Display.getCurrent(), "resource\\image\\red1.jpg");//��ʾ����״̬��ͼ��
        final Image green = new Image(Display.getCurrent(), "resource\\image\\green1.jpg");
        final Image conGraphic = new Image(Display.getCurrent(), "resource\\image\\conGraphic.jpg");
        
		final Shell shell = new Shell(display, SWT.MIN | SWT.CLOSE );//��ֹ���
		shell.setLayout(new GridLayout());
		shell.setText("XMPP Fuzzer");
		Image icon = new Image(Display.getCurrent(), "resource\\image\\ico.png");
        shell.setImage(icon);//�������ͼ�� 

		createMenu(shell);	    
		TabFolder folder = new TabFolder(shell, SWT.NONE);

		/******�������ӱ�ǩ******/
		Composite connect = createTabPage(folder, "��������");
		GridLayout con_layout = new GridLayout();
		con_layout.numColumns = 1;
		con_layout.verticalSpacing = 5;
		connect.setLayout(con_layout);
		
		Composite line0 = new Composite(connect, SWT.NONE);
		line0.setLayout(new RowLayout());
		Label blankline = new Label(line0,SWT.NONE);
		blankline.setVisible(false);
		
		Composite line1 = new Composite(connect, SWT.NONE);
		line1.setLayout(new RowLayout());
		Label serverLabel = new Label(line1,SWT.NONE);
		serverLabel.setText("������:");
		serverLabel.setLayoutData(new RowData(40,15));
		final Text serverArea = new Text(line1, SWT.SINGLE | SWT.BORDER); 
		serverArea.setBackground(white);
		serverArea.setLayoutData(new RowData(160,15));
		
		Composite line2 = new Composite(connect, SWT.NONE);
		line2.setLayout(new RowLayout());
		Label portLabel = new Label(line2,SWT.NONE);
		portLabel.setText("��   ��:");
		portLabel.setLayoutData(new RowData(40,15));
		final Text portArea = new Text(line2, SWT.SINGLE | SWT.BORDER); 
		portArea.setBackground(white);
		portArea.setLayoutData(new RowData(160,15));
		
		Composite line3 = new Composite(connect, SWT.NONE);
		line3.setLayout(new RowLayout());
		Label userLabel = new Label(line3,SWT.NONE);
		userLabel.setText("�û���:");
		userLabel.setLayoutData(new RowData(40,15));
		final Text userArea = new Text(line3, SWT.SINGLE | SWT.BORDER); 
		userArea.setBackground(white);
		userArea.setLayoutData(new RowData(160,15));

		Composite line4 = new Composite(connect, SWT.NONE);
		line4.setLayout(new RowLayout());
		Label passLabel = new Label(line4,SWT.NONE);
		passLabel.setText("��   ��:");
		passLabel.setLayoutData(new RowData(40,15));
		final Text passArea = new Text(line4, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD); 
		passArea.setBackground(white);
		passArea.setLayoutData(new RowData(160,15));
		
		Composite line5 = new Composite(connect, SWT.NONE);
		line5.setLayout(new RowLayout());
		Composite blank = new Composite(line5, SWT.NONE);//ռλ��
		blank.setVisible(false);
        final Button okButton = new Button(line5, SWT.PUSH | SWT.CENTER | SWT.FLAT);
		okButton.setText("����");
		okButton.setLayoutData(new RowData(80,25));
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(okButton.getText().equals("����")){		
					String username = userArea.getText();
					String password = passArea.getText();
					String server = serverArea.getText();
					String port = portArea.getText();
					try {					
						conn = new Connection(server, Integer.parseInt(port), username, password);
					} catch (Exception input) {
						readArea.append("[ϵͳ]���ӽ���ʧ�ܣ�����������Ϣ��\n");
						return;
					}

					try {
						int result = conn.XMPPConnect();

						if(result == 0){
							okButton.setText("�Ͽ�");//��ť��ʾ�ı仯��ζ�������Ƿ�ɹ�����
							userArea.setEnabled(false);
							serverArea.setEnabled(false);
							passArea.setEnabled(false);
							portArea.setEnabled(false);
							light.setImage(green);
							tips.setText(" �ѽ�����" + serverArea.getText() + "������");

							for(RosterEntry entry : conn.GetRoster()){
								buddyList.add(entry.getUser());
							}
							readArea.append("[ϵͳ]���ӽ����ɹ���\n");
							readArea.append("ʹ��˵��:\n ������Fuzz���ý���ѡ��һ������,֮��������Ҫ��Fuzz������\n");//����������������ʾʹ��˵��
							readArea.append("�·���������Ϊ��ʱ��������Ͱ�ť�������úõ�Fuzz������Ϊ��ʱ���������������ݷ��͡�\n\n");
						}
						else
							readArea.append("[ϵͳ]���ӽ���ʧ�ܣ�����������Ϣ�������״̬��\n");
					} catch (XMPPException e1) {
						// TODO Auto-generated catch block
						readArea.append("[ϵͳ]���ӽ�������\n");
						return;
					}
				}
				//�Ѿ���������ʱ������ť��Ͽ�����
				else{
					conn.DisConnect();
					okButton.setText("����");
					light.setImage(yellow);
					tips.setText(" ��δ����");
					userArea.setEnabled(true);
					serverArea.setEnabled(true);
					passArea.setEnabled(true);
					portArea.setEnabled(true);
					buddyList.removeAll();
					buddyList.setText("ѡ��һ��������ΪFuzz����");
					readArea.append("[ϵͳ]���ӶϿ���\n");
					return;
				}	
			}
		});

		Composite line6 = new Composite(connect, SWT.NONE);
		line6.setLayout(new RowLayout());
		Composite blank2 = new Composite(line6, SWT.NONE);//ռλ��
		blank2.setVisible(false);
		blank2.setLayoutData(new RowData(42, 42));
		Label conGra = new Label(line6, SWT.NONE);
		conGra.setImage(conGraphic);
		
		
		/******���Ӵ��ڱ�ǩ******/
		Composite window = createTabPage(folder, "���Ӵ���");
        GridLayout win_layout = new GridLayout();
        win_layout.numColumns = 1;
        win_layout.horizontalSpacing = 5;
        window.setLayout(win_layout);
      	
        Composite panel0 = new Composite(window, SWT.NONE);
        panel0.setLayout(new RowLayout());
        light = new Label(panel0 ,SWT.NONE);
        light.setImage(yellow);
        tips = new Label(panel0, SWT.NONE);
        tips.setText(" ��δ����");
        tips.setLayoutData(new RowData(185, 25));
      
        Composite panel1 = new Composite(window, SWT.BORDER);
        readArea = new Text(panel1, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		readArea.setSize(220, 300);
				
		Composite panel2 = new Composite(window, SWT.NONE);
		RowLayout layout2 = new RowLayout();
		layout2.spacing = 8;
		panel2.setLayout(layout2);
		
		Composite panel2_1 = new Composite(panel2, SWT.BORDER);
		writeArea = new Text(panel2_1, SWT.SINGLE | SWT.WRAP); 
		writeArea.setSize(150, 25);
		
		Composite panel2_2 = new Composite(panel2, SWT.NONE);
		final Button sendButton = new Button(panel2_2, SWT.PUSH | SWT.FLAT);
		sendButton.setText("����");
		sendButton.setSize(50, 25);
		sendButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				//�ж��Ƿ������ӵ�������
				if(sendButton.getText().equals("����")){
					if(okButton.getText().equals("����")){
						readArea.append("[ϵͳ]�����Ƚ���һ�����ӣ�\n");
						return;
					}
					if(buddyList.getText().equals("ѡ��һ��������ΪFuzz����")) {
						readArea.append("[ϵͳ]�������ں����б���ѡ��һ�����ѣ�\n");
						return;
					}
					conn.MakeChat(buddyList.getItem(buddyList.getSelectionIndex()));
					//����writeArea�Ƿ�Ϊ���ж��Ƿ����������ݻ���fuzz����
					if(writeArea.getText().equals("")){//Ϊ��ʱ����fuzz���þ������������͵İ�
						if(fuzzList.getText().equals("ѡ��һ�ֲ��Է���")){
							readArea.append("[ϵͳ]ѡ��һ�ֲ��Է���\n");
							return;
						}
						if(!messageButton.getSelection() && !iqButton.getSelection() && !presenceButton.getSelection()) {
							readArea.append("[ϵͳ]ѡ��һ�ֻ�������ݰ�����\n");
							return;
						}
						//��Ӧ���ֲ��Է����ı�ǩ��1Ϊ�Ŵ��㷨��2Ϊ��ȫ���
						conn.setFlag(fuzzList.getSelectionIndex()+1);
						
						if((fuzzList.getSelectionIndex()+1) == 1 && fuzzStr.getText().equals("")){
							readArea.append("[ϵͳ]ʹ���Ŵ��㷨ʱ����������һ������ַ���\n");
							return;
						}
						else
							conn.setSeed(fuzzStr.getText());						
						sendButton.setText("ֹͣ");
						readArea.append("[ϵͳ]Fuzz������" + nowTime() + "��ʼ\n");
						readArea.append("Fuzz������\n" + fuzzList.getText() + "\n");
						readArea.append("Fuzz���ݰ�����\n");
						if (messageButton.getSelection()) {
							conn.setMessageFlag(1);
							readArea.append("Message��\n");
						}	
						else	
							conn.setMessageFlag(0);
						if (iqButton.getSelection()) {
							conn.setIQFlag(1);
							readArea.append("IQ��\n");
						}
						else	
							conn.setIQFlag(0);
						if (presenceButton.getSelection()) {
							conn.setPresenceFlag(1);
							readArea.append("Presence��\n");
						}
						else	
							conn.setPresenceFlag(0);
					}
					else {//��Ϊ��ʱ������������		
						int result = conn.SentMsg(buddyList.getItem(buddyList.getSelectionIndex()), writeArea.getText());
						if(result == 1){
							readArea.append("[ϵͳ]������Ϣʧ�ܣ�\n");
							return;
						}
						else {
							readArea.append(nowTime() + " " + writeArea.getText()+ "\n");
							writeArea.setText("");
						}										
					}
				}
				else{
					sendButton.setText("����");
					conn.setFlag(0);
				}
			}	
		});
		
		/******fuzz���ñ�ǩ******/
		Composite option = createTabPage(folder, "Fuzz����");
		GridLayout op_layout = new GridLayout();
		op_layout.numColumns = 1;
		op_layout.verticalSpacing = 5;
		option.setLayout(op_layout);
		
		Composite op0 = new Composite(option, SWT.NONE);
        op0.setLayout(new RowLayout());
        Label chooseFriend = new Label(op0, SWT.NONE);
        chooseFriend.setText("��ǰ�˺ŵĺ����б�");
		
        Composite op1 = new Composite(option, SWT.NONE);
        op1.setLayout(new RowLayout());
		buddyList = new Combo(op1, SWT.DROP_DOWN);
		buddyList.setText("ѡ��һ��������ΪFuzz����");
		buddyList.setLayoutData(new RowData(180,15));
		
		Composite op2 = new Composite(option, SWT.NONE);
		op2.setLayout(new RowLayout());
        Label fuzzOption = new Label(op2, SWT.NONE);
        fuzzOption.setText("Fuzz����ѡ��");
        
        Composite op3 = new Composite(option, SWT.NONE);
        op3.setLayout(new RowLayout());
		fuzzList = new Combo(op3, SWT.DROP_DOWN);
		fuzzList.setText("ѡ��һ�ֲ��Է���");
		fuzzList.add("�Ŵ��㷨�������");
		fuzzList.add("��ȫ�������");
		fuzzList.setLayoutData(new RowData(180,15));
		
		Composite op6 = new Composite(option, SWT.NONE);
		op6.setLayout(new RowLayout());
        Label fuzzString = new Label(op6, SWT.NONE);
        fuzzString.setText("Fuzz����滻����ֵ��");
        
		Composite op7 = new Composite(option, SWT.NONE);
		op7.setLayout(new RowLayout());
		fuzzStr = new Text(op7, SWT.BORDER);
		fuzzStr.setLayoutData(new RowData(130,20));
		Button generateButton = new Button(op7, SWT.PUSH | SWT.FLAT);
		generateButton.setText("�Զ�����");
		generateButton.setSize(50, 25);
        generateButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String seed = new String(getRandomString(6));				
				fuzzStr.setText(seed);
			}
        });
		Composite op4 = new Composite(option, SWT.NONE);
		op4.setLayout(new RowLayout());
        Label fuzzObject = new Label(op4, SWT.NONE);
        fuzzObject.setText("Fuzz���ݰ�����");
        
		Composite op5 = new Composite(option ,SWT.NONE);
		op5.setLayout(new RowLayout());
		messageButton = new Button(op5, SWT.CHECK);
		messageButton.setText("Message��");
				
		Composite op8 = new Composite(option ,SWT.NONE);
		iqButton = new Button(op8, SWT.CHECK);
		op8.setLayout(new RowLayout());
		iqButton.setText("IQ��");
		
		Composite op9 = new Composite(option ,SWT.NONE);
		presenceButton = new Button(op9, SWT.CHECK);
		op9.setLayout(new RowLayout());
		presenceButton.setText("Presence��");
		
		shell.pack();
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
		    }
		}
		//SWT��Ҫ��ʾ����������Դ������Ϊ��Դ�ͷ�
		display.dispose();
		yellow.dispose();
		green.dispose();
		conGraphic.dispose();
		icon.dispose();
	}
	
	/*****�����±�ǩ******/
	private static Composite createTabPage(TabFolder folder, String label) {
	    TabItem tab = new TabItem(folder, SWT.NONE);
	    tab.setText(label);
	    Composite page = new Composite(folder, SWT.NONE);
	    tab.setControl(page);
	    return page;
	}

	/*****����Ŀ¼*******/
	public static Menu createMenu(final Shell shell) {
		
		Menu menuBar = new Menu(shell, SWT.BAR);
	    shell.setMenuBar(menuBar);
	    MenuItem fileTitle = new MenuItem(menuBar, SWT.CASCADE);
	    fileTitle.setText("�ļ�(&F)");
	    Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
	    fileTitle.setMenu(fileMenu);
	    
	    MenuItem item1_1 = new MenuItem(fileMenu, SWT.NULL);
	    item1_1.setText("�����¼(&S)");
	    item1_1.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		FileDialog saveFile = new FileDialog(shell,SWT.SINGLE | SWT.SAVE);    
	    		saveFile.setFilterNames(new String[]{"��¼�ļ�"}); 
	    		saveFile.setText("�����¼");
	    		saveFile.setFilterExtensions(new String[]{"*.txt"});  
	    		saveFile.setFileName ("record.txt");
	    		saveFile.open();
	    		writeLog(saveFile.getFilterPath() + "\\" + saveFile.getFileName(), readArea.getText());
	    	}
	    });
	    
	    MenuItem item1_2 = new MenuItem(fileMenu, SWT.NULL);
	    item1_2.setText("��ռ��Ӵ���(&C)");
	    item1_2.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		readArea.setText("");
	    	}
	    });
	    
	    MenuItem item1_3 = new MenuItem(fileMenu, SWT.NULL);
	    item1_3.setText("�˳�(&X)");
	    item1_3.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		System.exit(0);
	    	}
	    });
	    
	    MenuItem helpTitle = new MenuItem(menuBar, SWT.CASCADE);
	    helpTitle.setText("����(&H)");
	    Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
	    helpTitle.setMenu(helpMenu);
	    
	    MenuItem item2_1 = new MenuItem(helpMenu, SWT.NULL);
	    item2_1.setText("�����ĵ�(&D)");
	    item2_1.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		try {
	    			Runtime.getRuntime().exec("cmd /c \"resource\\document\\help.txt\"");
				} catch (IOException e1) {
					readArea.append("[ϵͳ]�����ĵ���ʧ��\n");
				}    
	    	}
	    });
	    
	    MenuItem item2_2 = new MenuItem(helpMenu, SWT.NULL);
	    item2_2.setText("���� XMPP Fuzzer(&A)");
	    item2_2.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		AboutFuzzer about = new AboutFuzzer(shell);
	    		about.open();
	    	}
	    });
	    return menuBar;
	}
	
	/*******д��־******/
	private static void writeLog(String logFileName, String content){
		  try {
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
		   Calendar c = Calendar.getInstance();
		   RandomAccessFile raf = new RandomAccessFile(logFileName, "rw");
		   String contents = sdf.format(c.getTime()) + "\r\n" + content + "\r\n";
		   raf.seek(raf.length());
		   raf.write(contents.getBytes("GBK"));
		   raf.close();
		  } catch (IOException e) {
			  readArea.append("[ϵͳ]�����¼ʧ�ܣ�\n");
		  }
	}

	/*******��ȡ��ǰʱ��******/
	private static String nowTime() {
		  Calendar c = Calendar.getInstance();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		  return dateFormat.format(c.getTime());
	} 
	
	/******����6λ����ַ���*******/
	private static String getRandomString(int size){
        char[] c = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q',
                'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
                'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++){
            sb.append(c[Math.abs(random.nextInt()) % c.length]);
        }
        return sb.toString();
    }
	
}
