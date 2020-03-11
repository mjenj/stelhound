import java.util.ArrayList;

/**
 * This is a modified Booyer-Moore algorithm for partial string matching
 * taken from http://algs4.cs.princeton.edu/53substring/BoyerMoore.java.html
 * @author Sedgwick & Wayne
 *
 */
public class BoyerMoore {
	private final int R;     // the radix
    private int[] right;     // the bad-character skip array
    private String pat;      // or as a string

    /**
     * Preprocesses the pattern string.
     *
     * @param pat the pattern string
     */
    public BoyerMoore(String pat) {
        this.R = 256;
        this.pat = pat;

        // position of rightmost occurrence of c in the pattern
        right = new int[R];
        for (int c = 0; c < R; c++)
            right[c] = -1;
        for (int j = 0; j < pat.length(); j++)
            right[pat.charAt(j)] = j;
    }
    
    /**
     * <b>search</b><br/>
     * Searches through the text with the pattern string
     * @param txt
     * @return all match occurrences
     */
    public ArrayList<Integer> search(String txt) {
    	ArrayList <Integer> full = new ArrayList<Integer>();
        int m = pat.length();
        int n = txt.length();
        int skip;
        for (int i = 0; i <= n - m; i += skip) {
            skip = 0;
            for (int j = m-1; j >= 0; j--) {
                if (pat.charAt(j) != txt.charAt(i+j)) {
                    skip = Math.max(1, j - right[txt.charAt(i+j)]);
                    break;
                }
            }
            if (skip == 0) {
            	full.add(i);
            	skip = pat.length();
            }
            
        }

        return full;                       // not found
    }
}
