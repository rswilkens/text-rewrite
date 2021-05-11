package edu.stanford.nlp.semgraph.semgrex.ssurgeon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.CoNLLDepParentIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CoNLLUMisc;
import edu.stanford.nlp.ling.CoreAnnotations.IndexAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreLabel.GenericAnnotation;
import edu.stanford.nlp.ling.CoreLabel.OutputFormat;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.TypesafeMap.Key;

public class Ssurgeon {
	private static Class<IndexAnnotation> index = CoreAnnotations.IndexAnnotation.class;

	/**
		 * 
		 * Delete the node and everything below it
		 * 
	(ROOT
	  (SBARQ
	    (SQ (NP (NNS Cats))
	          (VP (VBP do)
	                (VP (WHNP what)
				  (VB eat)))
	  (PUNCT ?)))
	
	
	PUNCT=punct > SBARQ
	delete punct
	
	(ROOT
	  (SBARQ
	    (SQ (NP (NNS Cats))
	          (VP (VBP do)
	                (VP (WHNP what)
				  (VB eat)))
	  ))
	
		 * @param word to be deleted
		 * @param graph sentence
		 * @return sentence without the word and all its children
		 * @throws Exception 
		 */
		public static SemanticGraph delete(SemanticGraph graph, IndexedWord... words) throws Exception {
			for (IndexedWord word : words) {
				graph = delete_operation(graph, word);
//				graph = makeHardCopy(graph);
				graph = makeSoftCopy(graph);
//				for (IndexedWord w : getAllChildren(graph.getFirstRoot(), graph)) 
//					w.reHashCode();	
			}
			
			return graph;
		}

	/**
	 * 
	 * Delete the node and everything below it
	 * @param word to be deleted
	 * @param graph sentence
	 * @return sentence without the word and all its children
	 * @throws Exception 
	 */
	private static SemanticGraph delete_operation(SemanticGraph graph, IndexedWord word) throws Exception {
//		for (IndexedWord word : words) {
			if(isRoot(word, graph))
				throw new Exception("We cannot delete a root node");

			if(graph.hasChildren(word))
				for (IndexedWord c : graph.getChildList(word))   //remove ALL children
					graph=delete_operation(graph, c);

			if(!isRoot(word, graph)) {							//remove relations with other nodes
				IndexedWord parent = graph.getParent(word); 
				graph.removeEdge(graph.getEdge(parent, word));
			}

			graph.removeVertex(word);							//remove word

			int wIndex = word.get(index);
			List<IndexedWord> allWords = graph.vertexListSorted();

			Map<Integer, Integer> mapIndex = new HashMap<Integer, Integer>();
			for (IndexedWord w : allWords) {
				if(w.get(index)>wIndex) {//fix word index
					mapIndex.put(w.get(index), w.get(index)-1);
					w.set(index, w.get(index)-1);
				}
				//fix dep index
			}
			for (IndexedWord w : allWords) {
				Integer nParent = w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class);
				nParent = mapIndex.get(nParent);
				if(nParent != null)
					w.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, nParent);
//				w.reHashCode();
			}
