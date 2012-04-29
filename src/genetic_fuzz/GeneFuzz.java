package genetic_fuzz;

import xmpp_fuzzer.FuzzConnection;
import java.util.ArrayList;
import java.util.Random;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.StringUtils;

public class GeneFuzz {
	
	/******定义可能用到的所有标签元素******/
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
	
	/*部分标签具有固定元素*/
	static String[] midObjex={"normal", "chat", "groupchat", "headline", "error", "available", "unavailable", "subscribe", "subscribed",
							"unsubscribed", "unsubscribe", "error", "subscription", "group", "jid", "none", "to", "from", "both", "remove"};
	static String[] elementObjex= {"chat", "available", "away", "xa", "dnd"};
	
	Random random = new Random();
	individualPacket pac[];
	
	FuzzConnection fuzzConnection;
	
	int ncross = 0;
	double pcross;    //交叉概率 
	double pmutation; //变异概率
	int popsize;      //种群大小     
	int lchrom;       //染色体条数 
	int maxgen;       //最大世代数  
	
	public GeneFuzz(FuzzConnection fc){
		fuzzConnection = fc;
	}
	
	/*******根据种子字符串初始化遗传算法所需要的参数*******/
	public void initData(String seed){
		 int i;
		 i = seed.charAt(random.nextInt(seed.length()));
		 popsize = 20 + i%5*2;            //种群大小 
		 i = seed.charAt(random.nextInt(seed.length()));
		 lchrom = 5 + i%20;               //染色体条数 
		 i = seed.charAt(random.nextInt(seed.length()));
		 maxgen = 50 + i%10*10;		      //最大世代数 
		 i = seed.charAt(random.nextInt(seed.length()));
		 pcross = 0.6 + i%10*0.2;         //交叉概率 
		 i = seed.charAt(random.nextInt(seed.length()));
		 pmutation = 0.005 + i%5/1000.0;  //变异概率
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
	
	private void generation(){ //产生下一代的函数
		int mate[][]=new int[2][popsize/2];
		int jcross, j = 0;
		do{
			mate = select();
		    //交叉和变异 
			jcross = crossover(pac[mate[0][j]].chrom, pac[mate[1][j]].chrom);
			mutation(pac[mate[0][j]].chrom);
			mutation(pac[mate[1][j]].chrom);
		    //记录亲子关系和交叉位置 
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
	private int[][] select(){ //选择染色体的双亲
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
	
	private int crossover(int m1[], int m2[]){ //交叉函数
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
	
	private void  mutation(int m[]){ //变异函数
	    for(int k = 0; k < lchrom; k++){
	        if(random.nextDouble() < pmutation)
	        	m[k] = random.nextInt(comparesize[comparesize.length-1]);
	    }
	}
	
	private class individualPacket extends Packet{//个体
		public int chrom[]; 				      //染色体
		public double fitness = 0; 				  //个体适应度      
		public double varible;    				  //个体对应的变量值  
		public int xsite;     				      //交叉位置
		public int parent[] = new int[2];  		  //父个体 
		
		individualPacket(){
			chrom = new int[lchrom]; 
		}
		
		public String makePacket(){	//构造fuzz包
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
