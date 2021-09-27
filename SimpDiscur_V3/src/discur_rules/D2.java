package discur_rules;

import corpus_reader.*;

import java.util.ArrayList;
import java.util.List;

public class D2 implements DiscourseSimp {

	@Override
	public Corpus simplify(Corpus corpus) {
		//replace art def p/ art ind IF nao elemento chain ou IF nao primeiro elemento da chain
		// replace quelqu'/quelque/quelques -> des
		// replace ces/cette ->  if first element of a chain UN(E); else LE/A

		List<Mention> lst = getDeterminedMentions(corpus);
		simplify(lst);
//		for (Mention mention : lst) {
//			mention.getWords().forEach(w->System.out.print(w.getSurface() + " "));
//			//			System.out.println(mention.getWords());
//			System.out.println();
//		}

		return corpus;
	}

	private void simplify(List<Mention> lst) {
		List<Word> processed = new ArrayList<Word>();
		for (Mention mention : lst) {
			List<Word> words = new ArrayList<Word>(mention.getWords());
			Word w0 = words.get(0);
			if(processed.contains(w0)) continue;
			
			List<Mention> ments = new ArrayList<Mention>(mention.getChain().getMentions());
			boolean firstOfChain = (ments.get(0).equals(mention));
			boolean allUpper = w0.getSurface().equals(w0.getSurface().toUpperCase());
			boolean firstUpper = w0.getSurface().substring(0, 1).equals(w0.getSurface().substring(0, 1).toUpperCase());
			String newsurface = null;
			switch (w0.getLemma()) {
			case "le":
				newsurface = simplifyLE(w0, firstOfChain);
				break;
			case "un":
				newsurface = simplifyUN(w0, firstOfChain);
				break;
			case "ce":
				newsurface = simplifyCE(w0, firstOfChain);
				break;
			case "quelque":
				newsurface = simplifyQQ(w0);
				break;
			default:
				break;
			}
			if(newsurface!=null) {
				if(allUpper) newsurface = newsurface.toUpperCase();
				else if(firstUpper) newsurface = newsurface.substring(0,1).toUpperCase() + newsurface.substring(1,newsurface.length());
				w0.setSurface(newsurface);
				mention.setProcessed();
			}
			
			processed.add(w0);
		}

	}

	private String simplifyQQ(Word w0) {
		String newsurface = "des";
		if(DiscourseSimp.showTrace())
			return "--QQ--" + newsurface;
		else
			return newsurface;
//		return newsurface;
	}

	private String simplifyCE(Word w0, boolean firstOfChain) {
		String newsurface = null;
		
		switch (w0.getSurface().toLowerCase()) {
		case "ce": 
			if(firstOfChain) newsurface = "un"; 
			else newsurface = "le"; 
			break;
		case "cet": 
			if(firstOfChain) newsurface = "un"; 
			else newsurface = "le"; 
			break;
		case "ces": 
			if(firstOfChain) newsurface = "des"; 
			else newsurface = "les"; 
			break;
		case "cette": 
			if(firstOfChain) newsurface = "une"; 
			else newsurface = "la"; break;

		default:
			System.err.println("check RULE D2. It's missing the " + w0.getSurface());
			break;
		}
		if(DiscourseSimp.showTrace())
			return "--CE--" + newsurface;
		else
			return newsurface;
//		return newsurface;
		

	}
	
	//keep it if first element of a chain; else replace it by « le »
	private String simplifyUN(Word w0, boolean firstOfChain) {
		if(firstOfChain) return null;
		String newsurface = null;
		
		switch (w0.getSurface().toLowerCase()) {
		case "un": newsurface = "le"; break;
		case "une": newsurface = "la"; break;

		default:
			System.err.println("check RULE D2. It's missing the " + w0.getSurface());
			break;
		}
		if(DiscourseSimp.showTrace())
			return "--UN--" + newsurface;
		else
			return newsurface;
//		return newsurface;
		
	}

	//replace art def p/ art ind IF not in a chain or IF not first m in a chain
	private String simplifyLE(Word w0, boolean firstOfChain) {
		if(!firstOfChain) return null;
		String newsurface = null;

		switch (w0.getSurface().toLowerCase()) {
		case "le": newsurface = "un"; break;
		case "la": newsurface = "une"; break;
		case "les": newsurface = "des"; break;
		case "l'":
			if(w0.getMorphology().contains("Gender=Fem"))
				newsurface = "une"; 
			else
				newsurface = "un"; 
			break;
		default:
			System.err.println("check RULE D2. It's missing the " + w0.getSurface());
			break;
		}
		if(DiscourseSimp.showTrace())
			return "--le--" + newsurface;
		else
			return newsurface;
//		return newsurface;
		
	}

	private List<Mention> getDeterminedMentions(Corpus corpus) {
		List<Mention> ret = new ArrayList<Mention>();
		for (CorefChain chain : corpus.getChains()) {
			for (Mention mention : chain) {
				boolean isDET = false;
				boolean isPROPN = false;
				Word w0 = new ArrayList<Word>(mention.getWords()).get(0);
				if(mention.getWords().size()>1 &&
						w0.getPos().equals("DET") &&
						!w0.getMorphology().contains("Poss")) //Poss is processed on D5
					for (Word word : mention.getWords()) {
						if (word.getPos().equals("DET"))
							isDET = true;
						if (word.getPos().equals("PROPN"))
							isPROPN = true;
					}
					
					if(!mention.precessed() && (!(isDET && isPROPN)))
						ret.add(mention);
			}
		}
		return ret;
	}

}
