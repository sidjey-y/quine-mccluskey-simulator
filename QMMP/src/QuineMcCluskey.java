//INSTRUCTIONS//
/*
Deadline: November 14, 2023 
Use any preferred programming language 
Employ 10 variables 
Sample input: 1,4,6,7,8,9,10,11,15
Expected Output: x'y'z + w'xz' + xyz + wx'
Bonus points if user is allowed to implement their own variable names (i.e. instead of wxyz, they may use abcd) [Note: default is A-J]
Expected Deliverables:
     1:  Code 
     2: User Manual (Expected Audience: Tech Illiterate User) 
           Contents: Screenshots, UI, any necessary information for expected audience
     3: Technical Manual
          Point of comparison: JavaDoc 
           Technical and Detailed Explanation of code
    
 */

//TO DO//
/*
 > Simplified expression final
 > error handling:
 	- kapag may spaces yung input
 	- kapag sobra ininput
 	- pag walang ininput
 	> pag hindi comma ininput/walang comma
 > Consider dividing code into diff classes under same package (cleaner ver.)
 	- 
 */

//REFERENCES//
/*
> https://github.com/grejojoby/Quine-McCluskey-Algorithm-Java/blob/master/l.java
> MAIN REF (dl the link): https://courses.cs.washington.edu/courses/cse370/07au/Homeworks/Quine.html
> QM method in C Language: https://arxiv.org/ftp/arxiv/papers/1410/1410.1059.pdf
 */


import java.util.*;

public class QuineMcCluskey {

    private class OnesComparator implements Comparator<Term>{
        @Override
        public int compare (Term a, Term b) {
            return a.getNumOnes() - b.getNumOnes();
        }
    }
    private Term[] terms;

    private ArrayList<Integer> minterms;

    private ArrayList<Integer> dc;

    private int maxLength;

    private ArrayList<String>[] solution;

    private ArrayList<String> primeImplicants;

    private ArrayList<Term> finalTerms;

    public ArrayList<ArrayList<Term>[]> firstStep;

    public ArrayList<HashSet<String>> checkedFirstStep;

    public ArrayList<String[][]> secondStep;

    public ArrayList<String> thirdStep;

    public ArrayList<String> simplified;

    public QuineMcCluskey (String mintermsStr, String dontCaresStr) {
        int[] minterms = convertString(mintermsStr);
        int[] dontCares = convertString(dontCaresStr);
        if (!checkRepeats(minterms, dontCares))
            throw new RuntimeException("Invalid Input.");

        Arrays.sort(minterms);
        Arrays.sort(dontCares);

        // calculate max. length of prime implicants
        maxLength = Integer.toBinaryString(minterms[minterms.length - 1]).length();

        this.minterms = new ArrayList<>();
        this.dc = new ArrayList<>();

        primeImplicants = new ArrayList<String>();
        firstStep = new ArrayList<ArrayList<Term>[]>();
        checkedFirstStep = new ArrayList<HashSet<String>>();
        secondStep = new ArrayList<String[][]>();
        thirdStep = new ArrayList<String>();
        simplified = new ArrayList<String>();

        // combine minterms and dontcares in one array
        Term[] temp = new Term[minterms.length + dontCares.length];
        int k = 0; // index in temp array
        for (int i = 0; i < minterms.length; i++) {
            temp[k++] = new Term(minterms[i], maxLength);
            this.minterms.add(minterms[i]);
        }
        for (int i = 0; i < dontCares.length; i++) {
            // ignore dontcares with bits > max minterm
            if (Integer.toBinaryString(dontCares[i]).length() > maxLength) {
                break;
            }
            temp[k++] = new Term(dontCares[i], maxLength);
            this.dc.add(dontCares[i]);
        }
        terms = new Term[k];
        for (int i = 0; i < k; i++) {
            terms[i] = temp[i];
        }

        // sort terms according to num of ones
        Arrays.sort(terms, new OnesComparator());
    }
    private int[] convertString(String s) { // convert Strings to int arrays; checks if valid
        s = s.replace(",", " "); //for comma-delimited inputs

        if (s.trim().equals("")) { // if empty
            return new int[] {};
        }

        String[] a = s.trim().split(" +");
        int[] t = new int[a.length]; // array of minterms

        for (int i = 0; i < t.length; i++) {
            try { // until it reaches outside bounds
                int temp = Integer.parseInt(a[i]); //convert String input to int
                t[i] = temp;
            } catch (Exception e) {
                throw new RuntimeException("Invalid input. Please try again.");
            }
        }
        HashSet<Integer> dup = new HashSet<>(); //check for duplicates
        for (int i = 0; i < t.length; i++) {
            if (dup.contains(t[i])) {
                throw new RuntimeException("Duplicates encountered. Please try again.");
            }
            dup.add(t[i]);
        }

        return t;
    }

