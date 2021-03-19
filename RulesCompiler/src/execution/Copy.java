package execution;

import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Copy implements Command {

	private String outVar;
	private String inVar;

	public Copy(String outVar, String inVar) {
		this.outVar = outVar;
		this.inVar = inVar;
	}

	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		SemanticGraph w = workspaceSent.get(inVar);
		SemanticGraph out = Ssurgeon.makeHardCopy(w);
		workspaceSent.put(outVar, out);
		return outVar;
	}
	
	@Override
	public String toString() {
		return outVar + " = copy(" + inVar + ")";
	}

}
