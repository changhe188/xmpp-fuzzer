/**
 * $RCSfile: fuzzXMPPError.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/06/29 15:46:48 $
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

import org.jivesoftware.smack.packet.*;

import java.util.*;

/**
 * Represents a XMPP error sub-packet. Typically, a server responds to a request that has
 * problems by sending the packet back and including an error packet. Each error has a code, type, 
 * error condition as well as as an optional text explanation. Typical errors are:<p>
 *
 * <table border=1>
 *      <hr><td><b>Code</b></td><td><b>XMPP Error</b></td><td><b>Type</b></td></hr>
 *      <tr><td>500</td><td>interna-server-error</td><td>WAIT</td></tr>
 *      <tr><td>403</td><td>forbidden</td><td>AUTH</td></tr>
 *      <tr><td>400</td<td>bad-request</td><td>MODIFY</td>></tr>
 *      <tr><td>404</td><td>item-not-found</td><td>CANCEL</td></tr>
 *      <tr><td>409</td><td>conflict</td><td>CANCEL</td></tr>
 *      <tr><td>501</td><td>feature-not-implemented</td><td>CANCEL</td></tr>
 *      <tr><td>302</td><td>gone</td><td>MODIFY</td></tr>
 *      <tr><td>400</td><td>jid-malformed</td><td>MODIFY</td></tr>
 *      <tr><td>406</td><td>no-acceptable</td><td> MODIFY</td></tr>
 *      <tr><td>405</td><td>not-allowed</td><td>CANCEL</td></tr>
 *      <tr><td>401</td><td>not-authorized</td><td>AUTH</td></tr>
 *      <tr><td>402</td><td>payment-required</td><td>AUTH</td></tr>
 *      <tr><td>404</td><td>recipient-unavailable</td><td>WAIT</td></tr>
 *      <tr><td>302</td><td>redirect</td><td>MODIFY</td></tr>
 *      <tr><td>407</td><td>registration-required</td><td>AUTH</td></tr>
 *      <tr><td>404</td><td>remote-server-not-found</td><td>CANCEL</td></tr>
 *      <tr><td>504</td><td>remote-server-timeout</td><td>WAIT</td></tr>
 *      <tr><td>502</td><td>remote-server-error</td><td>CANCEL</td></tr>
 *      <tr><td>500</td><td>resource-constraint</td><td>WAIT</td></tr>
 *      <tr><td>503</td><td>service-unavailable</td><td>CANCEL</td></tr>
 *      <tr><td>407</td><td>subscription-required</td><td>AUTH</td></tr>
 *      <tr><td>500</td><td>undefined-condition</td><td>WAIT</td></tr>
 *      <tr><td>400</td><td>unexpected-condition</td><td>WAIT</td></tr>
 *      <tr><td>408</td><td>request-timeout</td><td>CANCEL</td></tr>
 * </table>
 *
 * @author Matt Tucker
 */
public class fuzzXMPPError extends XMPPError{

    public fuzzXMPPError(Condition condition) {
		super(condition);
		// TODO Auto-generated constructor stub
	}

	/**
     * Returns the error as XML.
     *
     * @return the error as XML.
     */
    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<error code=\"").append(getCode()).append("\"");
        Random random = new Random();
        if (getType() != null&&random.nextBoolean()) {
            buf.append(" type=\"");
            buf.append(getType().name());
            buf.append("\"");
        }
        buf.append(">");
        int n1 = random.nextInt(4);
        for(int i=0; i<n1 ; i++){
        	int j = random.nextInt(3);
        	switch(j){
        		case 0:
        			if (getCondition() != null) {
        	            buf.append("<").append(getCondition());
        	            buf.append(" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>");
        	        }
        			break;
        		case 1:
        	        if (getMessage() != null) {
        	            buf.append("<text xml:lang=\"en\" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">");
        	            buf.append(getMessage());
        	            buf.append("</text>");
        	        }
        	        break;
        		case 2:
        	        for (PacketExtension element : getExtensions()) {
        	            buf.append(element.toXML());
        	        }
        	        break;
        	}
        }
        buf.append("</error>");
        return buf.toString();
    }
}
