package org.skynet.frame.fullFetch;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Regex {
	private List<String> blackList = new ArrayList<String>();

	private List<String> whiteList = new ArrayList<String>();
	
	public Regex(String blackPath, String whitePath) {
		addToList(blackPath, blackList);
		addToList(whitePath, whiteList);
    }
	private void addToList(String path,List<String> urlList){
		BufferedReader br = null;
		FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
        	fis = new FileInputStream(path);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                urlList.add(line);
            }
        } catch (Exception e) {
            System.out.println(String.format("读取%s出现异常：%s", path,  e.getMessage()));
        } finally{
        	try {
				if(br != null) br.close();
				if(isr != null) isr.close();
				if(fis != null) fis.close();
			} catch (IOException e) {
			}
        }
	}
	public List<String> getBlackList() {
		return blackList;
	}

	public void setBlackList(List<String> blackList) {
		this.blackList = blackList;
	}

	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}
}