//		}
		
		return graph;
	}

	public static SemanticGraph replaceRootDepTag(String tag, SemanticGraph graph) {
		graph.getFirstRoot().set(CoreAnnotations.CoNLLDepTypeAnnotation.class, tag);
		return graph;
	}
	
	private static boolean isRoot(IndexedWord word, SemanticGraph graph) {
		return graph.getRoots().contains(word);
	}

	/**
		 * name1 is name2 or dominates name2. All children of name2 go into the parent of name1, where name1 was.
	(ROOT
	  (SBARQ
	    (SQ (NP (NNS Cats))
	          (VP (VBP do)
	                (VP (WHNP what)
				  (VB eat))))))
	
	SBARQ=sbarq > ROOT
	excise sbarq sbarq
	
	(ROOT
	    (SQ (NP (NNS Cats))
	          (VP (VBP do)
	                (VP (WHNP what)
			  (VB eat)))))
	
	
		 * @param name1 Name1 is name2 or dominates name2
		 * @param name2 All children of name2 go into the parent of name1, where name1 was
		 * @param graph the sentence
		 * @return modified sentence
		 */
		public static SemanticGraph excise(IndexedWord name1, IndexedWord name2, SemanticGraph graph) {
			graph = excise_operation(name1, name2, graph);
//			graph = makeHardCopy(graph);
			graph = makeSoftCopy(graph);
//			for (IndexedWord w : getAllChildren(graph.getFirstRoot(), graph)) 
//				w.reHashCode();
			return graph;
		}

	/**
	 * name1 is name2 or dominates name2. All children of name2 go into the parent of name1, where name1 was.
	 * @param name1 Name1 is name2 or dominates name2
	 * @param name2 All children of name2 go into the parent of name1, where name1 was
	 * @param graph the sentence
	 * @return modified sentence
	 */
	private static SemanticGraph excise_operation(IndexedWord name1, IndexedWord name2, SemanticGraph graph) {
		if(graph.hasChildren(name1)) {
			Set<IndexedWord> children = graph.getChildren(name1);

			for (IndexedWord child : children) {
				SemanticGraphEdge edge = graph.getEdge(name1, child);
				graph.addEdge(name2, child, edge.getRelation(), edge.getWeight(), edge.isExtra());	
			}

		}
		graph.removeEdge(graph.getEdge(name2, name1));
		graph.removeVertex(name1);

		return graph;
	}

	/**
	 * Different from delete: If after the pruning the parent has no children anymore, the parent is pruned too.
	 * @param word
	 * @param graph
	 * @return
	 * @throws Exception 
	 */
	public static SemanticGraph prune(IndexedWord word, SemanticGraph graph) throws Exception {
		graph = prune_operation(word, graph);
//		graph = makeHardCopy(graph);
		graph = makeSoftCopy(graph);
//		for (IndexedWord w : getAllChildren(graph.getFirstRoot(), graph)) 
//			w.reHashCode();
		return graph;
	}

	/**
	 * Different from delete: If after the pruning the parent has no children anymore, the parent is pruned too.
	 * @param word
	 * @param graph
	 * @return
	 * @throws Exception 
	 */
	private static SemanticGraph prune_operation(IndexedWord word, SemanticGraph graph) throws Exception {
		IndexedWord parent = graph.getParent(word);
		try {
			delete_operation(graph, word);
		} catch (Exception e) {
			if(e.getMessage().equals("We cannot delete a root node"))
				throw new Exception("We cannot prune a root node");
			else
				throw e;
		}
		if(graph.getChildList(parent).size()==0)
			try {
				delete_operation(graph, parent);
			} catch (Exception e) {
				if(e.getMessage().equals("We cannot delete a root node"))
					throw new Exception("We cannot prune a root node");
				else
					throw e;
			}
		
		return graph;
	}

	public static enum ReplacingType {nodeRelation, //remove the oldWord; move all relations from oldWod to newWord; 
		nodeValues, // copy all values from newWord into oldWord; the relations wont be processed 
		word, // copy word attribute from newWord into oldWord
		lemma, // copy lemma attribute from newWord into oldWord 
		pos, // copy part-of-speech attribute from newWord into oldWord
		feats, // copy feats attribute from newWord into oldWord
		dettag}; // copy dependency tag attribute from newWord into oldWord

		public static SemanticGraph replace(IndexedWord oldWord, IndexedWord newWord, List<ReplacingType> type, SemanticGraph graph) throws Exception {
			for (ReplacingType r : type) 
				graph = replace(oldWord, newWord, r, graph);
			return graph;
		}

		public static SemanticGraph replace(IndexedWord oldWord, IndexedWord newWord, ReplacingType type, SemanticGraph graph) throws Exception {
			return replace_operation(oldWord, newWord, type, graph);
		}

		private static SemanticGraph replace_operation(IndexedWord oldWord, IndexedWord newWord, ReplacingType type, SemanticGraph graph) throws Exception {
			switch (type) {
			case nodeRelation:
				throw new Exception("Bad use of nodeRelation replacement. You should call replaceNodeRelation method.");
			case nodeValues:
				ArrayList<ReplacingType> lst = new ArrayList<ReplacingType>(Arrays.asList(ReplacingType.values()));
				lst.remove(ReplacingType.nodeValues);
				lst.remove(ReplacingType.nodeRelation);
				for (ReplacingType r : lst) 
					graph = replace(oldWord, newWord, r, graph);
				return graph;
			case word:
				oldWord.setValue(newWord.value()+"");
				return graph;
			case pos:
				oldWord.set(CoreAnnotations.PartOfSpeechAnnotation.class, newWord.get(CoreAnnotations.PartOfSpeechAnnotation.class)+"");
				return graph;
			case lemma:
				oldWord.set(CoreAnnotations.LemmaAnnotation.class, newWord.get(CoreAnnotations.LemmaAnnotation.class)+"");
				return graph;
			case dettag:
				oldWord.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, newWord.get(CoreAnnotations.CoNLLDepTypeAnnotation.class)+"");
				return graph;
			case feats:
				HashMap<String, String> newfeats = newWord.get(CoreAnnotations.CoNLLUFeats.class);
				if(newfeats==null)
					oldWord.set(CoreAnnotations.CoNLLUFeats.class, newfeats);
				HashMap<String, String> oldfeats = oldWord.get(CoreAnnotations.CoNLLUFeats.class);
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String k : oldfeats.keySet()) 
					temp.put(k, oldfeats.get(k));

				for (String k : newfeats.keySet()) {
					String v = newfeats.get(k);
					if(v.equals("-"))
						temp.remove(k);
					else if(v.equals("!")) {
						if(!oldfeats.containsKey(k))
							return graph;
					}else if(v.contains("&") && oldfeats.containsKey(k))
						temp.put(k, v.replace("&", ""));
					else if(v.contains("~") && !oldfeats.containsKey(k))
						temp.put(k, v.replace("~", ""));
					else
						temp.put(k, v);
				}
				oldWord.set(CoreAnnotations.CoNLLUFeats.class, temp);
				return graph;
			}
			return null;
		}

		/**
		 * If a node in the 2 graphs contains the same word+PoS+position then the new word (from new graph) will be ignored 
		 * @param oldWord
		 * @param graph
		 * @param graphAux
		 * @return
		 * @throws Exception
		 */
		public static SemanticGraph replaceNodeRelation(IndexedWord oldWord, SemanticGraph graph, SemanticGraph graphAux) throws Exception {
			graph = replaceNodeRelation_operation(oldWord, graph, graphAux);
//			graph = makeHardCopy(graph);
			graph = makeSoftCopy(graph);
//			for (IndexedWord w : getAllChildren(graph.getFirstRoot(), graph)) 
//				w.reHashCode();
			return graph;
		}

		/**
		 * If a node in the 2 graphs contains the same word+PoS+position then the new word (from new graph) will be ignored 
		 * @param oldWord
		 * @param graph
		 * @param graphAux
		 * @return
		 * @throws Exception
		 */
		private static SemanticGraph replaceNodeRelation_operation(IndexedWord oldWord, SemanticGraph graph, SemanticGraph graphAux) throws Exception {

			if(graph.getRoots().contains(oldWord))
				return graphAux;

			IndexedWord aux = graph.getFirstRoot();
			String docIDAnnotation = aux.get(CoreAnnotations.DocIDAnnotation.class);
			Integer sentenceIndexAnnotation = aux.get(CoreAnnotations.SentenceIndexAnnotation.class);
			
			int shift = graphAux.size();
			IndexedWord newWord = graphAux.getFirstRoot();

			IndexedWord leftmost = getLeftmostIndex(oldWord, graph);
			Integer oldWordIndex = leftmost.get(index);
			oldWordIndex += unsupressedWordShift(leftmost, oldWord, graph);//if is there words non dependent of oldWord between oldWord and LeftmostIndex, we have to take it into account as other shift
			IndexedWord parent = graph.getParent(oldWord);
			SemanticGraphEdge rel = graph.getEdge(parent, oldWord);
			graph = delete_operation(graph, oldWord);

			List<String> processedNodes = new ArrayList<String>();
			List<IndexedWord> processedNodesAux = new ArrayList<IndexedWord>();
			Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
			//fixing word position
			for (SemanticGraphEdge e : graph.edgeIterable()) {
				IndexedWord w = e.getDependent();
				if(!processedNodes.contains(w.value() + "#" + w.tag() + "#" + w.get(index))) {
					Integer oldIndex = w.get(index);
					if(w.get(index) >= oldWordIndex)
						w.set(index,w.get(index)+shift);
					mapping.put(oldIndex, w.get(index));
					processedNodes.add(w.value() + "#" + w.tag() + "#" + w.get(index));
					processedNodesAux.add(w);
				}
				w = e.getGovernor();
				if(!processedNodes.contains(w.value() + "#" + w.tag() + "#" + w.get(index))) {
					Integer oldIndex = w.get(index);
					if(w.get(index) >= oldWordIndex)
						w.set(index,w.get(index)+shift);
					mapping.put(oldIndex, w.get(index));
					processedNodes.add(w.value() + "#" + w.tag() + "#" + w.get(index));
					processedNodesAux.add(w);
				}
			}
			for (IndexedWord word : processedNodesAux) {//update dependencies
				Integer value = word.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class);
				if(value==0) continue; //do not mess roots up
				Integer newValue = mapping.get(value);
				word.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, newValue);
			}

			//insert new graph
			processedNodes = new ArrayList<>();
			processedNodesAux = new ArrayList<IndexedWord>();			
			mapping = new HashMap<Integer, Integer>();

			graph.addVertex(newWord);
			int newIndex = newWord.get(index)+oldWordIndex-1;
			mapping.put(newWord.get(index), newIndex);
			newWord.set(index, newIndex);//fixing word position
			newWord.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, parent.get(index));
			graph.addEdge(parent, newWord, rel.getRelation(), rel.getWeight(), rel.isExtra());
			processedNodes.add(newWord.value() + "#" + newWord.tag() + "#" + newWord.get(index));

			//copy auxiliary graph into main graph, while discover, fixing word position of non root and non children
			for (SemanticGraphEdge e : graphAux.edgeIterable()) {
				IndexedWord word = e.getDependent();
				if(!processedNodes.contains(word.value() + "#" + word.tag() + "#" + word.get(index))) {
					Integer oldIndex = word.get(index);
					newIndex = word.get(index)+oldWordIndex-1;
					word.set(index, newIndex);//fixing word position
					graph.addVertex(word);
					mapping.put(oldIndex, newIndex);
					processedNodes.add(word.value() + "#" + word.tag() + "#" + word.get(index));
					processedNodesAux.add(word);
					
					if(sentenceIndexAnnotation!=null)
						word.set(CoreAnnotations.SentenceIndexAnnotation.class, sentenceIndexAnnotation); // set word indentifiers
					if(docIDAnnotation!=null)
						word.set(CoreAnnotations.DocIDAnnotation.class, docIDAnnotation);
				}

				processedNodes.get(1).hashCode();
				word = e.getGovernor();
				if(!processedNodes.contains(word.value() + "#" + word.tag() + "#" + word.get(index))) {
					Integer oldIndex = word.get(index);
					newIndex = word.get(index)+oldWordIndex-1;
					word.set(index, newIndex);//fixing word position
					graph.addVertex(word);
					mapping.put(oldIndex, newIndex);
					processedNodes.add(word.value() + "#" + word.tag() + "#" + word.get(index));
					processedNodesAux.add(word);
					
					if(sentenceIndexAnnotation!=null)
						word.set(CoreAnnotations.SentenceIndexAnnotation.class, sentenceIndexAnnotation); // set word indentifiers
					if(docIDAnnotation!=null)
						word.set(CoreAnnotations.DocIDAnnotation.class, docIDAnnotation);
				}
				graph.addEdge(e);
			}
			for (IndexedWord word : processedNodesAux) {//update dependencies
				Integer value = word.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class);
				if(value==0) continue; //do not mess roots up
				Integer newValue = mapping.get(value);
				word.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, newValue);
			}
			
			return graph;			
		}

		private static int unsupressedWordShift(IndexedWord leftmost, IndexedWord word, SemanticGraph graph) {
			List<IndexedWord> children = getAllChildren(word, graph);
			List<IndexedWord> allWords = getAllChildren(graph.getFirstRoot(), graph);
			int ret = 0;
			for (IndexedWord w : allWords) 
				if(w.get(index) > leftmost.get(index) && w.get(index) < word.get(index)) {
					if(!children.contains(w))
						ret++;
				}
			return ret;
		}

		private static List<IndexedWord> getAllChildren(IndexedWord word, SemanticGraph graph) {
			Set<IndexedWord> aux = graph.hasChildren(word) ? graph.getChildren(word) : new HashSet<>();
			List<IndexedWord> ret = new ArrayList<IndexedWord>(aux);
			for (IndexedWord c : aux) 
				ret.addAll(getAllChildren(c, graph));
			Collections.sort(ret, new Comparator<IndexedWord>() {
				@Override
				public int compare(IndexedWord o1, IndexedWord o2) {
					return o1.get(index).compareTo(o2.get(index));
				}
			});
			
			return ret;
		}
		private static List<Integer> getAllChildrenIndex(IndexedWord word, SemanticGraph graph) {
			List<IndexedWord> lst = getAllChildren(word, graph);
			List<Integer> ret = new ArrayList<Integer>();
			for (IndexedWord w : lst) 
				ret.add(w.get(index));
			return ret;
		}

		private static IndexedWord getLeftmostIndex(IndexedWord word, SemanticGraph graph) {
			IndexedWord left = word; 
//			if word.index == 0: return 0 //TODO
			if(!graph.containsVertex(word))
				return word;
			
			
			Set<IndexedWord> lst = graph.getChildren(word);
			for (IndexedWord child : lst) {
				IndexedWord leftmost = getLeftmostIndex(child,graph);
				Integer leftmostIndex = leftmost.get(index);
				if(leftmostIndex < left.get(index))
					left = leftmost;
			}
			return left;
		}
		private static IndexedWord getLeftmostIndex(IndexedWord word, SemanticGraph graph, List<IndexedWord> blackList) {
			IndexedWord left = word;
			Set<IndexedWord> lst = graph.getChildren(word);
			for (IndexedWord child : lst) {
				if(blackList.contains(child)) continue;
				IndexedWord leftmost = getLeftmostIndex(child,graph,blackList);
				Integer leftmostIndex = leftmost.get(index);
				if(leftmostIndex < left.get(index))
					left = leftmost;
			}
			return left;
		}
		private static IndexedWord getRightmostIndex(IndexedWord word, SemanticGraph graph) {
			IndexedWord right = word;
			Set<IndexedWord> lst = graph.getChildren(word);
			for (IndexedWord child : lst) {
				IndexedWord rightmost = getRightmostIndex(child,graph);
				Integer rightmostIndex = rightmost.get(index);
				if(rightmostIndex > right.get(index))
					right = rightmost;
			}
			return right;
		}
		
		private static IndexedWord getRightmostIndex(IndexedWord word, SemanticGraph graph, List<IndexedWord> blackList) {
			IndexedWord right = word;
			Set<IndexedWord> lst = graph.getChildren(word);
			for (IndexedWord child : lst) {
				if(blackList.contains(child)) continue;
				IndexedWord rightmost = getRightmostIndex(child,graph,blackList);
				Integer rightmostIndex = rightmost.get(index);
				if(rightmostIndex > right.get(index))
					right = rightmost;
			}
			return right;
		}

		/**
		 * 
		 * @param word
		 * @param newWord
		 * @param postion: the negative symbol indicates if the new word will be inserted in the left side of the word. The 0 indicates the root, so the system will insert the new word on the left/right side of the root
		 * @param left: indicates if the insertion will the in the left/right side of the node indicated in the position attribute. If position is -0 or +0 the "left" attribute is ignored 
		 * @param gov: if the word will be the head of the new node OR the word will be a sibling of the new word (in any case, this does not impact the new word position) 
		 * @param graph
		 * @return
		 * @throws Exception
		 */
		public static SemanticGraph insert(IndexedWord word, IndexedWord newWord, String postion, boolean left, boolean gov, SemanticGraph graph) throws Exception {
			graph = insert_operation(word, newWord, postion, left, gov, graph);
//			graph = makeHardCopy(graph);
			graph = makeSoftCopy(graph);
//			for (IndexedWord w : getAllChildren(graph.getFirstRoot(), graph)) 
//				w.reHashCode();
			return graph;
		}

		/**
		 * 
		 * @param word
		 * @param newWord
		 * @param postion: the negative symbol indicates if the new word will be inserted in the left side of the word. The 0 indicates the root, so the system will insert the new word on the left/right side of the root
		 * @param left: indicates if the insertion will the in the left/right side of the node indicated in the position attribute. If position is -0 or +0 the "left" attribute is ignored 
		 * @param gov: if the word will be the head of the new node OR the word will be a sibling of the new word (in any case, this does not impact the new word position) 
		 * @param graph
		 * @return
		 * @throws Exception
		 */
		private static SemanticGraph insert_operation(IndexedWord word, IndexedWord newWord, String postion, boolean left, boolean gov, SemanticGraph graph) throws Exception {
			
			IndexedWord parent = gov ? word : graph.getParent(word);
			List<IndexedWord> allWords = graph.vertexListSorted();

			int newWordIndex = -1;
			// tous ces mangues VS ces belles mangues --> insert deep(N) ADV VS insert shallow(N) ADV   
			{
				boolean star = postion.contains("*");
				postion = postion.replace("*", "").trim();
				IndexedWord aux = null;
				if(postion.startsWith("-0") || postion.startsWith("+0") || postion.startsWith("0") || postion.equals("")) {
					aux = word;
					left = postion.startsWith("-0");
				}else {
					List<IndexedWord> children = new ArrayList<>(graph.getChildren(parent));
					if(Math.abs(Integer.valueOf(postion)) > children.size())
						throw new Exception("The position of the new word is incompatible with the sentence size.");
					int p2 = Math.abs(Integer.valueOf(postion))-1;
					int p1 = children.size() + Integer.valueOf(postion);
					aux = postion.charAt(0)=='-' ? children.get(p1) : children.get(p2);
				}
				if(star)
					if(left)
						newWordIndex = getLeftmostIndex(aux, graph).get(index);
					else
						newWordIndex = getRightmostIndex(aux, graph).get(index)+1;
				else
					if(left)
						newWordIndex = aux.get(index);
					else
						newWordIndex = aux.get(index)+1;
			}
//			if(newWordIndex == null)
				

			int wordsToInsert = 1;
			newWord.set(index, newWordIndex);

			String docIDAnnotation = graph.getFirstRoot().get(CoreAnnotations.DocIDAnnotation.class);
			if(docIDAnnotation!=null)
				newWord.set(CoreAnnotations.DocIDAnnotation.class, docIDAnnotation);
			Integer sentenceIndexAnnotation = graph.getFirstRoot().get(CoreAnnotations.SentenceIndexAnnotation.class);
			if(sentenceIndexAnnotation!=null)
				newWord.set(CoreAnnotations.SentenceIndexAnnotation.class, sentenceIndexAnnotation);

			String tag = newWord.get(CoreAnnotations.CoNLLDepTypeAnnotation.class);
			GrammaticalRelation gRel = GrammaticalRelation.valueOf(tag);

			Map<Integer, Integer> mapParents = new HashMap<Integer, Integer>();
			for (IndexedWord w : allWords) {
				if(w.get(index)>=newWord.get(index)) {
					mapParents.put(w.get(index), w.get(index)+wordsToInsert);
					w.set(index, w.get(index)+wordsToInsert);
				}
			}

			graph.addEdge(parent, newWord, gRel, Double.NEGATIVE_INFINITY, false);
			graph.addVertex(newWord);
			for (IndexedWord w : allWords) {
				Integer i = mapParents.get(w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class));
				if(i!=null)
					w.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, i);
			}
			
			newWord.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class,parent.get(index));
			
			return graph;
		}

		//bad support to non sequential trees
		public static SemanticGraph insert(IndexedWord word, SemanticGraph newGraph, String postion, boolean left, boolean gov, SemanticGraph graph) throws Exception {
			graph = insert_operation(word, newGraph, postion, left, gov, graph);
			graph = makeSoftCopy(graph);
			return graph;
		}

		//bad support to non sequential trees
		private static SemanticGraph insert_operation(IndexedWord word, SemanticGraph newGraph, String postion, boolean left, boolean gov, SemanticGraph graph) throws Exception {
			Collection<IndexedWord> roots = newGraph.getRoots();
			for (IndexedWord w : roots) 
				graph = insert(word, w, newGraph, postion, left, gov, graph, new ArrayList<IndexedWord>());
			return graph;
		}

		private static SemanticGraph insert(IndexedWord word, IndexedWord newWord, SemanticGraph newGraph, String postion, boolean left, boolean gov, SemanticGraph graph, List<IndexedWord> blacklist) throws Exception {
			for (IndexedWord w : blacklist) 
				if(w == newWord)
					return graph;
				
			Set<IndexedWord> children = new HashSet<IndexedWord>();
			if(newGraph.hasChildren(newWord))
				children = newGraph.getChildren(newWord);
			Integer newWord_OldIndex = newWord.get(index); 
			graph = insert_operation(word, newWord, postion, left, gov, graph);
			blacklist.add(newWord);
			
			for (IndexedWord w : children) {
				postion = newWord_OldIndex < w.get(index) ? "+0*" : "-0";
				graph = insert(newWord, w, newGraph, postion, left, true, graph, blacklist);
			}
			return graph;
		}

		public static SemanticGraph move(IndexedWord word, IndexedWord movedWord, String postion, boolean left, boolean gov, SemanticGraph graph) throws Exception {
			for (IndexedWord v : graph.vertexSet()) 
				System.out.print(v + "-" + v.get(index) + " ");
			System.out.println();
			graph= move_operation(word, movedWord, postion, left, gov, graph);
			graph = fixNonSequentials(graph);
			graph = makeSoftCopy(graph);
			return graph;
		}

		private static SemanticGraph fixNonSequentials(SemanticGraph graph) {
//			List<Integer> allWordsIndex = getAllChildrenIndex(graph.getFirstRoot(), graph);
//			allWordsIndex.add(graph.getFirstRoot().get(index));
			List<Integer> allWordsIndex = new ArrayList<Integer>();
			for (IndexedWord w : graph.vertexSet()) 
				allWordsIndex.add(w.get(index));
			
			Integer max = Collections.max(allWordsIndex);
			if(max>allWordsIndex.size()) {
				Collections.sort(allWordsIndex);
				List<Integer> gaps = new ArrayList<Integer>();
				for (int i = allWordsIndex.size()-1; i > 0; i--) 
					if(allWordsIndex.get(i-1) != allWordsIndex.get(i)-1)
						for (int j = allWordsIndex.get(i-1)+1; j < allWordsIndex.get(i); j++) 
							gaps.add(j); //inserted from biggest to lowest number
				Set<IndexedWord> allWords = graph.vertexSet();
				Map<String, Integer> map = new HashMap<String, Integer>();
				Map<Integer, String> mapAux = new HashMap<Integer, String>();
				for (IndexedWord w : allWords) {//save original the indexes
					String k = w.value() + "-" + w.tag() + "-" + w.get(CoreAnnotations.CoNLLDepTypeAnnotation.class) + "-" + w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class);
					mapAux.put(w.get(index), k);
				}
				while(gaps.size()>0) {
					for (IndexedWord w : allWords) {
						if(w.get(index)>gaps.get(0)) {
							String k = w.value() + "-" + w.tag() + "-" + w.get(CoreAnnotations.CoNLLDepTypeAnnotation.class) + "-" + w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class);
							map.put(k, w.get(index)-1);
							w.set(index, w.get(index)-1);
						}
					}
					gaps.remove(0);
				}
				for (IndexedWord w : allWords) {
					Integer p = w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class);
					String pKey = mapAux.get(p);
					if(map.containsKey(pKey)) {
						Integer value = map.get(pKey);
						w.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, value);
					}
				}
			}
			
			return graph;
		}

		private static SemanticGraph move_operation(IndexedWord word, IndexedWord movedWord, String postion, boolean left, boolean gov, SemanticGraph graph) throws Exception {

			if(graph.getRoots().contains(movedWord))
				throw new Exception("You cannot move the root");
			if(!gov && graph.getRoots().contains(word))
				throw new Exception("You cannot move the root.");

			Integer leftmostIndex = getLeftmostIndex(movedWord, graph).get(index);
			Integer rightmostIndex = getRightmostIndex(movedWord, graph).get(index);
			
			SemanticGraphEdge edgeToKill = graph.getEdge(graph.getParent(movedWord), movedWord);
			Integer wordOriginalIndex = movedWord.get(index);
			
			IndexedWord parent = gov ? word : graph.getParent(word);
//			if(parent.equals(word))
//				System.out.println();
			List<IndexedWord> allWords = graph.vertexListSorted();

			int newWordBlockIndex = -1;
			{
				boolean star = postion.contains("*");
				postion = postion.replace("*", "").trim();
				IndexedWord aux = null;
				if(postion.startsWith("-0") || postion.startsWith("+0") || postion.startsWith("0") || postion.equals("")) {
					aux = word;
					left = postion.startsWith("-0");
				}else {
					List<IndexedWord> children = new ArrayList<>(graph.getChildren(parent));
					if(children.contains(movedWord))	// the movedNode will NOT be counted as a child
						children.remove(movedWord);
					
					if(Math.abs(Integer.valueOf(postion)) > children.size())
						throw new Exception("The position of the new word is incompatible with the sentence size.");
					int p2 = Math.abs(Integer.valueOf(postion))-1;
					int p1 = children.size() + Integer.valueOf(postion);
					aux = postion.charAt(0)=='-' ? children.get(p1) : children.get(p2);
				}
				if(star) {
					List<IndexedWord> blackList = getAllChildren(movedWord, graph);
					blackList.add(movedWord);
					if(left) { 
						newWordBlockIndex = getLeftmostIndex(aux, graph, blackList).get(index);
						if(getLeftmostIndex(aux, graph).get(index)>wordOriginalIndex) // moved node comes from left of aux (new parent)
							newWordBlockIndex -= rightmostIndex-leftmostIndex+1;
					}else {
						newWordBlockIndex = getRightmostIndex(aux, graph, blackList).get(index)+1;
						if(getRightmostIndex(aux, graph).get(index)>wordOriginalIndex) // moved node comes from left of aux (new parent)
							newWordBlockIndex -= rightmostIndex-leftmostIndex+1;
					}
					
				}else {
					if(left)
						newWordBlockIndex = aux.get(index);
					else
						newWordBlockIndex = aux.get(index)+1;
					if(aux.get(index)>wordOriginalIndex)// moved node comes from left of aux (new parent)
						newWordBlockIndex -= rightmostIndex-leftmostIndex+1;
				}
			}


			int wordsToInsert = rightmostIndex-leftmostIndex;
			movedWord.set(index, newWordBlockIndex + (wordOriginalIndex-leftmostIndex));
			
			String tag = movedWord.get(CoreAnnotations.CoNLLDepTypeAnnotation.class);
			GrammaticalRelation gRel = GrammaticalRelation.valueOf(tag);

			Map<Integer, Integer> mapParents = new HashMap<Integer, Integer>();
			for (IndexedWord w : allWords) {
				if(!(w.get(index) == movedWord.get(index) 					// not the movedWord 
						&& w.value().equals(movedWord.value()) 
						&& w.get(CoreAnnotations.CoNLLDepTypeAnnotation.class).equals(movedWord.get(CoreAnnotations.CoNLLDepTypeAnnotation.class)) 
						&& w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class) == movedWord.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class))) {
					if(w.get(index)>=leftmostIndex && w.get(index)<=rightmostIndex) {
						int newIndex = newWordBlockIndex + (w.get(index)-leftmostIndex);
						mapParents.put(w.get(index), newIndex);
						w.set(index, newIndex);
					}else if(w.get(index)>newWordBlockIndex+wordsToInsert && w.get(index)>rightmostIndex) {
						//do nothing
					}else if(w.get(index)<leftmostIndex && w.get(index)<newWordBlockIndex) {
						//do nothing
					}else if(w.get(index)>leftmostIndex && w.get(index) <= newWordBlockIndex+wordsToInsert) {//moved from left to right
						mapParents.put(w.get(index), w.get(index)-(wordsToInsert+1));//entre posicao original movida e nova posicao
						w.set(index, w.get(index)-(wordsToInsert+1));
					}else if(w.get(index)<rightmostIndex && w.get(index) >= newWordBlockIndex+wordsToInsert) {//moved from right to left
						mapParents.put(w.get(index), w.get(index)+(wordsToInsert+1));
						w.set(index, w.get(index)+(wordsToInsert+1));
					}else {//moved words
						if(w.get(index)>= leftmostIndex && w.get(index)>= rightmostIndex) {
							
							mapParents.put(w.get(index), w.get(index)-leftmostIndex+newWordBlockIndex);
							w.set(index, w.get(index)-leftmostIndex+newWordBlockIndex);
						}else if(w.get(index)< leftmostIndex && w.get(index)< rightmostIndex) {
							mapParents.put(w.get(index), w.get(index)+wordsToInsert+1);
							w.set(index, w.get(index)+wordsToInsert+1);
						}else {
							if(w.get(index)>parent.get(index)) {
								mapParents.put(w.get(index), w.get(index)+(wordsToInsert+1));
								w.set(index, w.get(index)+(wordsToInsert+1));
							}else
								//I don't know what is that. 
								System.err.println(w);
						}
					}
				}else {
					mapParents.put(wordOriginalIndex, w.get(index));
				}
