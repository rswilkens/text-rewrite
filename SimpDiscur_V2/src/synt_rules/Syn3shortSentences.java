package synt_rules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import corpus_reader.Corpus;
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

public class Syn3shortSentences implements SyntacticSimp {

	////	private static String passive =  "{pos:VERB;conllfeats:/Voice=Pass/}=verb >auxpass {}=auxpass >oblagent {}=passOb >nsubjpass {}=nsubjpass : {}=passOb >case {word:/par/}=passprep";
	////	private static String adv = "{tag:VERB}=verb [ >advmod {tag:ADV}=adv | >oblagent {tag:NOUN}=adv ] : {}=adv ?$+ {tag:PUNCT}=punct";
	private static String conjSentSent = "{$}=root ?>punct {}=punct >conj ({tag:VERB}=verb >cc {tag:CCONJ}=cc >nsubj {}): {tag:PUNCT}=punct2 . {}=cc";
	private static String conjPhrasePhrase = "{tag:VERB}=mainverb > ({tag:VERB;conllfeats:/VerbForm=Inf/}=verb1 >conj ({tag:VERB;conllfeats:/VerbForm=Inf/}=verb2 >cc {tag:CCONJ}=conj) ) : {tag:PUNCT}=comma ?. {tag:CCONJ}=conj";
	////	private static String nenini = "{tag:VERB}=mainverb >advmod {tag:ADV;conllfeats:/Polarity=Neg/}=ne >obj ({}=phrase1 >cc {word:ni}=ni1) : {}=phrase1 >conj ({}=phrase2 >cc {word:ni}=ni2)";
	

	//	private static String relativeClauseQui     = "({tag:VERB}=verb1 == {$}) ?>punct {word:/,/}=comma2   >/(obj(.*)|(nsubj(.*)))/   ( ({}=subj ?>punct {word:/,/}=comma1 ?>>det {}=det ) >>/acl(.*)/ ( {}=verb2 >nsubj {lemma:qui;conllfeats:/PronType=Rel/}=qui))";
	//	private static String relativeClauseDont = "{tag:NOUN}=subj </.*subj/ {tag:VERB}=verb1 ?>det {tag:DET}=det >/acl(.*)/ ({tag:VERB}=verb2 >obl {lemma:dont}=dont)";
	//	private static String relativeClauseOu = "{tag:VERB}=verb1 >obj ( ({tag:NOUN}=obj ?>det {lemma:le}=det ) ?> {tag:ADP}=prep >/acl(.*)/ ( {tag:VERB}=verb2 >obl {lemma:/où/;tag:PRON;conllfeats:/PronType=Rel/}=ou ) ) : {$} ?>punct {tag:PUNCT}=punct";
	//	private static String relativeClauseLequel = "{tag:VERB}=verb1 ?>punct {tag:PUNCT}=punct >obj ({}=obj ?>det {tag:DET}=det ?> {tag:ADP}=badPrep >/acl.*/ ( {tag:VERB}=verb2 >/obl.*/ ( {tag:PRON;lemma:lequel}=lequel > {tag:ADP}=prep ) ) )";
	//
	//	private static String chaque = "{tag:PRON;lemma:chacun}=pron <obj ({tag:VERB} >obj {}=obj2 ) :  {}=pron . {tag:/DET|ADP/} : {}=pron!=={}=obj2";//chuqu(un|une) GN -> GNpluriel ! dep=mod  
	//	private static String tout = "{tag:DET;lemma:tout}=tout <det ({} >det ({}=det2 < {}=gn) ) :  {}=tout . {}=det2";//tout GN -> GNpluriel ! dep=mod

