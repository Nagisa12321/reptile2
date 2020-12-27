package com.jtchen.spider;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.jtchen.tool.UrlTool;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/************************************************
 *
 * @author jtchen
 * @date 2020/12/27 13:07
 * @version 1.0
 ************************************************/
public class Search {
    private static final String basicURL = "http://www.mangabz.com";

    public static String searchFromHomePage(String s) {
        String res = "";
        //1.创建连接client
        try {
            System.out.println("浏览器正在启动...");
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //2.设置连接的相关选项
            LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js
            webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常
            webClient.getOptions().setTimeout(20000);  //超时时间  ms

            HtmlPage page = webClient.getPage(basicURL);
            HtmlAnchor input = page.getHtmlElementById("btnSearch");
            HtmlTextInput textInput = page.getHtmlElementById("txtKeywords");
            textInput.setText(s);
            page = input.click();
            String tmp = page.getByXPath("//div[@class='mh-item'][1]/a").get(0).toString();
            res = UrlTool.Identify(tmp, "href=\"", '"');
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return res;
    }
}
