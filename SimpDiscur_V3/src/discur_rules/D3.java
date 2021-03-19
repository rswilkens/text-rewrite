package discur_rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import corpus_reader.CorefChain;
import corpus_reader.Corpus;
import corpus_reader.Mention;
import corpus_reader.Word;

public class D3 implements DiscourseSimp {

	protected int mindist = 1;
	
	public D3() {
		mindist = 1;
	}
	
	@Override
	public Corpus simplify(Corpus corpus) {
		List[] aux = getMentionAndPron(corpus.getChains());
		
		List<Mention> lstPossibleAmbiguousMentions = aux[0];
		List<Mention> lstPossibleAmbiguousMentionsAndReferences = aux[1];
		
		doTheSimplification(lstPossibleAmbiguousMentions, lstPossibleAmbiguousMentionsAndReferences);
		return corpus;
	}

	private List[] getMentionAndPron(List<CorefChain> chains) {
		List<Mention> lstPossibleAmbiguousMentions = new ArrayList<Mention>();
		List<Mention> lstPossibleAmbiguousMentionsAndReferences = new ArrayList<Mention>();
		for (CorefChain chain : chains) {
			//			System.out.println(chain);
			for (Mention mention : chain) {
				mention_type type = mention.getType();
				if(isValidePron(type, mention) &&!mention.precessed()) {
					lstPossibleAmbiguousMentions.add(mention);
					lstPossibleAmbiguousMentionsAndReferences.add(mention);
					//					Word head = mention.getHead();
					//					System.out.println("\t    applied to " + head.UID + "\t" + head.getSurface() + "/" + head.getPos() + "/" + head.getDeptag() + "/" + type);
				}else if(!mention.precessed() && type!=null && (type.equals(mention_type.propernoun) || type.equals(mention_type.noun))) {
					lstPossibleAmbiguousMentionsAndReferences.add(mention);
					//					Word head = mention.getHead();
					//					System.out.println("\t___ applied to " + head.UID + "\t" + head.getSurface() + "/" + head.getPos() + "/" + head.getDeptag() + "/" + type);
				}else {
					Word head = mention.getHead();
					//					System.out.println("\tNOT applied to " + head.UID + "\t" + head.getSurface() + "/" + head.getPos() + "/" + head.getDeptag() + "/" + type);
				}
			}
		}
		Collections.sort(lstPossibleAmbiguousMentionsAndReferences, new Comparator<Mention>() {
			@Override
			public int compare(Mention o1, Mention o2) {
				if(o1.getHead().getSentence().getID() != o2.getHead().getSentence().getID())
					return o1.getHead().getSentence().getID() < o2.getHead().getSentence().getID() ? -1 : 1;
				if(o1.getHead().getId()[0] != o2.getHead().getId()[0])
					return o1.getHead().getId()[0] < o2.getHead().getId()[0] ? -1 : 1;
				return 0;
			}
		});

		List[] aux = {lstPossibleAmbiguousMentions, lstPossibleAmbiguousMentionsAndReferences};
		return aux;
	}

	protected boolean isValidePron(mention_type type, Mention mention) {
		return type!=null && type.equals(mention_type.pron_prs);
	}

	private void doTheSimplification(List<Mention> lstPossibleAmbiguousMentions,
			List<Mention> lstPossibleAmbiguousMentionsAndReferences) {
		for (Mention mention : lstPossibleAmbiguousMentions) {
			mention.setProcessed();
			Set<Mention> entities = mention.getEntity();
			int distance = getDistancePron(mention, entities, lstPossibleAmbiguousMentionsAndReferences);
			if(distance > mindist) {
				Mention closest = Util.getClosest(mention.getEntity(), mention);
				//				System.out.print("changing: "); 
				//					mention.getWords().forEach(w->System.out.print(w.UID+"_"+w.getSurface() + " "));
				//					System.out.print("\t/\t");
				//					closest.getWords().forEach(w->System.out.print(w.UID+"_"+w.getSurface() + " "));
				//					System.out.print(Util.getDistanceWord(mention, closest));
				//					System.out.print("\tfrom:\t");
				//					mention.getEntity().forEach(wl -> wl.getWords().forEach(w->System.out.print(w.getSurface() + "/" + Util.getDistanceWord(mention, wl) + " | ")));
				//					System.out.println();

				Mention newm = new Mention(mention.getId());
				for (Word word : closest) {
					createWord(newm, word);
				}

				mention.getChain().replace(mention, newm);
			}
		}
	}

