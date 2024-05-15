/* Dylan Wang attests that this code is his original work and was written in compliance 
with the class Academic Integrity and Collaboration Policy found in the syllabus.
*/
import java.util.TreeSet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver
{
    private class Trie {
        private boolean isWord;
        private Trie[] children; 

        public Trie() {
            isWord = false;
            children = new Trie[26]; 
        }

        public void setWord(boolean w) {
            if (!isWord) {
                isWord = w;
            }
            
        }

        public Trie addChild(char branch) {
            if (children[branch - 'A'] == null) {
                children[branch - 'A'] = new Trie();
            }

            return children[branch - 'A'];
        }

        public Trie getChild(char branch) {
            return children[branch - 'A'];
        }
    }

    private final Trie root = new Trie();
    // private final TrieSET dic = new TrieSET();
    private final TreeSet<String> words = new TreeSet<String>();
    private BoggleBoard bigBoard;
    private boolean[][] checked; 
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String s : dictionary) {
            // put valid words into the dictionary
            char[] letters = s.toCharArray();
            Trie node = root;
            for (char c : letters) {
                node = node.addChild(c);
            }
            node.setWord(true);
        }
    }

    // this was the most difficult part since I had to use recursion to search all the surrounding tiels for possible words
    // Also had to make sure that once a word was checked this status would be wiped once it was returned
    // Also had to add an extra case jsut for Qu
    private void searchWord(Trie node, String sub, int i, int j) {
        checked[i][j] = true;
        char c = bigBoard.getLetter(i, j);
        node = node.getChild(c);
        if (node == null) {
            checked[i][j] = false;
            return;
        }

        sub += c;
        if (c == 'Q') {
            sub += 'U';
            node = node.getChild('U');
            if (node == null) {
                checked[i][j] = false;
                return;
            }
        }

        if (node.isWord && sub.length() >= 3) {
            words.add(sub);
        }  

        for (int k = i - 1; k <= i + 1; k++) {
            if (k >= 0 && k < bigBoard.rows()) {
                for (int m = j - 1; m <= j + 1; m++) {
                    if (m >= 0 && m < bigBoard.cols() && !checked[k][m]) {
                        searchWord(node, sub, k, m);
                    }
                }
            }
        }

        checked[i][j] = false;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words.clear();

        bigBoard = board;
        checked = new boolean[board.rows()][board.cols()]; 

        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                searchWord(root, "", i, j);
            }
        }

        return words;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {

        Trie node = root;
        char[] letters = word.toCharArray();
        for (char c : letters) {
            node = node.getChild(c);
            if (node == null) {
                return 0;
            }
        }

        if (!node.isWord || word.length() < 3) {
            return 0;
        }

        int len = word.length();
        int value = 0;
        if (len <= 4) {
            value = 1;
        }
        else if (len == 5) {
            value = 2;
        }
        else if (len == 6) {
            value = 3;
        }
        else if (len == 7) {
            value = 5;
        }
        else {
            value = 11;
        }

        return value;

    }

    public static void main(String[] args) {
        In in = new In("dictionary-common.txt");
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard("board-q.txt");
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);

        StdOut.println("Score = " + solver.scoreOf("HILLS"));
        StdOut.println("Score = " + solver.scoreOf("JO"));
        StdOut.println("Score = " + solver.scoreOf("FLATTENING"));
    }
}