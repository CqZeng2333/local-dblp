package revertedIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import connect_database.Selector;

public class Index {
	private class DocInfo {
		int docID;     //document ID
		int tf;        //term frequency
		double tf_idf;
		
		DocInfo(int id) {
			docID = id;
			tf = 1;
		}
	}
	
	int N = 0; //document quantity
	TreeMap<String, ArrayList<DocInfo>> index = new TreeMap<>(); //entire index for all documents
	TreeMap<String, Double> idf = new TreeMap<>();          //idf for each term
	TreeMap<Integer, Double> Elength = new TreeMap<>();     //Euclidean length for each document
	TreeMap<Integer, Integer> d_length = new TreeMap<>();    //document length for each document
	double ave_length; //average length for all document
	
	/*
	 * Tokensize a string
	 * para: str - the string to be tokenized
	 * return: an array of tokens
	 */
	private String[] tokenize(String str) {
		String[] strr = str.split("[\\s\\.,:;\\\"\\'\\|/~!@#$%^&*()+={}]+");
		return strr;
	}
	
	/*
	 * Get the set of stop words
	 * return: a set of stop words
	 */
	private HashSet<String> getStopWords() {
		HashSet<String> stopWords = new HashSet<>();
		try {
		BufferedReader reader = new BufferedReader(new FileReader(".\\data\\common-english-words.txt"));
		String str = reader.readLine();
		String strr[] = str.split(",");
		for (int i = 0; i < strr.length; i++) {
			stopWords.add(strr[i]);
		}
		reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return stopWords;
	}
	/*
	 * Add data to the map
	 * para: article - the document to be tokenized and added
	 * 		 id - the document id
	 */
	public void add(String article, int id) {
		HashSet<String> stopWords = this.getStopWords();
		Stemmer stemmer;
		
		int length = 0;
		String tokens[] = this.tokenize(article);
		for (int i = 0; i < tokens.length; i++) {
			String s = tokens[i].toLowerCase();
			//skip stop words
			if (stopWords.contains(tokens[i]))
				continue;
			
			//stem the s
			stemmer = new Stemmer();
			stemmer.add(s.toCharArray(), s.length());
			stemmer.stem();
			s = stemmer.toString();
			//term not in the index
			if (!index.containsKey(s)) {
				ArrayList<DocInfo> l = new ArrayList<>();
				l.add(new DocInfo(id));
				index.put(s, l);
			}
			//this term not contains this document record
			else if (getPos(index.get(s), id) == -1) {
				index.get(s).add(new DocInfo(id));
			}
			//term frequancy++ for this document record
			else {
				int pos = getPos(index.get(s), id);
				index.get(s).get(pos).tf++;
			}
			//count document length
			length++;
		}
		//record the quantity of all documents 
		//and the length of this document
		this.N++;
		this.d_length.put(id, length);
	}
	
	/*
	 * Return the index of an DocInfo 
	 * in an ArrayList by document id
	 * para: l - the ArrayList to search
	 * 		 id - the document id 
	 * return: index of this document id
	 * 		   or -1 if no this id
	 */
	private int getPos(ArrayList<DocInfo> l, int id) {
		for (DocInfo doc : l) {
			if (doc.docID == id)
				return l.indexOf(doc);
		}
		return -1;
	}
	
	/*
	 * Count idf for each term
	 */
	private void countIdf() {
		Iterator<String> iter = index.keySet().iterator();
		String term;
		double df;
		double idf;
		while (iter.hasNext()) {
			term = iter.next();
			df = index.get(term).size();
			idf = Math.log10(this.N / df);
			this.idf.put(term, idf);
		}
	}
	
	/*
	 * Count tf-idf for each document of each term
	 */
	private void countTF_IDF() {
		Iterator<String> iter = idf.keySet().iterator();
		String term;
		double idf;
		while (iter.hasNext()) {
			term = iter.next();
			idf = this.idf.get(term);
			ArrayList<DocInfo> l = index.get(term);
			for (DocInfo doc : l) {
				doc.tf_idf = (1 + Math.log10(doc.tf)) * idf;
			}
		}
	}
	
	/*
	 * Count Elength for each document
	 */
	private void countElength() {
		//储存各文件向量的分量的平方和
		ArrayList<DocInfo> docs;
		DocInfo doc;
		double tf_idf;
		Iterator<ArrayList<DocInfo>> iter = index.values().iterator();
		//iterate the index for all tf_idf
		while (iter.hasNext()) {
			docs = iter.next();
			for (int i = 0; i < docs.size(); i++) {
				doc = docs.get(i);
				tf_idf = doc.tf_idf;
				if (Elength.containsKey(doc.docID)) {
					Elength.replace(doc.docID, Elength.get(doc.docID) + tf_idf * tf_idf);
				}
				else {
					Elength.put(doc.docID, tf_idf * tf_idf);
				}
			}
		}
		//将TreeMap中的数值开平方
		Iterator<Integer> iter2 = Elength.keySet().iterator();
		int id;
		double square;
		while (iter2.hasNext()) {
			id = iter2.next();
			square = Elength.get(id);
			Elength.replace(id, Math.sqrt(square));
		}
	}
	
	/*
	 * Count average length of
	 * all the documents
	 */
	private void countAve_length() {
		Iterator<Integer> iter = this.d_length.keySet().iterator();
		int doc;
		double sum = 0;
		while (iter.hasNext()) {
			doc = iter.next();
			sum += this.d_length.get(doc);
		}
		this.ave_length = sum / this.N;
	}
	
	/*
	 * Combine all the count-method
	 */
	public void countAll() {
		this.countIdf();
		this.countTF_IDF();
		this.countElength();
		this.countAve_length();
	}
	
	/*
	 * Make an index for the test article
	 * para: query - the query to get an index
	 * return: an index of the query
	 */
	public TreeMap<String, Double> getQuery(String query) {
		//key as term
		//value first as term frequency and then as counted weight according to term frequency
		TreeMap<String, Double> result = new TreeMap<>();
		HashSet<String> stopWords = this.getStopWords();
		Stemmer stemmer;
		
		String str[] = this.tokenize(query);
		for (int i = 0; i < str.length; i++) {
			String s = str[i].toLowerCase();
			//skip stop words
			if (stopWords.contains(str[i]))
				continue;
			
			//stem the s
			stemmer = new Stemmer();
			stemmer.add(s.toCharArray(), s.length());
			stemmer.stem();
			s = stemmer.toString();
			//term not in the index
			if (!result.containsKey(s)) {
				result.put(s, 1d);
			}
			//term already in the index, then term frequency plus one
			else {
				result.put(s, result.get(s) + 1);
			}
		}
		
		double tf;
		String term;
		Iterator<String> iter = result.keySet().iterator();
		//iterate the index to count weight
		while (iter.hasNext()) {
			term = iter.next();
			tf = result.get(term);
			result.put(term, (1 + Math.log10(tf)));
		}
		return result;
	}
	
	/*
	 * Get top k matched documents
	 * para: k - the number of documents to get
	 * 		 toMatch - the TreeMap got by method "getQuery"
	 * return: the top-k-matched documents and corresponding scores
	 */
	public TreeMap<Double, Integer> getTopK(int k, TreeMap<String, Double> toMatch) {
		//null String to search
		if (toMatch.containsKey("") && toMatch.size() == 1) {
			TreeMap<Double, Integer> topK = new TreeMap<>(new Comparator<Double>() {
				@Override
				public int compare(Double o1, Double o2) {
					return o2.compareTo(o1);
				}});
			int count = 10;
			Iterator<Integer> iterr = this.Elength.keySet().iterator();
			while (iterr.hasNext() && count-- > 0) {
				topK.put((double) count, iterr.next());
			}
			return topK;
		}
		
		
		TreeMap<Integer, Double> scores = new TreeMap<>();  //score for each document
		TreeMap<Double, Integer> maxScores = new TreeMap<>(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o2.compareTo(o1);
			}});                               //sorter to find max scores
		
