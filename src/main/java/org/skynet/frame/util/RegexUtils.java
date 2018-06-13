package org.skynet.frame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	/**
     * 验证Email
     * @param email email地址，格式：zhangsan@zuidaima.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkEmail(String email) { 
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?"; 
        return Pattern.matches(regex, email); 
    } 
     
    /**
     * 验证身份证号码
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkIdCard(String idCard) { 
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}"; 
        return Pattern.matches(regex,idCard); 
    } 
     
    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     * @param mobile 移动、联通、电信运营商的号码段
     *<p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *<p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *<p>电信的号段：133、153、180（未启用）、189</p>
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkMobile(String mobile) { 
        String regex = "(\\+\\d+)?1[34578]\\d{9}$"; 
        return Pattern.matches(regex,mobile); 
    } 
     
    /**
     * 验证固定电话号码
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     * <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *  数字之后是空格分隔的国家（地区）代码。</p>
     * <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     * 对不使用地区或城市代码的国家（地区），则省略该组件。</p>
     * <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkPhone(String phone) { 
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$"; 
        return Pattern.matches(regex, phone); 
    }
     
    /**
     * 验证整数（正整数和负整数）
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkDigit(String digit) { 
        String regex = "\\-?[1-9]\\d+"; 
        return Pattern.matches(regex,digit); 
    } 
    /**
     * 验证整数（正整数和负整数）
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkDigit(String digit,int count) { 
        String regex = "\\d{"+count+"}"; 
        return Pattern.matches(regex,digit); 
    }
     
    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkDecimals(String decimals) { 
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?"; 
        return Pattern.matches(regex,decimals); 
    }  
     
    /**
     * 验证空白字符
     * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkBlankSpace(String blankSpace) { 
        String regex = "\\s+"; 
        return Pattern.matches(regex,blankSpace); 
    } 
     
    /**
     * 验证中文
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkChinese(String chinese) { 
        String regex = "^[\u4E00-\u9FA5]+$"; 
        return Pattern.matches(regex,chinese); 
    } 
     
    /**
     * 验证日期（年月日）
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkBirthday(String birthday) { 
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}"; 
        return Pattern.matches(regex,birthday); 
    } 
     
    /**
     * 验证URL地址
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkURL(String url) { 
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?"; 
        return Pattern.matches(regex, url); 
    } 
    
    /**
     * <pre>
     * 获取网址 URL 的一级域
     * </pre>
     * 
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        // 获取完整的域名
        // Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        matcher.find();
        return matcher.group();
    }
    /**
     * 匹配中国邮政编码
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkPostcode(String postcode) { 
        String regex = "[1-9]\\d{5}"; 
        return Pattern.matches(regex, postcode); 
    } 
     
    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     * @param ipAddress IPv4标准地址
     * @return 验证成功返回true，验证失败返回false
     */ 
    public static boolean checkIpAddress(String ipAddress) { 
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))"; 
        return Pattern.matches(regex, ipAddress); 
    }

    /**
     * 校验密码，字母和数字的组合
     * @param
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPassword(String ipAddress) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        return Pattern.matches(regex, ipAddress);
    }
    
    /**
     * 提取数字
     * @param ipAddress
     * @return
     */
    public static String getNum(String str) {
    	if(str==null) return null;
        String regex = "\\d+";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if(matcher.find())
        	return matcher.group();
        return null;
    }
    
    /**
     * 提取数字
     * @param ipAddress
     * @return
     */
    public static String getByRegex(String regex , String str) {
    	List<String> list = getByGroup(regex, str, null);
        return list.size()>0?list.get(0):null;
    }
    /**
     * 提取数字
     * @param ipAddress
     * @return
     */
    public static List<String> getAllMatchByRegex(String regex , String str) {
    	List<String> list = getByGroup(regex, str, null);
        return list.size()>0?list:null;
    }
    public static List<String> getByGroup(String regex , String str ,Integer idx) {
    	List<String> resultList = new ArrayList<String>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        int groupCount = matcher.groupCount();
        if(idx == null){
        	idx = 1;
        }
        if(idx > groupCount){
        	return null;
        }
        while(matcher.find()){
        	if(idx == null) {
        		resultList.add(matcher.group());
        	}else{
        		resultList.add(matcher.group(idx));
        	}
        }
        return resultList;
    }
    public static List<String> getByGroup(String regex , String str) {
    	List<String> resultList = new ArrayList<String>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if(matcher.find()){
	        int groupCount = matcher.groupCount();
	        for (int i = 1 ; i <= groupCount ; i++) {
	        	resultList.add(matcher.group(i));
			}
        }
        return resultList;
    }
    /**
     * 校验用户名，字母和数字的组合
     * @param
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkUsername(String username) {
        String regex = "[a-zA-Z0-9_]+$";
        return Pattern.matches(regex, username);
    }
    public static String dosageFilterRegex = ""
    		+ "("
    		+ "[0-9.一—二三四五六七八九]+\\s*[mg毫克片粒颗袋拴揿瓶片喷枚盒滴单位袋包百万单位μgWmlIUkgm²]?"
			+ "\\s*[至或~\\--—～]{1}"
			+ "\\s*[0-9.一—二三四五六七八九]+\\s*[mg毫克片粒颗袋拴揿瓶片喷枚盒滴单位袋包百万单位μgWmlIUkgm²]+"
			+ ")";
//    public static String dosageFilterRegex = "(-[0-9.]+\\s*[至或~-～mg毫克次片粒颗袋mg拴揿瓶片喷枚盒滴单位袋包百万单位μgWmlIUkgm²]+\\s*)";
    public static void main(String[] args) {
    	/*boolean rr = checkDigit("65m3", 4);
    	//System.out.println(rr);
    	rr = checkDigit("65.34", 4);
    	//System.out.println(rr);
    	rr = checkDigit("6534", 4);
    	//System.out.println(rr);
    	rr = checkDigit("65345", 4);
    	//System.out.println(rr);
*/
        /*boolean result = checkUsername("!aaaa");
        //System.out.println(result);
        result = checkUsername("11aaaa1 aaa");
        //System.out.println(result);
        result = checkUsername("aaaa{");
        //System.out.println(result);*/
    	
