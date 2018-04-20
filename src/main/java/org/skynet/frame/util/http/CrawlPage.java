package org.skynet.frame.util.http;


public class CrawlPage {
	/*public static void main(String[] args) throws Exception {  
//      CrawlPage crawl = new CrawlPage();  
        String url = "http://app1.sfda.gov.cn/datasearch/face3/base.jsp?tableId=25&tableName=TABLE25&title=%B9%FA%B2%FA%D2%A9%C6%B7&bcId=124356560303886909015737447882";  
//      System.out.println("----------------------抓取页面时不解析js-----------------");  
//      crawl.crawlPageWithoutAnalyseJs(url);  
        System.out.println("----------------------抓取页面时解析js-------------------");
        //1.创建连接client   
        WebClient webClient = new WebClient(BrowserVersion.CHROME);  
        //2.设置连接的相关选项  
        webClient.getOptions().setCssEnabled(false);  
        webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js  
        webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常  
        webClient.getOptions().setTimeout(10000);  //超时时间  ms  
        
        //3.抓取页面  
        HtmlPage page = webClient.getPage(url);  
        //4.将页面转成指定格式  
        webClient.waitForBackgroundJavaScript(10000);   //等侍js脚本执行完成  
        
        org.jsoup.nodes.Document doc = Jsoup.parse(page.asXml());
        Elements elements = doc.select("a[href^=javascript:commitForECMA(callbackC,]");
        for (Element ele : elements) {
        	String medicineTitle = ele.text();
			String href = ele.attr("href");
			href = href.replace("javascript:commitForECMA(callbackC,\"", "");
			href = href.replace("\",null)", "");
			href = "http://app1.sfda.gov.cn/datasearch/face3"+href;
			String detailHtm = HttpUtil.get(href, null);
			org.jsoup.nodes.Document detailDoc = Jsoup.parse(detailHtm);
			Elements detailEles = detailDoc.select(".listmain table tr:gt(0)");
			for (Element eachLineDetailEle : detailEles) {
				Element titleElement = eachLineDetailEle.select("td:eq(0)").first();
				Element contentElement = eachLineDetailEle.select("td:eq(1)").first();
				
			}
		}
        List<Object> object = page.getByXPath("//*[@id=\"content\"]/div/table[4]/tbody/tr/td[4]/img");
        System.out.println("------------------------");
        for (Object o : object) {
        	HtmlPage p = (HtmlPage)((HtmlImage)o).click();
        	System.out.println(p.asXml());  
		}
        webClient.close();  
    }  
	
	
      
    public void crawlPageWithoutAnalyseJs(String url) throws Exception{  
        //1.创建连接client   
        WebClient webClient = new WebClient(BrowserVersion.CHROME);  
        //2.设置连接的相关选项  
        webClient.getOptions().setCssEnabled(false);  
        webClient.getOptions().setJavaScriptEnabled(false);  
        webClient.getOptions().setTimeout(10000);  
        //3.抓取页面  
        HtmlPage page = webClient.getPage(url);  
        System.out.println(page.asXml());  
        //4.关闭模拟窗口  
        webClient.close();
    }  
      
    public static HtmlPage crawlPageWithAnalyseJs(String url) throws Exception{  
        //1.创建连接client   
        WebClient webClient = new WebClient(BrowserVersion.CHROME);  
        //2.设置连接的相关选项  
        webClient.getOptions().setCssEnabled(false);  
        webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js  
        webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常  
        webClient.getOptions().setTimeout(10000);  //超时时间  ms  
        //3.抓取页面  
        HtmlPage page = webClient.getPage(url);  
        //4.将页面转成指定格式  
        webClient.waitForBackgroundJavaScript(10000);   //等侍js脚本执行完成  
        System.out.println(page.asXml());  
        //5.关闭模拟的窗口  
        webClient.close();  
        return page;
    }  */
}  