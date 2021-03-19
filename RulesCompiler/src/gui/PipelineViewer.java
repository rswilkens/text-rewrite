
package gui;

import javax.swing.DefaultListModel;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JTree;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreLabel.OutputFormat;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.ud.CoNLLUDocumentReader;
import edu.stanford.nlp.util.Pair;
import execution.Command;
import execution.If;
import execution.Operation;
import execution.Pipeline;
import execution.Unit;
import grammar.Lexer;
import grammar.Parser;
import java_cup.runtime.Symbol;
import javax.swing.JProgressBar;

public class PipelineViewer extends JFrame {

	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtPipeline;
	private JTextField txtOutput;
//	private JLabel lblPipelinename = new JLabel("");
	private JTextPane txtpnLoadedFiles = new JTextPane();
	private DefaultTreeModel treeTop = new DefaultTreeModel(root);
	private Thread run;

	private File pipelineFile;
	private Map<String, Integer[]> corpora = new HashMap<String, Integer[]>();
	private Pipeline pipeline;
	
	
	public PipelineViewer() {
		init();
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1224,654);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(2, 3, 0, 0));
		
		JLabel lblPipeline = new JLabel("Pipeline: ");
		panel.add(lblPipeline);
		
		txtPipeline = new JTextField();
		panel.add(txtPipeline);
		txtPipeline.setColumns(10);
		
		JButton btnOpenPipeline = new JButton("...");
		btnOpenPipeline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(false);
				int returnVal = fc.showOpenDialog(null);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            txtPipeline.setText(file.getAbsolutePath());
		            pipelineFile = file;
		            loadPipeline();
		        }
			}

		});
		panel.add(btnOpenPipeline);
		
		JLabel lblOutputFolder = new JLabel("Output folder: ");
		panel.add(lblOutputFolder);
		
		txtOutput = new JTextField();
		panel.add(txtOutput);
		txtOutput.setColumns(10);
		
		JButton btnOpenOutput = new JButton("...");
		btnOpenOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setMultiSelectionEnabled(false);
		        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            txtOutput.setText(file.getAbsolutePath());
		        }
			}
		});
		panel.add(btnOpenOutput);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JLayeredPane layeredPane = new JLayeredPane();
		tabbedPane.addTab("Corpora", null, layeredPane, null);
		layeredPane.setLayout(new BorderLayout(0, 0));
		
		JList lstCorpora = new JList(new DefaultListModel());
		layeredPane.add(lstCorpora, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		layeredPane.add(panel_1, BorderLayout.NORTH);
		
		JButton btnAdd = new JButton("add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
		        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            ( (DefaultListModel) lstCorpora.getModel()).addElement(file.getAbsolutePath());
		            addCorpora(file.getAbsoluteFile());
		        }
				
			}
			
		});
		panel_1.add(btnAdd);
		
		JButton btnRemove = new JButton("remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeCorpora(lstCorpora.getSelectedValue().toString());
				( (DefaultListModel) lstCorpora.getModel()).remove(lstCorpora.getSelectedIndex());
			}

		});
		panel_1.add(btnRemove);
		
		JButton btnClear = new JButton("clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				( (DefaultListModel) lstCorpora.getModel()).clear();
				clearCorpora();
				
			}
		});
		panel_1.add(btnClear);
		
		JPanel panel_2 = new JPanel();
		layeredPane.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new CardLayout(0, 0));
		
		txtpnLoadedFiles.setBackground(UIManager.getColor("Button.background"));
		txtpnLoadedFiles.setForeground(Color.BLACK);
		txtpnLoadedFiles.setText("\n  Loaded files: 0\n  Loaded sentences: 0\n  Loaded words: 0");
		panel_2.add(txtpnLoadedFiles, "name_238697024513145");
		
		JLayeredPane layeredPane_1 = new JLayeredPane();
		tabbedPane.addTab("Pipeline", null, layeredPane_1, null);
		layeredPane_1.setLayout(new BorderLayout(0, 0));
		
//		layeredPane_1.add(lblPipelinename, BorderLayout.NORTH);
		JTree tree = new JTree(treeTop);
		layeredPane_1.add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JLayeredPane layeredPane_2 = new JLayeredPane();
		tabbedPane.addTab("Run", null, layeredPane_2, null);
		layeredPane_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		layeredPane_2.add(panel_3, BorderLayout.NORTH);
		
		JButton bttRun = new JButton("run");
		bttRun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(pipeline == null)
					JOptionPane.showMessageDialog(null, "First, you should read a pipeline");
				else if(corpora.size()==0)
					JOptionPane.showMessageDialog(null, "First, you should load at least one corpus");
				else {
					try {
						run = new Thread(pipeline);
						pipeline.setFiles(corpora.keySet());
						run.start();
						run.join();
						new ShowResults(pipeline.getAllCorpusProcessed());
						save(txtOutput.getText(), pipeline.getAllCorpusProcessed());
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		panel_3.add(bttRun);
		
		JButton bttAbort = new JButton("Abort");
		bttAbort.setEnabled(false);
		bttAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(run != null)
					run.stop();
			}
		});
		panel_3.add(bttAbort);
		
		JPanel panel_4 = new JPanel();
		layeredPane_2.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new GridLayout(0, 1, 0, 0));
