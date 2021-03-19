package edu.stanford.nlp.semgraph.semgrex.ssurgeon;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class _Move {

	/**
	 move name position
	 
	 moves the named node into the specified position
	 
	 position: relation name
	 
	 relation:
				 $+ 	the left sister of the named node
				 $-		the right sister of the named node
				 >i 	the i_th daughter of the named node
				 >-i 	the i_th daughter, counting from the right, of the named node.
	Example: move vb $+ wh
		name = vb
		relation = $+
		name = wh
	 
	(ROOT
	    (SQ 
			(NP (NNS Cats))
	          (VP (VBP do)
	                (VP (WHNP what)
			  	 (VB eat)))
			(PUNCT .)))
	
	VP < (/^WH/=wh $++ /^VB/=vb)
	move vb $+ wh


	(ROOT
	    (SQ 
		(NP (NNS Cats))
	          (VP (VBP do)
	                (VP (VB eat)
			 (WHNP what)))
		(PUNCT .)))
			
	 * @param word
	 * @param graph
	 * @return
	 * @throws Exception 
	 */
	public static SemanticGraph evaluate(IndexedWord name1, String position, IndexedWord name2, SemanticGraph graph) throws Exception {
		if(graph.getRoots().contains(name1))
			throw new Exception("name1 cannot be root");
		
		
		IndexedWord parent1 = graph.getParent(name1);
		IndexedWord parent2 = graph.getParent(name2);
		
		SemanticGraphEdge edge = graph.getEdge(parent1, name1);
		graph.removeEdge(edge);
		graph.addEdge(name1, parent2, edge.getRelation(), edge.getWeight(), edge.isExtra());
		
		name1.setIndex(name2.index()-1);
		
		return graph;
	}
	
}
