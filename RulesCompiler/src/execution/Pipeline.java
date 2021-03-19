

package execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel.OutputFormat;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;
import edu.stanford.nlp.util.Pair;

public class Pipeline implements Runnable{

	public static boolean DEBUG = false;
	private String name;
	private List<Operation> rules;
	private Set<String> corpora;
	private Map<String, List<Pair<String, Map<String, String>>>> allCorpusProcessed;
	private Map<String,List<Pair<SemanticGraph,List<String>>>> outputCorpora;

	public Pipeline(String name, List<Operation> rules) {
		this.name = name;
		this.rules = rules;
	}
	
	public String getName() {
		return name;
	}
	public List<Operation> getRules() {
		return rules;
	}

	public Map<String, List<Pair<String, Map<String, String>>>> getAllCorpusProcessed() {
		return allCorpusProcessed;
	}
	public Map<String,List<Pair<SemanticGraph,List<String>>>> getOutputCorpora() {
		return outputCorpora;
	}
	@Override
	public void run() {
		if(Pipeline.DEBUG) System.out.println(toString());
		allCorpusProcessed = new HashMap<>();
		outputCorpora = new HashMap<String, List<Pair<SemanticGraph,List<String>>>>();
		for (String corpus : corpora) {
			try {
				CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
				Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString(corpus));
				List<Pair<String, Map<String, String>>> corpusProcessed = new ArrayList<>();
				List<Pair<SemanticGraph,List<String>>> corpursOutput = new ArrayList<Pair<SemanticGraph,List<String>>>();
				while(it.hasNext()) {
					try {
						SemanticGraph sent = it.next();
						String sentOrig = sent.toRecoveredSentenceString();
						Map<String, String> corpusProcessedOperation = new HashMap<String, String>();
						List<String> appliedRules = new ArrayList<String>();
						for (Operation operation : rules) {
							operation.setSent(sent);
							operation.run(null, null);
							
							List<SemanticGraph> result = operation.getSentences(); // an operation impacts on the successives
							if(Pipeline.DEBUG) {
								System.out.println("****************************");
								System.out.println(sentOrig);
								System.out.println("----------------------------");
								for (SemanticGraph r : result) {
									System.out.println(r.toString(OutputFormat.CoNLL));
									
								}
								System.out.println("****************************");	
							}
								
							String resultStr = "";
							for (SemanticGraph r : result) {
								resultStr += r.toString(OutputFormat.CoNLL) + "\n";
								appliedRules.add(r.toString(OutputFormat.CoNLL));
							}
							corpusProcessedOperation.put(operation.getName(), resultStr);
						}
						Pair<SemanticGraph,List<String>> processedSent = new Pair<SemanticGraph, List<String>>(sent, appliedRules);
						corpursOutput.add(processedSent);
						corpusProcessed.add(new Pair<String, Map<String, String>>(sentOrig, corpusProcessedOperation));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				outputCorpora.put(corpus, corpursOutput);
				allCorpusProcessed.put(corpus, corpusProcessed);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setFiles(Set<String> corpora) {
		this.corpora = corpora;
	}
	
	@Override
	public String toString() {
		return name + " DEBUG = " + DEBUG;
	}
	

}
