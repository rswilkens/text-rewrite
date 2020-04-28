package edu.stanford.nlp.semgraph.semgrex.ssurgeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;

public class SsurgeonTestsInsertTrees {
	
	private String corpus = "examples/treebankCONV.conll";//"examples/treebankCONV.conll";
	private Iterator<SemanticGraph> it;
	private static SemanticGraph testGraph;

	void loadBaseGraph() {
		List<IndexedWord> words = new ArrayList<IndexedWord>();
		testGraph = new SemanticGraph();
		for (int i = 1; i <= 7; i++) {
			IndexedWord w = new IndexedWord();
			w.set(CoreAnnotations.IndexAnnotation.class, i);
			w.setValue("ow" + i); 
			w.set(CoreAnnotations.LemmaAnnotation.class, "ol" + i);
			w.setTag("ot" + i);
			w.set(CoreAnnotations.CoNLLDepTypeAnnotation.class,"oDep"+i);
			words.add(w);
			testGraph.addVertex(w);
		}
		GrammaticalRelation d = new GrammaticalRelation(Language.UniversalEnglish, "Other1", "Other1", null);
		GrammaticalRelation p = new GrammaticalRelation(Language.UniversalEnglish, "Other2", "Other2", null);
		testGraph.addEdge(words.get(3), words.get(0), d, 0, false); words.get(0).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 4);
		testGraph.addEdge(words.get(4), words.get(3), p, 0, false); words.get(3).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 5);
		testGraph.addEdge(words.get(2), words.get(1), d, 0, false); words.get(1).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 3);
		testGraph.addEdge(words.get(3), words.get(2), d, 0, false); words.get(2).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 4);
		testGraph.addEdge(words.get(6), words.get(5), d, 0, false); words.get(5).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 7);
		testGraph.addEdge(words.get(4), words.get(6), d, 0, false); words.get(6).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 5);
		
		testGraph.setRoot(words.get(4)); words.get(4).set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 0);
	}
	@BeforeEach
	void loadCorpus() throws IOException {
		CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
		it = reader.getIterator(IOUtils.readerFromString(corpus));
		if(testGraph==null)
			loadBaseGraph();
		
		/*		
		"1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
		"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
		"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
		"4	w4	w4	t4	_	_	5	N	_	_\n" + 
		"5	w5	w5	t5	_	_	3	P	_	_\n" + 
		"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
		"7	w7	w7	t7	_	_	9	P	_	_\n" + 
		"8	w8	w8	t8	_	_	7	N	_	_\n" + 
		"9	w9	w9	t9	_	_	6	D2	_	_\n" +
		"10	w10	w10	t10	_	_	1	P	_	_\n";

"1	w1	w1	t1	_	_	2	D1	_	_\n" +
"2	w2	w2	t2	_	_	0	root	_	_\n" +
"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
"5	w5	w5	t5	_	_	4	N	_	_\n" + 
"6	w6	w6	t6	_	_	2	P	_	_\n" + 
"7	w7	w7	t7	_	_	8	N	_	_\n" + 
"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
"10	w10	w10	t10	_	_	6	P1	_	_\n";
		 */
	}

	//replace root
	@Test
	void insertTree() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"4	ow1	ol1	ot1	_	_	7	oDep1	_	_\n" + 
				"5	ow2	ol2	ot2	_	_	6	oDep2	_	_\n" + 
				"6	ow3	ol3	ot3	_	_	7	oDep3	_	_\n" + 
				"7	ow4	ol4	ot4	_	_	8	oDep4	_	_\n" + 
				"8	ow5	ol5	ot5	_	_	3	oDep5	_	_\n" + 
				"9	ow6	ol6	ot6	_	_	10	oDep6	_	_\n" + 
				"10	ow7	ol7	ot7	_	_	8	oDep7	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	3	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	ow1	ol1	ot1	_	_	10	oDep1	_	_\n" + 
				"8	ow2	ol2	ot2	_	_	9	oDep2	_	_\n" + 
				"9	ow3	ol3	ot3	_	_	10	oDep3	_	_\n" + 
				"10	ow4	ol4	ot4	_	_	11	oDep4	_	_\n" + 
				"11	ow5	ol5	ot5	_	_	6	oDep5	_	_\n" + 
				"12	ow6	ol6	ot6	_	_	13	oDep6	_	_\n" + 
				"13	ow7	ol7	ot7	_	_	11	oDep7	_	_\n" + 
				"14	w7	w7	t7	_	_	15	N	_	_\n" + 
				"15	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";
		assert insert("{word:/w3/}=w", str1, "{word:/w6/}=w", str2, new Random().nextBoolean(), "+0", true, "insertTree");
	}


	@Test
	void ssurgeonInsert1w_0PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	ow1	ol1	ot1	_	_	6	oDep1	_	_\n" + 
				"4	ow2	ol2	ot2	_	_	5	oDep2	_	_\n" + 
				"5	ow3	ol3	ot3	_	_	6	oDep3	_	_\n" + 
				"6	ow4	ol4	ot4	_	_	7	oDep4	_	_\n" + 
				"7	ow5	ol5	ot5	_	_	10	oDep5	_	_\n" + 
				"8	ow6	ol6	ot6	_	_	9	oDep6	_	_\n" + 
				"9	ow7	ol7	ot7	_	_	7	oDep7	_	_\n" + 
				"10	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	10	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"9	ow1	ol1	ot1	_	_	12	oDep1	_	_\n" + 
				"10	ow2	ol2	ot2	_	_	11	oDep2	_	_\n" + 
				"11	ow3	ol3	ot3	_	_	12	oDep3	_	_\n" + 
				"12	ow4	ol4	ot4	_	_	13	oDep4	_	_\n" + 
				"13	ow5	ol5	ot5	_	_	16	oDep5	_	_\n" + 
				"14	ow6	ol6	ot6	_	_	15	oDep6	_	_\n" + 
				"15	ow7	ol7	ot7	_	_	13	oDep7	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";


		boolean dep = true;
		assert insert("{word:/w3/}=w", gold1 ,"{word:/w9/}=w", gold2, new Random().nextBoolean(),"-0", dep, "Insert1w_0PosDep");

	}

	// 1w Right 0Pos dep
	@Test
	void ssurgeonInsert1wPlus0PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"4	ow1	ol1	ot1	_	_	7	oDep1	_	_\n" + 
				"5	ow2	ol2	ot2	_	_	6	oDep2	_	_\n" + 
				"6	ow3	ol3	ot3	_	_	7	oDep3	_	_\n" + 
				"7	ow4	ol4	ot4	_	_	8	oDep4	_	_\n" + 
				"8	ow5	ol5	ot5	_	_	3	oDep5	_	_\n" + 
				"9	ow6	ol6	ot6	_	_	10	oDep6	_	_\n" + 
				"10	ow7	ol7	ot7	_	_	8	oDep7	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	3	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	ow1	ol1	ot1	_	_	13	oDep1	_	_\n" + 
				"11	ow2	ol2	ot2	_	_	12	oDep2	_	_\n" + 
				"12	ow3	ol3	ot3	_	_	13	oDep3	_	_\n" + 
				"13	ow4	ol4	ot4	_	_	14	oDep4	_	_\n" + 
				"14	ow5	ol5	ot5	_	_	9	oDep5	_	_\n" + 
				"15	ow6	ol6	ot6	_	_	16	oDep6	_	_\n" + 
				"16	ow7	ol7	ot7	_	_	14	oDep7	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";


		boolean dep = true;
		assert insert("{word:/w3/}=w", gold1 ,"{word:/w9/}=w", gold2, new Random().nextBoolean(), "+0", dep, "Insert1wPlus0PosDep");

	}

	// 1w left 0Pos gov
	@Test
	void ssurgeonInsert1w_0PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	ow1	ol1	ot1	_	_	6	oDep1	_	_\n" + 
				"4	ow2	ol2	ot2	_	_	5	oDep2	_	_\n" + 
				"5	ow3	ol3	ot3	_	_	6	oDep3	_	_\n" + 
				"6	ow4	ol4	ot4	_	_	7	oDep4	_	_\n" + 
				"7	ow5	ol5	ot5	_	_	13	oDep5	_	_\n" + 
				"8	ow6	ol6	ot6	_	_	9	oDep6	_	_\n" + 
				"9	ow7	ol7	ot7	_	_	7	oDep7	_	_\n" + 
				"10	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	10	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"9	ow1	ol1	ot1	_	_	12	oDep1	_	_\n" + 
				"10	ow2	ol2	ot2	_	_	11	oDep2	_	_\n" + 
				"11	ow3	ol3	ot3	_	_	12	oDep3	_	_\n" + 
				"12	ow4	ol4	ot4	_	_	13	oDep4	_	_\n" + 
				"13	ow5	ol5	ot5	_	_	2	oDep5	_	_\n" + 
				"14	ow6	ol6	ot6	_	_	15	oDep6	_	_\n" + 
				"15	ow7	ol7	ot7	_	_	13	oDep7	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";

		boolean dep = false;
		assert insert("{word:/w3/}=w", gold1 ,"{word:/w9/}=w", gold2, new Random().nextBoolean(), "-0", dep, "Insert1w_0PosGov");

	}

	// 1w Right 0Pos gov
	@Test
	void ssurgeonInsert1wPlus0PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"4	ow1	ol1	ot1	_	_	7	oDep1	_	_\n" + 
				"5	ow2	ol2	ot2	_	_	6	oDep2	_	_\n" + 
				"6	ow3	ol3	ot3	_	_	7	oDep3	_	_\n" + 
				"7	ow4	ol4	ot4	_	_	8	oDep4	_	_\n" + 
				"8	ow5	ol5	ot5	_	_	13	oDep5	_	_\n" + 
				"9	ow6	ol6	ot6	_	_	10	oDep6	_	_\n" + 
				"10	ow7	ol7	ot7	_	_	8	oDep7	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	3	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	ow1	ol1	ot1	_	_	13	oDep1	_	_\n" + 
				"11	ow2	ol2	ot2	_	_	12	oDep2	_	_\n" + 
				"12	ow3	ol3	ot3	_	_	13	oDep3	_	_\n" + 
				"13	ow4	ol4	ot4	_	_	14	oDep4	_	_\n" + 
				"14	ow5	ol5	ot5	_	_	2	oDep5	_	_\n" + 
				"15	ow6	ol6	ot6	_	_	16	oDep6	_	_\n" + 
				"16	ow7	ol7	ot7	_	_	14	oDep7	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";



		boolean dep = false;
		assert insert("{word:/w3/}=w", gold1 ,"{word:/w9/}=w", gold2, new Random().nextBoolean(), "+0", dep, "Insert1wPlus0PosGov");

	}

	// 1w -1Pos dep 
	@Test
	void ssurgeonInsert1w_1PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"4	ow1	ol1	ot1	_	_	7	oDep1	_	_\n" + 
				"5	ow2	ol2	ot2	_	_	6	oDep2	_	_\n" + 
				"6	ow3	ol3	ot3	_	_	7	oDep3	_	_\n" + 
				"7	ow4	ol4	ot4	_	_	8	oDep4	_	_\n" + 
				"8	ow5	ol5	ot5	_	_	3	oDep5	_	_\n" + 
				"9	ow6	ol6	ot6	_	_	10	oDep6	_	_\n" + 
				"10	ow7	ol7	ot7	_	_	8	oDep7	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	3	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	ow1	ol1	ot1	_	_	10	oDep1	_	_\n" + 
				"8	ow2	ol2	ot2	_	_	9	oDep2	_	_\n" + 
				"9	ow3	ol3	ot3	_	_	10	oDep3	_	_\n" + 
				"10	ow4	ol4	ot4	_	_	11	oDep4	_	_\n" + 
				"11	ow5	ol5	ot5	_	_	6	oDep5	_	_\n" + 
				"12	ow6	ol6	ot6	_	_	13	oDep6	_	_\n" + 
				"13	ow7	ol7	ot7	_	_	11	oDep7	_	_\n" + 
				"14	w7	w7	t7	_	_	15	N	_	_\n" + 
				"15	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";



		boolean dep = true;
		assert insert("{word:/w3/}=w", gold1 ,"{word:/w6/}=w", gold2, true, "-1*", dep, "Insert1w_1PosDep");

	}
	// 1w +1Pos dep
	@Test
	void ssurgeonInsert1wPlus1PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	ow1	ol1	ot1	_	_	9	oDep1	_	_\n" + 
				"7	ow2	ol2	ot2	_	_	8	oDep2	_	_\n" + 
				"8	ow3	ol3	ot3	_	_	9	oDep3	_	_\n" + 
				"9	ow4	ol4	ot4	_	_	10	oDep4	_	_\n" + 
				"10	ow5	ol5	ot5	_	_	3	oDep5	_	_\n" + 
				"11	ow6	ol6	ot6	_	_	12	oDep6	_	_\n" + 
				"12	ow7	ol7	ot7	_	_	10	oDep7	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"9	ow1	ol1	ot1	_	_	12	oDep1	_	_\n" + 
				"10	ow2	ol2	ot2	_	_	11	oDep2	_	_\n" + 
				"11	ow3	ol3	ot3	_	_	12	oDep3	_	_\n" + 
				"12	ow4	ol4	ot4	_	_	13	oDep4	_	_\n" + 
				"13	ow5	ol5	ot5	_	_	17	oDep5	_	_\n" + 
				"14	ow6	ol6	ot6	_	_	15	oDep6	_	_\n" + 
				"15	ow7	ol7	ot7	_	_	13	oDep7	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";



		boolean dep = true;
		assert insert("{word:/w3/}=w", gold1 ,"{word:/w10/}=w", gold2, false, "+1", dep, "Insert1wPlus1PosDep");
	}
	// 1w -2Pos dep 
	@Test
	void ssurgeonInsert1w_2PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	ow1	ol1	ot1	_	_	6	oDep1	_	_\n" + 
				"4	ow2	ol2	ot2	_	_	5	oDep2	_	_\n" + 
				"5	ow3	ol3	ot3	_	_	6	oDep3	_	_\n" + 
				"6	ow4	ol4	ot4	_	_	7	oDep4	_	_\n" + 
				"7	ow5	ol5	ot5	_	_	1	oDep5	_	_\n" + 
				"8	ow6	ol6	ot6	_	_	9	oDep6	_	_\n" + 
				"9	ow7	ol7	ot7	_	_	7	oDep7	_	_\n" + 
				"10	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	10	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	ow1	ol1	ot1	_	_	6	oDep1	_	_\n" + 
				"4	ow2	ol2	ot2	_	_	5	oDep2	_	_\n" + 
				"5	ow3	ol3	ot3	_	_	6	oDep3	_	_\n" + 
				"6	ow4	ol4	ot4	_	_	7	oDep4	_	_\n" + 
				"7	ow5	ol5	ot5	_	_	13	oDep5	_	_\n" + 
				"8	ow6	ol6	ot6	_	_	9	oDep6	_	_\n" + 
				"9	ow7	ol7	ot7	_	_	7	oDep7	_	_\n" + 
				"10	w3	w3	t3	_	_	13	P1	_	_\n" + 
				"11	casa	casa	NOUN	_	_	10	P2	_	_\n" + 
				"12	w5	w5	t5	_	_	11	N	_	_\n" + 
				"13	w6	w6	t6	_	_	2	P	_	_\n" + 
				"14	w7	w7	t7	_	_	15	N	_	_\n" + 
				"15	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	13	P1	_	_\n";



		boolean dep = true;
		assert insert("{word:/w1/}=w", gold1 ,"{word:/w6/}=w", gold2, true, "-2", dep, "Insert1w_2PosDep");
	}
	// 1w +2Pos dep
	@Test
	void ssurgeonInsert1wPlus2PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	ow1	ol1	ot1	_	_	13	oDep1	_	_\n" + 
				"11	ow2	ol2	ot2	_	_	12	oDep2	_	_\n" + 
				"12	ow3	ol3	ot3	_	_	13	oDep3	_	_\n" + 
				"13	ow4	ol4	ot4	_	_	14	oDep4	_	_\n" + 
				"14	ow5	ol5	ot5	_	_	1	oDep5	_	_\n" + 
				"15	ow6	ol6	ot6	_	_	16	oDep6	_	_\n" + 
				"16	ow7	ol7	ot7	_	_	14	oDep7	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n" + 
				"11	ow1	ol1	ot1	_	_	14	oDep1	_	_\n" + 
				"12	ow2	ol2	ot2	_	_	13	oDep2	_	_\n" + 
				"13	ow3	ol3	ot3	_	_	14	oDep3	_	_\n" + 
				"14	ow4	ol4	ot4	_	_	15	oDep4	_	_\n" + 
				"15	ow5	ol5	ot5	_	_	6	oDep5	_	_\n" + 
				"16	ow6	ol6	ot6	_	_	17	oDep6	_	_\n" + 
				"17	ow7	ol7	ot7	_	_	15	oDep7	_	_\n";


		boolean dep = true;
		assert insert("{word:/w1/}=w", gold1 ,"{word:/w6/}=w", gold2, false, "+2", dep, "Insert1wPlus2PosDep");
	}
	
	
	// 1w -1Pos gov
	@Test
	void ssurgeonInsert1w_1PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n" + 
				"11	ow1	ol1	ot1	_	_	14	oDep1	_	_\n" + 
				"12	ow2	ol2	ot2	_	_	13	oDep2	_	_\n" + 
				"13	ow3	ol3	ot3	_	_	14	oDep3	_	_\n" + 
				"14	ow4	ol4	ot4	_	_	15	oDep4	_	_\n" + 
				"15	ow5	ol5	ot5	_	_	1	oDep5	_	_\n" + 
				"16	ow6	ol6	ot6	_	_	17	oDep6	_	_\n" + 
				"17	ow7	ol7	ot7	_	_	15	oDep7	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	ow1	ol1	ot1	_	_	13	oDep1	_	_\n" + 
				"11	ow2	ol2	ot2	_	_	12	oDep2	_	_\n" + 
				"12	ow3	ol3	ot3	_	_	13	oDep3	_	_\n" + 
				"13	ow4	ol4	ot4	_	_	14	oDep4	_	_\n" + 
				"14	ow5	ol5	ot5	_	_	2	oDep5	_	_\n" + 
				"15	ow6	ol6	ot6	_	_	16	oDep6	_	_\n" + 
				"16	ow7	ol7	ot7	_	_	14	oDep7	_	_\n" + 
				"17	w10	w10	t10	_	_	6	P1	_	_\n";


		boolean dep = false;
		assert insert("{word:/w6/}=w", gold1 ,"{word:/w6/}=w", gold2, false, "-1", dep, "Insert1w_1PosGov");
	}
	// 1w +1Pos gov
	@Test
	void ssurgeonInsert1wPlus1PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	ow1	ol1	ot1	_	_	6	oDep1	_	_\n" + 
				"4	ow2	ol2	ot2	_	_	5	oDep2	_	_\n" + 
				"5	ow3	ol3	ot3	_	_	6	oDep3	_	_\n" + 
				"6	ow4	ol4	ot4	_	_	7	oDep4	_	_\n" + 
				"7	ow5	ol5	ot5	_	_	1	oDep5	_	_\n" + 
				"8	ow6	ol6	ot6	_	_	9	oDep6	_	_\n" + 
				"9	ow7	ol7	ot7	_	_	7	oDep7	_	_\n" + 
				"10	w3	w3	t3	_	m=ind|n=p	13	D1	_	_\n" + 
				"11	w4	w4	t4	_	_	12	N	_	_\n" + 
				"12	w5	w5	t5	_	_	10	P	_	_\n" + 
				"13	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"14	w7	w7	t7	_	_	16	P	_	_\n" + 
				"15	w8	w8	t8	_	_	14	N	_	_\n" + 
				"16	w9	w9	t9	_	_	13	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	9	D1	_	_\n" + 
				"2	ow1	ol1	ot1	_	_	5	oDep1	_	_\n" + 
				"3	ow2	ol2	ot2	_	_	4	oDep2	_	_\n" + 
				"4	ow3	ol3	ot3	_	_	5	oDep3	_	_\n" + 
				"5	ow4	ol4	ot4	_	_	6	oDep4	_	_\n" + 
				"6	ow5	ol5	ot5	_	_	9	oDep5	_	_\n" + 
				"7	ow6	ol6	ot6	_	_	8	oDep6	_	_\n" + 
				"8	ow7	ol7	ot7	_	_	6	oDep7	_	_\n" + 
				"9	w2	w2	t2	_	_	0	root	_	_\n" + 
				"10	w3	w3	t3	_	_	13	P1	_	_\n" + 
				"11	casa	casa	NOUN	_	_	10	P2	_	_\n" + 
				"12	w5	w5	t5	_	_	11	N	_	_\n" + 
				"13	w6	w6	t6	_	_	9	P	_	_\n" + 
				"14	w7	w7	t7	_	_	15	N	_	_\n" + 
				"15	w8	w8	t8	_	_	17	P2	_	_\n" + 
				"16	w9	w9	t9	_	m=ind|n=p	9	D2	_	_\n" + 
				"17	w10	w10	t10	_	_	13	P1	_	_\n";

		boolean dep = false;
		assert insert("{word:/w6/}=w", gold1 ,"{word:/w6/}=w", gold2, false, "+1", dep, "Insert1wPlus1PosGov");
	}
	// 1w -2Pos gov 
	@Test
	void ssurgeonInsert1w_2PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	ow1	ol1	ot1	_	_	13	oDep1	_	_\n" + 
				"11	ow2	ol2	ot2	_	_	12	oDep2	_	_\n" + 
				"12	ow3	ol3	ot3	_	_	13	oDep3	_	_\n" + 
				"13	ow4	ol4	ot4	_	_	14	oDep4	_	_\n" + 
				"14	ow5	ol5	ot5	_	_	1	oDep5	_	_\n" + 
				"15	ow6	ol6	ot6	_	_	16	oDep6	_	_\n" + 
				"16	ow7	ol7	ot7	_	_	14	oDep7	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n" + 
				"11	ow1	ol1	ot1	_	_	14	oDep1	_	_\n" + 
				"12	ow2	ol2	ot2	_	_	13	oDep2	_	_\n" + 
				"13	ow3	ol3	ot3	_	_	14	oDep3	_	_\n" + 
				"14	ow4	ol4	ot4	_	_	15	oDep4	_	_\n" + 
				"15	ow5	ol5	ot5	_	_	2	oDep5	_	_\n" + 
				"16	ow6	ol6	ot6	_	_	17	oDep6	_	_\n" + 
				"17	ow7	ol7	ot7	_	_	15	oDep7	_	_\n";


		boolean dep = false;
		assert insert("{word:/w6/}=w", gold1 ,"{word:/w6/}=w", gold2, false, "-2*", dep, "Insert1w_2PosGov");
	}
	// 1w +2Pos gov	
	@Test
	void ssurgeonInsert1wPlus2PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	ow1	ol1	ot1	_	_	13	oDep1	_	_\n" + 
				"11	ow2	ol2	ot2	_	_	12	oDep2	_	_\n" + 
				"12	ow3	ol3	ot3	_	_	13	oDep3	_	_\n" + 
				"13	ow4	ol4	ot4	_	_	14	oDep4	_	_\n" + 
				"14	ow5	ol5	ot5	_	_	1	oDep5	_	_\n" + 
				"15	ow6	ol6	ot6	_	_	16	oDep6	_	_\n" + 
				"16	ow7	ol7	ot7	_	_	14	oDep7	_	_\n" + 
				"17	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n" + 
				"11	ow1	ol1	ot1	_	_	14	oDep1	_	_\n" + 
				"12	ow2	ol2	ot2	_	_	13	oDep2	_	_\n" + 
				"13	ow3	ol3	ot3	_	_	14	oDep3	_	_\n" + 
				"14	ow4	ol4	ot4	_	_	15	oDep4	_	_\n" + 
				"15	ow5	ol5	ot5	_	_	2	oDep5	_	_\n" + 
				"16	ow6	ol6	ot6	_	_	17	oDep6	_	_\n" + 
				"17	ow7	ol7	ot7	_	_	15	oDep7	_	_\n";


		boolean dep = false;
		assert insert("{word:/w6/}=w", gold1 ,"{word:/w6/}=w", gold2, false, "2*", dep, "Insert1wPlus2PosGov");
	}

	@Test
	void ssurgeonInsertBigNumbers() {

		boolean find = true;

		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/w6/}=w");
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord word = matcher.getNode("w");
			try {
				graph = Ssurgeon.insert(word, word, "+10", new Random().nextBoolean(), new Random().nextBoolean(), graph);
				find = false;
			} catch (Exception e) {
				if(e.getMessage().equals("The position of the new word is incompatible with the sentence size."))
					find = true;
				else
					e.printStackTrace();
				
			}

		}
		//Sentence 2
		semgrex = SemgrexPattern.compile("{word:/w6/}=w");
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord word = matcher.getNode("w");
			try {
				graph = Ssurgeon.insert(word, word, "-10", new Random().nextBoolean(), new Random().nextBoolean(), graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("The position of the new word is incompatible with the sentence size.") & find; 
			}
		}
		assert find;
	}
	
	private boolean insert(String pattern1, String gold1, String pattern2, String gold2, boolean left, String position, boolean dep, String useCaseName) {
		SemgrexPattern semgrex = SemgrexPattern.compile(pattern1);
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		boolean find = true;
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				
				SemanticGraph copy = makeHardCopy(testGraph);
				graph = Ssurgeon.insert(node, copy, position, left, dep, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold1.equals(modified);
				System.out.println("# " + useCaseName + "\t1");
				System.out.println(modified);
				System.out.println("# ---------------------------");
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		
		semgrex = SemgrexPattern.compile(pattern2);
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.insert(node, makeHardCopy(testGraph), position, left, dep, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold2.equals(modified);
				System.out.println("# " + useCaseName + "\t2");
				System.out.println(modified);
				System.out.println("# ============================");
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		return find;
	}
	
	public SemanticGraph makeHardCopy(SemanticGraph graph) {
		Collection<IndexedWord> roots = graph.getRoots();
		SemanticGraph newSg = new SemanticGraph();
		for (SemanticGraphEdge edge : graph.edgeIterable()) {
			CoreLabel bl = edge.getSource().backingLabel();
			CoreLabel newCoreSource = new CoreLabel();
			
			newCoreSource.set(CoreAnnotations.IndexAnnotation.class, bl.get(CoreAnnotations.IndexAnnotation.class)+0);
			newCoreSource.setValue(bl.value()+"");
			newCoreSource.set(CoreAnnotations.LemmaAnnotation.class, bl.get(CoreAnnotations.LemmaAnnotation.class)+"");
			newCoreSource.set(CoreAnnotations.PartOfSpeechAnnotation.class, bl.get(CoreAnnotations.PartOfSpeechAnnotation.class)+"");
			newCoreSource.set(CoreAnnotations.CoNLLUFeats.class, bl.get(CoreAnnotations.CoNLLUFeats.class));
			newCoreSource.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, bl.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class)+0);
			newCoreSource.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, bl.get(CoreAnnotations.CoNLLDepTypeAnnotation.class)+"");
			IndexedWord newsource = new IndexedWord(newCoreSource); 
			
			IndexedWord target = edge.getTarget();
			CoreLabel newCoreTarget = new CoreLabel();
			newCoreTarget.set(CoreAnnotations.IndexAnnotation.class, target.get(CoreAnnotations.IndexAnnotation.class)+0);
			newCoreTarget.setValue(target.value()+"");
			newCoreTarget.set(CoreAnnotations.LemmaAnnotation.class, target.get(CoreAnnotations.LemmaAnnotation.class)+"");
			newCoreTarget.set(CoreAnnotations.PartOfSpeechAnnotation.class, target.get(CoreAnnotations.PartOfSpeechAnnotation.class)+"");
			newCoreTarget.set(CoreAnnotations.CoNLLUFeats.class, target.get(CoreAnnotations.CoNLLUFeats.class));
			newCoreTarget.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, target.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class)+0);
			newCoreTarget.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, target.get(CoreAnnotations.CoNLLDepTypeAnnotation.class)+"");
			IndexedWord newtarget = new IndexedWord(newCoreTarget);
				
			GrammaticalRelation relation = edge.getRelation();
			double weight = edge.getWeight();
			boolean extra = edge.isExtra();
			newSg.addVertex(newtarget);
			newSg.addVertex(newsource);
			newSg.addEdge(newsource, newtarget, relation, weight, extra);
			if(roots.contains(edge.getSource()))
				newSg.addRoot(newsource);
			if(roots.contains(target))
				newSg.addRoot(newtarget);
		}
		return newSg;
	}

}



