package main;

import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.semgraph.semgrex.NodePattern;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.semgraph.semgrex.VariableStrings;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.TypesafeMap;
import edu.stanford.nlp.util.TypesafeMap.Key;
import edu.stanford.nlp.util.logging.Redwood;

/**
 * A small demo that shows how to convert a tree to a SemanticGraph
 * and then run a SemgrexPattern on it
 *
 * @author John Bauer
 */
public class SemgrexDemo  {

	/** A logger for this class */
	private static final Redwood.RedwoodChannels log = Redwood.channels(SemgrexDemo.class);

	private SemgrexDemo() {} // just static main

	public static void main(String[] args) throws IOException {
		CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
		/**
		 * The modified version of the Semgrex removes the . and _ symbols from the feats and detRel columns, during the file reading 
		 */
		Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString("examples/semgrexTest.conll"));

//		SemgrexPattern semgrex = SemgrexPattern.compile("{pos:/.*T/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/Les/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*T/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{conllfeats:/gender=.*/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{conllfeats:/gender=.*;p=3/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*T/;conllfeats:/s=s|w/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*T/;conllfeats:/s=s|w;s=s/}=A");
//		SemgrexPattern semgrex = SemgrexPattern.compile("{pos:/V.*/}=Phrase1 <coord {}=C < {pos:/V.*/}=Phrase2");
		SemgrexPattern semgrex = SemgrexPattern.compile("{pos:/V.*/}=Phrase1 >coord ({}=C >depcoord {pos:/V.*/}=Phrase2)");
		int count =1;
		while(it.hasNext()) {
			log.info("++++++++++++++++++++++++");
			log.info("++  Sentence: " + count++);
			log.info("++++++++++++++++++++++++");
			doIt(it.next(), semgrex);
		}
		
	}

	private static void doIt(SemanticGraph graph, SemgrexPattern semgrex) {
		// Alternatively, this could have been the Chinese params or any
		// other language supported.  As of 2014, only English and Chinese
		//    TreebankLangParserParams params = new EnglishTreebankParserParams();
		//    GrammaticalStructureFactory gsf = params.treebankLanguagePack().grammaticalStructureFactory(params.treebankLanguagePack().punctuationWordRejectFilter(), params.typedDependencyHeadFinder());
		//
		//    GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		
		
		SemgrexMatcher matcher = semgrex.matcher(graph);
		// This will produce two results on the given tree: "likes" is an
		// ancestor of both "dog" and "my" via the nsubj relation
		
		while (matcher.find()) {
//			log.info(matcher.getNode("A") + " <<nsubj " + matcher.getNode("B"));
			IndexedWord w = matcher.getNode("A");
			if(w!=null) {
				log.info(w.value() + "\t" + w.tag() + "\t" + w.get(CoreAnnotations.CoNLLUFeats.class));
			}else {
				log.info("---------------------");
				log.info("--  ORIGINAL         ");
				log.info("---------------------");
				log.info(graph);
				log.info("---------------------");
				log.info("--  PATTERN");
				log.info("---------------------");
				w = matcher.getNode("Phrase1");
				IndexedWord w2 = matcher.getNode("Phrase2");
				IndexedWord cc = matcher.getNode("C");
				log.info(w.value() + "\t" + w.tag() + "\t" + w.get(CoreAnnotations.CoNLLUFeats.class));
				log.info("\t" + cc.value() + "\t" + cc.tag() + "\t" + cc.get(CoreAnnotations.CoNLLUFeats.class));
				log.info("\t" + w2.value() + "\t" + w2.tag() + "\t" + w2.get(CoreAnnotations.CoNLLUFeats.class));
				
				
				/*log.info("+++++++++++++++++++++");
				log.info("++  DELETE Phrase2");
				graph = Delete.evaluate(w2, graph);
				
				log.info("---------------------");
				log.info("--  MODIFIED         ");
				log.info("---------------------");
				log.info(graph);*/
				
				/*log.info("+++++++++++++++++++++");
				log.info("++  Excise Phrase2 C");
				graph = Excise.evaluate(w2, cc, graph);
				
				log.info("---------------------");
				log.info("--  Excise         ");
				log.info("---------------------");*/
				
//				log.info("+++++++++++++++++++++");
//				log.info("++  Prune Phrase2");
//				graph = Prune.evaluate(w2, graph);
//				
				
//				log.info("---------------------");
//				log.info("--  Move         ");
//				log.info("---------------------");
//				
//				log.info("+++++++++++++++++++++");
//				log.info("++  Move Phrase2 $+ Phrase1");
//				try {
//					graph = Move.evaluate(w, "", cc, graph);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
////				Adjoin
////				
////				split ???
////				intesert???
//						
//				log.info("---------------------");
//				log.info("--  MODIFIED         ");
//				log.info("---------------------");
//				log.info(graph);
			}
			
			System.out.println();
//			log.info(matcher.getNode("A").get(CoreAnnotations.PartOfSpeechAnnotation.class));
			
//			log.info(matcher.getNode("A").toString(CoreLabel.OutputFormat.ALL));
//			System.out.println();

		}
		log.info("------------------");
	}

}