//    	boolean is = checkDigit("835529", 6);
//    	//System.out.println(is);
    	
    	//System.out.println(getNum("gav333222"));;
//    	System.out.println(getByGroup("(\\d+)~(\\d+)", "1~2", 2));
//    	List<String> list = getByGroup("(\\d+|\\d+.\\d+)~(\\d+|\\d+.\\d+)", "2.5~5");
//    	System.out.println(list);
//    	list = getByGroup("(\\d+|\\d+.\\d+)~(\\d+|\\d+.\\d+)", "522~53311");
//    	System.out.println(list);
//    	
//    	String s = RegexUtils.getByRegex("documentId:\\s*'(.+)'", "documentId:  '391125'");
//    	System.out.println(s);
//    	String s = RegexUtils.getByRegex(dosageFilterRegex, "每次口服一至二片");
//    	System.out.println(s);
//    	System.out.println("一日 1～2 片".matches(".*mg.*|.*g.*|.*毫克.*|.*克.*|.*次.*|.*片.*[片|粒|颗|袋|mg|g|毫克|克]?"));
    	String tongYongName = "复方卡托不批";
    	List<String> tongYongNameRegexList = RegexUtils.getByGroup("(左旋|小儿|复方|右旋|注射用){0,1}(.+?)(口服|片|分散片|混悬液|缓释胶囊|胶囊|缓释片|乳膏|凝胶|软胶囊|糖浆|搽剂|缓释混悬液|混悬液|颗粒|注射液|滴眼液|泡腾片|眼膏|肠溶片|栓){1,3}", tongYongName, 2);
    	if(tongYongNameRegexList.size() == 0){
    		tongYongNameRegexList = RegexUtils.getByGroup("(左旋|小儿|复方|右旋|注射用){0,1}(.+)", tongYongName, 2);
    	}
    	if(tongYongNameRegexList.size() > 0){
    		tongYongName = tongYongNameRegexList.get(0);
    	}
    	System.out.println(tongYongName);
    }
}
