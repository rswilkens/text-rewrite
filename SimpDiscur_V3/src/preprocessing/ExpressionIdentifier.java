package preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import corpus_reader.Corpus;
import corpus_reader.Sentence;
import corpus_reader.Word;

public class ExpressionIdentifier {

	private Map<String, String[]> amalgams;
	private List<ExpressionWord> listExpressions;
	private Set<String> firstWords;
	
	public ExpressionIdentifier(String... dictionariesExpressions) {
		amalgams = setAmalgams();
		int dictionrySize = 0;
		Set<String[]> dictionry = new HashSet<String[]>();
		for (String dicPath : dictionariesExpressions) {
			System.out.println("reading: " + dicPath + "; actual size: " + dictionrySize);
			try (BufferedReader br = new BufferedReader(new FileReader(dicPath))){
				String ln = null;
				while ((ln = br.readLine()) != null) {
					if (ln.startsWith("#") || ln.length()<5);
					else {
//						if(corpusSpliter==null) corpusSpliter = findCorpusSpliter(ln);
//						String[] cols = {ln};
//						if(corpusSpliter.length()>0)
//							ln.split(corpusSpliter);
						String word = ln; //cols[0];
						
						if(word.contains("(") || word.contains(")") || word.contains("/") || word.contains("\\")) {
//							System.out.println("( or ) are not accepted: " + ln);
							continue;
						}
						
						List<String> aux = new ArrayList<String>();
						for (String w : word.split(" ")) 
							aux.addAll(split(w));
						dictionry.add(aux.toArray(new String[aux.size()]));
						dictionrySize++;
						if(dictionrySize%10000==0)
							System.out.println("\t\tactual size: " + dictionrySize);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("hasing "+ dictionry.size() + " expressions");
		List<ExpressionWord> exp = new ArrayList<ExpressionWord>();
		int hashCount = 0;
		for (String[] aux : dictionry) {
//			if(aux.length==3 && aux[0].equals("avoir") && aux[1].equals("libre") && aux[2].equals("carrière"))
//				System.out.println();
			boolean added = false;
			for (ExpressionWord expressionWord : exp) {
				if(expressionWord.is(aux[0])) {
					String[] words = new String[aux.length-1];
					for (int i = 1; i < aux.length; i++) 
						words[i-1] = aux[i];
//					System.out.println(Arrays.toString(aux));
					expressionWord.add(words);
					added = true;
				}
			}
			if(!added)
				exp.add(new ExpressionWord(aux));
			if(hashCount%1000==0)
				System.out.println("\t\thasing " + hashCount/((double)dictionry.size())+ "## " + dictionry.size() + "/" + hashCount + "= "+ dictionry.size()%((double)hashCount));
			hashCount++;
		}
		this.listExpressions = exp;
		this.firstWords = new HashSet<String>();
		for (ExpressionWord expressionWord : exp) 
			this.firstWords.add(expressionWord.getWord());
//		print(exp);
	}

	private void print(List<ExpressionWord> exp) {
		for (ExpressionWord e : exp) {
			System.out.println(e.toString());
		}
		
	}

	private Map<String, String[]> setAmalgams() {
		Map<String, String[]> ret = new HashMap<String, String[]>();
		ret.put("du", "de le".split(" "));
		ret.put("aux", "à les".split(" "));
		ret.put("des", "des les".split(" "));
		
		ret.put("duquel", "de lequel".split(" "));
		ret.put("auquel", "à lequel".split(" "));
		ret.put("desquels", "des lesquels".split(" "));
		ret.put("auxquels", "à lesquels".split(" "));
		ret.put("desquelles", "des lesquelles".split(" "));
		ret.put("auxquelles", "à lesquelles".split(" "));
		
		ret.put("dudit", "de dudit".split(" "));
		ret.put("audit", "à dudit".split(" "));
		ret.put("desdits", "des dudits".split(" "));
		ret.put("auxdits", "à dudits".split(" "));
		ret.put("desdites", "des dudites".split(" "));
		ret.put("auxdites", "à dudites".split(" "));
		return ret;
	}

	private Collection<? extends String> split(String words) {
		if(words.contains("'")) {
			String[] aux = words.split("'");
			words = "";
			for (int i = 0; i < aux.length-1; i++) 
				words += aux[i] + "' ";
			words += aux[aux.length-1] + " ";
		}
		
		List<String> ret = new ArrayList<String>();
		for (String word : words.split(" ")) 
			if(amalgams.containsKey(word))
				for (String a : amalgams.get(word)) 
					ret.add(a);
			else
				ret.add(word);
		
		return ret;
	}

//	private String findCorpusSpliter(String ln) {
//		if(ln.split(";").length>2) return ";";
//		if(ln.split("\t").length>2) return "\t";
//		return "";
//	}


	public void blockingAnnotate(Corpus corpus) {
		for (Sentence sentence : corpus.getSentences()) {
			List<Word> words = sentence.getWords();
			for (int start = 0; start < words.size(); start++) 
				if(firstWords.contains(words.get(start).getLemma())) {
//					if(words.get(start).getLemma().equalsIgnoreCase("avoir"))
//						System.out.println();
					int end = isIn(start,sentence);
					boolean changed = false;
					for (int i = start; i < start+end; i++) {
						words.get(i).setAlowOperation(false);
						changed = true;
					}
					if(changed)start = start+end;
				}
		}
	}
	
	private int isIn(int start, Sentence sentence) {
		int maxEnd = -1;
		for (ExpressionWord exp : listExpressions) {
//			System.out.println(exp);
			int end = exp.contains(sentence.getWords().subList(start, sentence.getWords().size()));
			maxEnd = Math.max(maxEnd, end);
		}
		return maxEnd;
	}

	public static void main(String[] args) {
		new ExpressionIdentifier("resources/simpleAprenent.csv");
	}
}

