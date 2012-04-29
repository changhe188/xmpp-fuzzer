package genetic_fuzz;

import xmpp_fuzzer.FuzzConnection;
import java.util.ArrayList;
import java.util.Random;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.StringUtils;

public class GeneFuzz {
	
	/******��������õ������б�ǩԪ��******/
	static String rootstr[] = {"properties", "message", "iq", "bind", "query", "presence", "item", "session", "error"};
	static String midstr[] = {" xmlns=\"", " xml:lang=\"", " to=\"", " from=\"", " name=\"", " action=\"", " order=\""};
	static String midint[] = {" id=\"", " order=\"", " code=\""};
	static String midObj[] ={" type=\""," value=\""," subscription=\"", " ask=\""};
	static String badelement[] = {"<default name=\"", "<active name=\"", "<list name=\""};
	static String elementstr[] = {"username", "name", "password", "resource", "group", "digest", "jid","name",
		"first", "last", "email", "city", "state", "zip", "url", "date", "misc","body",
		"status", "status" ,"instructions", "group", "text"};
	static String elementint[] = {"jid", "priority",  "phone"};
	static String elementObj[] = {"show"};
	
	int size[] = {rootstr.length, midstr.length, midint.length, midObj.length, badelement.length, elementstr.length, elementint.length, elementObj.length} ;
	int comparesize[] = new int[size.length];	
	
	/*���ֱ�ǩ���й̶�Ԫ��*/
	static String[] midObjex={"normal", "chat", "groupchat", "headline", "error", "available", "unavailable", "subscribe", "subscribed",
							"unsubscribed", "unsubscribe", "error", "subscription", "group", "jid", "none", "to", "from", "both", "remove"};
	static String[] elementObjex= {"chat", "available", "away", "xa", "dnd"};
	
	Random random = new Random();
	individualPacket pac[];
	
	FuzzConnection fuzzConnection;
	
	int ncross = 0;
	double pcross;    //������� 
	double pmutation; //�������
	int popsize;      //��Ⱥ��С     
	int lchrom;       //Ⱦɫ������ 
	int maxgen;       //���������  
	
	public GeneFuzz(FuzzConnection fc){
		fuzzConnection = fc;
	}
	
	/*******���������ַ�����ʼ���Ŵ��㷨����Ҫ�Ĳ���*******/
	public void initData(String seed){
		 int i;
		 i = seed.charAt(random.nextInt(seed.length()));
		 popsize = 20 + i%5*2;            //��Ⱥ��С 
		 i = seed.charAt(random.nextInt(seed.length()));
		 lchrom = 5 + i%20;               //Ⱦɫ������ 
		 i = seed.charAt(random.nextInt(seed.length()));
		 maxgen = 50 + i%10*10;		      //��������� 
		 i = seed.charAt(random.nextInt(seed.length()));
		 pcross = 0.6 + i%10*0.2;         //������� 
		 i = seed.charAt(random.nextInt(seed.length()));
		 pmutation = 0.005 + i%5/1000.0;  //�������
		 comparesize[0] = size[0];
		 for(int j = 1; j < size.length; j++){
			 comparesize[j] = comparesize[j-1] + size[j];
		 }
	}
	
	public void initPacket(){
		pac = new individualPacket[popsize];
		for(int i = 0; i < popsize; i++){
			pac[i] = new individualPacket();
			for(int j = 0; j < lchrom; j++)
				pac[i].chrom[j] = random.nextInt(comparesize[comparesize.length-1]);
		}
	}
	
	public int[] sort(int a[]){
		int i, j, k, t;
        for(i = 0; i < a.length; i++){
        	for(k = i, j = i+1; j < a.length; j++)   
                if(a[k] > a[j])  k =j ;   
        	if(k != i){   
        		t = a[k];     
                a[k] = a[i];
                a[i] = t;   
        	}
        }   
        return a;
	}
	
	public void beginfuzz(){
		send(pac);
		for(int gen = 0; gen < maxgen; gen++){
            generation();
            send(pac);
        }
	}
	
	private void send(individualPacket[] pac) {
		// TODO Auto-generated method stub
		for(int i=0; i<popsize; i++)
			fuzzConnection.sendPacket(pac[i]);
	}
	
	private void generation(){ //������һ���ĺ���
		int mate[][]=new int[2][popsize/2];
		int jcross, j = 0;
		do{
			mate = select();
		    //����ͱ��� 
			jcross = crossover(pac[mate[0][j]].chrom, pac[mate[1][j]].chrom);
			mutation(pac[mate[0][j]].chrom);
			mutation(pac[mate[1][j]].chrom);
		    //��¼���ӹ�ϵ�ͽ���λ�� 
			pac[mate[0][j]].parent[0] = mate[0][j]+1;
			pac[mate[0][j]].xsite = jcross;
			pac[mate[0][j]].parent[1] = mate[1][j]+1;
			pac[mate[1][j]].parent[0] = mate[0][j]+1;
			pac[mate[1][j]].xsite = jcross;
			pac[mate[1][j]].parent[1] = mate[1][j]+1;
			j = j + 1;
		}
		while(j < popsize/2); 
	}
	
