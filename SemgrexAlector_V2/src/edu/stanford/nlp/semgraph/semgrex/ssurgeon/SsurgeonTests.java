package edu.stanford.nlp.semgraph.semgrex.ssurgeon;

import java.io.IOException;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon.ReplacingType;
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;

class SsurgeonTests {

	private String corpus = "examples/treebankCONV.conll";//"/home/alector/eclipse-workspace/SemgrexAlector/examples/treebankCONV.conll";//"examples/treebankCONV.conll";
	private Iterator<SemanticGraph> it;

	//	@Test
	//	void t() {
	//		boolean find = true; // : {tag:V}=V > {word:/,/}=C2
	//		SemgrexPattern semgrex = SemgrexPattern.compile("{pos:/V.*/} < {pos:/V.*/} >mod {pos:ADV}=m");
	//		while(it.hasNext()) {
	//			SemanticGraph graph = it.next();
	//			SemgrexMatcher matcher = semgrex.matcher(graph);
	//			if(matcher.find()) {
	//				IndexedWord v = matcher.getNode("qv");
	//				IndexedWord c1 = matcher.getNode("v");
	////				IndexedWord c2 = matcher.getNode("wh");
	//				IndexedWord p = matcher.getNode("m");
	////				IndexedWord a = matcher.getNode("q");
	////				IndexedWord o = matcher.getNode("c");
	//				System.out.println(v.toString() + "\t" + v.get(CoreAnnotations.IndexAnnotation.class));
	//				System.out.println(c1.toString() + "\t" + c1.get(CoreAnnotations.IndexAnnotation.class));
	//				System.out.println(p.toString() + "\t" + p.get(CoreAnnotations.IndexAnnotation.class));
	////				if(c2 !=null)
	////				System.out.println(c2.toString() + "\t" + c2.get(CoreAnnotations.IndexAnnotation.class));
	////				System.out.println(a.toString() + "\t" + a.get(CoreAnnotations.IndexAnnotation.class));
	////				if(o !=null)
	////					System.out.println(o.toString() + "\t" + o.get(CoreAnnotations.IndexAnnotation.class));
	//				System.out.println("========================");
	//			}
	//		}
	//		assert find;
	//	}
	//	