//					
			}
			
			graph.removeEdge(edgeToKill);
			graph.addEdge(parent, movedWord, gRel, Double.NEGATIVE_INFINITY, false);
			graph.addVertex(movedWord);
			for (IndexedWord w : allWords) {
				Integer i = mapParents.get(w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class));
				if(i!=null)
					w.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, i);
			}
			
			movedWord.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class,parent.get(index));
			
			return graph;
		}

//		private static void removeNonSequentialElements(IndexedWord movedWord, SemanticGraph graph, Integer leftmostIndex,
//				Integer rightmostIndex, List<Integer> movedWords) {
//			boolean firstWord = true;
//			int maxWord = -1;
//			Map<Integer, Integer> mapIndex = new HashMap<Integer, Integer>();
//			List<IndexedWord> outWords = new ArrayList<IndexedWord>();
//			for (IndexedWord w : graph.vertexSet()) {
//				if(w.get(index)>=leftmostIndex && w.get(index)<=rightmostIndex) {
//					if(!firstWord) {
//						if(movedWords.contains(w.get(index))) {
//							Integer newIndex = movedWords.get(movedWords.indexOf(w.get(index)));
//							mapIndex.put(w.get(index), newIndex);
//							w.set(index, newIndex);
//							maxWord = newIndex;
//							
//						}else {
//							outWords.add(w);
//						}
//					}
//					firstWord = false;
//				}
//			}
//			for (IndexedWord w : outWords) {
//				Integer newIndex = maxWord+1;
//				mapIndex.put(w.get(index), newIndex);
//				w.set(index, newIndex);
//				maxWord = newIndex;
//			}
//			
//			for (IndexedWord w : graph.vertexSet()) {
//				Integer i = mapIndex.get(w.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class));
//				if(mapIndex.containsKey(i)) 
//					w.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, i);
//			}
//		}

		public static SemanticGraph[] split(IndexedWord wordOriginal, SemanticGraph graphOriginal) throws Exception {
			SemanticGraph[] graph = split_operation(wordOriginal, graphOriginal);
//			graph = makeSoftCopy(graph);
			return graph;
		}
		private static SemanticGraph[] split_operation(IndexedWord wordOriginal, SemanticGraph graph) throws Exception {
			//save values that will be changed during upper graph creation
			List<IndexedWord> lowerNodes = getAllChildren(wordOriginal, graph);
			lowerNodes.add(wordOriginal);
			List<Integer> indexes = new ArrayList<Integer>();
			List<Integer> indexParents = new ArrayList<Integer>();
			
			List<String> lowerNodesIDS = new ArrayList<String>(); 
			for (IndexedWord word : lowerNodes) {
				indexes.add(word.get(index));
				indexParents.add(word.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class));
				lowerNodesIDS.add(word.index() + "##" + word.lemma());
			}
			
			
			// prepare upper graph
			SemanticGraph upperGraph = new SemanticGraph();
			Set<IndexedWord> allNodes = graph.vertexSet();
			for (IndexedWord node : allNodes) 
				upperGraph.addVertex(node);
			for (SemanticGraphEdge edge : graph.edgeIterable()) 
				upperGraph.addEdge(edge.getSource(), edge.getTarget(), edge.getRelation(), edge.getWeight(), edge.isExtra());
			for (IndexedWord root : graph.getRoots()) 
				upperGraph.setRoot(root);
			upperGraph = delete(upperGraph, wordOriginal);
			
			
			SemanticGraph lowerGraph = new SemanticGraph();
			int min = Collections.min(indexes);
			for (int i = 0; i < lowerNodes.size(); i++) {
				IndexedWord node = lowerNodes.get(i);
				node.set(index, indexes.get(i)-min+1);
				node.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, indexParents.get(i)-min+1);
				lowerGraph.addVertex(node);
			}
			wordOriginal.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class,0);
			
			for (SemanticGraphEdge edge : graph.edgeIterable()) 
				if( lowerNodesIDS.contains((edge.getSource().index()+min-1) + "##" + edge.getSource().lemma()) //lowerGraph.containsVertex(edge.getSource())  
						&& lowerNodesIDS.contains((edge.getTarget().index()+min-1) + "##" + edge.getTarget().lemma())) //lowerGraph.containsVertex(edge.getTarget()))
					lowerGraph.addEdge(edge.getSource(), edge.getTarget(), edge.getRelation(), edge.getWeight(), edge.isExtra());
			lowerGraph.setRoot(wordOriginal);
			
			lowerGraph = fixNonSequentials(lowerGraph);
			lowerGraph = makeSoftCopy(lowerGraph);
