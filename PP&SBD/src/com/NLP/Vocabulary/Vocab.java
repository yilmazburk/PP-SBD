package com.NLP.Vocabulary;
/*
 * Burkan Yýlmaz
 * Tübitak Bilgem NLP Course Project
 * 25/07/2013
 * */
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

public class Vocab {
	private String[] data;
	private RegExp regexp;
	private HashSet<String> set;
	public Vocab(){
		set = new HashSet<String>();
		data = new String[8];
		regexp = new RegExp();
		data[0] = "cnn-turk.txt";
		data[1] = "derlem-lisans.txt";
		data[2] = "dunya.txt";
		//data[3] = "hukuki-net.txt";
		data[3] = "milliyet-sondakika.txt";
		data[4] = "ntvmsnbc.txt";
		data[5] = "radikal.txt";
		data[6] = "star-gazete.txt";
		data[7] = "tbmm.txt";
		//data[8] = "sentences_utf_8.txt";
		
	}
	
	public static void main(String[] args) {
		Vocab v = new Vocab();
		v.readTrainData();
		v.writeVocabulary();
		//v.print();
		
	}
	public void print(){
		if(!set.isEmpty())
			System.out.println(set.size());
		String a = "asd  swa";
		String[] ar = a.split(" ");
		System.err.println(ar.length);
		for(int i = 0 ;i<ar.length;i++)
		System.err.println(ar[i]);
		/*String a = "\"asdw.wad,awdas;awdasdawdswd\"wdadwada\"";
		System.out.println(a);
		a = a.replaceAll("\"","");
		System.out.print(a);
		String a = "asddw  adwaw  wawa ssd";
		StringTokenizer tokens = new StringTokenizer(a);
		while(tokens.hasMoreTokens()){
			System.out.println(tokens.nextToken());
		}
		
		Pattern p = Pattern.compile("\\p{Punct}");
		Matcher m = p.matcher(regexp.test);
		if(m.find()){
		
		System.out.println(regexp.test);
		}*/
	}
	public void writeVocabulary(){
		if(!set.isEmpty()){
			try {
				set.remove(new String(" "));
				File vocabulary = new File(System.getProperty("user.home")+"/Desktop/vocab.txt");
				if(!vocabulary.isFile()){
					vocabulary.createNewFile();
				}
				FileWriter writer = new FileWriter(vocabulary.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(writer);
				for (Object elem : set.toArray()) {
					String element = elem.toString();
					bw.write(element);
					bw.write('\n');
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void readTrainData(){		
		try {
			for(int i = 0;i<data.length;i++){
				BufferedReader reader = new BufferedReader(new FileReader("Dosya Yolu"));
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
