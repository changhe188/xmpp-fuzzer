package xmpp_fuzzer;

import org.jivesoftware.smack.packet.*;

class FuzzPacket extends Packet{
	 
	/*IQstr������֮������Բ���Ϊregistration������*/
	String IQstr[] = {"username", "password", "resource", "group", "digest", "jid",	"name",
		"first", "last", "email", "city", "state", "zip", "phone", "url", "date", "misc"};
	String Presencestr[] = {"status", };
	
	FuzzPacket(){
		init();
	}
	
	void init(){
		//properties.
	}
	
	public String toXML(){
		StringBuilder buf = new StringBuilder();
		return buf.toString();
	}
}
