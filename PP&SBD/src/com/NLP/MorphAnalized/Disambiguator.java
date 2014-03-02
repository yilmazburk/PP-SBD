package com.NLP.MorphAnalized;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

import com.NLP.ReqExp.RegExp;

public class Disambiguator {
	
	HashSet<String> set;
	String[] data;
	RegExp regexp;
	public Disambiguator(){
		set = new HashSet<String>();
		data = new String[8];
		data[0] = "cnn-turk.txt";
		data[1] = "derlem-lisans.txt";
		data[2] = "dunya.txt";
		data[3] = "milliyet-sondakika.txt";
		data[4] = "ntvmsnbc.txt";
		data[5] = "radikal.txt";
		data[6] = "star-gazete.txt";
		data[7] = "tbmm.txt";
		regexp = new RegExp();
	}
	public void disambiguatedVocab(){
		if(!set.isEmpty()){
			try{
				set.remove(" ");
				File f = new File(System.getProperty("user.home")+"/Desktop/vocab.txt");
				if(!f.isFile())
					f.createNewFile();
				FileWriter w = new FileWriter(f);
				BufferedWriter bw = new BufferedWriter(w);
				for (Object elem : set.toArray()) {
					String element = elem.toString();
					bw.write(element);
					bw.write('\n');
				}
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public void readData(){
		try {
			for(int i = 0;i<data.length;i++){
				BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/PROJECT/NLP/yaz-okulu/metin-derlemler/"+data[i]));
				String line = "";
				String token;
				while((line = reader.readLine())!=null){
					if(line.contains("<DOC_END>")){
						continue;
					}else{
						line = line.replaceAll("\"", " ");
						line = regexp.SplitPunct(line);
						StringTokenizer tokenizer = new StringTokenizer(line);
						while(tokenizer.hasMoreTokens()){
							token = tokenizer.nextToken();
							set.add(token);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
