package com.infilos.hints.eval;

import com.infilos.hints.Evaluator;
import com.infilos.hints.utils.Strings;
import org.apache.commons.text.similarity.LongestCommonSubsequence;

public class LcsEvaluator implements Evaluator<String> {
    private LcsEvaluator() {
    }

    private static final LongestCommonSubsequence lcsComparator = new LongestCommonSubsequence();
    public static final LcsEvaluator Instance = new LcsEvaluator();

    @Override
    public String evaluate(String attribute, String word) {
        return lcsComparator.longestCommonSubsequence(word, attribute).toString();
    }

    @Override
    public boolean check(String lcs) {
        return Strings.nonBlank(lcs);
    }

    @Override
    public int compare(String lcs1, String lcs2) {
        return lcs2.length() - lcs1.length();
    }
}
