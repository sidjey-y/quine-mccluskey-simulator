import java.util.*;
public class Term { // String represention of terms in binary – includes zeroes, ones, and dashes
    private String term;

    private int ones;

    private ArrayList<Integer> nums;

    public Term (int value, int length){
        String binary = Integer.toBinaryString(value); //convert minterm to binary

        // store
        StringBuffer temp = new StringBuffer(binary);
        while (temp.length() != length){
            temp.insert(0, 0);
        }
        this.term = temp.toString();

        nums = new ArrayList<Integer>();
        nums.add(value);

        ones = 0;
        for (int i = 0; i < term.length(); i++){
            if(term.charAt(i) == '1')
                ones++;
        }
    }

    public Term (Term term1, Term term2){
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < term1.getString().length(); i++){
            if (term1.getString().charAt(i) != term2.getString().charAt(i))
                temp.append("-");
            else
                temp.append(term1.getString().charAt(i));
        }
        this.term = temp.toString();

        ones = 0; // count new number of ones
        for (int i = 0; i < term.length(); i++){
            if (this.term.charAt(i) == '1')
                ones++;
        }

        nums = new ArrayList<Integer>();
        for (int i = 0; i < term1.getNums().size(); i++){
            nums.add(term1.getNums().get(i));
        }
        for (int i = 0; i < term2.getNums().size(); i++){
            nums.add(term2.getNums().get(i));
        }
    }

    String getString() {
        return term;
    }

    ArrayList<Integer> getNums(){
        return nums;
    }

    int getNumOnes(){
        return ones;
    }

}
