package execution;

import java.util.Map;
import edu.stanford.nlp.ling.IndexedWord;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface Command {

	public static enum cmdType {DELETE, UPDATE, INSERT, SPLIT, MOVE, REPLACE, REPLACEVAL}; 
	
//	public Command(cmdType cmdType, String sentFrom, String sentRet, List<String> vars) { // DELETE
//		this.cmd = cmdType;
//		this.from = sentFrom;
//		this.to = sentRet;
//		
//				
//	}
	
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) throws Exception;


}
