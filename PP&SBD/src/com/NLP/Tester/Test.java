package com.NLP.Tester;
/*
 * Burkan Yılmaz
 * Tübitak Bilgem NLP Course Project
 * 26/06/2013
 * */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.NLP.ReqExp.RegExp;

public class Test {
	public String bounders[] = {"...",".","!","?"};
	public RegExp regExp;
	public ArrayList<String> golden;
	public ArrayList<String> results;
	public int TP = 0,TN = 0,FN = 0,FP = 0;
	public double presicion,recall;
	public double fMeasure;
	public String[] punctuations = {"," ,"@","\"","<",">","+","-","*","/",")","(","{","}","=","/",";","&","^","'","#","$","£","|","_","~",":"};
 	public Test(){
		regExp = new RegExp();
		golden = new ArrayList<String>();
		results = new ArrayList<String>();
	}
	public void readTrainData(){
		try{
			BufferedReader  reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/rootsTrain.txt"));  
			String line = "";
			int counter = 0;
			while((line = reader.readLine())!=null && counter<1000){
				System.out.println(line);
				counter++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public int countTestData(){
		int counter = 0;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/PROJECT/NLP/yaz-okulu/metin-derlemler/zaman.txt"));
			String line = "";
			String str;
			while((line = reader.readLine())!=null){
				line = regExp.SplitPunct(line);
				StringTokenizer tokens = new StringTokenizer(line);
				while(tokens.hasMoreTokens()){
					str = tokens.nextToken();
					for(int i = 0;i<bounders.length;i++){
						if(str.contains(bounders[i])){
							counter++;
							break;
						}
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return counter;
	}
	public void createGolden(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/golden.txt"));
			String line = "";
			while((line = reader.readLine())!=null){
				String[] array = line.split(" ");
				for(int i = 0;i<array.length;i++){
					array[i] = RemoveAllUnBounders(array[i]);
				    for(int j = 0;j<bounders.length;j++)	 
				    	if(array[i].length()>0 && array[i].charAt(array[i].length()-1)==bounders[j].charAt(0))
				    	{	
				    		golden.add(array[i]);
				    		j = bounders.length;
				    	}else{
				    		golden.add(array[i].concat("*"));
				    		j = bounders.length;
				    	}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void createTest(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/output.txt"));
			String line = "";
			while((line = reader.readLine())!=null){
				line = RemoveAllUnBounders(line);
				String[] array = line.split(" ");
				for(int i = 0;i<array.length;i++)
				    for(int j = 0 ;j<bounders.length;j++)	 
				    	if(array[i].charAt(array[i].length()-1)==bounders[j].charAt(0))
				    	{	
				    		results.add(array[i]);
				    		j = bounders.length;
				    	}else{
				    		results.add(array[i].concat("*"));
				    		j = bounders.length;
				    	}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void compare(){
		createGolden();
		createTest();
		if(golden.size()!=results.size()){
			System.err.println("Sıkıntı var");
			System.err.println("Golden size: "+golden.size());
			System.err.println("Output size: "+results.size());
		}else{
			for(int i = 0;i<golden.size();i++){
				if(golden.get(i).charAt(golden.get(i).length()-1) == '*' && results.get(i).charAt(results.get(i).length()-1) == '*'){
					TN++;
				}else if(golden.get(i).charAt(golden.get(i).length()-1) != '*' && results.get(i).charAt(results.get(i).length()-1) != '*'){
					TP++;
				}else if(golden.get(i).charAt(golden.get(i).length()-1) == '*' && results.get(i).charAt(results.get(i).length()-1) != '*'){
					FP++;
				}else if(golden.get(i).charAt(golden.get(i).length()-1) != '*' && results.get(i).charAt(results.get(i).length()-1) == '*'){
					FN++;
				}else{
					System.err.println("Hata var!");
				}
			}
			presicion = (double)TP/(TP+FP); 
			recall = (double)TP/(TP+FN);
			fMeasure = (double)(2*presicion*recall)/(presicion+recall);
			System.out.println("TP: "+TP);
			System.out.println("TN: "+TN);
			System.out.println("FP: "+FP);
			System.out.println("FN: "+FN);
			System.out.println("Precision: "+presicion);
			System.out.println("Recall: "+recall);
			System.out.println("F measure: "+fMeasure);
			System.out.println("Token size: "+golden.size());
		}
	}
	public String RemoveAllUnBounders(String line){
		for(int i = 0;i<punctuations.length;i++){
			line.replace(punctuations[i], "");
		}
		return line;
	}



}
