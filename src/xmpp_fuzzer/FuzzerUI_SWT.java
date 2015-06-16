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
        final Image yellow = new Image(Display.getCurrent(), "resource\\image\\red1.jpg");//表示连接状态的图标
        final Image green = new Image(Display.getCurrent(), "resource\\image\\green1.jpg");
        final Image conGraphic = new Image(Display.getCurrent(), "resource\\image\\conGraphic.jpg");
        
		final Shell shell = new Shell(display, SWT.MIN | SWT.CLOSE );//禁止最大化
		shell.setLayout(new GridLayout());
		shell.setText("XMPP Fuzzer");
		Image icon = new Image(Display.getCurrent(), "resource\\image\\ico.png");
        shell.setImage(icon);//本程序的图标 

		createMenu(shell);	    
		TabFolder folder = new TabFolder(shell, SWT.NONE);

		/******建立连接标签******/
		Composite connect = createTabPage(folder, "建立连接");
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
		serverLabel.setText("服务器:");
		serverLabel.setLayoutData(new RowData(40,15));
		final Text serverArea = new Text(line1, SWT.SINGLE | SWT.BORDER); 
		serverArea.setBackground(white);
		serverArea.setLayoutData(new RowData(160,15));
		
		Composite line2 = new Composite(connect, SWT.NONE);
		line2.setLayout(new RowLayout());
		Label portLabel = new Label(line2,SWT.NONE);
		portLabel.setText("端   口:");
		portLabel.setLayoutData(new RowData(40,15));
		final Text portArea = new Text(line2, SWT.SINGLE | SWT.BORDER); 
		portArea.setBackground(white);
		portArea.setLayoutData(new RowData(160,15));
		
		Composite line3 = new Composite(connect, SWT.NONE);
		line3.setLayout(new RowLayout());
		Label userLabel = new Label(line3,SWT.NONE);
		userLabel.setText("用户名:");
		userLabel.setLayoutData(new RowData(40,15));
		final Text userArea = new Text(line3, SWT.SINGLE | SWT.BORDER); 
		userArea.setBackground(white);
		userArea.setLayoutData(new RowData(160,15));

		Composite line4 = new Composite(connect, SWT.NONE);
		line4.setLayout(new RowLayout());
		Label passLabel = new Label(line4,SWT.NONE);
		passLabel.setText("密   码:");
		passLabel.setLayoutData(new RowData(40,15));
		final Text passArea = new Text(line4, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD); 
		passArea.setBackground(white);
		passArea.setLayoutData(new RowData(160,15));
		
		Composite line5 = new Composite(connect, SWT.NONE);
		line5.setLayout(new RowLayout());
		Composite blank = new Composite(line5, SWT.NONE);//占位用
		blank.setVisible(false);
        final Button okButton = new Button(line5, SWT.PUSH | SWT.CENTER | SWT.FLAT);
		okButton.setText("连接");
		okButton.setLayoutData(new RowData(80,25));
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(okButton.getText().equals("连接")){		
					String username = userArea.getText();
					String password = passArea.getText();
					String server = serverArea.getText();
					String port = portArea.getText();
					try {					
						conn = new Connection(server, Integer.parseInt(port), username, password);
					} catch (Exception input) {
						readArea.append("[系统]连接建立失败，请检查输入信息！\n");
						return;
					}

					try {
						int result = conn.XMPPConnect();

						if(result == 0){
							okButton.setText("断开");//按钮显示的变化意味着连接是否成功建立
							userArea.setEnabled(false);
							serverArea.setEnabled(false);
							passArea.setEnabled(false);
							portArea.setEnabled(false);
							light.setImage(green);
							tips.setText(" 已建立到" + serverArea.getText() + "的连接");

							for(RosterEntry entry : conn.GetRoster()){
								buddyList.add(entry.getUser());
							}
							readArea.append("[系统]连接建立成功！\n");
							readArea.append("使用说明:\n 请先在Fuzz配置界面选择一个好友,之后配置想要的Fuzz方法。\n");//连接至服务器后显示使用说明
							readArea.append("下方输入栏内为空时，点击发送按钮发出配置好的Fuzz包；不为空时，将输入栏内内容发送。\n\n");
						}
						else
							readArea.append("[系统]连接建立失败，请检查输入信息或服务器状态！\n");
					} catch (XMPPException e1) {
						// TODO Auto-generated catch block
						readArea.append("[系统]连接建立出错！\n");
						return;
					}
				}
				//已经建立连接时单击按钮则断开连接
				else{
					conn.DisConnect();
					okButton.setText("连接");
					light.setImage(yellow);
					tips.setText(" 尚未连接");
					userArea.setEnabled(true);
					serverArea.setEnabled(true);
					passArea.setEnabled(true);
					portArea.setEnabled(true);
					buddyList.removeAll();
					buddyList.setText("选择一个好友作为Fuzz对象");
					readArea.append("[系统]连接断开！\n");
					return;
				}	
			}
		});

		Composite line6 = new Composite(connect, SWT.NONE);
		line6.setLayout(new RowLayout());
		Composite blank2 = new Composite(line6, SWT.NONE);//占位用
		blank2.setVisible(false);
		blank2.setLayoutData(new RowData(42, 42));
		Label conGra = new Label(line6, SWT.NONE);
		conGra.setImage(conGraphic);
		
		
		/******监视窗口标签******/
		Composite window = createTabPage(folder, "监视窗口");
        GridLayout win_layout = new GridLayout();
        win_layout.numColumns = 1;
        win_layout.horizontalSpacing = 5;
        window.setLayout(win_layout);
      	
        Composite panel0 = new Composite(window, SWT.NONE);
        panel0.setLayout(new RowLayout());
        light = new Label(panel0 ,SWT.NONE);
        light.setImage(yellow);
        tips = new Label(panel0, SWT.NONE);
        tips.setText(" 尚未连接");
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
		sendButton.setText("发送");
		sendButton.setSize(50, 25);
		sendButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				//判断是否已连接到服务器
				if(sendButton.getText().equals("发送")){
					if(okButton.getText().equals("连接")){
						readArea.append("[系统]请首先建立一个连接！\n");
						return;
					}
					if(buddyList.getText().equals("选择一个好友作为Fuzz对象")) {
						readArea.append("[系统]请首先在好友列表中选择一个好友！\n");
						return;
					}
					conn.MakeChat(buddyList.getItem(buddyList.getSelectionIndex()));
					//根据writeArea是否为空判断是发送正常数据还是fuzz测试
					if(writeArea.getText().equals("")){//为空时根据fuzz配置决定发何种类型的包
						if(fuzzList.getText().equals("选择一种测试方法")){
							readArea.append("[系统]选择一种测试方法\n");
							return;
						}
						if(!messageButton.getSelection() && !iqButton.getSelection() && !presenceButton.getSelection()) {
							readArea.append("[系统]选择一种或多种数据包对象\n");
							return;
						}
						//对应两种测试方法的标签：1为遗传算法，2为完全随机
						conn.setFlag(fuzzList.getSelectionIndex()+1);
						
						if((fuzzList.getSelectionIndex()+1) == 1 && fuzzStr.getText().equals("")){
							readArea.append("[系统]使用遗传算法时请输入或产生一个随机字符串\n");
							return;
						}
						else
							conn.setSeed(fuzzStr.getText());						
						sendButton.setText("停止");
						readArea.append("[系统]Fuzz测试于" + nowTime() + "开始\n");
						readArea.append("Fuzz方法：\n" + fuzzList.getText() + "\n");
						readArea.append("Fuzz数据包对象：\n");
						if (messageButton.getSelection()) {
							conn.setMessageFlag(1);
							readArea.append("Message包\n");
						}	
						else	
							conn.setMessageFlag(0);
						if (iqButton.getSelection()) {
							conn.setIQFlag(1);
							readArea.append("IQ包\n");
						}
						else	
							conn.setIQFlag(0);
						if (presenceButton.getSelection()) {
							conn.setPresenceFlag(1);
							readArea.append("Presence包\n");
						}
						else	
							conn.setPresenceFlag(0);
					}
					else {//不为空时发送正常数据		
						int result = conn.SentMsg(buddyList.getItem(buddyList.getSelectionIndex()), writeArea.getText());
						if(result == 1){
							readArea.append("[系统]发送信息失败！\n");
							return;
						}
						else {
							readArea.append(nowTime() + " " + writeArea.getText()+ "\n");
							writeArea.setText("");
						}										
					}
				}
				else{
					sendButton.setText("发送");
					conn.setFlag(0);
				}
			}	
		});
		
		/******fuzz配置标签******/
		Composite option = createTabPage(folder, "Fuzz配置");
		GridLayout op_layout = new GridLayout();
		op_layout.numColumns = 1;
		op_layout.verticalSpacing = 5;
		option.setLayout(op_layout);
		
		Composite op0 = new Composite(option, SWT.NONE);
        op0.setLayout(new RowLayout());
        Label chooseFriend = new Label(op0, SWT.NONE);
        chooseFriend.setText("当前账号的好友列表：");
		
        Composite op1 = new Composite(option, SWT.NONE);
        op1.setLayout(new RowLayout());
		buddyList = new Combo(op1, SWT.DROP_DOWN);
		buddyList.setText("选择一个好友作为Fuzz对象");
		buddyList.setLayoutData(new RowData(180,15));
		
		Composite op2 = new Composite(option, SWT.NONE);
		op2.setLayout(new RowLayout());
        Label fuzzOption = new Label(op2, SWT.NONE);
        fuzzOption.setText("Fuzz方法选择：");
        
        Composite op3 = new Composite(option, SWT.NONE);
        op3.setLayout(new RowLayout());
		fuzzList = new Combo(op3, SWT.DROP_DOWN);
		fuzzList.setText("选择一种测试方法");
		fuzzList.add("遗传算法随机混乱");
		fuzzList.add("完全随机混乱");
		fuzzList.setLayoutData(new RowData(180,15));
		
		Composite op6 = new Composite(option, SWT.NONE);
		op6.setLayout(new RowLayout());
        Label fuzzString = new Label(op6, SWT.NONE);
        fuzzString.setText("Fuzz随机替换初试值：");
        
		Composite op7 = new Composite(option, SWT.NONE);
		op7.setLayout(new RowLayout());
		fuzzStr = new Text(op7, SWT.BORDER);
		fuzzStr.setLayoutData(new RowData(130,20));
		Button generateButton = new Button(op7, SWT.PUSH | SWT.FLAT);
		generateButton.setText("自动产生");
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
        fuzzObject.setText("Fuzz数据包对象：");
        
		Composite op5 = new Composite(option ,SWT.NONE);
		op5.setLayout(new RowLayout());
		messageButton = new Button(op5, SWT.CHECK);
		messageButton.setText("Message包");
				
		Composite op8 = new Composite(option ,SWT.NONE);
		iqButton = new Button(op8, SWT.CHECK);
		op8.setLayout(new RowLayout());
		iqButton.setText("IQ包");
		
		Composite op9 = new Composite(option ,SWT.NONE);
		presenceButton = new Button(op9, SWT.CHECK);
		op9.setLayout(new RowLayout());
		presenceButton.setText("Presence包");
		
		shell.pack();
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
		    }
		}
		//SWT需要显示管理分配的资源，以下为资源释放
		display.dispose();
		yellow.dispose();
		green.dispose();
		conGraphic.dispose();
		icon.dispose();
	}
	
	/*****建立新标签******/
	private static Composite createTabPage(TabFolder folder, String label) {
	    TabItem tab = new TabItem(folder, SWT.NONE);
	    tab.setText(label);
	    Composite page = new Composite(folder, SWT.NONE);
	    tab.setControl(page);
	    return page;
	}

	/*****建立目录*******/
	public static Menu createMenu(final Shell shell) {
		
		Menu menuBar = new Menu(shell, SWT.BAR);
	    shell.setMenuBar(menuBar);
	    MenuItem fileTitle = new MenuItem(menuBar, SWT.CASCADE);
	    fileTitle.setText("文件(&F)");
	    Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
	    fileTitle.setMenu(fileMenu);
	    
	    MenuItem item1_1 = new MenuItem(fileMenu, SWT.NULL);
	    item1_1.setText("保存记录(&S)");
	    item1_1.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		FileDialog saveFile = new FileDialog(shell,SWT.SINGLE | SWT.SAVE);    
	    		saveFile.setFilterNames(new String[]{"记录文件"}); 
	    		saveFile.setText("保存记录");
	    		saveFile.setFilterExtensions(new String[]{"*.txt"});  
	    		saveFile.setFileName ("record.txt");
	    		saveFile.open();
	    		writeLog(saveFile.getFilterPath() + "\\" + saveFile.getFileName(), readArea.getText());
	    	}
	    });
	    
	    MenuItem item1_2 = new MenuItem(fileMenu, SWT.NULL);
	    item1_2.setText("清空监视窗口(&C)");
	    item1_2.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		readArea.setText("");
	    	}
	    });
	    
	    MenuItem item1_3 = new MenuItem(fileMenu, SWT.NULL);
	    item1_3.setText("退出(&X)");
	    item1_3.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		System.exit(0);
	    	}
	    });
	    
	    MenuItem helpTitle = new MenuItem(menuBar, SWT.CASCADE);
	    helpTitle.setText("帮助(&H)");
	    Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
	    helpTitle.setMenu(helpMenu);
	    
	    MenuItem item2_1 = new MenuItem(helpMenu, SWT.NULL);
	    item2_1.setText("帮助文档(&D)");
	    item2_1.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		try {
	    			Runtime.getRuntime().exec("cmd /c \"resource\\document\\help.txt\"");
				} catch (IOException e1) {
					readArea.append("[系统]帮助文档丢失！\n");
				}    
	    	}
	    });
	    
	    MenuItem item2_2 = new MenuItem(helpMenu, SWT.NULL);
	    item2_2.setText("关于 XMPP Fuzzer(&A)");
	    item2_2.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		AboutFuzzer about = new AboutFuzzer(shell);
	    		about.open();
	    	}
	    });
	    return menuBar;
	}
	
	/*******写日志******/
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
			  readArea.append("[系统]保存记录失败！\n");
		  }
	}

	/*******获取当前时间******/
	private static String nowTime() {
		  Calendar c = Calendar.getInstance();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		  return dateFormat.format(c.getTime());
	} 
	
	/******产生6位随机字符串*******/
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
