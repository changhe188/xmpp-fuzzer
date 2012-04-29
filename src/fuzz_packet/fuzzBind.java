/**
 * $RCSfile: fuzzBind.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/06/29 15:46:41 $
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fuzz_packet;

import java.util.Random;

/**
 * IQ packet used by Smack to bind a resource and to obtain the jid assigned by the server.
 * There are two ways to bind a resource. One is simply sending an empty Bind packet where the
 * server will assign a new resource for this connection. The other option is to set a desired
 * resource but the server may return a modified version of the sent resource.<p>
 *
 * For more information refer to the following
 * <a href=http://www.xmpp.org/specs/rfc3920.html#bind>link</a>. 
 *
 * @author Gaston Dombiak
 */
public class fuzzBind extends fuzzIQ {

    private String resource = null;
    private String jid = null;

    public fuzzBind() {
        setType(fuzzIQ.Type.SET);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">");
        Random random = new Random();
        int n1 = random.nextInt(3);
        for(int i=0; i<n1; i++){
        	int j = random.nextInt(2);
        	switch(j){
        	case 0:
        		 if (resource != null) {
        	            buf.append("<resource>").append(resource).append("</resource>");
        	     }
        		 break;
        	case 1:
        	     if (jid != null) {
        	            buf.append("<jid>").append(jid).append("</jid>");
        	     }
        	}
        }
        buf.append("</bind>");
        return buf.toString();
    }
}
