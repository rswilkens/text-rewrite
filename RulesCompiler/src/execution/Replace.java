package execution;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraph.OutputFormat;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon.ReplacingType;

public class Replace implements Command {

	private String from;
	private String to;
	private String var1;
	private String var2;
//	private boolean dep;
//	private String position;
//	private boolean left;
	private SemanticGraph tree;
	private List<String> replacingType;

	public Replace(String from, String to, String var1, String var2) {
		this.from=from;
		this.to = to;
		this.var1=var1;
		this.var2=var2;
	}
	
	public Replace(String from, String to, String var1, SemanticGraph tree) {
		this.from=from;
		this.to = to;
		this.var1=var1;
		this.tree=tree;
	}

	public Replace(String from, String to, String var1, String var2, List<String> replacingType) {
		this.from=from;
		this.to = to;
		this.var1=var1;
		this.var2=var2;
		this.replacingType=replacingType;
	}
	
	public Replace(String from, String to, String var1, SemanticGraph tree, List<String> replacingType) {
		this.from=from;
		this.to = to;
		this.var1=var1;
		this.tree=tree;
		this.replacingType=replacingType;
	}

	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		
		SemanticGraph sent = workspaceSent.get(from);
		IndexedWord w1 = workspaceVar.get(var1);
		
		if(replacingType == null) {
			if(tree != null) {
				//System.out.println(to + " = replace " + var1 + " (" + replacingType + ") \"" + tree + "\" from " + from);
				sent = Ssurgeon.replaceNodeRelation(w1, tree, sent);
			}else if( var2 != null) {
				//System.out.println(to + " = replace " + var1 + " (" + replacingType + ")" + var2 + " from " + from);
				IndexedWord w2 = workspaceVar.get(var2);
				SemanticGraph aux = new SemanticGraph();
				aux.addVertex(w2);
				aux.setRoot(w2);
				sent = Ssurgeon.replaceNodeRelation(w1, aux, sent);
			}
		}else {
			List<ReplacingType> replacing = new ArrayList<Ssurgeon.ReplacingType>();
			for (String aux : replacingType) 
				replacing.add(ReplacingType.valueOf(aux));
			
			if(tree != null) {
				//System.out.println(to + " = replace " + var1 + ", " + dep + ", " + position + ", " + left + ", \"" + tree + "\" from " + from);
				sent = Ssurgeon.replace(w1, tree.getFirstRoot(), replacing, sent);
			}else if( var2 != null) {
				//System.out.println(to + " = replace " + var1 + ", " + dep + ", " + position + ", " + left + ", " + var2 + " from " + from);
				if(workspaceVar.containsKey(var2))
					sent = Ssurgeon.replace(w1, workspaceVar.get(var2), replacing, sent);
				else
					sent = Ssurgeon.replace(w1, workspaceSent.get(var2).getFirstRoot(), replacing, sent);
			}
		}
		
		workspaceSent.put(to, sent);
		return to;
	}
	
	@Override
	public String toString() {
		if(replacingType != null) {
			if(tree != null) 
				return to + " = replace " + var1 + replacingType.toString().replace("[", "(").replace("]", ")") + " \"" + tree.toString(OutputFormat.RECURSIVE) + "\" from " + from;
			else
				return  (to + " = replace " + var1 + replacingType.toString().replace("[", "(").replace("]", ")") + var2 + " from " + from);
			
		}else {
			if(tree != null) 
				return (to + " = replace " + var1 + ", \"" + tree.toString(OutputFormat.RECURSIVE) + "\" from " + from);
			else 
				return (to + " = replace " + var1 + ", " + var2 + " from " + from);
			
		}
	}
	
	

}