	@Override
	public Corpus simplify(Corpus corpus) {
		try(BufferedWriter out = new BufferedWriter(new FileWriter("tmp1.s4.conll"))){
			out.write(corpus.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			
			StringBuffer buff = new StringBuffer();
			CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
			Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString("tmp1.s4.conll"));
			while(it.hasNext()) {
				SemanticGraph graph = it.next();
//				System.out.println(graph.toRecoveredSentenceString());
				SemgrexPattern semgrex = SemgrexPattern.compile(conjPhrasePhrase);
				SemgrexMatcher matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					conjPhrasePhrase(matcher, buff);
					continue;
				}

				semgrex = SemgrexPattern.compile(conjSentSent);
				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					conjSentSent(matcher, buff);
					continue;
				}

				//else
				buff.append(graph.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");

			}
			BufferedWriter out = new BufferedWriter(new FileWriter("tmp2.s4.conll"));
			out.write(buff.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		corpus = new Corpus("tmp2.s4.conll");
		new File("tmp2.s4.conll").delete();
		new File("tmp1.s4.conll").delete();
		return corpus;
	}

	private static void conjPhrasePhrase(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("conjPhrasePhrase");
		SemanticGraph graph = matcher.getGraph();
		IndexedWord mainverb = matcher.getNode("mainverb");
		IndexedWord verb1 = matcher.getNode("verb1");
		IndexedWord verb2 = matcher.getNode("verb2");
		IndexedWord conj = matcher.getNode("conj");
		IndexedWord comma = matcher.getNode("comma");

		//		System.out.println(mainverb);
		//		System.out.println("\t" + verb1);
		//		System.out.println("\t" + conj);
		//		System.out.println("\t" + verb2);
		//		System.out.println("\t" + comma);

		if(comma != null)
			graph = Ssurgeon.delete(graph, comma);
		graph = Ssurgeon.delete(graph, conj);		

		SemanticGraph[] split = Ssurgeon.split(verb2, graph);
		SemanticGraph graph2 = split[1];
		graph = split[0];//Ssurgeon.delete(graph, verb2);

		split = Ssurgeon.split(verb1, graph);
		SemanticGraph graph1 = split[1];
		graph = split[0];//Ssurgeon.delete(graph, verb1);


		graph1 = Ssurgeon.insert(Ssurgeon.makeHardCopy(mainverb), graph1, "+0", false, true, Ssurgeon.makeHardCopy(graph));
		graph2 = Ssurgeon.insert(mainverb, graph2, "+0", false, true, Ssurgeon.makeHardCopy(graph));

		//		String modified = graph1.toString(CoreLabel.OutputFormat.CoNLL);
		//		System.out.println(modified);
		//		
		//		modified = graph2.toString(CoreLabel.OutputFormat.CoNLL);
		//		System.out.println(modified);
		buff.append(graph1.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");
		buff.append(graph2.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");
	}

	private static void conjSentSent(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("conjSentSent");
		SemanticGraph graph = matcher.getGraph();
		IndexedWord cc = matcher.getNode("cc");
		IndexedWord punct = matcher.getNode("punct");
		IndexedWord punct2 = matcher.getNode("punct2");
		IndexedWord verb = matcher.getNode("verb");

		if( cc!=null /*&& punct!=null */) {
			//			System.out.println(graph.toRecoveredSentenceString());
			//			if(graph.toRecoveredSentenceString().equals("Pierre se crut perdu , et fondit en larmes."))
			//				System.out.println();
			//			System.out.println("\t" + cc);
			//			System.out.println("\t\t" + punct);
			//			System.out.println("\t\t" + verb);

			if(punct2 != null)
				graph = Ssurgeon.delete(graph, punct2);
			IndexedWord newWord = new IndexedWord();
			newWord.setValue(".");
			newWord.set(CoreAnnotations.LemmaAnnotation.class,".");
			newWord.set(CoreAnnotations.PartOfSpeechAnnotation.class,"PUNCT");
			newWord.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "punct");
			newWord.set(CoreAnnotations.CoNLLUMisc.class,"true");
			newWord.set(CoreAnnotations.CoNLLCorefAnnotation.class,"_");

			if(punct!=null)
				graph = Ssurgeon.delete(graph,punct);
			graph = Ssurgeon.delete(graph, cc/*, punct*/);
			
			SemanticGraph[] split = Ssurgeon.split(verb, graph);
			SemanticGraph graph2 = split[1];
			graph = split[0]; //Ssurgeon.delete(graph, verb);
			graph = Ssurgeon.insert(graph.getFirstRoot(), Ssurgeon.makeHardCopy(newWord), "+0*", false, true, graph);


			graph2 = Ssurgeon.insert(graph2.getFirstRoot(), newWord, "+0*", false, true, graph2);		
			graph2.getFirstRoot().set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "root");
			toUpperCase(graph2.getNodeByIndexSafe(1), graph2);
			buff.append(graph.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");
			buff.append(graph2.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");
			//			
			//			String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
			//			System.out.println(modified);
			//			
			//			modified = graph2.toString(CoreLabel.OutputFormat.CoNLL);
			//			System.out.println(modified);
		}

	}

	private static void toLowerCase(IndexedWord node, SemanticGraph graph) {
		Set<IndexedWord> aux = graph.getSubgraphVertices(node);
		for (IndexedWord word : aux) {
			String pos = word.tag();
			if(!pos.equals("PROPN") && !word.value().contentEquals(word.value().toUpperCase()))
				word.setValue(word.value().toLowerCase());
			break;
		}
	}
	private static void toUpperCase(IndexedWord node, SemanticGraph graph) {
		Set<IndexedWord> aux = graph.getSubgraphVertices(node);
		for (IndexedWord word : aux) {
			word.setValue(word.value().substring(0,1).toUpperCase() + word.value().substring(1,word.value().length()));
			break;
		}
	}


}
