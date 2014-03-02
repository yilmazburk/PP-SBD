package com.NLP.Gui;
/*
 * Burkan Yýlmaz
 * Tübitak Bilgem NLP Course Project
 * 27/06/2013
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import com.NLP.Tester.Test;

public class Gui {
	
	public static String[] arguments;                //{"bigramModelDeneme3.lm","trigramModelDeneme2.lm"};
	public Hashtable<String,Hashtable<String,Double> > bigramHash;
	public Hashtable<String,String> disambiguted;
	public String punct = "[\\p{Punct}]";//Regular Expression for Punctuation
	public String whiteSpace = "[\\n]";//Regular Expression for Whitespace Characters
	public String[] punctuations = {"." , "," ,"@", "!","?","\"","<",">","+","-","*","/",")","(","{","}","=","/",";","&","^","'","#","$","Â£","|","_","~",":"};
	public String[] punctCommas = {"," ,"@","\"","<",">","+","-","*","/",")","(","{","}","=","/",";","&","^","'","#","$","Â£","|","_","~",":"};
	public String bounders[] = {"...",".","!","?"};
	public static void main(String[] args) {
		if(args.length<3){
			System.err.println("Language Model, Disambiguated Vocabulary and Test file are required in this order.");
		}else{
			arguments = new String[3];
			arguments[0] = args[0];//bigram language model
			arguments[1] = args[1];//disambiguated vocabulary list
			arguments[2] = args[2];//test file
			Gui g = new Gui();
			Test t = new Test();
			g.readBigramModel();
			g.testBoundation();
			t.compare();
		}
	}
	public Gui(){
		bigramHash = new Hashtable<String, Hashtable<String,Double>>();
		disambiguted = new Hashtable<String,String>();
	}
	public void printHash(String s){
		System.out.println(s);
		
	}
	public String splitMorphology(String str){
		String[] array = str.split("\\+");
		String feature = array[1];//Morphological Feature
		for (int i=0;i<array.length;i++) {
			if(array[i].contains("DB")){
				feature = array[i+1];
			}else if(array[i].contains("Cond")){
				feature = "Cond";
			}
		}
		return feature;
	}
	public void readDisambiguator(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(arguments[1]));//System.getProperty("user.home")+"/Desktop/PROJECT/NLP/esas/yaz-okulu/araclar/disambiguated.txt"
			String line = "";
			String word = "";
			String feature = "";
			while((line=reader.readLine())!=null){
				String[] tokens = line.split(" ");
				word = tokens[0];
				feature = splitMorphology(tokens[1]);
				disambiguted.put(word,feature);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void disambiguator(String file){
		try{
			String[] commands = {"java","-jar","smooth-parse-full-0.6.jar","-i",file};
			ProcessBuilder builder = new ProcessBuilder(commands);
			builder.directory(new File(System.getProperty("user.home")+"/Desktop/PROJECT/NLP/esas/yaz-okulu/araclar"));
			Process process = builder.start();
			int exitValue = process.waitFor();
			if(exitValue!=0){
				System.err.println("An error occured, exit value is "+exitValue);
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
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
	public void writeTextFile(String text){
		try{
			text = text.replaceAll("\n", " ");
			File f = new File(System.getProperty("user.home")+"/Desktop/PROJECT/NLP/esas/yaz-okulu/araclar/kelime.txt");
			if(!f.isFile())
				f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void testBoundation(){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arguments[2]),"UTF-8"));//System.getProperty("user.home")+"/Desktop/PROJECT/NLP/yaz-okulu/metin-derlemler/zaman.txt"
			String line = "";
			String paraph = "";
			String golden = "";
			String text = "";
			int counter = 0;
			while((line = reader.readLine())!=null && counter<1200){
					golden += removeAllFloats(line);
					text += (line+"\n");
					line = line.replaceAll("\""," ");
					line = SplitPunct(line);
					line = line.replaceAll(punct, "");
					StringTokenizer tokens = new StringTokenizer(line);
					line = "";
					while(tokens.hasMoreTokens()){
						String str = tokens.nextToken();
						line += str+" ";
					}
					line+="\n";
					paraph += line; 
					counter++;
			}
			golden = removeAllCommas(golden);
			writeResults(golden,"golden.txt");
			readDisambiguator();
			System.out.println("Creation is done.");
			System.out.println(text);
			System.out.println("=============================================================================");
			paraph = sentenceBounder(paraph);
			writeResults(paraph,"output.txt");
			System.out.println(paraph);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public String replaceLast(String str,String regexp){
		StringBuffer tmp = new StringBuffer();
		String s;
		tmp.append(str);
		s = tmp.reverse().toString().replaceFirst(regexp, "");
		tmp.delete(0, tmp.length()-1);
		tmp.append(s);
		return tmp.reverse().toString();
	}
	
	public String removeAllFloats(String line){
		String sentence = "";
		StringTokenizer tokens = new StringTokenizer(line);
		while(tokens.hasMoreTokens()){
			String str = tokens.nextToken();
			if(str.contains(".") && (Character.isLetterOrDigit(str.charAt(str.indexOf(".") > 0 ? str.indexOf(".") - 1 : str.indexOf("."))) && Character.isLetterOrDigit(str.charAt(str.indexOf(".") < str.length()-1 ? str.indexOf(".") + 1 : str.indexOf("."))))){
				str = str.replace(".", " ").replace(",", " ").trim();
			}else if(str.contains(",") && (Character.isLetterOrDigit(str.charAt(str.indexOf(",") > 0 ? str.indexOf(",") - 1 : str.indexOf(","))) && Character.isLetterOrDigit(str.charAt(str.indexOf(",") < str.length()-1 ? str.indexOf(",") + 1 : str.indexOf(","))))){
				str = str.replace(","," ").replace(".", " ").trim();
			}
			sentence += (str + " "); 
		}
		sentence += "\n";
		return sentence;
	}
	public String removeAllCommas(String golden) {
		for(int i = 0;i<punctCommas.length;i++){
			if(punctCommas[i] == "'")
				golden = golden.replace(punctCommas[i], " ");
			else
				golden = golden.replace(punctCommas[i], "");
		}
		return golden;
	}
	public void writeResults(String sentence,String filename){
		try{
			File output = new File(System.getProperty("user.home")+"/Desktop/"+filename);
			if(!output.isFile()){
				output.createNewFile();
			}
			FileWriter fw = new FileWriter(output.getAbsoluteFile());
			BufferedWriter w = new BufferedWriter(fw);
			w.write(sentence);
			w.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public String sentenceBounder(String sentence){
		StringTokenizer tokens = new StringTokenizer(sentence);
		ArrayList<String> array = new ArrayList<String>();
		String line = "";
		
		while(tokens.hasMoreTokens()){
			array.add(tokens.nextToken());
		}
		ArrayList<Double> all = new ArrayList<Double>();
		for (int i = 0;i<array.size();i++) {
			String str = array.get(i);
			line+=str;
			boolean added = false;
			if(bigramHash.containsKey(str)){
				if(bigramHash.get(str).containsKey(i < array.size()-1 ? array.get(i+1) : "<NULL>")){
//					Enumeration<Double> em = bigramHash.get(str).elements();
//					boolean isPossible = true;
//					while(em.hasMoreElements()){
//						if(bigramHash.get(str).get(i < array.size()-1 ? array.get(i+1) : "<NULL>") < em.nextElement()){
//							isPossible = false;
//							break;
//						}
//					}
//					if(isPossible){
						line += " ";
						added = true;
//					}
				}
				if(!added){
					Enumeration<Double> e = bigramHash.get(str).elements();
					while(e.hasMoreElements()){
						all.add(e.nextElement());
					}
					ArrayList<Double> pList = new ArrayList<Double>();
					for(String s:bounders){
						if(bigramHash.get(str).containsKey(s)){
							pList.add(bigramHash.get(str).get(s));
						}
					}
					
					if(pList.size()>=1){
						Object[] values = pList.toArray();
						Object[] allValues = all.toArray();
						Arrays.sort(values);
						Arrays.sort(allValues);
						Hashtable<String, Double> tmp = bigramHash.get(str);
						Enumeration<String> keys = tmp.keys();
						boolean isAdded = false;//Punctuation is added or not
						if(values[values.length-1]==allValues[allValues.length-1]){
							while(keys.hasMoreElements()){
								String k = keys.nextElement();
								if(tmp.get(k)==values[values.length-1] && (isUpper(array.get(i),i < array.size()-1 ? array.get(i+1) : null) || leftBounder(i < array.size()-1 ? array.get(i+1) : null))){
									if(isDegree(line)){
										line+=k;
									}else{
										line+=k+" \n";
										isAdded = true;
									}
									break;
								}
							}
							if(!isAdded){
								if(i+1<array.size()){
									if(disambiguted.containsKey(array.get(i))){
										if(disambiguted.get(array.get(i)).equals("Verb") && (Character.isUpperCase(array.get(i+1).charAt(0)) || Character.isDigit(array.get(i+1).charAt(0)))){
											if(isDegree(line)){
												line+=". ";
											}else{
												line+="."+" \n";
											}
										}else{
											line+=" ";
										}
									}else{
										line+=" ";
									}
								}else
									line+=" ";
							}
						}else{
							line+=" ";
						}
					}else{
						if(i+1<array.size()){
							if(disambiguted.containsKey(array.get(i))){
								if(disambiguted.get(array.get(i)).equals("Verb") && (Character.isUpperCase(array.get(i+1).charAt(0)) || Character.isDigit(array.get(i+1).charAt(0)) )){
									if(isDegree(line)){
										line+=". ";
									}else{
										line+="."+" \n";
									}
								}else{
									line+=" ";
								}
							}else{
								line+=" ";
							}
						}else
							line+=" ";
					}
					pList.clear();
				}
				all.clear();
			}else{
				if(i+1<array.size()){
					if(disambiguted.containsKey(array.get(i))){
						if(disambiguted.get(array.get(i)).equals("Verb") && (Character.isUpperCase(array.get(i+1).charAt(0)) || Character.isDigit(array.get(i+1).charAt(0)))){
							if(isDegree(line)){
								line+=". ";
							}else{
								line+="."+" \n";
							}
						}else{
							line+=" ";
						}
					}else{
						line+=" ";
					}
				}else
					line+=" ";
			}
		}
		return line;
	}
	public boolean isDegree(String line) {
		if(Character.isDigit(line.charAt(line.length()-1))){
			return true;
		}
		for(int i = line.length()-1;i>=0;i--){
			if(line.charAt(i)==' '){
				if(Character.isUpperCase(line.charAt(i+1))){
					return true;
				}
				else
					break;
			}
		}
		return false;
	}
	public boolean isUpper(String str0,String str) {
		if(str!=null && Character.isDigit(str0.charAt(0)) && Character.isUpperCase(str.charAt(0))){
			return false;
		}
		if(str!=null && Character.isUpperCase(str.charAt(0))){
			return true;
		}else{
			return false;
		}
	}
	public boolean leftBounder(String word){
		if(word==null)
			return true;
		else{
			for(int i = 0 ;i<bounders.length;i++){
				if(bigramHash.containsKey(bounders[i])){
					if(bigramHash.get(bounders[i]).containsKey(word)){
						Enumeration<Double> em = bigramHash.get(bounders[i]).elements();
						while(em.hasMoreElements()){
							if(bigramHash.get(bounders[i]).get(word) < em.nextElement()){
								return false;
							}
						}
						return true;
					}else{
						return false;
					}
				}
			}
			return false;
		}
	}
	public void readBigramModel(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(arguments[0]));
			String line = "";
			ArrayList<String> array = new ArrayList<String>();
			while((line = reader.readLine())!=null){
				if(line.contains("2-grams")){
					while((line = reader.readLine())!=null){
						StringTokenizer tokens = new StringTokenizer(line);
						if(tokens.countTokens()<=1){
							break;
						}
						while(tokens.hasMoreTokens()){
							String str = tokens.nextToken();
							array.add(str);
						}
						if(bigramHash.containsKey(array.get(1))){
							bigramHash.get(array.get(1)).put(array.get(2), Math.pow(10.0,Double.parseDouble(array.get(0))));
						}else{
							Hashtable<String, Double> hash = new Hashtable<String, Double>();
							hash.put(array.get(2), Math.pow(10.0,Double.parseDouble(array.get(0))));
							bigramHash.put(array.get(1),hash);
						}
						array.clear();
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
