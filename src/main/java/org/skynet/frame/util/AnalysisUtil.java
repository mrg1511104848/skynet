package org.skynet.frame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;

public class AnalysisUtil {
	public static void loadDict(List<String> dictList,String nature){
		for (String dict : dictList) {
			if(StringUtils.isNotBlank(dict)){
				DicLibrary.insert(DicLibrary.DEFAULT, dict , nature, 1000);
			}
		}
	}
	public static List<String> getTermNameList(String sentence){
		List<String> termNameList = new ArrayList<String>();
		Result ansjResult = ToAnalysis.parse(sentence);
		List<Term> terms = ansjResult.getTerms();
		for (Term term : terms) {
			termNameList.add(term.getName());
		}
		return termNameList;
	}
	public static List<String> getTermNameNotRepeatList(String sentence){
		List<String> newTermList = new ArrayList<String>();
		List<String> termList = getTermNameList(sentence);
		for (String term : termList) {
			if(!newTermList.contains(term)){
				newTermList.add(term);
			}
		}
		return newTermList;
	}
	public static String getTermStr(String sentence,String bySplit){
		if(bySplit == null){
			bySplit = " ";
		}
		StringBuffer sb = new StringBuffer();
		List<Term> termList = getTermList(sentence);
		for (Term term : termList) {
			sb.append(term.getName());
			sb.append(bySplit);
		}
		return StringUtils.removeEnd(sb.toString(),bySplit);
	}
	public static List<Term> getTermList(String sentence){
		Result ansjResult = DicAnalysis.parse(sentence);
		List<Term> terms = ansjResult.getTerms();
		return terms;
	}
	public static List<String> getTermNameList(String sentence,String... nature){
		List<String> results = new ArrayList<String>();
		List<Term> terms = AnalysisUtil.getTermList(sentence);
		for (Term term : terms) {
			String natureStr = term.getNatureStr();
			String name = term.getName();
			for (String nt : nature) {
				if(natureStr.equals(nt)){
					results.add(name);
					break;
				}
			}
		}
		return results;
	}
	public static List<String> getTermNameList(String sentence,String nature){
		return getTermNameList(sentence,nature);
	}
}
