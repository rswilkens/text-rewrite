package corpus_reader;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import discur_rules.mention_type;

public class CorefChain implements Iterable<Mention>{

	private SortedSet<Mention> chain;
	private SortedSet<Mention> allEntities;
	//	private Iterator<Mention> it;
	private int id;

	public CorefChain(int id) {
		chain = new ConcurrentSkipListSet<Mention>();
		this.id = id;
	}

	public void add(Mention m) {
		chain.add(m);
	}

	public int getId() {
		return id;
	}

	public Set<Mention> getMentions() {
		return chain;
	}

	public int size() {
		return allEntities.size();
	}

	@Override
	public Iterator<Mention> iterator() {
		return chain.iterator();
	}

	//	private SortedSet<Mention> getChain() {
	//		return chain;
	//	}
	public Set<Mention> getEntity() {
		if (allEntities == null) {
			allEntities = new ConcurrentSkipListSet<Mention>();
			for (Mention mention : chain) {
				mention_type type = mention.getType();
				if(type!=null)
					switch (type) {
					case noun:
					case propernoun:
						allEntities.add(mention);
					default:
						break;
					}
			}
		}
		return allEntities;
	}

	public void replace(Mention old_mention, Mention newm) {
		chain.remove(old_mention);
		allEntities = null;
		chain.add(newm);

		Sentence sent = old_mention.getHead().getSentence();
		sent.replace(old_mention.getWords(), newm.getWords());

	}
	@Override
	public String toString() {
		return id + ":{" + chain.size() +";" + getEntity().size() + "}";
	}

	//	@Override
	//	public boolean hasNext() {
	//		if(it==null)
	//			it = chain.iterator();
	//		return it.hasNext();
	//	}
	//
	//	@Override
	//	public Mention next() {
	//		if(it==null)
	//			it = chain.iterator();
	//		return it.next();
	//	}

}
