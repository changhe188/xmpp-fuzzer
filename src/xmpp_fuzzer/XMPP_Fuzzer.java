/*
 * XMPP Fuzzer v1.0
 * 
 * 用于针对XMPP协议的Fuzz测试
 * 
 * 北京邮电大学计算机学院信息安全系
 * 
 * 作者：姜峰，常贺，李绅，李坚
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
			new FuzzerUI_SWT();//SWT界面
		} catch (SWTException e) {
			// TODO Auto-generated catch block
			System.out.println("Resource File Not Found!");
		}
	}
}

