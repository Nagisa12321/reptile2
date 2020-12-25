package com.jtchen;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.IOException;

/************************************************
 *
 * @author jtchen
 * @date 2020/12/26 0:54
 * @version 1.0
 ************************************************/
public class Download implements Runnable {

    private final String linkURL;
    private final String realAddress;
    private final String realTail;
    private final int p;

    public Download(String realAddress, String linkURL, String realTail, int p) {
        this.linkURL = linkURL;
        this.realAddress = realAddress;
        this.realTail = realTail;
        this.p = p;
    }

    @Override
    public void run() {
        try {
            //1.创建连接client
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //2.设置连接的相关选项
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js
            webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常
            webClient.getOptions().setTimeout(10000);  //超时时间  ms
            //3.抓取页面
            HtmlPage page;
            if (p == 1) page = webClient.getPage(linkURL);
            else page = webClient.getPage(linkURL + realTail);
            //4.将页面转成指定格式
            webClient.waitForBackgroundJavaScript(1000);   //等侍js脚本执行完成

            String div = page.getElementById("cp_image").toString();

            String pictureURI = Spider.Identify(div, "img src=\"", '?');
            //下载
            Connection.Response resultImageResponse = Jsoup.connect(pictureURI).ignoreContentType(true).execute();
            FileOutputStream out = new FileOutputStream(realAddress +"/" + p + ".jpg");
            out.write(resultImageResponse.bodyAsBytes());
            out.close();
            webClient.closeAllWindows();
        } catch (IOException e) {
            System.err.println(e.toString() + " 下载出现IO问题。");
        }
    }

}