//			for (IndexedWord word : allNodes) 
//				if(upperGraph.containsVertex(word) && lowerGraph.containsVertex(word) && lower) {
//					lowerGraph = delete_operation(lowerGraph, word);
//				}lowerGraph.toString(CoreLabel.OutputFormat.CoNLL)
			SemanticGraph[] ret = {upperGraph, lowerGraph};
			return ret;
		}
//		private static SemanticGraph extract_operation_old(IndexedWord wordOriginal, SemanticGraph graphOriginal) throws Exception {
//			IndexedWord word = makeHardCopy(wordOriginal);		//nao deveria usar copias, mas as versoes com referencia 
//			SemanticGraph graph = makeHardCopy(graphOriginal);
//
//			List<IndexedWord> children = getAllChildren(word, graph);
//			children.add(word);
//			List<SemanticGraphEdge> toSave = new ArrayList<SemanticGraphEdge>();
//			int leftMostIndex = Integer.MAX_VALUE;
//
//
//			for (IndexedWord c : children) {
//				if(c==null) continue;
//				if(c.get(index)<leftMostIndex)
//					leftMostIndex=c.get(index);
//
//				List<SemanticGraphEdge> allEdges = graph.getIncomingEdgesSorted(c);
//				allEdges.addAll(graph.getOutEdgesSorted(c));
//				for (SemanticGraphEdge edge : allEdges) {
//					boolean dep = children.contains(edge.getDependent());
//					boolean gov = children.contains(edge.getGovernor());
//					if (dep && gov && !toSave.contains(edge)){
//						toSave.add(edge);
//					}
//				}
//			}
//
//			SemanticGraph newGraph = new SemanticGraph();
//			List<String> processedNodes = new ArrayList<String>();
//
//			if(toSave.size()==0) {
//				word.set(index,1);
//				word.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class,0);
//				newGraph.addRoot(word);
//				return newGraph;
//			}
//
//			for (SemanticGraphEdge se : toSave){
//				IndexedWord s = se.getSource();
//				IndexedWord t = se.getTarget();
//				String str1 = s.docID() + "#" + s.sentIndex() + "#" + s.value() + "#" +s.tag() + "#" +s.get(index);
//				String str2 = wordOriginal.docID() + "#" + wordOriginal.sentIndex() + "#" + wordOriginal.value() + "#" +wordOriginal.tag() + "#" +wordOriginal.get(index);
//				boolean isSRoot = false;
//				if(str1.equals(str2))
//					isSRoot = true;
//
//				//fix word/parent index
//				str1 = s.docID() + "#" + s.sentIndex() + "#" + s.value() + "#" +s.tag() + "#" +s.get(index);
//				if(!processedNodes.contains(str1)) {
//					s.set(index, s.get(index) - leftMostIndex +1);
//					s.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, s.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class) - leftMostIndex +1);
//					str1 = s.docID() + "#" + s.sentIndex() + "#" + s.value() + "#" +s.tag() + "#" +s.get(index);
//					processedNodes.add(str1);
//				}
//
//				str1 = t.docID() + "#" + t.sentIndex() + "#" + t.value() + "#" +t.tag() + "#" +t.get(index);
//				if(!processedNodes.contains(str1)) {
//					t.set(index, t.get(index) - leftMostIndex +1);
//					t.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, t.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class) - leftMostIndex +1);
//					str1 = t.docID() + "#" + t.sentIndex() + "#" + t.value() + "#" +t.tag() + "#" +t.get(index);
//					processedNodes.add(str1);
//				}
//				if(isSRoot){
//					newGraph.addRoot(s);
//					s.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, 0);
//				}
//
//				newGraph.addEdge(s, t, se.getRelation(), se.getWeight(), se.isExtra());
//			}
//			
//			return newGraph;
//		}

		public static SemanticGraph makeHardCopy(SemanticGraph graph) {

			Collection<IndexedWord> roots = graph.getRoots();
			Set<IndexedWord> allNodes = graph.vertexSet();
			Set<IndexedWord> allRoots = new HashSet<IndexedWord>(graph.getRoots());
			
			SemanticGraph newSg = new SemanticGraph();
			Map<String, IndexedWord> map = new HashMap<String, IndexedWord>();
			List<SemanticGraphEdge> allEdges = graph.edgeListSorted();
			List<String> processed = new ArrayList<String>();

			for (SemanticGraphEdge edge : allEdges) {

				IndexedWord sO = edge.getSource();
				IndexedWord s = null;
				String str = sO.value() + "#" + sO.tag() + "#" + sO.get(index);
				if(!processed.contains(str)) {
					s = makeHardCopy(sO);
					map.put(str, s);
					processed.add(str);
					newSg.addVertex(s);
					if(roots.contains(sO))
						newSg.addRoot(s);
				}else
					s = map.get(str);

				IndexedWord tO = edge.getTarget();
				IndexedWord d = null;
				str = tO.value() + "#" + tO.tag() + "#" + tO.get(index);
				if(!processed.contains(str)) {
					d = makeHardCopy(tO);
					map.put(str, d);
					processed.add(str);
					newSg.addVertex(d);
					if(roots.contains(tO))
						newSg.addRoot(d);
				}else
					d = map.get(str);

				GrammaticalRelation reln = GrammaticalRelation.valueOf(edge.getRelation().getShortName());
				newSg.addEdge(s, d, reln, edge.getWeight(), edge.isExtra());

			}

			return newSg;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public
		static IndexedWord makeHardCopy(IndexedWord w) {
			CoreLabel bl = w.backingLabel();
			CoreLabel newCoreSource = new CoreLabel();
			//			w.docID()
			//			CoreAnnotations.SRL_ID
			newCoreSource.setValue(bl.value()+"");
			//			for (Class<? extends Key<?>> k : bl.getKeys()) {
			//				Object v = bl.get(k);
			//				if(v!=null)
			//					System.out.println("++++++\n" + k + "\t" + v + "\n---------------");
			//				//				newCoreSource.set(k,v);				
			//			}

			Class[] features = {CoreAnnotations.CoarseTagAnnotation.class, CoreAnnotations.CoNLLUMisc.class, CoreAnnotations.ValueAnnotation.class, CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class,CoreAnnotations.LineNumberAnnotation.class,CoreAnnotations.TextAnnotation.class,
					CoreAnnotations.IndexAnnotation.class, CoreAnnotations.LemmaAnnotation.class, CoreAnnotations.PartOfSpeechAnnotation.class, CoreAnnotations.CoNLLUFeats.class, CoreAnnotations.CoNLLDepParentIndexAnnotation.class,CoreAnnotations.CoNLLDepTypeAnnotation.class, CoreAnnotations.SentenceIndexAnnotation.class,CoreAnnotations.DocIDAnnotation.class,
					CoreAnnotations.CoNLLCorefAnnotation.class, CoreAnnotations.CoNLLUMisc.class};
			for (Class f : features) 
				if(bl.get(f)!=null)
					newCoreSource.set(f, bl.get(f));
			
//			
//			newCoreSource.set(CoreAnnotations.CoarseTagAnnotation.class,bl.get(CoreAnnotations.CoarseTagAnnotation.class)+"");
//			newCoreSource.set(CoreAnnotations.CoNLLUMisc.class, bl.get(CoreAnnotations.CoNLLUMisc.class)+"");
//			newCoreSource.set(CoreAnnotations.ValueAnnotation.class,bl.get(CoreAnnotations.ValueAnnotation.class)+""); 
//			newCoreSource.set(CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class,bl.get(CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class));
//			newCoreSource.set(CoreAnnotations.LineNumberAnnotation.class, bl.get(CoreAnnotations.LineNumberAnnotation.class)+0);
//			newCoreSource.set(CoreAnnotations.TextAnnotation.class, bl.get(CoreAnnotations.TextAnnotation.class)+"");
//
//			newCoreSource.set(CoreAnnotations.IndexAnnotation.class, bl.get(CoreAnnotations.IndexAnnotation.class)+0);
//			newCoreSource.set(CoreAnnotations.LemmaAnnotation.class, bl.get(CoreAnnotations.LemmaAnnotation.class)+"");
//			newCoreSource.set(CoreAnnotations.PartOfSpeechAnnotation.class, bl.get(CoreAnnotations.PartOfSpeechAnnotation.class)+"");
//			newCoreSource.set(CoreAnnotations.CoNLLUFeats.class, bl.get(CoreAnnotations.CoNLLUFeats.class));
//			newCoreSource.set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, bl.get(CoreAnnotations.CoNLLDepParentIndexAnnotation.class)+0);
//			newCoreSource.set(CoreAnnotations.CoNLLDepTypeAnnotation.class, bl.get(CoreAnnotations.CoNLLDepTypeAnnotation.class)+"");
//
//			if(bl.get(CoreAnnotations.SentenceIndexAnnotation.class)!=null)
//				newCoreSource.set(CoreAnnotations.SentenceIndexAnnotation.class, bl.get(CoreAnnotations.SentenceIndexAnnotation.class)); // set word indentifiers
//			if(bl.get(CoreAnnotations.DocIDAnnotation.class)!=null)
//				newCoreSource.set(CoreAnnotations.DocIDAnnotation.class, bl.get(CoreAnnotations.DocIDAnnotation.class));
			IndexedWord newWord = new IndexedWord(newCoreSource); 
			return newWord;
		}
		
		private static SemanticGraph makeSoftCopy(SemanticGraph graph) {
			Map<Integer, IndexedWord> m = new  HashMap<Integer, IndexedWord>();
			List<IndexedWord> allNodes = new ArrayList<IndexedWord>(graph.vertexSet());
			Collections.sort(allNodes, new Comparator<IndexedWord>() {
				@Override
				public int compare(IndexedWord o1, IndexedWord o2) {
					return o1.get(index).compareTo(o2.get(index));
				}
			});
			for (IndexedWord w : allNodes) {
				Integer i = w.get(index);
				if(m.containsKey(i))
					System.err.println("orphan node detected (" + w + ")");
				else
					m.put(i, w);
			}
			for (IndexedWord w : allNodes) 
				w.reHashCode();
//			return graph;
			SemanticGraph newgraph = new SemanticGraph();
			for (SemanticGraphEdge edge : graph.edgeListSorted()) {
				IndexedWord s = m.get(edge.getSource().get(index));
				IndexedWord d = m.get(edge.getTarget().get(index));
				newgraph.addEdge(s, d, edge.getRelation(), edge.getWeight(), edge.isExtra());
			}
			for (IndexedWord r : graph.getRoots()) 
				newgraph.setRoot(r);
			
			return newgraph;
		}
}
