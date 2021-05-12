package corpus_reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class Corpus {
	private List<Sentence> sentences;
	private List<CorefChain> chains;

	private Corpus() {};
	public Corpus(String corefparsedcorpus) {
		sentences = new ArrayList<Sentence>();
		Map<String, List<Mention>> chain = new LinkedHashMap<String, List<Mention>>();
		int sentId = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(corefparsedcorpus))){
			Map<String, Mention> openedMent = new HashMap<String, Mention>();
			String ln = null;
			Sentence auxSent = new Sentence(sentId, this);
			while ((ln = br.readLine()) != null) {
				if (ln.startsWith("#"));
				else if (ln.trim().length()==0) {
					sentences.add(auxSent);
					for (Word word : auxSent.getWords()) {
						for (Mention ment : word.getMentions()) {
							if(!chain.containsKey(ment.getId()))
								chain.put(ment.getId(), new ArrayList<Mention>());
							chain.get(ment.getId()).add(ment);
						}
					}
					auxSent = new Sentence(sentId, this);
					sentId++;
				}else {
					//					System.out.println(ln);
					String[] colsP = ln.split("\t");

					Word w = new Word(auxSent, colsP[0], colsP[1], colsP[2], colsP[3], colsP[5], colsP[6], colsP[7], colsP[9]);
					auxSent.add(w);
					if(colsP[0].contains("-")){
						//System.out.println("\tSKIP");
						for (Mention m : openedMent.values()) 
							m.addWord(w);
					}else {
						String colsCoref = colsP[colsP.length-1];
						//						String[] colsCoref = iter.next().split(" ");
						if(colsCoref.trim().equals("_") || colsCoref.trim().equals("-")) {
							for (Mention m : openedMent.values()) 
								m.addWord(w);

						} else {
							//							System.out.println("\t" + colsCoref[1]);
							String[] cols = colsCoref.split("\\|");
							for (String coref : cols) {
								if(coref.contains(")") && !coref.contains("(")) {
									String id = coref.replace("(", "").replace(")", "");
									Mention m = openedMent.remove(id);
//																		System.out.println("DEBUG:" +m +" / "+ id +"\t"+ln + "\n::::" + auxSent);
									m.addWord(w);
									for (Word otherW : m.getWords()) 
										otherW.addMention(m);
								}
							}
							for (Mention m : openedMent.values()) 
								m.addWord(w);
							for (String coref : cols) {
								if(coref.contains("(") && coref.contains(")")) {
									Mention m = new Mention(coref.replace("(", "").replace(")", ""));
									w.addMention(m);
									m.addWord(w);
								}else if(coref.contains("(")) {
									Mention m = new Mention(coref.replace("(", ""));
									m.addWord(w);
									openedMent.put(m.getId(), m);
								}
							}
						}
					}
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
            Scanner in = new Scanner(System.in);
            in.nextLine();
		}
		chains = new ArrayList<CorefChain>();
		for (String id : chain.keySet()) {
			CorefChain c = new CorefChain(Integer.valueOf(id));
			for (Mention m : chain.get(id)) {
				c.add(m);
				m.setChain(c);
			}
			chains.add(c);
		}
	}
	public Corpus(String parsedcorpus, String corefcorpus) {
		sentences = new ArrayList<Sentence>();
		Map<String, List<Mention>> chain = new LinkedHashMap<String, List<Mention>>();
		try (BufferedReader brParsed = new BufferedReader(new FileReader(parsedcorpus)); BufferedReader brCoref = new BufferedReader(new FileReader(corefcorpus)) ) {

			String lnCoref;
			boolean read = false;
			boolean skip = true;
			List<String> corefLines = new ArrayList<String>();
			while ((lnCoref = brCoref.readLine()) != null) {
				if(lnCoref.contains("#end document")){

					if(read && corefLines.size()>0) {//#end document without line break
						List<String> parsedLines = getParsedLines(brParsed);
						Sentence s = align(parsedLines, corefLines, sentences.size());
						sentences.add(s);
						for (Word word : s.getWords()) {
							for (Mention ment : word.getMentions()) {
								if(!chain.containsKey(ment.getId()))
									chain.put(ment.getId(), new ArrayList<Mention>());
								chain.get(ment.getId()).add(ment);
							}
						}
						corefLines = new ArrayList<String>();
					}


					read = false;
					skip = true;
					corefLines = new ArrayList<String>();
				}

				if(read) {
					if (lnCoref.length()>0)
						skip = false;
					if(!skip)
						if(lnCoref.length()>0)
							corefLines.add(lnCoref);
						else{
							List<String> parsedLines = getParsedLines(brParsed);
							Sentence s = align(parsedLines, corefLines, sentences.size());
							sentences.add(s);
							for (Word word : s.getWords()) {
								for (Mention ment : word.getMentions()) {
									if(!chain.containsKey(ment.getId()))
										chain.put(ment.getId(), new ArrayList<Mention>());
									chain.get(ment.getId()).add(ment);
								}
							}
							corefLines = new ArrayList<String>();
						}
				}

				if(lnCoref.contains("#begin document") && lnCoref.contains(parsedcorpus.split("/")[parsedcorpus.split("/").length-1])){
					read = true;
					skip = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		chains = new ArrayList<CorefChain>();
		for (String id : chain.keySet()) {
			CorefChain c = new CorefChain(Integer.valueOf(id));
			for (Mention m : chain.get(id)) {
				c.add(m);
				m.setChain(c);
			}
			chains.add(c);
		}
	}

	private Sentence align(List<String> parsedLines, List<String> corefLines, int sentId) {
		Sentence sentence = new Sentence(sentId, this);
		Iterator<String> iter = corefLines.iterator();

		Map<String, Mention> openedMent = new HashMap<String, Mention>();
		for (String p : parsedLines) {
			//			System.out.println(p);
			String[] colsP = p.split("\t");
			Word w = new Word(sentence, colsP[0], colsP[1], colsP[2], colsP[3], colsP[5], colsP[6], colsP[7],colsP[9]);
			sentence.add(w);
			if(colsP[0].contains("-")){
				//System.out.println("\tSKIP");
				for (Mention m : openedMent.values()) 
					m.addWord(w);
			}else {
				String[] colsCoref = iter.next().trim().split(" ");
				if(colsCoref[colsCoref.length-1].trim().equals("_") || colsCoref[colsCoref.length-1].trim().equals("-")) {
					for (Mention m : openedMent.values()) 
						m.addWord(w);

				} else {
					//					System.out.println("\t" + colsCoref[1]);
					String[] cols = colsCoref[colsCoref.length-1].split("\\|");
					for (String coref : cols) {
						if(coref.contains(")") && !coref.contains("(")) {
							String id = coref.replace("(", "").replace(")", "");
							Mention m = openedMent.remove(id);
							m.addWord(w);
							for (Word otherW : m.getWords()) 
								otherW.addMention(m);
						}
					}
					for (Mention m : openedMent.values()) 
						m.addWord(w);
					for (String coref : cols) {
						if(coref.contains("(") && coref.contains(")")) {
							Mention m = new Mention(coref.replace("(", "").replace(")", ""));
							w.addMention(m);
							m.addWord(w);
						}else if(coref.contains("(")) {
							Mention m = new Mention(coref.replace("(", ""));
							m.addWord(w);
							openedMent.put(m.getId(), m);
						}
					}
				}
			}
		}
		//		System.out.println(openedMent.size() + "\t" + openedMent.keySet());
		//		System.out.println();
		return sentence;
	}

	private List<String> getParsedLines(BufferedReader brParsed) throws IOException {
		List<String> ret = new ArrayList<String>();
		String ln;
		while ((ln = brParsed.readLine()) != null) {
			if(ln.length() >0)
				//				if (!ln.split("\t")[0].contains("-"))
				ret.add(ln.trim());
			else
				return ret;
		}
		return ret;
	}

	public List<CorefChain> getChains() {
		return chains;
	}
	public List<Sentence> getSentences() {
		return sentences;
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		for (Sentence sentence : sentences) 
			if (sentence.getWords().size()>0)
				ret.append(sentence).append("\n");

		return ret.toString();
	}
}
