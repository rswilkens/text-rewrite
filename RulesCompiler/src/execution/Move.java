package execution;

import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Move implements Command {

	
	private String from;
	private String to;
	private String var1;
	private String var2;
	private boolean dep;
	private String position;
	private boolean left;

	public Move(String from, String to, String var1, String var2, boolean dep, String position, boolean left) {
		this.from = from;
		this.to = to;
		this.var1 = var1;
		this.var2 = var2;
		this.dep=dep;
		this.position = position;
		this.left = left;
	}
	
	@Override
	public  String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		
		SemanticGraph sent = workspaceSent.get(from);
		IndexedWord w1 = workspaceVar.get(var1);
		IndexedWord w2 = workspaceVar.get(var2);
		sent = Ssurgeon.move(w1, w2, position, left, dep, sent);
		
		workspaceSent.put(to, sent);
		
		return to;
	}
	
	@Override
	public String toString() {
		return to + " = move " + var1 + ", " + dep + ", " + position + ", " + left + ", " + var2 + " from " + from;
	} 
	

}
