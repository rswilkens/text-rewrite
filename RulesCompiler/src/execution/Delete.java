package execution;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Delete implements Command{

	
	private String from;
	private String to;
	private List<String> vars;

	public Delete(String sentFrom, String sentRet, List<String> vars) { 
		this.from = sentFrom;
		this.to = sentRet;
		this.vars = vars;
	}
	
	@Override
	public String toString() {
		return to + " = delete " + vars + " from " + from;
	}
	
	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		SemanticGraph sent = workspaceSent.get(from);
		IndexedWord[] words = new IndexedWord[vars.size()];
		
		for (int i = 0; i < vars.size(); i++) 
			words[i] = workspaceVar.get(vars.get(i));
		
		sent = Ssurgeon.delete(sent, words);
		if(Pipeline.DEBUG) System.out.println(sent);
		workspaceSent.put(to, sent);
		return to;
	}

}
