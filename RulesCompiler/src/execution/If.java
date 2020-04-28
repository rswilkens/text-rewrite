package execution;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class If implements Command {

	private List<String> condition;
	private List<Command> blockIF;
	private List<Command> blockELSE;

	public If(List<String> condition, List<Command> blockIF, List<Command> blockELSE) {
		this.condition=condition;
		this.blockIF=blockIF;
		this.blockELSE=blockELSE;
	}
	public If(List<String> condition, List<Command> blockIF) {
		this.condition=condition;
		this.blockIF=blockIF;
	}

	public List<String> getCondition() {
		return condition;
	}
	
	public List<Command> getBlockIF() {
		return blockIF;
	}
	public List<Command> getBlockELSE() {
		return blockELSE;
	}
	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception {
		if(Pipeline.DEBUG) System.out.println(toString());
		
		boolean cond = true;
		for (String c : condition) 
			if(!workspaceVar.containsKey(c)) {
				cond = false;
				break;
			}

		if(cond) {
			if(Pipeline.DEBUG) System.out.println("\tCondition is TRUE");
			String aux = null;
			for (Command c : blockIF) {
				aux = c.run(workspaceSent, workspaceVar);
			}
			return aux;
		}else {
			if(Pipeline.DEBUG) System.out.println("\tCondition is FALSE");
			String aux = null;
			if(blockELSE != null)
				for (Command c : blockELSE) {
					aux = c.run(workspaceSent, workspaceVar);
				}
			return aux;
		}
	}
	@Override
	public String toString() {
		return "IF " + condition;
	}
}
