

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
	private Map<String, List<Pair<String, Map<String, List<SemanticGraph>>>>> allCorpusProcessed;

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

	public Map<String, List<Pair<String, Map<String, List<SemanticGraph>>>>> getAllCorpusProcessed() {
		return allCorpusProcessed;
	}
	
	@Override
	public void run() {
		if(Pipeline.DEBUG) System.out.println(toString());
		allCorpusProcessed = new HashMap<>();
		for (String corpus : corpora) {
			try {
				CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
				Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString(corpus));
				List<Pair<String, Map<String, List<SemanticGraph>>>> corpusProcessed = new ArrayList<>();
				while(it.hasNext()) {
					try {
						SemanticGraph sent = it.next();
						String sentOrig = sent.toRecoveredSentenceString();
						Map<String, List<SemanticGraph>> corpusProcessedOperation = new HashMap<String, List<SemanticGraph>>();
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
							corpusProcessedOperation.put(operation.getName(), result);
						}
						corpusProcessed.add(new Pair<String, Map<String, List<SemanticGraph>>>(sentOrig, corpusProcessedOperation));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
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
