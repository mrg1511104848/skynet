package org.skynet.frame.util;

import org.apache.commons.lang3.StringUtils;

public class StringBufferUtil {
	public static StringBuffer removeEnd(StringBuffer sb, String remove) {
		StringBuffer resultBuffer = new StringBuffer(StringUtils.removeEnd(
				sb.toString(), remove));
		return resultBuffer;
	}
}
