/*
 * XMPP Fuzzer v1.0
 * 
 * �������XMPPЭ���Fuzz����
 * 
 * �����ʵ��ѧ�����ѧԺ��Ϣ��ȫϵ
 * 
 * ���ߣ����壬���أ��������
 * 
 * 2009.6
 *  
 */

package xmpp_fuzzer;

import org.eclipse.swt.SWTException;

public class XMPP_Fuzzer {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new FuzzerUI_SWT();//SWT����
		} catch (SWTException e) {
			// TODO Auto-generated catch block
			System.out.println("Resource File Not Found!");
		}
	}
}

