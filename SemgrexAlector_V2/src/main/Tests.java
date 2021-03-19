package main;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon.ReplacingType;
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;

public class Tests {

	public static void main(String[] args) {
		try {
			CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
			Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString("/mnt/data/eclipse_workspace/SemgrexAlector_V2/tests"));
			SemgrexPattern semgrex = SemgrexPattern.compile("{tag:NOUN}=subj </.*subj/ {tag:VERB}=verb1 ?>det {tag:DET}=det >>/acl(.*)/ ({}=verb2 >>iobj {lemma:dont}=dont)");
			while(it.hasNext()) {
				SemanticGraph graph = it.next();
				SemgrexMatcher matcher = semgrex.matcher(graph);
				if (matcher.find()) {
					System.out.println(graph.toRecoveredSentenceString());
					IndexedWord dont = matcher.getNode("dont");
					IndexedWord subj = matcher.getNode("subj");
					IndexedWord verb1 = matcher.getNode("verb1");
					IndexedWord verb2 = matcher.getNode("verb2");
					IndexedWord det = matcher.getNode("det");
					
					System.out.println(
							dont + "\n" +
							subj + "\n" +
							verb1 + "\n" +
							verb2 + "\n" +
							det + "\n" 
							);
//					sent = delete dont from sent
					graph = Ssurgeon.delete(graph, dont);
					
//					sent1, sent2 = split subj from sent
					SemanticGraph[] aux = Ssurgeon.split(subj, graph);
					SemanticGraph sent1 = aux[0]; // soigne les oiseaux .
					SemanticGraph sent2 = aux[1]; // le garçon parlé
					System.out.println("A)" + sent1.toRecoveredSentenceString());
					System.out.println(sent1.toString(CoreLabel.OutputFormat.CoNLL));
					System.out.println("B)" + sent2.toRecoveredSentenceString());
					System.out.println(sent2.toString(CoreLabel.OutputFormat.CoNLL));
					
//					sentSubj, sent2 = split verb2 from sent2
					aux = Ssurgeon.split(verb2, sent2);
					SemanticGraph sentSubj = aux[0]; // le garçon
					sent2 = aux[1]; // empty
					
//					sentSubjC = copy sentSubj
					SemanticGraph sentSubjC = Ssurgeon.makeHardCopy(sentSubj);
//					sentVerb = copy sent2
					SemanticGraph sentVerb = Ssurgeon.makeHardCopy(sent2);
					
//					sentDe = insert sentVerb -1 right '1;de;de;ADP;;0;mark' from sentVerb
					IndexedWord newWord = new IndexedWord();
					newWord.setValue("de");
					newWord.setLemma("de");
					newWord.set(CoreAnnotations.PartOfSpeechAnnotation.class,"ADP");
					newWord.set(CoreAnnotations.CoNLLDepTypeAnnotation.class,"mark");
					newWord.set(CoreAnnotations.CoNLLUMisc.class,"_");
					SemanticGraph sentDe = Ssurgeon.insert(sentVerb.getFirstRoot(), newWord, "-1", true, true, sentVerb);
//					sent3 = copy sentDe
					SemanticGraph sent3 = Ssurgeon.makeHardCopy(sentDe);
//					sent1 = insert verb1 -0* sentSubjC from sent1
					sent1 = Ssurgeon.insert(verb1, sentSubjC, "+0*", true, true, sent1);
//					sent3 = insert verb2 +0* sentSubj from sent3
					sent3 = Ssurgeon.insert(verb2, sentSubj, "+0*", true, true, sent3);
					
					/*
					if det begin
						sent3 = replace det (FORM, LEMMA, FEATS) '1;ce;ce;;PronType=Dem;0;' from sent3
					end
					 * */
					if(det != null) {
						IndexedWord auxReplace = new IndexedWord();
						auxReplace.setValue("ce");
						auxReplace.setLemma("ce");
						HashMap<String,String> auxCoNLLUFeats = new HashMap<String,String>();
						auxCoNLLUFeats.put("PronType", "Dem");
						auxReplace.set(CoreAnnotations.CoNLLUFeats.class,auxCoNLLUFeats);
						sent3 = Ssurgeon.replace(det, auxReplace, Arrays.asList(ReplacingType.word, ReplacingType.lemma, ReplacingType.feats), sent3); 
					}
//					IndexedWord newWord = new IndexedWord();
//					newWord.setValue("jaune");
//					newWord.setLemma("jaune");
//					newWord.set(CoreAnnotations.PartOfSpeechAnnotation.class,"ADJ");
//					newWord.set(CoreAnnotations.CoNLLDepTypeAnnotation.class,"nmod");
//					newWord.set(CoreAnnotations.CoNLLUMisc.class,"_");
//					SemanticGraph newG = Ssurgeon.insert(w, newWord, "1", false, true, graph);
//
					System.out.println(sent1.toString(CoreLabel.OutputFormat.CoNLL));
					System.out.println(sent3.toString(CoreLabel.OutputFormat.CoNLL));
					
//					System.out.println("1)" + sent3.toRecoveredSentenceString());
//					System.out.println("2)" + sent1.toRecoveredSentenceString());
//					System.out.println("3)" + sent2.toRecoveredSentenceString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
