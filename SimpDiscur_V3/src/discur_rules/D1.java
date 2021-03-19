package discur_rules;

import corpus_reader.Mention;
import corpus_reader.Word;

public class D1 extends D3 {

	public D1() {
		mindist = -1;
	}

	@Override
	protected boolean isValidePron(mention_type type, Mention mention) {
		return !mention.precessed() && type!=null && type.equals(mention_type.pron_prs) && mention.getHead().getDeptag().contains("subj");
	}

	@Override
	protected void createWord(Mention newm, Word word) {
		Word w = new Word(word);
		if(w.getPos().equals("DET") && w.getMorphology().contains("Definite=Ind"))
			w.setSurface(Util.transformIndIntoDefArt(w.getSurface()));
		if(DiscourseSimp.showTrace())
			w.setSurface("++D1++" + w.getSurface());
		else
			w.setSurface(w.getSurface());
//		w.setSurface(w.getSurface());
		w.clearMentions();
		w.addMention(newm);
		newm.addWord(w);
	}

}