	@BeforeEach
	void loadCorpus() throws IOException {
		CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
		it = reader.getIterator(IOUtils.readerFromString(corpus));
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


	/*
	 * Test the semgrex on its original functions 
	 * 
	 */
	@Test
	void semgrexOriginal1() {
		boolean find = true;
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/w1/}=A");
		while(it.hasNext()) {
			SemanticGraph graph = it.next();
			SemgrexMatcher matcher = semgrex.matcher(graph);
			boolean matches = matcher.find();
			find = matches & find;			
		}
		assert find;
	}
	@Test
	void semgrexOriginal2() {
		boolean find = true;
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*/}=w1 > {word:/.*/}=w2");
		while(it.hasNext()) {
			SemanticGraph graph = it.next();
			SemgrexMatcher matcher = semgrex.matcher(graph);
			find = matcher.find() & find;
		}
		assert find;
	}
	/* **********************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************ 
	 ***********************************************************************************/

	/*
	 * Test the new conll features 
	 * 
	 */
	@Test
	void semgrexModified() {
		boolean find = true;
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*/;conllfeats:/g=.*/}=w1");
		while(it.hasNext()) {
			SemanticGraph graph = it.next();
			SemgrexMatcher matcher = semgrex.matcher(graph);
			find = matcher.find() & find;
			break;
		}
		assert find;
	}

	@Test
	void semgrexModified2() {
		boolean find = true;
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*/;conllfeats:/g=m/}=w1");
		while(it.hasNext()) {
			SemanticGraph graph = it.next();
			SemgrexMatcher matcher = semgrex.matcher(graph);
			find = matcher.find() & find;
			break;
		}
		assert find;
	}


	@Test
	void semgrexModified3() {
		boolean find = true;
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/.*/;pos:/.*/;conllfeats:/g=m/}=w1 > {word:/.*/;pos:/.*/}=w2");
		while(it.hasNext()) {
			SemanticGraph graph = it.next();
			SemgrexMatcher matcher = semgrex.matcher(graph);
			find = matcher.find() & find;
			break;
		}
		assert find;
	}
	/* **********************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************ 
	 ***********************************************************************************/

	/*
	 * Test Ssurgeon delete 
	 * 
	 */
	@Test
	void ssurgeonDeleteLeaf() {
		String str = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n";
		String str2 = "1	w2	w2	t2	_	_	0	root	_	_\n" + 
				"2	w3	w3	t3	_	_	5	P1	_	_\n" + 
				"3	casa	casa	NOUN	_	_	2	P2	_	_\n" + 
				"4	w5	w5	t5	_	_	3	N	_	_\n" + 
				"5	w6	w6	t6	_	_	1	P	_	_\n" + 
				"6	w7	w7	t7	_	_	7	N	_	_\n" + 
				"7	w8	w8	t8	_	_	9	P2	_	_\n" + 
				"8	w9	w9	t9	_	m=ind|n=p	1	D2	_	_\n" + 
				"9	w10	w10	t10	_	_	5	P1	_	_\n";
		assert delete("{word:/w10/}=w", str, "{word:/w1/}=w", str2);
	}

	@Test
	void ssurgeonDeleteRoot() {

		boolean find = true;

		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/w1/}=w");
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.delete(graph, node);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("We cannot delete a root node");
			}

		}

		//Sentence 2
		semgrex = SemgrexPattern.compile("{word:/w2/}=w");
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.delete(graph, node);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("We cannot delete a root node") & find; 
			}
		}
		assert find;
	}

	@Test
	void ssurgeonDeleteWith1child() {
		String str = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" + 
				"7	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w6	w6	t6	_	_	2	P	_	_\n" + 
				"4	w7	w7	t7	_	_	5	N	_	_\n" + 
				"5	w8	w8	t8	_	_	7	P2	_	_\n" + 
				"6	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"7	w10	w10	t10	_	_	3	P1	_	_\n";
		assert delete("{word:/w3/}=w", str, "{word:/w3/}=w", str2);
	}

	@Test
	void ssurgeonDeleteWith3children() {
		String str = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n"; 
		assert delete("{word:/w6/}=w", str, "{word:/w6/}=w", str2);
	}

	@Test
	void ssurgeonDeletePreLeaf() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w7	w7	t7	_	_	7	P	_	_\n" + 
				"6	w8	w8	t8	_	_	5	N	_	_\n" + 
				"7	w9	w9	t9	_	_	4	D2	_	_\n" +
				"8	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"8	w10	w10	t10	_	_	6	P1	_	_\n";
		assert delete("{word:/w5/}=w", str1, "{word:/w8/}=w", str2);
	}
	@Test
	void ssurgeonDeletePrePreLeaf() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" +
				"7	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n";
		assert delete("{word:/w3/}=w", str1, "{word:/w10/}=w", str2);
	}

	private boolean delete(String p1, String result1,String p2, String result2) {
		boolean find = true;
		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile(p1);
		SemanticGraph graph = it.next();
		graph = Ssurgeon.makeHardCopy(graph);
		SemgrexMatcher matcher = semgrex.matcher(graph);
		String str = result1;

		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.delete(graph, node);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = str.equals(modified);
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		//Sentence 2
		semgrex = SemgrexPattern.compile(p2);
		graph = it.next();
		graph = Ssurgeon.makeHardCopy(graph);
		matcher = semgrex.matcher(graph);
		str = 	result2; 

		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.delete(graph, node);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = str.equals(modified);
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		return find;

	}

	/* **********************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************ 
	 ***********************************************************************************/

	/*
	 * Test Ssurgeon prune 
	 * 
	 */

	@Test
	void ssurgeonPruneLeaf() {
		String str = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n";
		String str2 = "1	w2	w2	t2	_	_	0	root	_	_\n" + 
				"2	w3	w3	t3	_	_	5	P1	_	_\n" + 
				"3	casa	casa	NOUN	_	_	2	P2	_	_\n" + 
				"4	w5	w5	t5	_	_	3	N	_	_\n" + 
				"5	w6	w6	t6	_	_	1	P	_	_\n" + 
				"6	w7	w7	t7	_	_	7	N	_	_\n" + 
				"7	w8	w8	t8	_	_	9	P2	_	_\n" + 
				"8	w9	w9	t9	_	m=ind|n=p	1	D2	_	_\n" + 
				"9	w10	w10	t10	_	_	5	P1	_	_\n";
		assert prune("{word:/w10/}=w", str, "{word:/w1/}=w", str2);
	}

	@Test
	void ssurgeonpruneRoot() {

		boolean find = true;

		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/w1/}=w");
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.prune(node, graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("We cannot prune a root node");
			}

		}

		//Sentence 2
		semgrex = SemgrexPattern.compile("{word:/w2/}=w");
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.prune(node, graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("We cannot prune a root node") & find; 
			}
		}
		assert find;
	}

	@Test
	void ssurgeonPruneWith1child() {
		String str = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	m=ind|n=p	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" + 
				"7	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w6	w6	t6	_	_	2	P	_	_\n" + 
				"4	w7	w7	t7	_	_	5	N	_	_\n" + 
				"5	w8	w8	t8	_	_	7	P2	_	_\n" + 
				"6	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"7	w10	w10	t10	_	_	3	P1	_	_\n";
		assert prune("{word:/w3/}=w", str, "{word:/w3/}=w", str2);
	}

	@Test
	void ssurgeonPruneWith3children() {
		String str = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n"; 
		assert prune("{word:/w6/}=w", str, "{word:/w6/}=w", str2);
	}

	@Test
	void ssurgeonPrunePreLeaf() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" +
				"7	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n";
		assert prune("{word:/w5/}=w", str1, "{word:/w8/}=w", str2);
	}
	@Test
	void ssurgeonPrunePrePreLeaf() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" +
				"7	w10	w10	t10	_	_	1	P	_	_\n";
		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n";
		assert prune("{word:/w3/}=w", str1, "{word:/w10/}=w", str2);
	}

	private boolean prune(String p1, String result1,String p2, String result2) {
		boolean find = true;
		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile(p1);
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		String str = result1;

		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.prune(node, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = str.equals(modified);
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		//Sentence 2
		semgrex = SemgrexPattern.compile(p2);
		graph = it.next();
		matcher = semgrex.matcher(graph);
		str = 	result2; 

		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.prune(node, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = str.equals(modified);
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		return find;

	}

	/* **********************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************ 
	 ***********************************************************************************/

	/*
	 * Test Ssurgeon replace 
	 * 
	 */

	@Test
	void ssurgeonReplaceWord() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3modified	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9modified	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		IndexedWord w2 = new IndexedWord();
		w2.setValue("w9modified");
		assert replace("{word:/w3/}=w", w1, ReplacingType.word, str1, "{word:/w9/}=w", w2, str2);
	}

	@Test
	void ssurgeonReplaceLemma() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3modified	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9modified	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		w1.set(CoreAnnotations.LemmaAnnotation.class,"w3modified");
		IndexedWord w2 = new IndexedWord();
		w2.set(CoreAnnotations.LemmaAnnotation.class, "w9modified");
		assert replace("{word:/w3/}=w", w1, ReplacingType.lemma, str1, "{word:/w9/}=w", w2, str2);
	}

	@Test
	void ssurgeonReplacePoS() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3modified	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9modified	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		w1.set(CoreAnnotations.PartOfSpeechAnnotation.class,"t3modified");
		IndexedWord w2 = new IndexedWord();
		w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "t9modified");
		assert replace("{word:/w3/}=w", w1, ReplacingType.pos, str1, "{word:/w9/}=w", w2, str2);
	}

	@Test
	void ssurgeonReplaceList() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3modified	w3modified	t3modified	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9modified	w9modified	t9modified	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.LemmaAnnotation.class,"w3modified");
		w1.set(CoreAnnotations.PartOfSpeechAnnotation.class,"t3modified");
		IndexedWord w2 = new IndexedWord();
		w2.setValue("w9modified");
		w2.set(CoreAnnotations.LemmaAnnotation.class, "w9modified");
		w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "t9modified");


		String pattern1 = "{word:/w3/}=w";
		IndexedWord newWord1 = w1;
		List<ReplacingType> type = Arrays.asList(ReplacingType.word, ReplacingType.lemma, ReplacingType.pos);
		String gold1 = str1; 
		String pattern2 = "{word:/w9/}=w";
		IndexedWord newWord2 = w2;
		String gold2 = str2;		

		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile(pattern1);
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		boolean find = true;
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.replace(node, newWord1, type, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold1.equals(modified) && find;
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}

		//Sentence 2
		semgrex = SemgrexPattern.compile(pattern2);
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.replace(node, newWord2, type, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold2.equals(modified) && find;
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}

		assert find;
	}

	@Test
	void ssurgeonReplaceDettag() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	DEPmodified	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	DEPmodified	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class,"DEPmodified");
		IndexedWord w2 = new IndexedWord();
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEPmodified");
		assert replace("{word:/w3/}=w", w1, ReplacingType.dettag, str1, "{word:/w9/}=w", w2, str2);
	}

	@Test
	void ssurgeonReplaceFeats() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=featsmodified|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	g=featsmodified|m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("m", "featsmodified");
		w1.set(CoreAnnotations.CoNLLUFeats.class,value);
		IndexedWord w2 = new IndexedWord();
		value = new HashMap<String, String>();
		value.put("g", "featsmodified");
		w2.set(CoreAnnotations.CoNLLUFeats.class, value);
		assert replace("{word:/w3/}=w", w1, ReplacingType.feats, str1, "{word:/w9/}=w", w2, str2);
	}

	@Test
	void ssurgeonReplaceFeatsOperators() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	g=m|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	g=m|m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";
		IndexedWord w1 = new IndexedWord();
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("g", "m"); //added
		value.put("m", "-"); //remove m=ind
		value.put("n", "!"); //keep n=p
		w1.set(CoreAnnotations.CoNLLUFeats.class,value);
		IndexedWord w2 = new IndexedWord();
		value = new HashMap<String, String>();
		value.put("g", "m"); //added
		value.put("m", "ind"); //replaced by the same value
		//		value.put("n", "p"); //ignored
		w2.set(CoreAnnotations.CoNLLUFeats.class, value);

		assert replace("{word:/w3/}=w", w1, ReplacingType.feats, str1, "{word:/w9/}=w", w2, str2);
	}

	@Test
	void ssurgeonReplaceNodeValues() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	wmodified1	lmodified1	tmodified1	_	g=featsmodified1|m=ind|n=p	6	DEPmodified1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" +
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" +
				"2	w2	w2	t2	_	_	0	root	_	_\n" +
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	wmodified2	lmodified2	tmodified2	_	g=featsmodified2|m=ind|n=p	2	DEPmodified2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";

		IndexedWord w1 = new IndexedWord();
		w1.setValue("wmodified1");
		w1.set(CoreAnnotations.LemmaAnnotation.class,"lmodified1");
		w1.set(CoreAnnotations.PartOfSpeechAnnotation.class,"tmodified1");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class,"DEPmodified1");
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("g", "featsmodified1");
		w1.set(CoreAnnotations.CoNLLUFeats.class,value);

		IndexedWord w2 = new IndexedWord();
		w2.setValue("wmodified2");
		w2.set(CoreAnnotations.LemmaAnnotation.class,"lmodified2");
		w2.set(CoreAnnotations.PartOfSpeechAnnotation.class,"tmodified2");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class,"DEPmodified2");
		value = new HashMap<String, String>();
		value.put("g", "featsmodified2");
		w2.set(CoreAnnotations.CoNLLUFeats.class,value);

		assert replace("{word:/w3/}=w", w1, ReplacingType.nodeValues, str1, "{word:/w9/}=w", w2, str2);
	}
	//	nodeRelation 

	private boolean replace(String pattern1, IndexedWord newWord1, ReplacingType type, String gold1, 
			String pattern2, IndexedWord newWord2, String gold2) {
		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile(pattern1);
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		boolean find = true;
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			Set<String> a = matcher.getNodeNames();
			try {
				graph = Ssurgeon.replace(node, newWord1, type, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold1.equals(modified) && find;
				
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}

		//Sentence 2
		semgrex = SemgrexPattern.compile(pattern2);
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord node = matcher.getNode("w");
			try {
				graph = Ssurgeon.replace(node, newWord2, type, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold2.equals(modified) && find;
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		return find;
	}

	/* **********************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************
	 ************************************************************************************ 
	 ***********************************************************************************/

	/*
	 * Test Ssurgeon Move 
	 * 
	 */

	@Test
	void ssurgeonMoveRoot() {
		boolean find = true;

		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/w1/}=w1 : {word:/w5/}=w2");
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord nodeFrom = matcher.getNode("w1");
			IndexedWord nodeTo = matcher.getNode("w2");
			try {
				graph = Ssurgeon.move(nodeTo, nodeFrom, "-0", true, true, graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("You cannot move the root");
			}

		}

		//Sentence 2
		semgrex = SemgrexPattern.compile("{word:/w2/}=w1 : {word:/w5/}=w8");
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord nodeFrom = matcher.getNode("w1");
			IndexedWord nodeTo = matcher.getNode("w2");
			try {
				graph = Ssurgeon.move(nodeTo, nodeFrom, "-0", true, true, graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("You cannot move the root") & find; 
			}
		}
		assert find;
	}

	@Test
	void ssurgeonMoveRootSibling() {
		boolean find = true;

		//Sentence 1
		SemgrexPattern semgrex = SemgrexPattern.compile("{word:/w8/}=w1 : {word:/w1/}=w2");
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord nodeFrom = matcher.getNode("w1");
			IndexedWord nodeTo = matcher.getNode("w2");
			try {
				graph = Ssurgeon.move(nodeTo, nodeFrom, "-0", true, false, graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("You cannot move the root.");
				if(!find)
					e.printStackTrace();
			}

		}

		//Sentence 2
		semgrex = SemgrexPattern.compile("{word:/w5/}=w1 : {word:/w2/}=w2");
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord nodeFrom = matcher.getNode("w1");
			IndexedWord nodeTo = matcher.getNode("w2");
			try {
				graph = Ssurgeon.move(nodeTo, nodeFrom, "-0", true, false, graph);
				find = false;
			} catch (Exception e) {
				find = e.getMessage().equals("You cannot move the root.") & find; 
			}
		}
		assert find;
	}

	@Test
	void move_0PosDep() {
		String str1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	5	D1	_	_\n" + 
				"4	w5	w5	t5	_	_	3	P	_	_\n" + 
				"5	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"6	w7	w7	t7	_	_	9	P	_	_\n" + 
				"7	w4	w4	t4	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	6	N	_	_\n" + 
				"9	w9	w9	t9	_	_	5	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String str2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w7	w7	t7	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	4	N	_	_\n" + 
				"7	w6	w6	t6	_	_	2	P	_	_\n" + 
				"8	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = true;
		assert move("{word:/w4/}=wFrom : {word:/w8/}=wTo", str1, "{word:/w7/}=wFrom : {word:/w5/}=wTo", str2, new Random().nextBoolean(),"-0", dep, "move_0PosDep");
	}

	@Test
	void movePlus0PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" + 
				"7	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"8	w4	w4	t4	_	_	9	N	_	_\n" + 
				"9	w5	w5	t5	_	_	7	P	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	7	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";

		boolean dep = true;
		assert move("{word:/w3/}=wFrom : {word:/w9/}=wTo", gold1 ,"{word:/w8/}=wFrom : {word:/w9/}=wTo", gold2, new Random().nextBoolean(), "+0", dep, "movePlus0PosDep");
	}

	@Test
	void move_0PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	9	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w3	w3	t3	_	m=ind|n=p	3	D1	_	_\n" + 
				"7	w4	w4	t4	_	_	8	N	_	_\n" + 
				"8	w5	w5	t5	_	_	6	P	_	_\n" + 
				"9	w9	w9	t9	_	_	3	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	2	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w3/}=wFrom : {word:/w9/}=wTo", gold1 ,"{word:/w8/}=wFrom : {word:/w9/}=wTo", gold2, new Random().nextBoolean(), "-0", dep, "move_0PosGov");
	}
	
	@Test
	void move_0PosStarGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w3	w3	t3	_	m=ind|n=p	3	D1	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	4	P	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	3	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	2	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w3/}=wFrom : {word:/w9/}=wTo", gold1 ,"{word:/w8/}=wFrom : {word:/w9/}=wTo", gold2, new Random().nextBoolean(), "-0*", dep, "move_0PosStarGov");
	}

	@Test
	void movePlus0PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"4	w7	w7	t7	_	_	6	P	_	_\n" + 
				"5	w8	w8	t8	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	_	3	D2	_	_\n" + 
				"7	w3	w3	t3	_	m=ind|n=p	3	D1	_	_\n" + 
				"8	w4	w4	t4	_	_	9	N	_	_\n" + 
				"9	w5	w5	t5	_	_	7	P	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	2	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w3/}=wFrom : {word:/w9/}=wTo", gold1 ,"{word:/w8/}=wFrom : {word:/w9/}=wTo", gold2, new Random().nextBoolean(), "+0", dep, "movePlus0PosGov");
	}
	
	@Test
	void move_1PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	4	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	4	P	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	4	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w9	w9	t9	_	m=ind|n=p	6	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	6	P1	_	_\n";

		boolean dep = true;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, true, "-1*", dep, "move_1PosDep");
	}
	
	@Test
	void movePlus1PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	6	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	m=ind|n=p	7	D2	_	_\n" + 
				"7	w6	w6	t6	_	_	2	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = true;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "+1*", dep, "movePlus1PosDep");
	}
	
	@Test
	void move_2PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	6	P	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w9	w9	t9	_	m=ind|n=p	7	D2	_	_\n" + 
				"7	w6	w6	t6	_	_	2	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = true;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "-2*", dep, "move_2PosDep");
	}
	
	@Test
	void movePlus2PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	4	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w7	w7	t7	_	_	7	P	_	_\n" + 
				"6	w8	w8	t8	_	_	5	N	_	_\n" + 
				"7	w9	w9	t9	_	_	4	D2	_	_\n" + 
				"8	w4	w4	t4	_	_	9	N	_	_\n" + 
				"9	w5	w5	t5	_	_	4	P	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	9	P2	_	_\n" + 
				"9	w10	w10	t10	_	_	6	P1	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	6	D2	_	_\n";

		boolean dep = true;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "+2", dep, "movePlus2PosDep");
	}
	
	@Test
	void move_1PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	4	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w7	w7	t7	_	_	7	P	_	_\n" + 
				"6	w8	w8	t8	_	_	5	N	_	_\n" + 
				"7	w9	w9	t9	_	_	4	D2	_	_\n" + 
				"8	w10	w10	t10	_	_	1	P	_	_\n" + 
				"9	w4	w4	t4	_	_	10	N	_	_\n" + 
				"10	w5	w5	t5	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	9	P2	_	_\n" + 
				"9	w10	w10	t10	_	_	6	P1	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n";

		boolean dep = false;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "-1*", dep, "move_1PosGov");
	}
	
	@Test
	void movePlus1PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w4	w4	t4	_	_	4	N	_	_\n" + 
				"4	w5	w5	t5	_	_	1	P	_	_\n" + 
				"5	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	3	D1	_	_\n" + 
				"2	w9	w9	t9	_	m=ind|n=p	3	D2	_	_\n" + 
				"3	w2	w2	t2	_	_	0	root	_	_\n" + 
				"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
				"6	w5	w5	t5	_	_	5	N	_	_\n" + 
				"7	w6	w6	t6	_	_	3	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "+1", dep, "movePlus1PosGov");
	}
	
	@Test
	void movePlus1PosStarGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w4	w4	t4	_	_	4	N	_	_\n" + 
				"4	w5	w5	t5	_	_	1	P	_	_\n" + 
				"5	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
				"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	3	D1	_	_\n" + 
				"2	w9	w9	t9	_	m=ind|n=p	3	D2	_	_\n" + 
				"3	w2	w2	t2	_	_	0	root	_	_\n" + 
				"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
				"6	w5	w5	t5	_	_	5	N	_	_\n" + 
				"7	w6	w6	t6	_	_	3	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "+1*", dep, "movePlus1PosStarGov");
	}
	
	@Test
	void move_2PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	4	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	1	P	_	_\n" + 
				"7	w7	w7	t7	_	_	9	P	_	_\n" + 
				"8	w8	w8	t8	_	_	7	N	_	_\n" + 
				"9	w9	w9	t9	_	_	4	D2	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	3	D1	_	_\n" + 
				"2	w9	w9	t9	_	m=ind|n=p	3	D2	_	_\n" + //this case is very important. -2 indeed is W1, because W9 is not considered 
				"3	w2	w2	t2	_	_	0	root	_	_\n" + 
				"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
				"6	w5	w5	t5	_	_	5	N	_	_\n" + 
				"7	w6	w6	t6	_	_	3	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "-2", dep, "move_2PosGov");
	}	
	
	@Test
	void move_2PosStarGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	4	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w7	w7	t7	_	_	7	P	_	_\n" + 
				"6	w8	w8	t8	_	_	5	N	_	_\n" + 
				"7	w9	w9	t9	_	_	4	D2	_	_\n" + 
				"8	w4	w4	t4	_	_	9	N	_	_\n" + 
				"9	w5	w5	t5	_	_	1	P	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	3	D1	_	_\n" + 
				"2	w9	w9	t9	_	m=ind|n=p	3	D2	_	_\n" + 
				"3	w2	w2	t2	_	_	0	root	_	_\n" + 
				"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
				"6	w5	w5	t5	_	_	5	N	_	_\n" + 
				"7	w6	w6	t6	_	_	3	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	10	P2	_	_\n" + 
				"10	w10	w10	t10	_	_	7	P1	_	_\n";

		boolean dep = false;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "-2*", dep, "move_2PosStarGov");
	}	
	
	@Test
	void movePlus2PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	4	D1	_	_\n" + 
				"4	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"5	w7	w7	t7	_	_	7	P	_	_\n" + 
				"6	w8	w8	t8	_	_	5	N	_	_\n" + 
				"7	w9	w9	t9	_	_	4	D2	_	_\n" + 
				"8	w4	w4	t4	_	_	9	N	_	_\n" + 
				"9	w5	w5	t5	_	_	1	P	_	_\n" + 
				"10	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	9	P2	_	_\n" + 
				"9	w10	w10	t10	_	_	6	P1	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n";

		boolean dep = false;
		assert move("{word:/w5/}=wFrom : {word:/w6/}=wTo", gold1 ,"{word:/w9/}=wFrom : {word:/w6/}=wTo", gold2, false, "2*", dep, "movePlus2PosGov");
	}	
	
	private boolean move(String pattern1, String gold1, String pattern2, String gold2, boolean left, String pos, boolean dep, String useCaseName) {
		SemgrexPattern semgrex = SemgrexPattern.compile(pattern1);
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		boolean find = true;
		if(matcher.find()) {
			IndexedWord from = matcher.getNode("wFrom");
			IndexedWord to = matcher.getNode("wTo");
			try {
				graph = Ssurgeon.move(to, from, pos, left, dep, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold1.equals(modified) && find;
				System.out.println("# " + useCaseName + "\t1\t" + find);
				System.out.println(modified);
				System.out.println("# --------------------");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		semgrex = SemgrexPattern.compile(pattern2);
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord from = matcher.getNode("wFrom");
			IndexedWord to = matcher.getNode("wTo");
			try {
				graph = Ssurgeon.move(to, from, pos, left, dep, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find = gold2.equals(modified) && find;
				System.out.println("# " + useCaseName + "\t2\t" + find);
				System.out.println(modified);
				System.out.println("# ============================");
			} catch (Exception e) {
				find = false;
				e.printStackTrace();
			}
		}
		return find;
	}

	@Test
	void ssurgeonInsert1w_0PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3modified	null	null	_	_	4	DEP	_	_\n" + 
				"4	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	4	P	_	_\n" + 
				"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	10	P	_	_\n" + 
				"9	w8	w8	t8	_	_	8	N	_	_\n" + 
				"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"9	w3modified	null	null	_	_	10	DEP	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	6	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = true;
		assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w9/}=w", w2, gold2, new Random().nextBoolean(),"-0", dep);

	}

	// 1w Right 0Pos dep
	@Test
	void ssurgeonInsert1wPlus0PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
				"4	w3modified	null	null	_	_	3	DEP	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	3	P	_	_\n" + 
				"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	10	P	_	_\n" + 
				"9	w8	w8	t8	_	_	8	N	_	_\n" + 
				"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w3modified	null	null	_	_	9	DEP	_	_\n" + 
				"11	w10	w10	t10	_	_	6	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = true;
		assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w9/}=w", w2, gold2, new Random().nextBoolean(), "+0", dep);

	}

	// 1w left 0Pos gov
	@Test
	void ssurgeonInsert1w_0PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3modified	null	null	_	_	7	DEP	_	_\n" + 
				"4	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	4	P	_	_\n" + 
				"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	10	P	_	_\n" + 
				"9	w8	w8	t8	_	_	8	N	_	_\n" + 
				"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n";

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"9	w3modified	null	null	_	_	2	DEP	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	6	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = false;
		assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w9/}=w", w2, gold2, true, "-0", dep);

	}

	// 1w Right 0Pos gov
	@Test
	void ssurgeonInsert1wPlus0PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
				"4	w3modified	null	null	_	_	7	DEP	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	3	P	_	_\n" + 
				"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	10	P	_	_\n" + 
				"9	w8	w8	t8	_	_	8	N	_	_\n" + 
				"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w3modified	null	null	_	_	2	DEP	_	_\n" + 
				"11	w10	w10	t10	_	_	6	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = false;
		assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w9/}=w", w2, gold2, new Random().nextBoolean(), "+0", dep);

	}

	// 1w -1Pos dep 
		@Test
		void ssurgeonInsert1w_1PosDep() {
			String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
					"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
					"3	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
					"4	w4	w4	t4	_	_	6	N	_	_\n" + 
					"5	w3modified	null	null	_	_	3	DEP	_	_\n" + 
					"6	w5	w5	t5	_	_	3	P	_	_\n" + 
					"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
					"8	w7	w7	t7	_	_	10	P	_	_\n" + 
					"9	w8	w8	t8	_	_	8	N	_	_\n" + 
					"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	1	P	_	_\n"; 

			String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
					"2	w2	w2	t2	_	_	0	root	_	_\n" + 
					"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
					"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
					"5	w5	w5	t5	_	_	4	N	_	_\n" + 
					"6	w6	w6	t6	_	_	2	P	_	_\n" + 
					"7	w7	w7	t7	_	_	8	N	_	_\n" + 
					"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
					"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
					"10	w3modified	null	null	_	_	6	DEP	_	_\n" + 
					"11	w10	w10	t10	_	_	6	P1	_	_\n";


			IndexedWord w1 = new IndexedWord();
			w1.setValue("w3modified");
			w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			IndexedWord w2 = new IndexedWord();
			w2.setValue("w3modified");
			w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			boolean dep = true;
			assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, true, "-1", dep);

		}
		
		// 1w -1Pos* dep 
		@Test
		void ssurgeonInsert1w_1PosPlusDep() {
			String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
					"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
					"3	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
					"4	w3modified	null	null	_	_	3	DEP	_	_\n" + 
					"5	w4	w4	t4	_	_	6	N	_	_\n" + 
					"6	w5	w5	t5	_	_	3	P	_	_\n" + 
					"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
					"8	w7	w7	t7	_	_	10	P	_	_\n" + 
					"9	w8	w8	t8	_	_	8	N	_	_\n" + 
					"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	1	P	_	_\n"; 

			String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
					"2	w2	w2	t2	_	_	0	root	_	_\n" + 
					"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
					"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
					"5	w5	w5	t5	_	_	4	N	_	_\n" + 
					"6	w6	w6	t6	_	_	2	P	_	_\n" + 
					"7	w3modified	null	null	_	_	6	DEP	_	_\n" + 
					"8	w7	w7	t7	_	_	9	N	_	_\n" + 
					"9	w8	w8	t8	_	_	11	P2	_	_\n" + 
					"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	6	P1	_	_\n";


			IndexedWord w1 = new IndexedWord();
			w1.setValue("w3modified");
			w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			IndexedWord w2 = new IndexedWord();
			w2.setValue("w3modified");
			w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			boolean dep = true;
			assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, true, "-1*", dep);

		}
	// 1w +1Pos dep
	@Test
	void ssurgeonInsert1wPlus1PosDep() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
				"4	w4	w4	t4	_	_	5	N	_	_\n" + 
				"5	w5	w5	t5	_	_	3	P	_	_\n" + 
				"6	w3modified	null	null	_	_	3	DEP	_	_\n" + 
				"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	10	P	_	_\n" + 
				"9	w8	w8	t8	_	_	8	N	_	_\n" + 
				"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"9	w3modified	null	null	_	_	11	DEP	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	6	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = true;
		assert insert("{word:/w3/}=w", w1, gold1 ,"{word:/w10/}=w", w2, gold2, false, "+1", dep);
	}
	// 1w -2Pos dep 
		@Test
		void ssurgeonInsert1w_2PosDep() {
			String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
					"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
					"3	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
					"4	w4	w4	t4	_	_	5	N	_	_\n" + 
					"5	w5	w5	t5	_	_	3	P	_	_\n" + 
					"6	w3modified	null	null	_	_	1	DEP	_	_\n" + 
					"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
					"8	w7	w7	t7	_	_	10	P	_	_\n" + 
					"9	w8	w8	t8	_	_	8	N	_	_\n" + 
					"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	1	P	_	_\n"; 

			String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
					"2	w2	w2	t2	_	_	0	root	_	_\n" + 
					"3	w3modified	null	null	_	_	7	DEP	_	_\n" + 
					"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
					"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
					"6	w5	w5	t5	_	_	5	N	_	_\n" + 
					"7	w6	w6	t6	_	_	2	P	_	_\n" + 
					"8	w7	w7	t7	_	_	9	N	_	_\n" + 
					"9	w8	w8	t8	_	_	11	P2	_	_\n" + 
					"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	7	P1	_	_\n";


			IndexedWord w1 = new IndexedWord();
			w1.setValue("w3modified");
			w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			IndexedWord w2 = new IndexedWord();
			w2.setValue("w3modified");
			w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			boolean dep = true;
			assert insert("{word:/w1/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, true, "-2", dep);
		}
		
		// 1w -2Pos* dep 
		@Test
		void ssurgeonInsert1w_2PosStarDep() {
			String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
					"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
					"3	w3modified	null	null	_	_	1	DEP	_	_\n" + 
					"4	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
					"5	w4	w4	t4	_	_	6	N	_	_\n" + 
					"6	w5	w5	t5	_	_	4	P	_	_\n" + 
					"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
					"8	w7	w7	t7	_	_	10	P	_	_\n" + 
					"9	w8	w8	t8	_	_	8	N	_	_\n" + 
					"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	1	P	_	_\n"; 

			String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
					"2	w2	w2	t2	_	_	0	root	_	_\n" + 
					"3	w3modified	null	null	_	_	7	DEP	_	_\n" + 
					"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
					"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
					"6	w5	w5	t5	_	_	5	N	_	_\n" + 
					"7	w6	w6	t6	_	_	2	P	_	_\n" + 
					"8	w7	w7	t7	_	_	9	N	_	_\n" + 
					"9	w8	w8	t8	_	_	11	P2	_	_\n" + 
					"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	7	P1	_	_\n";


			IndexedWord w1 = new IndexedWord();
			w1.setValue("w3modified");
			w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			IndexedWord w2 = new IndexedWord();
			w2.setValue("w3modified");
			w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			boolean dep = true;
			assert insert("{word:/w1/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, true, "-2*", dep);
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
				"10	w3modified	null	null	_	_	1	DEP	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n"; 

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
				"11	w3modified	null	null	_	_	6	DEP	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = true;
		assert insert("{word:/w1/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, false, "+2*", dep);
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
				"11	w3modified	null	null	_	_	1	DEP	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
				"2	w2	w2	t2	_	_	0	root	_	_\n" + 
				"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
				"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
				"5	w5	w5	t5	_	_	4	N	_	_\n" + 
				"6	w6	w6	t6	_	_	2	P	_	_\n" + 
				"7	w7	w7	t7	_	_	8	N	_	_\n" + 
				"8	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"9	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
				"10	w3modified	null	null	_	_	2	DEP	_	_\n" + 
				"11	w10	w10	t10	_	_	6	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = false;
		assert insert("{word:/w6/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, false, "-1", dep);
	}
	// 1w +1Pos gov
	@Test
	void ssurgeonInsert1wPlus1PosGov() {
		String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
				"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
				"3	w3modified	null	null	_	_	1	DEP	_	_\n" + 
				"4	w3	w3	t3	_	m=ind|n=p	7	D1	_	_\n" + 
				"5	w4	w4	t4	_	_	6	N	_	_\n" + 
				"6	w5	w5	t5	_	_	4	P	_	_\n" + 
				"7	w6	w6	t6	_	_	1	D2	_	_\n" + 
				"8	w7	w7	t7	_	_	10	P	_	_\n" + 
				"9	w8	w8	t8	_	_	8	N	_	_\n" + 
				"10	w9	w9	t9	_	_	7	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n"; 

		String gold2 = "1	w1	w1	t1	_	_	3	D1	_	_\n" + 
				"2	w3modified	null	null	_	_	3	DEP	_	_\n" + 
				"3	w2	w2	t2	_	_	0	root	_	_\n" + 
				"4	w3	w3	t3	_	_	7	P1	_	_\n" + 
				"5	casa	casa	NOUN	_	_	4	P2	_	_\n" + 
				"6	w5	w5	t5	_	_	5	N	_	_\n" + 
				"7	w6	w6	t6	_	_	3	P	_	_\n" + 
				"8	w7	w7	t7	_	_	9	N	_	_\n" + 
				"9	w8	w8	t8	_	_	11	P2	_	_\n" + 
				"10	w9	w9	t9	_	m=ind|n=p	3	D2	_	_\n" + 
				"11	w10	w10	t10	_	_	7	P1	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = false;
		assert insert("{word:/w6/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, false, "+1", dep);
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
				"10	w3modified	null	null	_	_	1	DEP	_	_\n" + 
				"11	w10	w10	t10	_	_	1	P	_	_\n"; 

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
				"11	w3modified	null	null	_	_	2	DEP	_	_\n";


		IndexedWord w1 = new IndexedWord();
		w1.setValue("w3modified");
		w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		IndexedWord w2 = new IndexedWord();
		w2.setValue("w3modified");
		w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

		boolean dep = false;
		assert insert("{word:/w6/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, false, "-2*", dep);
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
					"7	w3modified	null	null	_	_	1	DEP	_	_\n" + 
					"8	w7	w7	t7	_	_	10	P	_	_\n" + 
					"9	w8	w8	t8	_	_	8	N	_	_\n" + 
					"10	w9	w9	t9	_	_	6	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	1	P	_	_\n"; 

			String gold2 = "1	w1	w1	t1	_	_	2	D1	_	_\n" + 
					"2	w2	w2	t2	_	_	0	root	_	_\n" + 
					"3	w3	w3	t3	_	_	6	P1	_	_\n" + 
					"4	casa	casa	NOUN	_	_	3	P2	_	_\n" + 
					"5	w5	w5	t5	_	_	4	N	_	_\n" + 
					"6	w6	w6	t6	_	_	2	P	_	_\n" + 
					"7	w3modified	null	null	_	_	2	DEP	_	_\n" + 
					"8	w7	w7	t7	_	_	9	N	_	_\n" + 
					"9	w8	w8	t8	_	_	11	P2	_	_\n" + 
					"10	w9	w9	t9	_	m=ind|n=p	2	D2	_	_\n" + 
					"11	w10	w10	t10	_	_	6	P1	_	_\n";


			IndexedWord w1 = new IndexedWord();
			w1.setValue("w3modified");
			w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			IndexedWord w2 = new IndexedWord();
			w2.setValue("w3modified");
			w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			boolean dep = false;
			assert insert("{word:/w6/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, false, "2", dep);
		}
		
		// 1w +2Pos* gov	
		@Test
		void ssurgeonInsert1wPlus2PosStarGov() {
			String gold1 = "1	w1	w1	t1	_	g=m	0	root	_	_\n" + 
					"2	w2	w2	t2	_	_	1	D1	_	_\n" + 
					"3	w3	w3	t3	_	m=ind|n=p	6	D1	_	_\n" + 
					"4	w4	w4	t4	_	_	5	N	_	_\n" + 
					"5	w5	w5	t5	_	_	3	P	_	_\n" + 
					"6	w6	w6	t6	_	_	1	D2	_	_\n" + 
					"7	w7	w7	t7	_	_	9	P	_	_\n" + 
					"8	w8	w8	t8	_	_	7	N	_	_\n" + 
					"9	w9	w9	t9	_	_	6	D2	_	_\n" + 
					"10	w3modified	null	null	_	_	1	DEP	_	_\n" + 
					"11	w10	w10	t10	_	_	1	P	_	_\n"; 

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
					"11	w3modified	null	null	_	_	2	DEP	_	_\n";


			IndexedWord w1 = new IndexedWord();
			w1.setValue("w3modified");
			w1.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			IndexedWord w2 = new IndexedWord();
			w2.setValue("w3modified");
			w2.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, "DEP");

			boolean dep = false;
			assert insert("{word:/w6/}=w", w1, gold1 ,"{word:/w6/}=w", w2, gold2, false, "2*", dep);
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


	private boolean insert(String pattern1, IndexedWord w1, String gold1, String pattern2, IndexedWord w2, String gold2, boolean left, String pos, boolean dep) {
		boolean find = true;

		SemgrexPattern semgrex = SemgrexPattern.compile(pattern1);
		SemanticGraph graph = it.next();
		SemgrexMatcher matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord w = matcher.getNode("w");
			try {

				graph = Ssurgeon.insert(w, w1, /*left, */pos, left, dep, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find &= modified.equals(gold1);

			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		semgrex = SemgrexPattern.compile(pattern2);
		graph = it.next();
		matcher = semgrex.matcher(graph);
		if(matcher.find()) {
			IndexedWord w = matcher.getNode("w");
			try {
				graph = Ssurgeon.insert(w, w2, /*left,*/ pos, left, dep, graph);
				String modified = graph.toString(CoreLabel.OutputFormat.CoNLL);
				find &= modified.equals(gold2);

			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return find;
	}
}
