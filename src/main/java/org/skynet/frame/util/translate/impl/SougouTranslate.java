package org.skynet.frame.util.translate.impl;

import java.util.List;

import org.skynet.frame.util.translate.Translate;

import com.swjtu.lang.Lang;
import com.swjtu.querier.Querier;
import com.swjtu.trans.Sogou;

public class SougouTranslate extends Translate {
	static Querier querier = Querier.getQuerier();
	static{
		querier.attach(new Sogou());
	}
	@Override
	public String startTranslate(String sentence) {
		querier.setParams(Lang.EN, Lang.ZH, sentence);
	    List<String> result = querier.execute();
	    if(result!=null && result.size()>0) {
	    	String translateResult = result.get(0);
	    	return translateResult;
	    }
		return null;
	}
}
