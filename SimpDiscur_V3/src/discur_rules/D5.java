package discur_rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import corpus_reader.CorefChain;
import corpus_reader.Corpus;
import corpus_reader.Mention;
import corpus_reader.Word;

public class D5 implements DiscourseSimp {

	public D5() {
		
	}
	
	@Override
	public Corpus simplify(Corpus corpus) {
			
		List<Mention> lst = getDetPossMentions(corpus);
		boolean mentHead = false;
		simplifyPoss(lst,mentHead);

		return corpus;
	}
	private void simplifyPoss(List<Mention> lst, boolean mentHead) {
		for (Mention mention : lst) {
			Set<Mention> entities = mention.getEntity();
			Mention closest = Util.getClosest(entities, mention);
			Mention newm = new Mention(mention.getId());
			if(mentHead) {
				Word aux = null;
				if(closest.getType().equals(mention_type.noun)) {
					Word det = null;
					if(closest.getHead().getMorphology().contains("Gender=Fem") && closest.getHead().getMorphology().contains("Number=Plur"))
						det = createWord(newm, 
								new Word(
										(new ArrayList<Word>(mention.getWords()).get(0).getId()[0] + "	les	le	DET	_	Definite=Def|Gender=Fem|Number=Plur|PronType=Art	" + mention.getHead().getDep() + "	det	_	_").split("\t")
										, mention.getHead().getSentence()
										, closest.getHead().getMentions())); 
					else if(closest.getHead().getMorphology().contains("Number=Plur"))
						det = createWord(newm, 
								new Word(
										(new ArrayList<Word>(mention.getWords()).get(0).getId()[0] + "	les	le	DET	_	Definite=Def|Gender=Masc|Number=Plur|PronType=Art	" + mention.getHead().getDep() + "	det	_	_").split("\t")
										, mention.getHead().getSentence()
										, closest.getHead().getMentions()));
					else if(closest.getHead().getMorphology().contains("Gender=Fem"))
						det = createWord(newm, 
								new Word(
										(new ArrayList<Word>(mention.getWords()).get(0).getId()[0] + "	la	le	DET	_	Definite=Def|Gender=Fem|Number=Sing|PronType=Art	" + mention.getHead().getDep() + "	det	_	_").split("\t")
										, mention.getHead().getSentence()
										, closest.getHead().getMentions())); 
					else
						det = createWord(newm, 
								new Word(
										(new ArrayList<Word>(mention.getWords()).get(0).getId()[0] + "	le	le	DET	_	Definite=Def|Gender=Masc|Number=Sing|PronType=Art	" + mention.getHead().getDep() + "	det	_	_").split("\t")
										, mention.getHead().getSentence()
										, closest.getHead().getMentions()));
					int[] idaux = {closest.getHead().getId()[0]-1};
					det.setId(idaux);
					createWord(newm, det);
					aux = createWord(newm, closest.getHead());
				}else if(closest.getType().equals(mention_type.propernoun))
					aux = createWord(newm, closest.getHead());
				newm.setChain(closest.getChain());
				newm.setHead(aux);
			}else
				for (Word word : closest) 
					createWord(newm, word);
			mention.setProcessed();
			mention.getChain().replace(mention, newm);
		}

	}

	protected Word createWord(Mention newm, Word word) {
		Word w = new Word(word);
		//					if(w.getPos().equals("DET") && w.getMorphology().contains("Poss=Yes"))
		//							w.setSurface(Util.transformPossIntoDefArt(w.getSurface()));
		if(w.getPos().equals("DET") && w.getMorphology().contains("Poss")) 
			w.setSurface(Util.transformPossIntoDefArt(w.getSurface()));
		if(DiscourseSimp.showTrace())
			w.setSurface("++D5++" + w.getSurface());
		else
			w.setSurface(w.getSurface());
//		w.setSurface(w.getSurface());
		w.clearMentions();
		w.addMention(newm);
		newm.addWord(w);
		return w;
	}

	private List<Mention> getDetPossMentions(Corpus corpus) {
		List<Mention> ret = new ArrayList<Mention>();
		for (CorefChain chain : corpus.getChains()) {
			for (Mention mention : chain) {
				Word w0 = new ArrayList<Word>(mention.getWords()).get(0);
				if(mention.getWords().size()>=2 &&
						w0.getPos().equals("DET") &&
						w0.getMorphology().contains("Poss")) 
					if(mention.getEntity().size()>=1)
						if(!mention.precessed())
							ret.add(mention);
			}
		}
		return ret;
	}
}