    private ArrayList<Term>[] group(Term[] terms) {
        ArrayList<Term>[] groups = new ArrayList[terms[terms.length - 1].getNumOnes() + 1];

        for (int i = 0; i < groups.length; i++) {
            groups[i] = new ArrayList<>();
        }
        for (int i = 0; i < terms.length; i++) {
            int k = terms[i].getNumOnes();
            groups[k].add(terms[i]);
        }
        return groups;
    }

    public void solve(){
        ArrayList<Term> unchecked = new ArrayList<>(); // keep track of unchecked terms

        ArrayList<Term>[] list = group(this.terms);
        ArrayList<Term>[] result;

        firstStep.add(list);

        boolean insert = true;

        do {
            HashSet<String> checked= new HashSet<>();

            result = new ArrayList[list.length - 1];

            ArrayList<String> temp;
            insert = false;

            for (int i = 0; i < list.length - 1; i++){
                result[i] = new ArrayList<>();
                temp = new ArrayList<>();

                for (int j = 0; j < list[i].size(); j++){
                    for (int k = 0; k < list[i + 1].size(); k++){
                        if (checkValidity(list[i].get(j), list[i + 1].get(k))) {
                            checked.add(list[i].get(j).getString());
                            checked.add(list[i+1].get(k).getString());

                            Term n = new Term(list[i].get(j), list[i+1].get(k));

                            if (!temp.contains(n.getString())) {
                                result[i].add(n);
                                insert = true;
                            }
                            temp.add(n.getString());

                        }
                    }
                }
            }

            if (insert) {
                for (int i = 0; i < list.length; i++) {
                    for (int j = 0; j < list[i].size(); j++) {
                        if (!checked.contains(list[i].get(j).getString())) {
                            unchecked.add(list[i].get(j));
                        }
                    }
                }
                list = result;

                firstStep.add(list);
                checkedFirstStep.add(checked);
            }
        } while (insert && list.length > 1);

        finalTerms = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[i].size(); j++) {
                finalTerms.add(list[i].get(j));
            }
        }
        for (int i = 0; i < unchecked.size(); i++) {
            finalTerms.add(unchecked.get(i));
        }

        solveSecond();
    }

    public void solveSecond(){
        addToTable();

        if (!identifyPrimeImplicants()) {
            if (!rowDominance()) {
                if (!columnDominance()) {
                    // if none succeeds go to petrick
                    simplify();
                    return;
                }
            }
        }
        // if there are still minterms to be taken call this function again
        if (minterms.size() != 0)
            solveSecond();
            // if all minterms taken, add to solution
        else {
            // for displaying steps
            addToTable();
            solution = new ArrayList[1];
            solution[0] = primeImplicants;
        }
    }

    boolean checkRepeats(int[] m, int[] d){
        HashSet<Integer> temp =new HashSet<>();
        for (int i = 0; i < m.length; i++){
            temp.add(m[i]);
        }
        for (int i = 0; i < d.length; i++){
            if (temp.contains(d[i]))
                return false;
        }
        return true;
    }

    boolean checkValidity (Term term1, Term term2) {
        if (term1.getString().length() != term2.getString().length())
            return false;

        int k = 0;
        for (int i = 0; i < term1.getString().length(); i++) {
            if (term1.getString().charAt(i) == '-' && term2.getString().charAt(i) != '-')
                return false;
            else if (term1.getString().charAt(i) != '-' && term2.getString().charAt(i) == '-')
                return false;
            else if (term1.getString().charAt(i) != term2.getString().charAt(i))
                k++;
            else
                continue;
        }

        if (k != 1) //if there is exactly one match
            return false;
        else
            return true;
    }

    boolean contains(Term term1, Term term2) {
        if (term1.getNums().size() <= term2.getNums().size()) {
            return false;
        }
        ArrayList<Integer> a = term1.getNums();
        ArrayList<Integer> b = term2.getNums();
        b.removeAll(dc);

        if (a.containsAll(b))
            return true;
        else
            return false;
    }


    void simplify(){
        HashSet<String>[] temp = new HashSet[minterms.size()];

        for (int i = 0; i < minterms.size(); i++) {
            temp[i] = new HashSet<>();
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    char t = (char) ('a' + j);
                    simplified.add(t + ": " + finalTerms.get(j).getString());
                    temp[i].add(t + "");
                }
            }
        }

        HashSet<String> finalResult = multiply(temp, 0);

        HashSet<String>[] step = new HashSet[1];
        step[0] = finalResult;
        stepSimplification(step, 0);
        thirdStep.add("\nMin:");

        int min = -1;
        int count = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String m = t.next();
            if (min == -1 || m.length() < min) {
                min = m.length();
                count = 1;
            } else if (min == m.length()) {
                count++;
            }
        }

        solution = new ArrayList[count];
        int k = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String c = t.next();
            if (c.length() == min) {
                solution[k] = new ArrayList<>();
                thirdStep.add(c);
                for (int i = 0; i < c.length(); i++) {
                    solution[k].add(finalTerms.get((int) c.charAt(i) - 'a').getString());
                }
                for (int i = 0; i < primeImplicants.size(); i++) {
                    solution[k].add(primeImplicants.get(i));
                }
                k++;
            }
        }
    }

    HashSet<String> multiply(HashSet<String>[] p, int k){
        if (k >= p.length - 1)
            return p[k];
        HashSet<String> s = new HashSet<>();
        for (Iterator<String> t = p[k].iterator(); t.hasNext();) {
            String temp2 = t.next();
            for (Iterator<String> g = p[k + 1].iterator(); g.hasNext();) {
                String temp3 = g.next();
                s.add(mix(temp2, temp3));
            }
        }
        p[k + 1] = s;
        return multiply(p, k + 1);
    }

    String mix (String str1, String str2){
        HashSet<Character> r = new HashSet<>();
        for (int i = 0; i < str1.length(); i++)
            r.add(str1.charAt(i));
        for (int i = 0; i < str2.length(); i++)
            r.add(str2.charAt(i));
        String result = "";
        for (Iterator<Character> i = r.iterator(); i.hasNext();)
            result += i.next();
        return result;
    }

    void addToTable() {
        String[][] table = new String[finalTerms.size() + 1][minterms.size() + maxLength + 1];
        for (int i = 0; i < maxLength; i++) {
            table[0][i + 1] = String.valueOf((char) ('A' + i));
        }
        for (int i = maxLength; i < minterms.size() + maxLength; i++) {
            table[0][i + 1] = String.valueOf(minterms.get(i - maxLength));
        }
        for (int i = 1; i < finalTerms.size() + 1; i++) {
            for (int j = 0; j < maxLength; j++) {
                table[i][j + 1] = String.valueOf(finalTerms.get(i - 1).getString().charAt(j));
            }
        }
        for (int i = 1; i < finalTerms.size() + 1; i++) {
            for (int j = maxLength; j < minterms.size() + maxLength; j++) {
                if (finalTerms.get(i - 1).getNums().contains(minterms.get(j - maxLength))) {
                    table[i][j + 1] = "X";
                } else {
                    table[i][j + 1] = " ";
                }
            }
        }
        for (int i = 0; i < finalTerms.size() + 1; i++) {
            table[i][0] = " ";
        }
        secondStep.add(table);
    }

    void stepSimplification(HashSet<String>[] p, int k) {
        StringBuilder s3 = new StringBuilder();
        for (int i = k; i < p.length; i++) {
            if (p.length != 1)
                s3.append("(");
            for (Iterator<String> g = p[i].iterator(); g.hasNext();) {
                s3.append(g.next());
                if (g.hasNext()) {
                    s3.append(" + ");
                }
            }
            if (p.length != 1)
                s3.append(")");
        }
        thirdStep.add(s3.toString());
    }
    private boolean identifyPrimeImplicants(){
        ArrayList<Integer>[] columns = new ArrayList[minterms.size()];
        for (int i = 0; i < minterms.size(); i++) {
            columns[i] = new ArrayList();
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    columns[i].add(j);
                }
            }
        }
        boolean isPrimeImplicant = false;
        for (int i = 0; i < minterms.size(); i++) {
            if (columns[i].size() == 1) {
                isPrimeImplicant = true;
                ArrayList<Integer> del = finalTerms.get(columns[i].get(0)).getNums();

                for (int j = 0; j < minterms.size(); j++) {
                    if (del.contains(minterms.get(j))) {
                        dc.add(minterms.get(j));
                        minterms.remove(j);
                        j--;
                    }
                }

                secondStep.get(secondStep.size() - 1)[columns[i].get(0).intValue() + 1][0] = "T";

                primeImplicants.add(finalTerms.get(columns[i].get(0)).getString());
                finalTerms.remove(columns[i].get(0).intValue());
                break;
            }
        }
        return isPrimeImplicant;
    }

    private boolean columnDominance(){
        boolean flag = false;

        ArrayList<ArrayList<Integer>> columns = new ArrayList<>();
        for (int i = 0; i < minterms.size(); i++){
            columns.add(new ArrayList<Integer>());
            for (int j = 0; j < finalTerms.size(); j++){
                if (finalTerms.get(j).getNums().contains(minterms.get(i)))
                    columns.get(i).add(j);
            }
        }

        // identify dominating cols and remove them
        for (int i = 0; i < columns.size() - 1; i++){
            for (int j = i + 1; j < columns.size() - 1; i++){
                if (columns.get(j).containsAll(columns.get(i)) &&  columns.get(j).size() > columns.get(i).size()){
                    columns.remove(j);
                    minterms.remove(j);
                    j--;
                    flag = true;
                } else if (columns.get(i).containsAll(columns.get(j)) && columns.get(i).size() > columns.get(j).size()){
                    columns.remove(i);
                    minterms.remove(i);
                    i--;
                    flag = true;
                    break;
                }

            }
        }
        return flag;
    }

    private boolean rowDominance(){
        boolean flag = false;
        // identify dominated rows and delete them
        for (int i = 0; i < finalTerms.size() - 1; i++) {
            for (int j = i + 1; j < finalTerms.size(); j++) {
                if (contains(finalTerms.get(i), finalTerms.get(j))) {
                    finalTerms.remove(j);
                    j--;
                    flag = true;
                } else if (contains(finalTerms.get(j), finalTerms.get(i))) {
                    finalTerms.remove(i);
                    i--;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    String toStandardForm(String s) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '-') {
                continue;
            } else if (s.charAt(i) == '1') {
                r.append((char) ('A' + i));
            } else {
                r.append((char) ('A' + i));
                r.append('\'');
            }
        }
        if (r.toString().length() == 0) {
            r.append("1");
        }
        return r.toString();
    }

    // used to print final results
    public void printResults(String[] variables) {

        for (int i = 0; i < solution.length; i++) {
        	
        	
            System.out.println("Solution #" + (i + 1) + ":");
            /*
            for (int j = 0; j < solution[i].size(); j++) {
                System.out.print(solution[i].get(j));
                if (j != solution[i].size() - 1) {
                    System.out.print(" + ");
                }
            }
            */
            
            StringBuilder finalAnswer = new StringBuilder();
 
            /*
            System.out.print("\n");
            */
            
            for (int j = 0; j < solution[i].size(); j++) {
                finalAnswer.append(toStandardForm(solution[i].get(j)));
                if (j != solution[i].size() - 1) {
                    finalAnswer.append(" + ");
                }
            }
            
            
            StringBuilder printedAnswer = new StringBuilder();
            for (int j = 0; j < finalAnswer.toString().length(); j++){
                if(finalAnswer.charAt(j) == 'A')
                    printedAnswer.append(variables[0]);
                else if (finalAnswer.charAt(j) == 'B')
                    printedAnswer.append(variables[1]);
                else if (finalAnswer.charAt(j) == 'C')
                    printedAnswer.append(variables[2]);
                else if (finalAnswer.charAt(j) == 'D')
                    printedAnswer.append(variables[3]);
                else if (finalAnswer.charAt(j) == 'E')
                    printedAnswer.append(variables[4]);
                else if (finalAnswer.charAt(j) == 'F')
                    printedAnswer.append(variables[5]);
                else if (finalAnswer.charAt(j) == 'G')
                    printedAnswer.append(variables[6]);
                else if (finalAnswer.charAt(j) == 'H')
                    printedAnswer.append(variables[7]);
                else if (finalAnswer.charAt(j) == 'I')
                    printedAnswer.append(variables[8]);
                else if (finalAnswer.charAt(j) == 'J')
                    printedAnswer.append(variables[9]);
                else
                    printedAnswer.append(finalAnswer.toString().charAt(j));
            }
            System.out.println(printedAnswer);
            System.out.println("\n");
        }
    }
}
