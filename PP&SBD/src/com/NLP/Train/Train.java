package com.NLP.Train;
/*
 * Burkan Yýlmaz
 * Tübitak Bilgem NLP Course Project
 * 26/06/2013
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;



import com.NLP.ReqExp.RegExp;

public class Train {
	
	public String[] data;
	public RegExp regexp;
	public static void main(String[] args) {
		Train t = new Train();
		t.WriteTrainData();
	}
	public Train(){
		regexp = new RegExp();
		data = new String[8];
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
	public void WriteTrainData(){
		try{
			File trainData = new File(System.getProperty("user.home")+"/Desktop/sentence1M.txt");
			if(!trainData.isFile()){
				trainData.createNewFile();
			}
			FileWriter writer = new FileWriter(trainData.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(writer);
			for(int i = 0;i<data.length;i++){
				BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Desktop/PROJECT/NLP/yaz-okulu/metin-derlemler/"+data[i]));
				String line = "";
				while((line = reader.readLine())!=null){
					if(line.contains("<DOC_END>")){
						continue;
					}else{
						line = line.replaceAll("\""," ");
						line = regexp.SplitPunct(line);
						StringTokenizer tokens = new StringTokenizer(line);
						line = "";
						while(tokens.hasMoreTokens()){
							String str = tokens.nextToken();
							line += str+" ";
						}
						bw.write(line);
						bw.write("\n");
					}
				}
				System.out.println("File "+(int)(i+1)+" is done.");
				System.out.println("File is done.");
			}
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
