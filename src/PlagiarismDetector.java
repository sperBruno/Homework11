

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * SD2x Homework #11
 * Improve the efficiency of the code below according to the guidelines in the assignment description.
 * Please be sure not to change the signature of the detectPlagiarism method!
 * However, you may modify the signatures of any of the other methods as needed.
 */

public class PlagiarismDetector {

	private static Scanner in;

	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {
		int startDetectPlagiarism = (int) System.currentTimeMillis();
		
		File dirFile = new File(dirName);
		String[] files = dirFile.list();
		
		Map<String, Integer> numberOfMatches = new HashMap<String, Integer>();
		
		for (int i = 0; i < files.length; i++) {
			String file1 = files[i];

			for (int j = 0; j < files.length; j++) { 
				String file2 = files[j];
				
				Set<String> file1Phrases = createPhrases(dirName + "/" + file1, windowSize); 
				Set<String> file2Phrases = createPhrases(dirName + "/" + file2, windowSize); 
				
				if (file1Phrases == null || file2Phrases == null)
					return null;
				
				Set<String> matches = findMatches(file1Phrases, file2Phrases);
				
				if (matches == null)
					return null;
								
				if (matches.size() > threshold) {
					String key = file1 + "-" + file2;
					if (numberOfMatches.containsKey(file2 + "-" + file1) == false && file1.equals(file2) == false) {
						numberOfMatches.put(key,matches.size());
					}
				}				
			}
			
		}		
		int endDetectPlagiarism = (int) System.currentTimeMillis();
		System.out.println("Time detect Plugir " + (endDetectPlagiarism - startDetectPlagiarism));
		return sortResults(numberOfMatches);

	}

	
	/*
	 * This method reads the given file and then converts it into a Collection of Strings.
	 * It does not include punctuation and converts all words in the file to uppercase.
	 */
	protected synchronized static List<String> readFile(String filename) {
//		int start = (int) System.currentTimeMillis();
		if (filename == null || filename.length() == 0) return null;
		
		List<String> words = new ArrayList<String>();
		
		try {
			in = new Scanner(new File(filename));
			while (in.hasNext()) {
				words.add(in.next().replaceAll("[^a-zA-Z]", "").toUpperCase());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
//		int end = (int) System.currentTimeMillis();
//		System.out.println("time read File" + (end - start));
		return words;
	}

	
	/*
	 * This method reads a file and converts it into a Set/List of distinct phrases,
	 * each of size "window". The Strings in each phrase are whitespace-separated.
	 */
	protected synchronized static Set<String> createPhrases(String filename, int window) {

		if (filename == null || window < 1) return null;
//		synchronized (lock1) {
			
		
		List<String> words = readFile(filename);
		
		Set<String> phrases = new HashSet<String>();
		
		for (int i = 0; i < words.size() - window + 1; i++) {
			String phrase = "";
			for (int j = 0; j < window; j++) {
				phrase += words.get(i+j) + " ";
			}

			phrases.add(phrase);

		}

//		}
		return phrases;		
	}

	

	
	/*
	 * Returns a Set of Strings that occur in both of the Set parameters.
	 * However, the comparison is case-insensitive.
	 */
	protected static Set<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {
//		int startFindMatches = (int) System.currentTimeMillis();
//		int endFindMatches = 0;
		Set<String> matches = new HashSet<String>();
		
		
			if (myPhrases != null && yourPhrases != null) {
//			myPhrases.stream().filter(x -> yourPhrases.parallelStream().anyMatch(s -> s.equalsIgnoreCase(x))).forEach(x-> matches.add(x));
			matches = myPhrases.stream().filter(x -> yourPhrases.contains(x)).collect(Collectors.toSet());
//				for (String mine : myPhrases) {
//					for (String yours : yourPhrases) {
//						if (mine.equalsIgnoreCase(yours)) {
//							matches.add(mine);
//						}
//					}
//				}
			}
		
		System.out.println("Find matches size"+ matches.size());
		return matches;
	}
	
	/*
	 * Returns a LinkedHashMap in which the elements of the Map parameter
	 * are sorted according to the value of the Integer, in non-ascending order.
	 */
	protected static LinkedHashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {
//		int startSortResutl = (int)System.currentTimeMillis();
//		int endSortResult = 0;
		// Because this approach modifies the Map as a side effect of printing 
		// the results, it is necessary to make a copy of the original Map
		Map<String, Integer> copy = new HashMap<String, Integer>(possibleMatches);

//		for (String key : possibleMatches.keySet()) {
//			copy.put(key, possibleMatches.get(key));
//		}	
//		LinkedHashMap<String, Integer> list = new LinkedHashMap<>();
		LinkedHashMap<String, Integer> list = possibleMatches.entrySet().stream()
		    	.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		    	.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
		    	(oldValue, newValue) -> oldValue, LinkedHashMap::new));;
//		for (int i = 0; i < copy.size(); i++) {
//			int maxValue = 0;
//			String maxKey = null;
//			for (String key : copy.keySet()) {
//				if (copy.get(key) > maxValue) {
//					maxValue = copy.get(key);
//					maxKey = key;
//				}
//			}
//			
//			list.put(maxKey, maxValue);
//			
//			copy.put(maxKey, -1);
//		}
//		endSortResult = (int) System.currentTimeMillis();
//		System.out.println("time Sort result "+ (endSortResult - startSortResutl));
		return list;
	}
	