	@SuppressWarnings("unchecked")
	private int[][] select(){ //ѡ��Ⱦɫ���˫��
		int m[][] = new int[2][popsize/2];
		ArrayList pop = new ArrayList();
		for(int i = 0; i < popsize ;i++)
			pop.add((Integer)i);
		int temp;
		for(int i = popsize-1, j = 0; i > 2; i = i-2, j++){
			temp = random.nextInt(i);
			m[0][j] = ((Integer)pop.get(temp)).intValue();
			pop.remove(temp);
			temp = (int)random.nextInt(i-1);
			m[1][j] = ((Integer)pop.get(temp)).intValue();
			pop.remove(temp);
		}
		m[0][popsize/2-1] = ((Integer)pop.get(1)).intValue();
		m[1][popsize/2-1] = ((Integer)pop.get(0)).intValue();
		return m;
	}
	
	private int crossover(int m1[], int m2[]){ //���溯��
		int jcross;
			if(random.nextInt(20) < 16){
				double m;
				int temp;
				jcross = random.nextInt(lchrom);
			    ncross++;
			    double flag = (double)jcross/lchrom;   
			    for(int k = 0; k < lchrom; k++){
			    	m = random.nextDouble();
			        if(m <= flag/4){
			                m1[k] = m1[k];
			                m2[k] = m2[k];
			        }
			        else if((m > flag/4) && (m <= flag/2)){
			        		temp = m2[k];
			        		m2[k] = m1[k];
			        		m1[k] = temp;
			        }
			        else if((m > flag/2) && (m <= 3*flag/4)){
			        		m1[k] = m2[k];
			        		m2[k] = m2[k];
			        }
			        else{
			        		m1[k] = m1[k];
			        		m2[k] = m1[k];
			        }
			    }
			}
		    else{
			     jcross = 0;
		    }
			return jcross;
	}
	
	private void  mutation(int m[]){ //���캯��
	    for(int k = 0; k < lchrom; k++){
	        if(random.nextDouble() < pmutation)
	        	m[k] = random.nextInt(comparesize[comparesize.length-1]);
	    }
	}
	
	private class individualPacket extends Packet{//����
		public int chrom[]; 				      //Ⱦɫ��
		public double fitness = 0; 				  //������Ӧ��      
		public double varible;    				  //�����Ӧ�ı���ֵ  
		public int xsite;     				      //����λ��
		public int parent[] = new int[2];  		  //������ 
		
		individualPacket(){
			chrom = new int[lchrom]; 
		}
		
		public String makePacket(){	//����fuzz��
			chrom = sort(chrom);
			StringBuilder buf = new StringBuilder();
			try{
			if(chrom[0] > comparesize[0]-1){
				chrom[0] = random.nextInt(comparesize[0]);
				buf.append("<" + rootstr[chrom[0]]);
			}
			else 
				buf.append("<" + rootstr[chrom[0]]);
			int k = 1;
			for(int i = k; i < chrom.length; i++){
				if(chrom[i] < comparesize[0])	 
					continue;
				else if(chrom[i] > comparesize[3]-1){
					k = i;
					break;
				}
				else{
					if(chrom[i] < comparesize[1])
						buf.append(midstr[chrom[i]-comparesize[0]]+StringUtils.randomString(random.nextInt(50))+"\"");
					else if(chrom[i] > comparesize[2]-1)
						buf.append(midObj[chrom[i]-comparesize[2]]+midObjex[random.nextInt(midObjex.length)]+"\"");
					else
						buf.append(midint[chrom[i]-comparesize[1]]+random.nextLong()+"\"");
				}
			}
			buf.append(">");
			for(int i = k; i < chrom.length; i++){
				if(chrom[i] > comparesize[4]-1){
					k = i;
					break;
				}
				else
					buf.append(badelement[chrom[i]-comparesize[3]]+StringUtils.randomString(random.nextInt(50))+"\"/>");
			}
			for(int i = k; i < chrom.length; i++){
				if(chrom[i] < comparesize[5])
					buf.append("<"+elementstr[chrom[i]-comparesize[4]]+">"+StringUtils.randomString(random.nextInt(50))+"</"+elementstr[chrom[i]-comparesize[4]]+">");
				else if(chrom[i] > comparesize[6]-1)
					buf.append("<"+elementObj[chrom[i]-comparesize[6]]+">"+elementObjex[random.nextInt(elementObjex.length)]+"</"+elementObj[chrom[i]-comparesize[6]]+">");
				else
					buf.append("<"+elementint[chrom[i]-comparesize[5]]+">"+random.nextLong()+"</"+elementint[chrom[i]-comparesize[5]]+">");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return buf.toString();
		}
			return buf.toString();
		}
		
		public String toXML(){
			return makePacket();
		}
	}
}