	protected void createWord(Mention newm, Word word) {
		Word w = new Word(word);
//					if(w.getPos().equals("DET") && w.getMorphology().contains("Poss=Yes"))
//							w.setSurface(Util.transformPossIntoDefArt(w.getSurface()));
		if(w.getPos().equals("DET") && w.getMorphology().contains("Definite=Ind"))
			w.setSurface(Util.transformIndIntoDefArt(w.getSurface()));
		if(DiscourseSimp.showTrace())
			w.setSurface("++D3++" + w.getSurface());
		else
			w.setSurface(w.getSurface());
//		w.setSurface(w.getSurface());
		w.clearMentions();
		w.addMention(newm);
		newm.addWord(w);
	}

	private int getDistancePron(Mention mention, Set<Mention> entities, List<Mention> lstPossibleAmbiguousMentionsAndReferences) {
		int d = 0;
		for (int i = lstPossibleAmbiguousMentionsAndReferences.indexOf(mention)-1; i>=0; i--) {
			Mention ant = lstPossibleAmbiguousMentionsAndReferences.get(i);
			if(ant.getType().equals(mention_type.noun) || ant.getType().equals(mention_type.propernoun)) {
				if(entities.contains(ant))
					return d;
			}else if(ant.getType().equals(mention_type.pron_dem) || ant.getType().equals(mention_type.pron_prs)) {
				if(isAmbigous(mention.getHead(), ant.getHead()))
					d++;
			}
		}
		return -1;
	}

	private List<String> pronPers1s = Arrays.asList("je me m' moi".split(" "));
	private List<String> pronPers2s = Arrays.asList("tu te t' toi".split(" "));
	private List<String> pronPers3sm = Arrays.asList("il on se le l' lui".split(" "));
	private List<String> pronPers3sf = Arrays.asList("elle on se la l' lui".split(" "));
	private List<String> pronPers1p = Arrays.asList("nous".split(" "));
	private List<String> pronPers2p = Arrays.asList("vous".split(" "));
	private List<String> pronPers3pm = Arrays.asList("ils les se leur eux".split(" "));
	private List<String> pronPers3pf = Arrays.asList("elles les se leur elles".split(" "));
	private boolean isAmbigous(Word w1, Word w2) {
		if(w1.getSurface().equalsIgnoreCase(w2.getSurface())) return true;
		if(pronPers1s.contains(w1.getSurface().toLowerCase()) && pronPers1s.contains(w2.getSurface().toLowerCase())) return true;
		if(pronPers2s.contains(w1.getSurface().toLowerCase()) && pronPers2s.contains(w2.getSurface().toLowerCase())) return true;
		if(pronPers3sm.contains(w1.getSurface().toLowerCase()) && pronPers3sm.contains(w2.getSurface().toLowerCase())) return true;
		if(pronPers3sf.contains(w1.getSurface().toLowerCase()) && pronPers3sf.contains(w2.getSurface().toLowerCase())) return true;

		if(pronPers1p.contains(w1.getSurface().toLowerCase()) && pronPers1p.contains(w2.getSurface().toLowerCase())) return true;
		if(pronPers2p.contains(w1.getSurface().toLowerCase()) && pronPers2p.contains(w2.getSurface().toLowerCase())) return true;
		if(pronPers3pm.contains(w1.getSurface().toLowerCase()) && pronPers3pm.contains(w2.getSurface().toLowerCase())) return true;
		if(pronPers3pf.contains(w1.getSurface().toLowerCase()) && pronPers3pf.contains(w2.getSurface().toLowerCase())) return true;

		return false;
	}

}