		//below is to count all the scores
		String term;
		ArrayList<DocInfo> l;
		double product;
		Iterator<String> iter = toMatch.keySet().iterator();
		//iterate the "toMatch" index to count inner product
		while (iter.hasNext()) {
			term = iter.next();
			if (index.get(term) != null) {
				l = index.get(term);
				for (DocInfo doc : l) {
					product = doc.tf_idf * toMatch.get(term);
					if (scores.containsKey(doc.docID)) {
						scores.replace(doc.docID, scores.get(doc.docID) + product);
					}
					else {
						scores.put(doc.docID, product);
					}
				}
			}
		}
		
		//count the Euclidean length of "toMatch" index
		double sum = 0;
		double tf;
		iter = toMatch.keySet().iterator();
		while (iter.hasNext()) {
			term = iter.next();
			tf = toMatch.get(term);
			sum += tf * tf;
		}
		sum = Math.sqrt(sum);
		
		//count the cosine similarity for each document
		Iterator<Integer> iter2 = scores.keySet().iterator();
		int id;
		double score;
		while (iter2.hasNext()) {
			id = iter2.next();
			score = scores.get(id);
			scores.replace(id, scores.get(id) / (Elength.get(id) * sum));
		}
		
		//time punishment
		TreeMap<Integer, Integer> yearGap = Selector.getYearGap(scores.keySet());
		iter2 = scores.keySet().iterator();
		double a = 0.2; //time attenuation parameter
		while (iter2.hasNext()) {
			id = iter2.next();
			score = scores.get(id);
			score *= (1 / (1 + a * yearGap.get(id)));
			scores.replace(id, score);
		}
		
		//below is to sort and find out the max k scores
		iter2 = scores.keySet().iterator();
		while (iter2.hasNext()) {
			id = iter2.next();
			score = scores.get(id);
			maxScores.put(score, id);
		}
		//put k max scores into TreeMap sorted decending
		TreeMap<Double, Integer> result = new TreeMap<>(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o2.compareTo(o1);
			}});
		Iterator<Double> iter3 = maxScores.keySet().iterator();
		double temp1;
		int temp2;
		int count = 0;
		while (iter3.hasNext()) {
			temp1 = iter3.next();
			temp2 = maxScores.get(temp1);
			if (Double.compare(temp1, 0) != 0 && !Double.isNaN(temp1)) {
				result.put(temp1, temp2);
				count++;
			}
			if (count >= 10)
				break;
		}
		return result;
	}
}
