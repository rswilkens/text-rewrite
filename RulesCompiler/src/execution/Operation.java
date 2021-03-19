package execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.ssurgeon.Ssurgeon;

public class Operation implements Command {

	private String name;
	private List<Unit> units;
	private SemanticGraph sent;
	private String strategy;
//	private List<String> output;
//	private Map<String, SemanticGraph> workspaceSet;
	private List<SemanticGraph> output = new ArrayList<SemanticGraph>();

//	public Operation(String name, List<Unit> unit, String strategy, List<String> vars) {
//		this.name = name;
//		this.units = unit;
//		this.strategy = strategy;
//		output = vars;
//	}
//	public Operation(String name, List<Unit> unit, List<String> vars) {
//		this.name = name;
//		this.units = unit;
//		this.strategy = "seq";
//		output = vars;
//	}
	public Operation(String name, List<Unit> unit, String strategy) {
		this.name = name;
		this.units = unit;
		this.strategy = strategy;
//		output = new ArrayList<String>();
	}
	public Operation(String name, List<Unit> unit) {
		this.name = name;
		this.units = unit;
		this.strategy = "seq";
//		output = new ArrayList<String>();
	}
	public String getStrategy() {
		return strategy;
	}
	public String getName() {
		return name;
	}
	public List<Unit> getUnits() {
		return units;
	}
//	public String getUnitSemgrex() {
//		return unit.getSemgrex();
//	}
//	public List<Command> getUnitCommands() {
//		return unit.getCommands();
//	}
//	public String getUnitSent() {
//		return unit.getSent();
//	}
	
	@Override
	public String run(Map<String, SemanticGraph> workspaceSet, Map<String, IndexedWord> workspaceVar)  throws Exception{
		if(Pipeline.DEBUG) System.out.println(toString());
		output = new ArrayList<SemanticGraph>();
		List<List<SemanticGraph>> rets = new ArrayList<>(); 
//		List<String> retsNames = new ArrayList<String>(); 
		for (Unit unit : units) {
			workspaceSet = new HashMap<String, SemanticGraph>();
			workspaceVar = new HashMap<String, IndexedWord>();
			unit.setSent(Ssurgeon.makeHardCopy(sent));
			String s = unit.run(workspaceSet, workspaceVar);
			if(s != null) {
				List<SemanticGraph> sents = unit.getSavedSentences();
				rets.add(sents);
				
				if(strategy.equals("seq")) {					
					sent = rets.get(rets.size()-1).get(0);	
				}else if(strategy.equals("first")) {
					sent = rets.get(rets.size()-1).get(0);
					break;
				}
			}
				
		}
		if(rets.size()>0)
			if(strategy.equals("rand")) {
				int i = new Random().nextInt(rets.size());
				output.addAll(rets.get(i));
			}else {
				output.addAll(rets.get(rets.size()-1));
			}
		return null;
	}
	
	

	public void setSent(SemanticGraph sent) {
		this.sent = sent;
	}
//	public Map<String, SemanticGraph> getSent() {
//		Map<String, SemanticGraph> ret = new HashMap<String, SemanticGraph>();
//		for (String v : output) {
//			ret.put(v, workspaceSet.get(v));
//		}
//		return ret;
//	}

	@Override
	public String toString() {
		return "OPERATION " + name; 
	}
	public List<SemanticGraph> getSentences() {
		return output;
	}
}
