package corpus_reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.TreeSet;

public class Word implements Comparable<Word>{
	private static int nextUID = 0;
	public int UID = 0;
	private int[] id;
	private String surface;
	private String lemma;
	private String pos;
	private String morphology;
	private String dep;
	private String deptag;
	private Sentence sentence;
	private SortedSet<Mention> mentions;
	private boolean allowOperation = true;

	private Word() {}
	public static Word punct(Sentence s,String id, int parent) {
		Word w = new Word();
		w.sentence = s;
		String[] auxId = id.split("-");
		w.id = new int[auxId.length];
		for (int i = 0; i < auxId.length; i++) 
			w.id[i] = Integer.valueOf(auxId[i]).intValue();	
		w.surface = ".";
		w.lemma = ".";
		w.pos = "PUNCT";
		w.morphology = "_";
		w.dep = ""+parent;
		w.deptag = "punct";
		
		w.allowOperation = true;
		
		w.UID = nextUID;
		nextUID++;
		return w;
	}
	
	public Word(Word w) {
		id = new int[w.getId().length];
		for (int i = 0; i < w.getId().length; i++) 
			id[i] = w.getId()[i];
		surface = w.getSurface();
		lemma = w.getLemma();
		pos = w.getPos();
		morphology = w.getMorphology();
		dep = w.getDep();
		deptag = w.getDeptag();
		sentence = w.getSentence();
		mentions = w.getMentions();
		UID = nextUID;
		allowOperation = w.allowOperation;
		nextUID++;
	}

	public Word(Sentence s, String id, String surface, String lemma, String pos, String morphology, String dep, String deptag, String allowOperation) {
		sentence = s;
		String[] auxId = id.split("-");
		this.id = new int[auxId.length];
		for (int i = 0; i < auxId.length; i++) 
			this.id[i] = Integer.valueOf(auxId[i]).intValue();	
		this.surface = surface;
		this.lemma = lemma;
		this.pos = pos;
		this.morphology = morphology;
		this.dep = dep;
		this.deptag = deptag;
		
		this.allowOperation = !allowOperation.equalsIgnoreCase("false");
		
		UID = nextUID;
		nextUID++;
	}
//	public Word(String[] conll, Sentence s) {
//		sentence = s;
//		String[] auxId = conll[0].split("-");
//		id = new int[auxId.length]; 
//		for (int i = 0; i < auxId.length; i++) 
//			id[i] = Integer.valueOf(auxId[i]).intValue();	
//		surface = conll[1];
//		lemma = conll[2];
//		pos = conll[3];
//		morphology = conll[5];
//		dep = conll[6];
//		deptag = conll[7];
//		UID = nextUID;
//		nextUID++;
//	}


	public Word(String[] conll, Sentence s, SortedSet<Mention> mentions) {
		sentence = s;
		String[] auxId = conll[0].split("-");
		id = new int[auxId.length]; 
		for (int i = 0; i < auxId.length; i++) 
			id[i] = Integer.valueOf(auxId[i]).intValue();	
		surface = conll[1];
		lemma = conll[2];
		pos = conll[3];
		morphology = conll[5];
		dep = conll[6];
		deptag = conll[7];
		allowOperation = Boolean.getBoolean(conll[9]);
		UID = nextUID;
		nextUID++;
		this.mentions = mentions;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public int[] getId() {
		return id;
	}

	public void setId(int id[]) {
		this.id = id;
	}

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		if(allowOperation)
		this.surface = surface;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getMorphology() {
		return morphology;
	}

	public void setMorphology(String morphology) {
		this.morphology = morphology;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getDeptag() {
		return deptag;
	}

	public void setDeptag(String deptag) {
		this.deptag = deptag;
	}

	public void addMention(Mention m) {
		if(mentions == null)
			mentions = new TreeSet<Mention>();
			//mentions = new ConcurrentSkipListSet<Mention>();
		mentions.add(m);
	}
	public SortedSet<Mention> getMentions() {
		if(mentions == null)
			mentions = new TreeSet<Mention>();
			//mentions = new ConcurrentSkipListSet<Mention>();
		return mentions;
	}

	@Override
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean firstWord) {
		String ret = "\t" + lemma + "\t" + pos + "\t" + "_" + "\t" + morphology + "\t" + dep + "\t" + deptag + "\t";
		if (firstWord)
			ret = (""+surface.charAt(0)).toUpperCase() + surface.substring(1,surface.length()) + ret;
		else
			ret = surface + ret;
//		if (ret.contains("Sud\tSud\tPROPN\t_\t_	5\tnmod"))
//			System.out.println();
		ret += "_\t" + allowOperation + "\t"; 
		if(id.length==1)
			ret = id[0] + "\t" + ret;
		else
			ret = id[0]+"-"+id[1] + "\t" + ret;

		String ment = "";
		for (Mention i : getMentions()) {
			List<Word> lst = new ArrayList<Word>(i.getWords());
			String aux = "";
			if(lst.size()==0) 
				aux = "_";
			else if(lst.size()==1) aux = "(" + i + ")";
			else if(lst.get(0).equals(this)) aux += "(" + i;
			else if(lst.get(lst.size()-1).equals(this)) aux += i + ")";
//			else aux = "_";
			if (aux.length()>0)
				ment += aux + "|";
		}
		if(ment.length()>0)
			ment = ment.substring(0,ment.length()-1);
		
		if (ment.length()>0)
			ret += ment;
		else
			ret += "_";
		
//		if(allowOperation)
		if (ret.contains("_|") || ret.contains("|_"))
			System.out.println();
		
			return  /*UID + ":\t" + */ ret;
//		else
//			return  "*" + ret;
	}

	public void removeMention(Mention old_mention) {
		if(mentions == null)
			mentions = new TreeSet<Mention>();
			//mentions = new ConcurrentSkipListSet<Mention>();
		else
			mentions.remove(old_mention);
	}

	public void clearMentions() {
		if(mentions == null)
			mentions = new TreeSet<Mention>();
			//mentions = new ConcurrentSkipListSet<Mention>();
		else
			mentions.clear();

	}

	@Override
	public int compareTo(Word o) {
		if(sentence.equals(o.getSentence())) {
			if(id.length!=o.getId().length && id[0]==o.getId()[0])
				if(id.length==1) return +1;
				else return -1;
			else{
				return id[0] - o.getId()[0];
			}
		}
		return (sentence.getID()-o.getSentence().getID())*1000;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Word)
			return compareTo((Word) obj) == 0;
		return false;
	}

	public void setAlowOperation(boolean b) {
		allowOperation = b;
		if(mentions!=null)
			for (Mention m : getMentions()) {
				m.setProcessed();
			}
	}
	public void setAlowOperationWeak(boolean b) {
		allowOperation = b;
	}
	public boolean allowOperation() {
		return allowOperation;
	}

}
