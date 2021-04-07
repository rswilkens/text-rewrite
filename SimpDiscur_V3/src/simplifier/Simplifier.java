package simplifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import corpus_reader.Corpus;
import corpus_reader.Sentence;
import corpus_reader.Word;
import discur_rules.D3;
import discur_rules.D4;
import discur_rules.D5;
import discur_rules.D1;
import discur_rules.D2;
import discur_rules.DiscourseSimp;
import preprocessing.ExpressionIdentifier;
import synt_rules.Syn2deleteInfo;
import synt_rules.Syn3shortSentences;
import synt_rules.SynSimpUsingExternalFiles;
import synt_rules.SyntacticSimp;

public class Simplifier {

	public static String currentCorpus = null;
	public static void main(String[] args) {

		String[] resources = {//"resources/test" 
//								"resources/dela-fr-public_mwe.txt", 
//								"resources/wikitionary_words_mwe",
////								"resources/simpleAprenent.csv", 
//////				"resources/expressio_vocabulary.txt" 
		};
		String[] parsed = {
				"testGSD/clivtest1.conll",
				"testGSD/clivtestqui.conll",
				"testGSD/dontbasetest.conll",
//				"amalia_22Feb/texte2.conll",
//				"amalia_22Feb/texte3.conll",
//				"amalia_22Feb/texte4.conll",
//				"amalia_22Feb/texte5.conll"
		};
//		String[] parsed = { //corpus Alector
//				"corpusAlector/07_CE1_litt_orig_mousquetaires.conll",
//				"corpusAlector/08_CE1_litt_orig_warda.conll",
//				"corpusAlector/09_CE1_litt_orig_nicolas.conll",
//				"corpusAlector/10_CE1_litt_orig_samourai.conll",
//				"corpusAlector/142_CM1_litt_orig_oeilduloup.conll",
//				"corpusAlector/143_CM1_litt_orig_dragonneminuit.conll",
//				"corpusAlector/144_CM1_litt_orig_crinblanc.conll",
//				"corpusAlector/145_CM1_litt_orig_sources.conll",
//				"corpusAlector/152_CM1_lit_Oeilduloup_SIMP.conll",
//				"corpusAlector/153_CM1_lit_Dragonneminuit_SIMP.conll",
//				"corpusAlector/154_CM1_lit_CrinBlanc_SIMP.conll",
//				"corpusAlector/155_CM1_lit_Sources_SIMP.conll",
//				"corpusAlector/156_CM1_sci_orig_ecureuil.conll",
//				"corpusAlector/157_CM1_sci_orig_arbres.conll",
//				"corpusAlector/158_CM1_sci_orig_chocolat.conll",
//				"corpusAlector/159_CM1_sci_orig_perles.conll",
//				"corpusAlector/166_CM1_sci_Ecureuil_SIMP.conll",
//				"corpusAlector/167_CM1_sci_Arbres_SIMP.conll",
//				"corpusAlector/168_CM1_sci_Chocolat_SIMP.conll",
//				"corpusAlector/169_CM1_sci_Perles_SIMP.conll",
//				"corpusAlector/17_CE1_litt_mousquetaires_SIMP.conll",
//				"corpusAlector/18_CE1_litt_warda_SIMP.conll",
//				"corpusAlector/19_CE1_litt_nicolas_SIMP.conll",
//				"corpusAlector/20_CE1_litt_samourai_SIMP.conll",
//				"corpusAlector/21_CE1_sci_orig_grotte.conll",
//				"corpusAlector/22_CE1_sci_orig_nuit.conll",
//				"corpusAlector/23_CE1_sci_orig_herisson.conll",
//				"corpusAlector/24_CE1_sci_orig_moulin.conll",
//				"corpusAlector/31_CE1_sci_grotte_SIMP.conll",
//				"corpusAlector/32_CE1_sci_nuit_SIMP.conll",
//				"corpusAlector/33_CE1_sci_herisson_SIMP.conll",
//				"corpusAlector/34_CE1_sci_moulin_SIMP.conll"
//		};

		//		String parsedcorpus = "corpus/cendrillon_1.conll";
//		String corefcorpus = "corpusAlector/corpusAlector.coref"; //ge:cendrillon_1.conll_000
		String corefcorpus = "testGSD/testGSD.coref"; 

		ExpressionIdentifier ei = new ExpressionIdentifier(resources);

		for (String parsedcorpus : parsed) {
			Simplifier s = new Simplifier();
			System.out.println("***************************************************");
			System.out.println("*****   PROCESSING   " + parsedcorpus);
			System.out.println("***************************************************");
			currentCorpus = parsedcorpus;
			Corpus corpus = s.simplify(parsedcorpus, corefcorpus, ei);
			s.saveCorpus(parsedcorpus + ".output.conll",corpus);
			s.saveCorpusSent(parsedcorpus + ".output.txt",corpus);
		}

		//		System.out.println("-----------------------------------------");
		//		System.out.println(corpus);
		//		for (Sentence s : corpus.getSentences()) {
		//			for (Word w : s) 
		////				System.out.print(w.getSurface() + " ");
		//				System.out.print(w + "\n");
		//			System.out.println();
		//		}
	}