	/*
	 * This method is here to help you measure the execution time and get the output of the program.
	 * You do not need to consider it for improving the efficiency of the detectPlagiarism method.
	 */
    public static void main(String[] args) {
    	//if ("/Homework11/corpus" == 0) {
    		//System.out.println("Please specify the name of the directory containing the corpus.");
    		//System.exit(0);
    	//}
    	String directory = "corpus";
    	long start = System.currentTimeMillis();
    	Map<String, Integer> map = PlagiarismDetector.detectPlagiarism(directory, 4, 5);
    	long end = System.currentTimeMillis();
    	double timeInSeconds = (end - start) / (double)1000;
    	System.out.println("Execution time (wall clock): " + timeInSeconds + " seconds");
    	map.entrySet().stream().forEach(System.out::println);
//    	Set<Map.Entry<String, Integer>> entries = map.entrySet();
//    	for (Map.Entry<String, Integer> entry : entries) {
//    		System.out.println(entry.getKey() + ": " + entry.getValue());
//    	}
    	
    	
//    	Set<String> s1 = new HashSet<>();
//    	Set<String> s2 = new HashSet<>();
//    	Set<String> s3 = new HashSet<>();
//    	s1.add("bruno");
//    	s1.add("Bruno");
//    	s1.add("Luis");
//    	s1.add("luis");
//    	
//    	s2.add("bruno");
//    	s2.add("luis");
//    	
//    	s3 = s1.stream().filter(x -> s2.contains(x)).collect(Collectors.toSet());
//    	s3.stream().forEach(System.out::println);
    	
    	
//    	Map<String, Integer> t1 = new LinkedHashMap<>();
//    	LinkedHashMap<String, Integer> t2 = new LinkedHashMap<>();
//    	t1.put("s", 1);
//    	t1.put("ssd", 10);
//    	t1.put("se", 5);
//    	t1.put("s1", 100);
////    	t1.entrySet().stream()
////        .sorted(Entry.comparingByValue()).forEach(x -> t2.put(x.getKey(), x.getValue()));
////    	
//    	LinkedHashMap<String, Integer> result = t1.entrySet().stream()
//    	.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//    	.collect(Collectors.Map(Map.Entry::getKey, Map.Entry::getValue,
//    	(oldValue, newValue) -> oldValue, LinkedHashMap::new));
    	
//    	result.entrySet().stream().forEach(System.out::println);
//        
    }

}
