package xmpp_fuzzer;

import java.util.Collection;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

public class Connection {
	
	private static String domain = null;
	private static int port = 0;
	private static String username = null;
	private static String password = null;
	private ConnectionConfiguration config;
	private FuzzConnection con;
	private Chat newChat;
	
	Connection(String domain, int port, String username, String password) {
		Connection.domain = domain;
		Connection.port = port;
		Connection.username = username;
		Connection.password = password;
	}
	
	public int XMPPConnect() throws XMPPException {
		int result = 0;
		config = new ConnectionConfiguration(domain, port);
		con = new FuzzConnection(config);
		
		try {
			con.connect();
			con.login(username, password);
		}
		catch(XMPPException e) {
			result = 1;
		}
		return result;
	}

	public void DisConnect() {
		con.disconnect();
	}
	
	public Collection<RosterEntry> GetRoster() {
		Roster roster = con.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		return entries;
	}
	
	public void MakeChat(String buddy) {
		ChatManager chatManager = con.getChatManager();
		newChat = chatManager.createChat(buddy, new Listener());
	}
	
	public int SentMsg(String buddy, String msg) {
		try {			
			newChat.sendMessage(msg);
		} catch (XMPPException e) {
			return 1;
		}
		return 0;
	}
	
	private class Listener implements MessageListener{
		//@Override
		public void processMessage(Chat c, Message m) {
			// TODO Auto-generated method stub		
		}
	}
	
	public void setFlag(int f){
		con.setflag(f);
	}
	
	public void setIQFlag(int f){
	    con.setIQFlag(f);
	}
	    
	public void setMessageFlag(int f){
	    con.setMessageFlag(f);
	}
	    
	public void setPresenceFlag(int f){
	    con.setPresenceFlag(f);
	}
	
	public void setSeed(String s){
		con.setSeed(s);
	}
	
}
