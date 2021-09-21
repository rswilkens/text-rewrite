package corpus_reader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.TreeSet;

import discur_rules.Util;
import discur_rules.mention_type;

public class Mention implements Iterable<Word>, Comparable<Mention>{

	private String id;
	private SortedSet<Word> words;
	private CorefChain chain;
	private mention_type type;
	private Word head;
	private boolean simplified = false;

	public Mention(String id) {
		this.id = id;
	}

	public void addWord(Word w) {
		if(words == null)
			words = new TreeSet<Word>();
			//words = new ConcurrentSkipListSet<Word>();
		words.add(w);
	}
	public Set<Word> getWords() {
		return words;
	}

	public String getId() {
		return id;
	}
	@Override
	public String toString() {
		return id;
	}
	public CorefChain getChain() {
		return chain;
	}
	public void setChain(CorefChain chain) {
		this.chain = chain;
	}

	@Override
	public Iterator<Word> iterator() {
		if(words == null)
			words = new TreeSet<Word>();
			//words = new ConcurrentSkipListSet<Word>();
		return words.iterator();
	}

	public mention_type getType() {
		if(type==null)
			type = Util.getMentionType(this);
		return type;
	}

	public Word getHead() {
		if(head == null)
			head = Util.getHead(this);
		return head;
	}

	public void setHead(Word h) {
		head = h;
	}

	public Set<Mention> getEntity() {
		return chain.getEntity();
	}

	@Override
	public int compareTo(Mention o) {
		StringBuffer b1 = new StringBuffer();
		for (Word word : this) 
			b1.append(word);
		StringBuffer b2 = new StringBuffer();
		for (Word word : o) 
			b2.append(word);

		if (id.equals(o.getId()) && b1.toString().equals(b2.toString())) return 0;
		else if(Integer.valueOf(o.getId()) > Integer.valueOf(getId())) return -1;
		else if(Integer.valueOf(o.getId()) < Integer.valueOf(getId())) return +1;
		else {
			if(getHead()==null && o.getHead()==null)
				return 0;
			if(getHead()==null)
				return -1;
			if(o.getHead()==null)
				return +1;
			return getHead().compareTo(o.getHead());
			//			if(getHead().getSentence().getID() > o.getHead().getSentence().getID()) return +1;
			//			if(getHead().getSentence().getID() < o.getHead().getSentence().getID()) return -1;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Mention))
			return false;

		Mention mention = (Mention)obj;
		StringBuffer b1 = new StringBuffer();
		for (Word word : this) 
			b1.append(word);
		StringBuffer b2 = new StringBuffer();
		for (Word word : mention) 
			b2.append(word);

		return id.equals(mention.getId()) && b1.toString().equals(b2.toString());
	}

	public boolean precessed() {
		return simplified;
	}

	public void setProcessed() {
		simplified = true;
		for (Word word : words) {
			word.setAlowOperationWeak(false);
		}
	}
}
