package execution;

import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Split implements Command {

	private String from;
	private String to1;
	private String to2;
	private String var;


	public Split(String from, String to1, String to2, String var) {
		this.from = from;
		this.to1 = to1;
		this.to2 = to2;
		this.var = var;
	}
	
	@Override
	public String toString() {
		return to1 + ", " + to2 + " = split " + var + " from " + from;
	}
	
	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		SemanticGraph sent = workspaceSent.get(from);
		IndexedWord v = workspaceVar.get(var);
		SemanticGraph[] aux = Ssurgeon.split(v, sent);
		SemanticGraph s1 = aux[0];
		SemanticGraph s2 = aux[1];		
		
		workspaceSent.put(to1, s1);
		workspaceSent.put(to2, s2);
		if(Pipeline.DEBUG) System.out.println(s1 + "\n" + s2);
		return to1;
	}

}
