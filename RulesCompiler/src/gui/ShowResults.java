package gui;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Dimension;

public class ShowResults extends JFrame {
	private DefaultComboBoxModel<String> sentModel;
	private JTextArea txtResults;
	private JComboBox cmbCorpus;
	private Map<String, List<Pair<String, Map<String, List<SemanticGraph>>>>> allCorporaProcessed;
	private Map<String, Map<String, List<SemanticGraph>>> descCorpus = new HashMap<>();
	private Map<String, List<SemanticGraph>> descSents = new HashMap<>();
	private DefaultComboBoxModel<String> operationModel;

	public ShowResults(Map<String, List<Pair<String, Map<String, List<SemanticGraph>>>>> map) {
		setSize(new Dimension(1048, 588));
		this.allCorporaProcessed = map;


		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblCorpus = new JLabel("Corpus: ");
		panel.add(lblCorpus);

		cmbCorpus = new JComboBox(new Vector<String>(map.keySet()));
		cmbCorpus.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//				System.out.println("SELECTED = " + cmbCorpus.getSelectedItem());
				descCorpus = new HashMap<>();
				sentModel.removeAllElements();
				Object selectedItem = cmbCorpus.getSelectedItem();
				for (Pair<String, Map<String, List<SemanticGraph>>> vals : map.get(selectedItem.toString())) {
					descCorpus.put(vals.first(), vals.second());
					sentModel.addElement(vals.first());
				}
			}
		});
		panel.add(cmbCorpus);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.NORTH);

		JLabel lblOriginalSentence = new JLabel("Original sentence: ");
		panel_2.add(lblOriginalSentence);

		sentModel = new DefaultComboBoxModel<String>();
		JComboBox cmbSentences = new JComboBox(sentModel);
		cmbSentences.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				Object item = sentModel.getSelectedItem();

				if (item != null) {
					//					System.out.println(item);
					Map<String, List<SemanticGraph>> ops = descCorpus.get(item.toString());
					operationModel.removeAllElements();
					descSents = new Hashtable<String, List<SemanticGraph>>();
					for (String op : ops.keySet()) {
						operationModel.addElement(op);
						descSents.put(op, ops.get(op));
					}
					List<SemanticGraph> lst = descSents.get(operationModel.getSelectedItem().toString());
					StringBuffer aux = new StringBuffer();
					if(lst != null)
						for (SemanticGraph semanticGraph : lst) {
							aux.append("# sentence = " + semanticGraph.toRecoveredSentenceString() + "\n");
							aux.append(semanticGraph.toString(CoreLabel.OutputFormat.CoNLL));
							aux.append("\n########################################################\n");
						}
					txtResults.setText(aux.toString());
				}
			}
		});
		panel_2.add(cmbSentences);


		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));

		JPanel panel_4 = new JPanel(new FlowLayout());
		panel_4.add(new JLabel("Operation: "));
		operationModel = new DefaultComboBoxModel<String>();
		JComboBox<String> cmbOperations = new JComboBox<String>(operationModel);
		panel_4.add(cmbOperations);
		cmbOperations.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Object item = operationModel.getSelectedItem();

				if (item != null) {
					List<SemanticGraph> lst = descSents.get(item.toString());
					StringBuffer aux = new StringBuffer();
					if(lst != null)
					for (SemanticGraph semanticGraph : lst) {
						aux.append("# sentence = " + semanticGraph.toRecoveredSentenceString() + "\n");
						aux.append(semanticGraph.toString(CoreLabel.OutputFormat.CoNLL));
						aux.append("\n########################################################\n");
					}
					txtResults.setText(aux.toString());
				}

			}
		});
		panel_3.add(panel_4, BorderLayout.NORTH);	

		txtResults = new JTextArea();
		panel_3.add(txtResults, BorderLayout.CENTER);


		descCorpus = new HashMap<>();
		descSents = new HashMap<>();
		sentModel.removeAllElements();
		operationModel.removeAllElements();
		
		
		Object selectedItem = new ArrayList<String>(allCorporaProcessed.keySet()).get(0);
		for (Pair<String, Map<String, List<SemanticGraph>>> vals : allCorporaProcessed.get(selectedItem.toString())) {
			descCorpus.put(vals.first(), vals.second());
			sentModel.addElement(vals.first());
		}
		Map<String, List<SemanticGraph>> ops = descCorpus.get(cmbSentences.getItemAt(0).toString());
		operationModel.removeAllElements();
		descSents = new Hashtable<String, List<SemanticGraph>>();
		for (String op : ops.keySet()) {
			operationModel.addElement(op);
			descSents.put(op, ops.get(op));
		}
		List<SemanticGraph> lst = descSents.get(operationModel.getElementAt(0).toString());
		StringBuffer aux = new StringBuffer();
		if(lst != null)
			for (SemanticGraph semanticGraph : lst) {
				aux.append("# sentence = " + semanticGraph.toRecoveredSentenceString() + "\n");
				aux.append(semanticGraph.toString(CoreLabel.OutputFormat.CoNLL));
				aux.append("\n########################################################\n");
			}
		txtResults.setText(aux.toString());
		
		pack();
		setVisible(true);
	}

}
