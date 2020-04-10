package discur_rules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;

public class D4 implements DiscourseSimp {

	private static String relativeClauseQui     = "({tag:VERB}=verb1 == {$}) ?>punct {word:/,/}=comma2   >/(obj(.*)|(nsubj(.*)))/   ( ({}=subj ?>punct {word:/,/}=comma1 ?>>det {}=det ) >>/acl(.*)/ ( {}=verb2 >nsubj {lemma:qui;conllfeats:/PronType=Rel/}=qui))";
	private static String relativeClauseDont = "{tag:NOUN}=subj </.*subj/ {tag:VERB}=verb1 ?>det {tag:DET}=det >/acl(.*)/ ({tag:VERB}=verb2 >obl {lemma:dont}=dont)";
	private static String relativeClauseOu = "{tag:VERB}=verb1 >obj ( ({tag:NOUN}=obj ?>det {lemma:le}=det ) ?> {tag:ADP}=prep >/acl(.*)/ ( {tag:VERB}=verb2 >obl {lemma:/où/;tag:PRON;conllfeats:/PronType=Rel/}=ou ) ) : {$} ?>punct {tag:PUNCT}=punct";
	private static String relativeClauseLequel = "{tag:VERB}=verb1 ?>punct {tag:PUNCT}=punct >obj ({}=obj ?>det {tag:DET}=det ?> {tag:ADP}=badPrep >/acl.*/ ( {tag:VERB}=verb2 >/obl.*/ ( {tag:PRON;lemma:lequel}=lequel > {tag:ADP}=prep ) ) )";

	private static String chaque = "{tag:PRON;lemma:chacun}=pron <obj ({tag:VERB} >obj {}=obj2 ) :  {}=pron . {tag:/DET|ADP/} : {}=pron!=={}=obj2";//chuqu(un|une) GN -> GNpluriel ! dep=mod  
	private static String tout = "{tag:DET;lemma:tout}=tout <det ({} >det ({}=det2 < {}=gn) ) :  {}=tout . {}=det2";//tout GN -> GNpluriel ! dep=mod

	@Override
	public Corpus simplify(Corpus corpus) {
		try(BufferedWriter out = new BufferedWriter(new FileWriter("tmp1.d4.conll"))){
			out.write(corpus.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuffer buff = new StringBuffer();
			CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
			Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString("tmp1.d4.conll"));
			while(it.hasNext()) {
				SemanticGraph graph = it.next();
//				System.out.println(graph.toRecoveredSentenceString());
				SemgrexPattern semgrex = SemgrexPattern.compile(relativeClauseQui);
				SemgrexMatcher matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					relativeClauseQui(matcher, buff);
					continue;
				}
				semgrex = SemgrexPattern.compile(relativeClauseOu);
				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					relativeClauseOu(matcher, buff);
					continue;
				}

				semgrex = SemgrexPattern.compile(relativeClauseLequel);
				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					relativeClauseLequel(matcher, buff);
					continue;
				}