//		getContentPane().add(new PipelineViewer());
		setVisible(true);
	}
	
	protected void save(String file, Map<String, List<Pair<String, Map<String, String>>>> allCorpusProcessed) {
		try {
			if(!file.endsWith("/")) file += "/";
			
			for (String corpus : allCorpusProcessed.keySet()) {
				String corpusName = corpus;
				if(corpusName.contains("/"))
					corpusName = corpusName.substring(corpusName.lastIndexOf("/")+1, corpusName.length());
				BufferedWriter writer = new BufferedWriter(new FileWriter(file + corpusName));
				for (Pair<String, Map<String, String>> sentInfo : allCorpusProcessed.get(corpus)) {
					writer.write("## " + sentInfo.first() + "\n");
					for (String operation : sentInfo.second().keySet()) {
						writer.write("#### OPERATION: " + operation + "\n");
						writer.write(sentInfo.second().get(operation) + "\n");
//						for (SemanticGraph sent : sentInfo.second().get(operation)) {
//							writer.write("###### " + sent.toRecoveredSentenceString() + "\n");
//							writer.write(sent.toString(OutputFormat.CoNLL) + "\n");
//							System.out.println(sent.toString(OutputFormat.CoNLL));
//						}
					}
				}
			    writer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void loadPipeline() {
		Parser parser;
		try {
			StringBuffer resultStringBuilder = new StringBuffer();
			try (BufferedReader br = new BufferedReader(new FileReader(pipelineFile.getAbsolutePath()))) {
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
			pipeline = parser.getPipeline();
			updatePipeline();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File didn't find");
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File doesn't load");
			e.printStackTrace();
		}
        
		
	}
	
	private void updatePipeline() {
//		lblPipelinename.setText(pipeline.getName());
		int opid = 1;
		root.removeAllChildren();
		
		root.add(new DefaultMutableTreeNode("Pipeline: " + pipeline.getName()));
		
//		treeTop.setUserObject("Pipeline: " + pipeline.getName());
		for (Operation operations : pipeline.getRules()) {
			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode("Operation " + opid + ": " + operations.getName() + " (" + operations.getStrategy() + ")");
			for (Unit unit : operations.getUnits()) {
				String search = unit.getSent() + ": " + unit.getSemgrex();
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(search);
				newChild.add(node);
				
//				DefaultMutableTreeNode cmd = new DefaultMutableTreeNode("Commands");
				addCmds(node, unit.getCommands());
//				node.add(cmd);
				newChild.add(new DefaultMutableTreeNode("save " + unit.getSave()));
			}
			
			
			root.add(newChild);
			opid++;
		}
		treeTop.reload(root);
		
	}

	private void addCmds(DefaultMutableTreeNode cmd, List<Command> unitCommands) {
		for (Command c : unitCommands) {
			if (c instanceof If) {
				If val = ((If) c);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode("If " + val.getCondition());
				
				DefaultMutableTreeNode ifNode = new DefaultMutableTreeNode("If condition is TRUE");
				addCmds(ifNode, val.getBlockIF());
				node.add(ifNode);
				
				if(val.getBlockELSE() != null) {
					DefaultMutableTreeNode elseNode = new DefaultMutableTreeNode("If condition is FALSE");
					addCmds(elseNode, val.getBlockELSE());
					node.add(elseNode);
				}
				cmd.add(node);
			}else
				cmd.add(new DefaultMutableTreeNode(c));
		}
		
	}

	private void addCorpora(File file) {
		try {
			System.out.println(file.getAbsolutePath());
			CoNLLUDocumentReader reader = new CoNLLUDocumentReader();
			Iterator<SemanticGraph> it = reader.getIterator(IOUtils.readerFromString(file.getAbsolutePath()));
			int sents = 0;
			int words = 0;
			while(it.hasNext()) {
				SemanticGraph s = it.next();
				sents += 1;
				words += s.size();
			}
			Integer[] aux = {sents, words};
			corpora.put(file.getAbsolutePath(), aux);
			updateCorporaCount();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File didn't load.\n\n" + e.getMessage());
		}
		
	}
	private void clearCorpora() {
		txtpnLoadedFiles.setText("\n  Loaded files: 0\n  Loaded sentences: 0\n  Loaded words: 0");
		corpora = new HashMap<String, Integer[]>();
	}
	private void removeCorpora(String file) {
		corpora.remove(file);
		updateCorporaCount();
	}
	private void updateCorporaCount() {
		int files = corpora.size();
		int sents = 0;
		int words = 0;
		for (Integer[] v : corpora.values()) {
			sents += v[0];
			words += v[1];
		}
		
		txtpnLoadedFiles.setText("\n  Loaded files: " + files + "\n  Loaded sentences: " + sents + "\n  Loaded words: "+words);
	}
	
	public static void main(String[] args) {
		new PipelineViewer();
//		JFrame frame = new JFrame("My First GUI");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(300,300);
//		frame.getContentPane().add(new PipelineViewer());
//		frame.setVisible(true);
	}
}
