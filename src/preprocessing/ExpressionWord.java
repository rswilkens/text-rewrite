package preprocessing;

import java.util.ArrayList;
import java.util.List;

import corpus_reader.Word;

public class ExpressionWord {

	private String word;
	private boolean hasNext;
	private List<ExpressionWord> next;
	private boolean mayFinish;

	//	public ExpressionWord(String words, boolean hasNext) {
	//		
	//	}

	public ExpressionWord(String[] words) {
		word = words[0];
		if (words.length==1) {
			mayFinish = true;
			hasNext = false;
		}else {
			mayFinish = false;
			hasNext = true;
			String[] aux = new String[words.length-1];
			for (int i = 1; i < words.length; i++) 
				aux[i-1] = words[i];
			next = new ArrayList<ExpressionWord>();
			next.add(new ExpressionWord(aux));
		}
	}

	public boolean is(String word) {
		return this.word.equals(word);
	}

	//	public boolean hasNext() {
	//		return hasNext;
	//	}

	public List<ExpressionWord> getNext() {
		return next;
	}

	public void add(String[] words) {
		ExpressionWord addedAux = null;
		if(next != null) {
			for (ExpressionWord n : next) 
				if(n.is(words[0])) { 
					addedAux = n;
					break;
				}
			//			String[] aux = new String[words.length-1];
			//			for (int i = 1; i < words.length; i++) 
			//				aux[i-1] = words[i];
			if(addedAux==null){
				//				if(aux.length==0)
				//					hasNext = false;
				//				else {
				hasNext = true;
				next.add(new ExpressionWord(words));
				//				}
			}else {
				//				String[] aux = new String[words.length-1];
				//				for (int i = 1; i < words.length; i++) 
				//					aux[i-1] = words[i];
				if(words.length>0)
					addedAux.add(words);
				else
					mayFinish = true;
			}
		}else {

			String[] aux = new String[words.length-1];
			for (int i = 1; i < words.length; i++) 
				aux[i-1] = words[i];
			if(aux.length>0) {
				next = new ArrayList<ExpressionWord>();
				next.add(new ExpressionWord(aux));
				hasNext = true;
				//				mayFinish=false;
			}else {
				hasNext = false;
				mayFinish= true;
			}

		}
		//		for (ExpressionWord n : next) {
		//			if(n.is(words[0])) {
		//				if(words.length==1) { // to avoid repeated expressions
		//					added = true;
		//					return;
		//				}
		//				String[] aux = new String[words.length-1];
		//				for (int i = 1; i < words.length; i++) 
		//					aux[i-1] = words[i];
		//				n.add(aux);
		//				added = true;
		//				return;
		//				
		//			}else {
		//				String[] aux = new String[words.length-1];
		//				for (int i = 1; i < words.length; i++) 
		//					aux[i-1] = words[i];
		//				next.add(new ExpressionWord(aux));
		//				return;
		//			}
		//		}
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append(word + " ");
		if(hasNext) {
			if(mayFinish) ret.append("\\");
			for (ExpressionWord e : next) {
				ret.append(e.toString("-"));
			}
		}else{
			ret.append("/");
		}
		return ret.toString();
	}

	private String toString(String prefix) {
		StringBuffer ret = new StringBuffer();
		ret.append(prefix + word + "\n");
		if(hasNext) {
			for (ExpressionWord e : next) {
				ret.append(e.toString(prefix+"-"));
			}
		}else{
			ret.append("/");
		}
		return ret.toString();
	}

	public String getWord() {
		return word;
	}

	public int contains(List<Word> subList) {
		if(word.equalsIgnoreCase(subList.get(0).getLemma())) {
			subList = subList.subList(1, subList.size());
			if(hasNext) {
				if(subList.size()>0) {
					for (ExpressionWord n : next) 
						if(n.getWord().equalsIgnoreCase(subList.get(0).getLemma())) {
							int contains = n.contains(subList);
							if(contains>=0)
								return 1 + contains;
						}
					if(mayFinish)return 1;
					else return -100000000;

				}
				if(mayFinish)return 1;
				else return -100000000;
			}else
				return 1;
		}
		return -10000000;
	}

}
