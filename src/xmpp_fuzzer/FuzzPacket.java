package xmpp_fuzzer;

import org.jivesoftware.smack.packet.*;

class FuzzPacket extends Packet{
	 
	/*IQstr第六个之后的属性部分为registration包独有*/
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
