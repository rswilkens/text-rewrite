package execution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Unit implements Command {

	private String semgrex;
	private List<Command> commands;
	private String sent;
	private SemanticGraph sentence;
	private List<String> save;
	private List<SemanticGraph> savedSentences;

	public Unit(String semgrex, List<Command> commands, String sent) {
		if (Pipeline.DEBUG) System.out.println(semgrex);
		this.semgrex = semgrex.substring(1,semgrex.length()-1);
		this.commands = commands;
		this.sent = sent;
		save = new ArrayList<String>();
	}
	public Unit(String semgrex, List<Command> commands, String sent, List<String> save) {
		if (Pipeline.DEBUG) System.out.println(semgrex);
		this.semgrex = semgrex.substring(1,semgrex.length()-1);
		this.commands = commands;
		this.sent = sent;
		this.save = save;
	}
	public List<Command> getCommands() {
		return commands;
	}
	public String getSemgrex() {
		return semgrex;
	}
	public String getSent() {
		return sent;
	}

	public List<String> getSave() {
		return save;
	}
	public List<SemanticGraph> getSavedSentences() {
		return savedSentences;
	}
	@Override
	public String run(Map<String, SemanticGraph> workspaceSent, Map<String, IndexedWord> workspaceVar) {
		if(Pipeline.DEBUG) System.out.println(toString());
		SemgrexPattern semgrex = SemgrexPattern.compile(this.semgrex);
		SemgrexMatcher matcher = semgrex.matcher(sentence);
		if(matcher.find()) {
			System.out.println("---------------------------------------------");
			System.out.println("-- DEBUG: ");
			System.out.println("---------------------------------------------");
			System.out.println("-- Node Names:");
			for (String name : matcher.getNodeNames()) 
				System.out.println("-- \t" + name + " = " + matcher.getNode(name));
			System.out.println("-- Relation Names:");
			for (String name : matcher.getRelationNames()) 
				System.out.println("-- \t" + name + " = " + matcher.getRelnString(name));
			System.out.println("---------------------------------------------");
			
			try {
				workspaceVar.clear();
				for (String name : matcher.getRelationNames()) 
					workspaceVar.put(name, matcher.getNode(name));
				for (String name : matcher.getNodeNames()) 
					workspaceVar.put(name, matcher.getNode(name));
				workspaceSent.clear();
				workspaceSent.put(this.sent, this.sentence);
				String ret = null;
				for (Command command : commands) {
					ret = command.run(workspaceSent, workspaceVar);
				}
				savedSentences = new ArrayList<SemanticGraph>();
				for (String k : save) 
					savedSentences.add(workspaceSent.get(k));
				
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public void setSent(SemanticGraph sent) {
		this.sentence = sent;
	}
	@Override
	public String toString() {
		return "UNIT " + sent + ": " + semgrex;
	}
}
