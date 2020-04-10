package simplifier;

import java.io.BufferedWriter;
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
import synt_rules.SyntacticSimp;

public class Simplifier {


	public static void main(String[] args) {

		String[] resources = {//"resources/test" 
				"resources/dela-fr-public_mwe.txt", "resources/wikitionary_words_mwe",
				"resources/simpleAprenent.csv", "resources/expressio_vocabulary.txt" };

		String[] parsed = {//"corpus/cendrillon_1.conll",// "corpus/cendrillon_2.conll", 
				"corpus/LaSoupeDeLaDiscorde_1.conll", //"corpus/LaSoupeDeLaDiscorde_2.conll",
				"corpus/LeCheneEtLeRoseau_1.conll",//"corpus/LeCheneEtLeRoseau_2.conll",
				"corpus/LesTroisSots_1.conll",//"corpus/LesTroisSots_2.conll",
				"corpus/pierrelapin_1.conll", //"corpus/pierrelapin_2.conll"
				};

		//		String parsedcorpus = "corpus/cendrillon_1.conll";
		String corefcorpus = "corpus/corpus_coref_pred_selected.conll"; //ge:cendrillon_1.conll_000

		ExpressionIdentifier ei = new ExpressionIdentifier(resources);
		
		for (String parsedcorpus : parsed) {
			Simplifier s = new Simplifier();
			System.out.println("***************************************************");
			System.out.println("*****   PROCESSING   " + parsedcorpus);
			System.out.println("***************************************************");
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
		//		System.out.println(corpus);

		return corpus;
	}

//	private Corpus preprocessing(String parsedcorpus, String corefcorpus, String[] dictionariesExpressions) {
//		//join parser and coref annotation in one file
//		
//		return preprocessing(parsedcorpus, corefcorpus, dictionariesExpressions);
//	}
	public Corpus preprocessing(String parsedcorpus, String corefcorpus, ExpressionIdentifier ei) {
		//join parser and coref annotation in one file
		Corpus corpus = new Corpus(parsedcorpus, corefcorpus);
		saveCorpus(parsedcorpus.replace(".conll", "") + ".coref.conll", corpus);
//		saveCorpus(parsedcorpus + ".outputAUX",corpus);
		//annotate expressions
		corpus = new Corpus(parsedcorpus.replace(".conll", "") + ".coref.conll");

		ei.blockingAnnotate(corpus);
//		saveCorpus("aux.conll" + "_preprocessingOutput.coref.conll", corpus);
//		System.out.println();
		return corpus;
	}

	private Corpus simpSyntactic(Corpus corpus) {
		//		System.out.println(corpus);
		SyntacticSimp rule = null;

		rule = new Syn2deleteInfo();
		corpus = rule.simplify(corpus);

		rule = new Syn3shortSentences();
		corpus = rule.simplify(corpus);
		return corpus;
	}

	private Corpus simpDiscurs(Corpus corpus) {
		//		Corpus corpus;
		//		corpus = new Corpus(parsedcorpus.replace(".conll", "") + ".coref.conll");

		//		System.out.println(corpus);
//		saveCorpus("aux.conll" + "D0.coref.conll", corpus);
		DiscourseSimp rule = null;
		rule = new D4();
		corpus = rule.simplify(corpus);
		//		System.out.println(corpus);
//		saveCorpus("aux.conll" + "D4.coref.conll", corpus);

		rule = new D2();
		corpus = rule.simplify(corpus);
//				saveCorpus("aux.conll" + "D2.coref.conll", corpus);
		//		System.out.println(corpus);

		rule = new D5();
		corpus = rule.simplify(corpus);
//				saveCorpus("aux.conll" + "D5.coref.conll", corpus);

		rule = new D3();
		corpus = rule.simplify(corpus);
//				saveCorpus("aux.conll" + "D3.coref.conll", corpus);

		rule = new D1();
		corpus = rule.simplify(corpus);
//		saveCorpus("aux.conll" + "D1.coref.conll", corpus);
//				System.out.println(corpus);

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