	private Corpus simplify(String parsedcorpus, String corefcorpus, ExpressionIdentifier ei) {
		Corpus corpus = preprocessing(parsedcorpus, corefcorpus, ei);

		corpus = simpSyntactic(corpus);

		corpus = simpDiscurs(corpus);
////				System.out.println(corpus);

		return corpus;
	}

	//	private Corpus preprocessing(String parsedcorpus, String corefcorpus, String[] dictionariesExpressions) {
	//		//join parser and coref annotation in one file
	//		
	//		return preprocessing(parsedcorpus, corefcorpus, dictionariesExpressions);
	//	}
	public Corpus preprocessing(String parsedcorpus, String corefcorpus, ExpressionIdentifier ei) {
		Corpus corpus = null;
		if (corefcorpus != null){
			//join parser and coref annotation in one file
			corpus = new Corpus(parsedcorpus, corefcorpus);
			saveCorpus(parsedcorpus.replace(".conll", "") + ".coref.conll", corpus);
			//annotate expressions
			corpus = new Corpus(parsedcorpus.replace(".conll", "") + ".coref.conll");
			ei.blockingAnnotate(corpus);			
		}else {
			corpus = new Corpus(parsedcorpus);
			ei.blockingAnnotate(corpus);	
		}
		
		return corpus;
	}

	private Corpus simpSyntactic(Corpus corpus) {
		//		System.out.println(corpus);
		SyntacticSimp rule = null;

		rule = new Syn2deleteInfo();
		corpus = rule.simplify(corpus);

		//		rule = new Syn3shortSentences();
		//		corpus = rule.simplify(corpus);

		rule = new SynSimpUsingExternalFiles(
				new File("rule_pipelines/relclause_dont_commas"), //relclauseDontcommas
				new File("rule_pipelines/participe_new3"),
				new File("rule_pipelines/pipeline_passive"), // passive
				new File("rule_pipelines/relativeclause_dont4"), // relativeclauseDONT
				new File("rule_pipelines/relativeclause_ou2"),  // relativeclause
				new File("rule_pipelines/relclause_dont_coprel"), // relativeclauseCOPinside
				new File("rule_pipelines/test_relclause_OU2"), //relativeclause #test_relclause_ou2 CHECK 
				new File("rule_pipelines/conjunction1_19082020"), // conjMAIS
				new File("rule_pipelines/cliv22"),// cleaved
				new File("rule_pipelines/relativeclausequi_30092020")// relativeclausequi_19082020 CHECK 
				);
		corpus = rule.simplify(corpus);
		return corpus;
	}

	private Corpus simpDiscurs(Corpus corpus) {
		//		Corpus corpus;
		//		corpus = new Corpus(parsedcorpus.replace(".conll", "") + ".coref.conll");

		//		System.out.println(corpus);
		//		saveCorpus("aux.conll" + "D0.coref.conll", corpus);
		DiscourseSimp rule = null;
//		try {
//			rule = new D4();
//			Corpus aux = rule.simplify(corpus);
//			corpus = aux;
//			//		System.out.println(corpus);
//			//			saveCorpus("aux.conll" + "D4.coref.conll", corpus);
//		} catch (Exception e) {}

		try {
			rule = new D2();
			corpus = rule.simplify(corpus);
			//					saveCorpus("aux.conll" + "D2.coref.conll", corpus);
			//		System.out.println(corpus);
		} catch (Exception e) {}
		try {
			rule = new D5();
			corpus = rule.simplify(corpus);
			//					saveCorpus("aux.conll" + "D5.coref.conll", corpus);
		} catch (Exception e) {}
		try {
			rule = new D3();
			corpus = rule.simplify(corpus);
			//					saveCorpus("aux.conll" + "D3.coref.conll", corpus);
		} catch (Exception e) {}
		try {
			rule = new D1();
			corpus = rule.simplify(corpus);
			//			saveCorpus("aux.conll" + "D1.coref.conll", corpus);
			//					System.out.println(corpus);
		} catch (Exception e) {}


		return corpus;
	}

	private void saveCorpus(String outputNeme, Corpus corpus) {
		try(BufferedWriter out = new BufferedWriter(new FileWriter(outputNeme))){
			out.write(corpus.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private void saveCorpusSent(String outputNeme, Corpus corpus) {
		try(BufferedWriter out = new BufferedWriter(new FileWriter(outputNeme))){
			for (Sentence sent : corpus.getSentences()) {
				for (Word word : sent) 
					out.write(word.getSurface()+" ");					
				out.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
