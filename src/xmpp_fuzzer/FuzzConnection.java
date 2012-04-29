package xmpp_fuzzer;

import java.util.Random;

import fuzz_packet.*;
import genetic_fuzz.GeneFuzz;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

public class FuzzConnection extends XMPPConnection{

	private int flag = 0;
    private static int messageFlag = 0;
    private static int iqFlag = 0;
    private static int presenceFlag = 0;
    private String seed = null;
	private FuzzConnection fc = this;
    
	public FuzzConnection(ConnectionConfiguration config) {
		super(config);
		new fuzz().start();//启动fuzz线程
	}
	
	public void setflag(int f){
    	flag = f;
    }
    
    public void setIQFlag(int f){
    	iqFlag = f;
    }
    
    public void setMessageFlag(int f){
    	messageFlag = f;
    }
    
    public void setPresenceFlag(int f){
    	presenceFlag = f;
    }
    
    public void setSeed(String s){
    	seed = s;
    }
    
    class fuzz extends Thread{
    	public void run(){
    		while(true){
    			switch(flag){
    				case 0:
    					try{
    						sleep(10000);
    					}catch(InterruptedException e){
    						break;
    					}
    					break;
    				case 1:
    					method1();//利用遗传算法混乱标签
    					break;
    				case 2:
    					method2();//完全随机混乱标签
    					break;
    			}
    		}
    	}
    	
    	void method1(){//利用遗传算法混乱标签
    		GeneFuzz gf = new GeneFuzz(fc);
    	    gf.initData(seed);
    	    gf.initPacket();
    	    gf.beginfuzz();
    	}
    	
    	void method2(){//完全随机混乱标签
    		Random random = new Random();
    		if(messageFlag == 1 && random.nextBoolean()){
    			sendPacket(new fuzzMessage());
    		}
    		if(presenceFlag == 1 && random.nextBoolean()){
    			sendPacket(new fuzzPresence(fuzzPresence.Type.available));
    		}
    		if(iqFlag == 1){
    			switch(random.nextInt(7)){
    				case 0:
    					sendPacket(new fuzzAuthentication());
    					break;
    				case 1:
    					sendPacket(new fuzzBind());
    					break;
    				case 2:
    					sendPacket(new fuzzPrivacy());
    					break;
    				case 3:
    					sendPacket(new fuzzRegistration());
    					break;
    				case 4:
    					sendPacket(new fuzzRosterPacket());
    					break;
    				case 5:
    					sendPacket(new fuzzSession());
    					break;
    				case 6:
    					break;
    			}
    		}
    	}
    }
}
