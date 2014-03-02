package com.NLP.ReqExp;
/*
 * Burkan Yılmaz
 * Tübitak Bilgem NLP Course Project
 * 25/06/2013
 * */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegExp {
	public String punct = "[\\p{Punct}]";//Regular Expression for Punctuation
	public String whiteSpace = "[\\n]";//Regular Expression for Whitespace Characters
	public String[] punctuations = {":","...","." , "," ,"@", "!",
			                        "?","\"","<",">","+","-","*",
			                        "/",")","(","{","}","=","/",
			                        ";","&","^","'","#","$","£",
			                        "|","_","~"};
	public String test = "Natural- Langu&age_? Pr^#$oces'=/*-%s&ing \n Burkan Yilmaz";
	public ArrayList<String> list;
	public RegExp() {
		list = new ArrayList<String>();
	}
	public String SplitPunct(String sentence){
		Pattern p = Pattern.compile(punct);
		Matcher m = p.matcher(sentence);
		if(m.find()){
			for(int i = 0;i<punctuations.length-1;i++)
			{
				if(sentence.indexOf(punctuations[i])!=-1)
					sentence = sentence.replace(punctuations[i]," "+punctuations[i]+" ");
			}
		}
		return sentence;
	}
	
	public String PunctReq(String sentence){
		return sentence.replaceAll(punct, "");
	}
	
	public String WhiteSpaceReg(String sentence){
		return sentence.replaceAll(whiteSpace, "");
	}
	public void print(){	
		for(int i = 0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
	public void readProcess(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/PROJECT/NLP/yaz-okulu/metin-derlemler/cnn-turk.txt"));
			String line = "";
			String sentence = "";
			while((line = reader.readLine())!=null){
				if(line.contains("<DOC_END>")){
					line = "";
					sentence = PunctReq(sentence);
					list.add(sentence);
					break;
				}else{
					sentence += line+" ";
				}
			}
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}