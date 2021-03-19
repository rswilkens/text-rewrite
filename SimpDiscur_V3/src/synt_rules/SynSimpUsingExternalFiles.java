package synt_rules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import corpus_reader.Corpus;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreLabel.OutputFormat;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;
import execution.Pipeline;
import grammar.Lexer;
import grammar.Parser;
import java_cup.runtime.Symbol;
import simplifier.Simplifier;


public class SynSimpUsingExternalFiles implements SyntacticSimp {

	private List<Pipeline> pipelines;
	
	public SynSimpUsingExternalFiles(File ... ruleFiles) {
		pipelines = new ArrayList<Pipeline>();
		for (File file : ruleFiles) {
			System.out.println(file);
			Pipeline pipe = loadPipeline(file);
			pipelines.add(pipe);
		}
	}
	
	private Pipeline loadPipeline(File pipelineFilePath) {
		Parser parser;
		try {
			StringBuffer resultStringBuilder = new StringBuffer();
			try (BufferedReader br = new BufferedReader(new FileReader(pipelineFilePath.getAbsolutePath()))) {
				        String line;
				        while ((line = br.readLine()) != null) {
				        	if(line.contains(" #"))  line = line.substring(0,line.indexOf(" #"));
				        	if(line.contains("\t#"))  line = line.substring(0,line.indexOf("\t#"));
				            resultStringBuilder.append(line.trim()).append("\n");
				        }
				    }
			
			
			StringReader r = new StringReader(resultStringBuilder.toString().trim());
			
			//new FileReader(pipelineFile.getAbsolutePath()))
			Lexer lexer = new Lexer(r);
			parser = new Parser(lexer);
			Symbol s = parser.parse();
			Pipeline pipeline = parser.getPipeline();
//			updatePipeline();
			return pipeline;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File didn't find");
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File doesn't load");
			e.printStackTrace();
		}
        
		return null;
	}
	
	@Override
	public Corpus simplify(Corpus corpus) {
		for (Pipeline pipeline : pipelines) {
			Corpus corpusAux = corpus;
			try{
				System.out.println("running " + pipeline.getName());
				corpus = simplify(corpus, pipeline);
			}catch (Exception e) {
				System.err.println("######################################");
				System.err.println(e);
				System.err.println("######################################");
				corpus = corpusAux;
			}
		}
		return corpus;
	}
	private Corpus simplify(Corpus corpusOri, Pipeline pipeline) {
				
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("tmp1." + pipeline.getName() + ".conll"));
			out.write(corpusOri.toString());		
			out.close();
			
			Thread run = new Thread(pipeline);
			Set<String> corpora = new HashSet<String>();
			corpora.add("tmp1." + pipeline.getName() + ".conll");
			pipeline.setFiles(corpora);
			try {
				run.start();
				run.join();
				Map<String, List<Pair<SemanticGraph, List<String>>>> output = pipeline.getOutputCorpora();
				for (List<Pair<SemanticGraph, List<String>>> sents : output.values()) {
					save("tmp2." + pipeline.getName() + ".conll",sents);	
				}
				Corpus corpus = new Corpus("tmp2." + pipeline.getName() + ".conll");
				
				new File(Simplifier.currentCorpus + "_input." + pipeline.getName() + ".conll").delete();
				Files.copy(
						Paths.get("tmp1." + pipeline.getName() + ".conll"), 
						Paths.get(Simplifier.currentCorpus + "_input." + pipeline.getName() + ".conll"));
				new File("tmp1." + pipeline.getName() + ".conll").delete();  
				new File("tmp2.Syn" + pipeline.getName() + ".conll").delete();
				corpusOri = corpus;
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			return corpusOri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void save(String file, List<Pair<SemanticGraph, List<String>>> sents) throws Exception {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (Pair<SemanticGraph, List<String>> pair : sents) {
				if(pair.second().size()==0) {
					writer.write(pair.first().toString(OutputFormat.CoNLLcoref) + "\n");
				}else {
					for (String output : pair.second()) {
//						String output = pair.second().get(0);<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< for>
						writer.write(output + "\n");	
					}
//					String output = pair.second().get(0);<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< for>
//					writer.write(output + "\n");
				}
			}
//			if(!file.endsWith("/")) file += "/";
//			
//			for (String corpus : sents.keySet()) {
//				String corpusName = corpus;
//				if(corpusName.contains("/"))
//					corpusName = corpusName.substring(corpusName.lastIndexOf("/")+1, corpusName.length());
//				
//				for (Pair<String, Map<String, String>> sentInfo : sents.get(corpus)) {
//					writer.write("## " + sentInfo.first() + "\n");
//					for (String operation : sentInfo.second().keySet()) {
//						writer.write("#### OPERATION: " + operation + "\n");
//						writer.write(sentInfo.second().get(operation) + "\n");
//						if(sentInfo.second().get(operation).length()>0)
//							System.out.println();
////						for (SemanticGraph sent : sentInfo.second().get(operation)) {
////							writer.write("###### " + sent.toRecoveredSentenceString() + "\n");
////							writer.write(sent.toString(OutputFormat.CoNLL) + "\n");
////							System.out.println(sent.toString(OutputFormat.CoNLL));
////						}
//					}
//				}
			    writer.close();
//			}
		
	}

}