				semgrex = SemgrexPattern.compile(relativeClauseDont);
				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					relativeClauseDont(matcher, buff);
					continue;
				}

				semgrex = SemgrexPattern.compile(chaque);
				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					chaque(matcher, buff);
					continue;
				}

				semgrex = SemgrexPattern.compile(tout);
				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				if(matcher.find()) {
					tout(matcher, buff);
					continue;
				}



				//				semgrex = SemgrexPattern.compile(conjPhrasePhrase);
				//				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				//				if(matcher.find()) {
				//					conjPhrasePhrase(matcher, buff);
				//					continue;
				//				}
				//				
				//				semgrex = SemgrexPattern.compile(conjSentSent);
				//				matcher = semgrex.matcher(Ssurgeon.makeHardCopy(graph));
				//				if(matcher.find()) {
				//					conjSentSent(matcher, buff);
				//					continue;
				//				}

				//else
				buff.append(graph.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");

			}
			BufferedWriter out = new BufferedWriter(new FileWriter("tmp2.d4.conll"));
			out.write(buff.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		corpus = new Corpus("tmp2.d4.conll");
		new File("tmp2.d4.conll").delete();
		new File("tmp1.d4.conll").delete();
		return corpus;
	}

	private void tout(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("tout");
		SemanticGraph graph = matcher.getGraph();
		IndexedWord tout = matcher.getNode("tout");
		//		IndexedWord det2 = matcher.getNode("det2");
		IndexedWord gn = matcher.getNode("gn");

		String coref = tout.get(CoreAnnotations.CoNLLCorefAnnotation.class);

		graph = Ssurgeon.delete(graph, tout);

		List<IndexedWord> children = graph.getChildList(gn);
		IndexedWord firstChild = null;
		for (int i = 0; i < children.size(); i++) {
			IndexedWord c = children.get(i);
			Map<String, String> m = c.get(CoreAnnotations.CoNLLUFeats.class);
			m.put("Number","Plur");
			c.setWord("*"+c.word());
//			c.set(CoreAnnotations.CoNLLUMisc.class, "false");
			for (IndexedWord aux : graph.getChildList(c)) 
				if(!children.contains(aux))
					children.add(aux);
			if(firstChild==null) firstChild = c;
			else if(firstChild.index()>c.index()) firstChild = c;
		}
		if(firstChild.get(CoreAnnotations.CoNLLCorefAnnotation.class).equals("_"))
			firstChild.set(CoreAnnotations.CoNLLCorefAnnotation.class,coref);
		else
			firstChild.set(CoreAnnotations.CoNLLCorefAnnotation.class,
					firstChild.get(CoreAnnotations.CoNLLCorefAnnotation.class) + "|" + coref);
		buff.append(graph.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");
	}

	private void chaque(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("chaque");
		SemanticGraph graph = matcher.getGraph();
		IndexedWord pron = matcher.getNode("pron");
		IndexedWord obj2 = matcher.getNode("obj2");

		String coref = pron.get(CoreAnnotations.CoNLLCorefAnnotation.class);

		graph = Ssurgeon.delete(graph, pron);

		List<IndexedWord> children = graph.getChildList(obj2);
		IndexedWord firstChild = null;
		for (int i = 0; i < children.size(); i++) {
			IndexedWord c = children.get(i);
			Map<String, String> m = c.get(CoreAnnotations.CoNLLUFeats.class);
			m.put("Number","Plur");
			c.setWord("*"+c.word());
//			c.set(CoreAnnotations.CoNLLUMisc.class, "false");
			for (IndexedWord aux : graph.getChildList(c)) 
				if(!children.contains(aux))
					children.add(aux);
			if(firstChild==null) firstChild = c;
			else if(firstChild.index()>c.index()) firstChild = c;
		}
		if(firstChild.get(CoreAnnotations.CoNLLCorefAnnotation.class).equals("_"))
			firstChild.set(CoreAnnotations.CoNLLCorefAnnotation.class,coref);
		else
			firstChild.set(CoreAnnotations.CoNLLCorefAnnotation.class,
					firstChild.get(CoreAnnotations.CoNLLCorefAnnotation.class) + "|" + coref);

		buff.append(graph.toString(CoreLabel.OutputFormat.CoNLLcoref)).append("\n");
	}

	private static void relativeClauseLequel(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("relativeClauseLequel");
		SemanticGraph graph = matcher.getGraph();
		//		IndexedWord verb1 = matcher.getNode("verb1");
		IndexedWord verb2 = matcher.getNode("verb2");
		//		IndexedWord lequel = matcher.getNode("lequel");
		//		IndexedWord obj = matcher.getNode("obj");
		//		IndexedWord prep = matcher.getNode("prep");
		//		IndexedWord punct = matcher.getNode("punct");
		//		IndexedWord det = matcher.getNode("det");
		//		IndexedWord badPrep = matcher.getNode("badPrep");
		////		
		//		System.out.println(obj + "\t" + prep + "\t" + punct);
		//		System.out.println("\t" + verb1);
		//		System.out.println("\t" + verb2);
		//		System.out.println("\t" + lequel);

		graph = Ssurgeon.delete(graph, verb2);
		String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
		buff.append(modified).append("\n");

	}

	private void relativeClauseDont(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("relativeClauseDont");
		SemanticGraph graph = matcher.getGraph();
		//		IndexedWord subj = matcher.getNode("subj");
		//		IndexedWord verb1 = matcher.getNode("verb1");
		IndexedWord verb2 = matcher.getNode("verb2");
		//		IndexedWord dont = matcher.getNode("dont");
		//		IndexedWord det = matcher.getNode("det");

		//		System.out.println(subj + "\t" + det);
		//		System.out.println("\t" + verb1);
		//		System.out.println("\t" + verb2);
		//		System.out.println("\t" + dont);

		graph = Ssurgeon.delete(graph, verb2);
		String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
		buff.append(modified).append("\n");

	}

	private static void relativeClauseOu(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("relativeClauseOu");
		SemanticGraph graph = matcher.getGraph();
		//		IndexedWord verb1 = matcher.getNode("verb1");
		IndexedWord verb2 = matcher.getNode("verb2");
		//		IndexedWord ou = matcher.getNode("ou");
		//		IndexedWord obj = matcher.getNode("obj");
		//		IndexedWord prep = matcher.getNode("prep");
		//		IndexedWord det = matcher.getNode("det");
		//		IndexedWord punct = matcher.getNode("punct");


		//		System.out.println(obj + "\t" + prep + "\t" + det);
		//		System.out.println("\t" + verb1);
		//		System.out.println("\t" + verb2);
		//		System.out.println("\t" + ou);

		graph = Ssurgeon.delete(graph, verb2);
		String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
		buff.append(modified).append("\n");

		//		graph = Ssurgeon.delete(graph, ou);
		//				
		//		SemanticGraph[] split = Ssurgeon.split(obj, graph);
		//		graph = null;
		//		
		//		SemanticGraph graph1 = split[0]; // verb1
		//		SemanticGraph graph2 = split[1]; // verb2 + obj
		//		
		//		split = Ssurgeon.split(verb2, graph2);
		//		graph2 = null;
		//		
		//		graph2 = split[1]; // verb2
		//		SemanticGraph graphObj = split[0];
		//		
		//		if(punct==null)
		//			graph1 = Ssurgeon.insert(verb1, Ssurgeon.makeHardCopy(graphObj), "+0*", false, true, graph1);
		//		else
		//			graph1 = Ssurgeon.insert(verb1, Ssurgeon.makeHardCopy(graphObj), "-1*", true, true, graph1);
		//		
		//		if(prep == null) {
		//			prep = new IndexedWord();
		//			prep.setValue("dans");
		//			prep.set(CoreAnnotations.LemmaAnnotation.class, "dans");
		//			prep.setTag("ADP");
		//			prep.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "case");
		//			
		//			graphObj = Ssurgeon.insert(obj, prep, "-0*", true, true, graphObj);
		//		}
		//		
		//		graph2 = Ssurgeon.insert(verb2, graphObj, "+0*", false, true, graph2);
		//		
		//		if(det != null){
		//			IndexedWord newWord = new IndexedWord();
		//			newWord.setValue("?");
		//			newWord.set(CoreAnnotations.LemmaAnnotation.class,"ce");
		//			HashMap<String, String> map = new HashMap<String, String>();
		//			map.put("Definite", "-");
		//			map.put("PronType", "Dem");
		//			newWord.set(CoreAnnotations.CoNLLUFeats.class, map);
		//			graph2 = Ssurgeon.replace(det, newWord, Arrays.asList(ReplacingType.word, ReplacingType.lemma, ReplacingType.feats), graph2);
		//		}
		//		graph2 = Ssurgeon.replaceRootDepTag("root", graph2);
		//		
		//		toUpperCase(verb2, graph2);
		//		
		//		String modified = graph1.toString(CoreLabel.OutputFormat.CoNLL);
		//		System.out.println(modified);
		//		
		//		modified = graph2.toString(CoreLabel.OutputFormat.CoNLL);
		//		System.out.println(modified);
	}

	private static void relativeClauseQui(SemgrexMatcher matcher, StringBuffer buff) throws Exception {
		System.out.println("relativeClauseQui");
		SemanticGraph graph = matcher.getGraph();
		//		IndexedWord verb1 = matcher.getNode("verb1");
		//		IndexedWord subj = matcher.getNode("subj");
		IndexedWord verb2 = matcher.getNode("verb2");
		//		IndexedWord qui = matcher.getNode("qui");
		//		IndexedWord det = matcher.getNode("det");
		IndexedWord comma1 = matcher.getNode("comma1");
		IndexedWord comma2 = matcher.getNode("comma2");


		if(comma1 != null) graph = Ssurgeon.delete(graph, comma1);
		if(comma2 != null) graph = Ssurgeon.delete(graph, comma2);

		graph = Ssurgeon.delete(graph, verb2);


		String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
		buff.append(modified).append("\n");


		//		
		//		graph = Ssurgeon.delete(graph, qui);
		//		
		//		SemanticGraph[] split = Ssurgeon.split(subj, graph);
		//		graph=null;
		//		
		//		SemanticGraph graph1 = split[0]; //verb1
		//		SemanticGraph graph2 = split[1]; //subj + verb2
		//		
		//		split = Ssurgeon.split(verb2, graph2);
		//		graph2=null;
		//		
		//		SemanticGraph graphSubj = split[0];
		//		graph2 = split[1]; //verb2
		//		
		//		if(det != null)
		//			graph1 = Ssurgeon.insert(verb1, Ssurgeon.makeHardCopy(graphSubj), "-1*", true, true, graph1);
		//		else
		//			graph1 = Ssurgeon.insert(verb1, Ssurgeon.makeHardCopy(graphSubj), "0*", true, true, graph1);
		//		
		//		if(requiredDet(graph2)) {
		//			String gender = graphSubj.getFirstRoot().get(CoreAnnotations.CoNLLUFeats.class).get("Gender");
		//			String number = graphSubj.getFirstRoot().get(CoreAnnotations.CoNLLUFeats.class).get("Number");
		//			IndexedWord newdet = new IndexedWord();
		//			if(number==null || number.equals("Sing"))
		//				if(gender != null && gender.equals("Fem")) 
		//					newdet.setValue("la");
		//				else
		//					newdet.setValue("le");
		//			else
		//				newdet.setValue("les");
		//			newdet.set(CoreAnnotations.LemmaAnnotation.class, "le");
		//			newdet.setTag("DET");
		//			HashMap<String, String> feats = new HashMap<String, String>();
		//			feats.put("Gender", gender);
		//			feats.put("Number", number);
		//			newdet.set(CoreAnnotations.CoNLLUFeats.class, feats);
		//			newdet.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "det");
		//			newdet.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, graphSubj.getFirstRoot().get(CoreAnnotations.IndexAnnotation.class));
		//			newdet.set(CoreAnnotations.CoNLLCorefAnnotation.class, graphSubj.getFirstRoot().get(CoreAnnotations.CoNLLCorefAnnotation.class));
		//			
		//			graphSubj = Ssurgeon.insert(graphSubj.getFirstRoot(), newdet, "-0*", true, true, graphSubj);
		//		}
		//		graph2 = Ssurgeon.insert(verb2, Ssurgeon.makeHardCopy(graphSubj), "-0*", true, true, graph2);
		//		
		//		IndexedWord punct = new IndexedWord();
		//		punct.setValue(".");
		//		punct.set(CoreAnnotations.LemmaAnnotation.class, ".");
		//		punct.setTag("PUNCT");
		//		punct.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "punct");
		//		punct.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, graph2.getFirstRoot().get(CoreAnnotations.IndexAnnotation.class));
		//		graph2 = Ssurgeon.insert(verb2, punct, "+0*", false, true, graph2);
		//		
		//		String modified = graph1.toString(CoreLabel.OutputFormat.CoNLL);
		//		System.out.println(modified);
		//		
		//		modified = graph2.toString(CoreLabel.OutputFormat.CoNLL);
		//		System.out.println(modified);
	}


//	private static void toLowerCase(IndexedWord node, SemanticGraph graph) {
//		Set<IndexedWord> aux = graph.getSubgraphVertices(node);
//		for (IndexedWord word : aux) {
//			String pos = word.tag();
//			if(!pos.equals("PROPN") && !word.value().contentEquals(word.value().toUpperCase()))
//				word.setValue(word.value().toLowerCase());
//			break;
//		}
//	}
//	private static void toUpperCase(IndexedWord node, SemanticGraph graph) {
//		Set<IndexedWord> aux = graph.getSubgraphVertices(node);
//		for (IndexedWord word : aux) {
//			word.setValue(word.value().substring(0,1).toUpperCase() + word.value().substring(1,word.value().length()));
//			break;
//		}
//	}


}
