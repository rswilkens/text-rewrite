package execution;
import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraph.OutputFormat;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Insert implements Command {

	private String from;
	private String to;
	private String var1;
	private String var2;
	private boolean dep;
	private String position;
	private boolean left;
	private SemanticGraph tree;

	public Insert(String from, String to, String var1, String var2, boolean dep, String position, boolean left) {
		this.from =from;
		this.to=to;
		this.var1=var1;
		this.var2=var2;
		this.dep=dep;
		this.position=position;
		this.left = left;
	}
	
	public Insert(String from, String to, String var1, SemanticGraph tree, boolean dep, String position, boolean left) {
		this.from =from;
		this.to=to;
		this.var1=var1;
		this.tree=tree;
		this.dep=dep;
		this.position=position;
		this.left = left;
	}

	@Override
	public String toString() {
		if(tree != null) {
			return (to + " = insert " + var1 + ", " + dep + ", " + position + ", " + left + ", \"" + tree.toString(OutputFormat.RECURSIVE) + "\" from " + from);
		}else {
			return (to + " = insert " + var1 + ", " + dep + ", " + position + ", " + left + ", " + var2 + " from " + from);
		}
	}
	
	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		
		SemanticGraph sent = workspaceSent.get(from);
		IndexedWord w1 = workspaceVar.get(var1);
		if(w1==null)
			w1 = workspaceSent.get(var1).getFirstRoot();
		if(tree != null) {
//			System.out.println(to + " = insert " + var1 + ", " + dep + ", " + position + ", " + left + ", \"" + tree + "\" from " + from);
			sent = Ssurgeon.insert(w1, tree, position, left, dep, sent);			
		}else if( var2 != null) {
			//System.out.println(to + " = insert " + var1 + ", " + dep + ", " + position + ", " + left + ", " + var2 + " from " + from);
			if(workspaceVar.containsKey(var2))
				sent = Ssurgeon.insert(w1, workspaceVar.get(var2), position, left, dep, sent);
			else
				sent = Ssurgeon.insert(w1, workspaceSent.get(var2), position, left, dep, sent);
		}
		if(Pipeline.DEBUG) System.out.println(sent);
		workspaceSent.put(to, sent);
		return to;

	}

}
