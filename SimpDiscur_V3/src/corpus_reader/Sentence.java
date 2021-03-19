package corpus_reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Sentence implements Iterable<Word>{

	private Corpus corpus;
	private List<Word> words;
	private int id;

	public Sentence(int id, Corpus corpus) {
		this.id = id;
		words = new ArrayList<Word>();
		this.corpus = corpus;
	}
	
	public Corpus getCorpus() {
		return corpus;
	}
	
	public void add(Word w) {
		words.add(w);
		
	}

	public List<Word> getWords() {
		return words;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Word word : words) 
//			if (word.getId().length==1)
				b.append(word).append("\n");
//			else
//				System.out.println("skipping: " + word);
		return b.toString();
	}

	@Override
	public Iterator<Word> iterator() {
		return words.iterator();
	}

	public int getID() {
		return id;
	}

	public void replace(Set<Word> old_words, Set<Word> new_words) {
		Comparator<Word> c = new Comparator<Word>() {
			@Override
			public int compare(Word o1, Word o2) {
				if(o1.getId()[0]==o2.getId()[0]) return 0;
				return o1.getId()[0]<o2.getId()[0] ? -1 : 1;
			}
		};
		
		ArrayList<Word> oldw = new ArrayList<Word>(old_words);
		Collections.sort(oldw, c);
		ArrayList<Word> neww = new ArrayList<Word>(new_words);
		Collections.sort(neww, c);
		int index = -1;
		for (Word word : old_words) {
			if(index<0)
				index = words.indexOf(word);
			if(index<0)
				System.out.println();
			words.remove(word);
		}
		for (int i = neww.size()-1; i >= 0; i--) {
			words.add(index, neww.get(i));
		}
		
	}
}