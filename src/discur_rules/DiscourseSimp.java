package discur_rules;

import corpus_reader.Corpus;

public interface DiscourseSimp {
	public static boolean showTrace() {
		return false;
	}
	public abstract Corpus simplify(Corpus corpus);
	
}
