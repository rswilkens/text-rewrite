package discur_rules;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import corpus_reader.Mention;
import corpus_reader.Word;

public class Util {


	public static mention_type getMentionType(Mention m) {
		Word word = m.getHead();
		switch (word.getPos()) {
		case "PRON":
			if(word.getMorphology().contains("PronType=Prs"))
				return mention_type.pron_prs;
			if(word.getMorphology().contains("PronType=Dem"))
				return mention_type.pron_dem;
			if(word.getMorphology().contains("PronType=Rel"))
				return mention_type.pron_rel;
			
		case "NOUN":
		case "ADJ":
			return mention_type.noun;
		case "PROPN":
			return mention_type.propernoun;
		case "SYM":
			return mention_type.sym;
		case "DET":
			if(word.getMorphology().contains("Poss=Yes"))
				return mention_type.det_poss;
		}
		return null;
	}

	public static Word getHead(Mention m) {
		Map<String, String> tree = new HashMap<String, String>();
		Map<String, Word> aux = new HashMap<String, Word>();
		for (Word word : m) {
			if(word.getId().length>1) continue;
			String id = word.getId()[0] + "";
			aux.put(id, word);
			String parent = word.getDep();
			if(parent.equals("0")) return word;
//			if(!tree.containsKey(parent))
			tree.put(id, parent);
		}

		String topid = null;
		for (String id : tree.keySet()) 
			if(topid==null) topid = id;
			else if(!tree.containsKey(tree.get(id)))
				return aux.get(id);
		return aux.get(topid);
	}

	public static Mention getClosest(Set<Mention> entity, Mention mention) {
		Mention ret = null;
		int dist = -1;
		//		int distS = 0;
		//		int distW = 0;
		for (Mention ent : entity) {
			int distanceWord = getDistanceWord(mention, ent);
			if(ret == null || distanceWord<dist) {
				ret = ent;
				dist = distanceWord;
				//				if(mention.getHead().getSentence().getID()==ent.getHead().getSentence().getID())
				//					distS = 0;
				//				else
				//					distS = Math.abs(mention.getHead().getSentence().getID()-ent.getHead().getSentence().getID());
				//				if
			}
		}
		return ret;
	}
	public static int getDistanceWord(Mention mention, Mention entity) {
		//return Math.abs(m1.getHead().UID - m2.getHead().UID);
		if(entity.getHead().UID < mention.getHead().UID)
			return Math.abs(entity.getHead().UID - mention.getHead().UID);
		return Integer.MAX_VALUE;
	}

//	private static boolean closest(Mention m1, Mention m2, Mention mention) {
//		int ds1 = Math.abs(mention.getHead().getSentence().getID()-m1.getHead().getSentence().getID());
//		int ds2 = Math.abs(mention.getHead().getSentence().getID()-m2.getHead().getSentence().getID());
//		if(ds2<ds1) return true;
//
//		//		if(m1.getHead().getSentence().getID()<=mention.getHead().getSentence().getID() && m2.getHead().getSentence().getID()<=mention.getHead().getSentence().getID()) {
//		ds1 = Math.abs(mention.getHead().getSentence().getID()-m1.getHead().getSentence().getID());
//		ds2 = Math.abs(mention.getHead().getId()[0]-m2.getHead().getId()[0]);
//		if(ds2<ds1) return true;
//		//		}
//		//		
//		//		if(m1.getHead().getSentence().getID()>mention.getHead().getSentence().getID() && m2.getHead().getSentence().getID()>mention.getHead().getSentence().getID()) {
//		//			ds1 = Math.abs(mention.getHead().getSentence().getID()-m1.getHead().getSentence().getID());
//		//			ds2 = Math.abs(mention.getHead().getId()[0]-m2.getHead().getId()[0]);
//		//			if(ds2<ds1) return true;
//		//			
//		//		}
//		//			
//		return false;
//	}

	public static String transformIndIntoDefArt(String surface) {
		if(surface.equalsIgnoreCase("un")) return "le";
		if(surface.equalsIgnoreCase("une")) return "la";
		if(surface.equalsIgnoreCase("des")) 
			return "les";
		return surface;
	}
	public static String transformPossIntoDefArt(String surface) {
		switch (surface) {
		case "mon":
		case "ton":
		case "son":
			return "le";
		case "ma":
		case "ta":
		case "sa":
			return "la";
		case "mes":
		case "tes":
		case "ses":
		case "notre":
		case "votre":
		case "leur":
		case "nos":
		case "vos":
		case "leurs":
			return "les";

		default:
			break;
		}
		return surface;
	}

}
