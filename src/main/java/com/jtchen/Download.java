package com.jtchen;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
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
    private final int p;
    private WebClient webClient;

    public Download(String realAddress, String linkURL, int p) {
        this.linkURL = linkURL;
        this.realAddress = realAddress;
        this.p = p;
    }

    @Override
    public void run() {
        /*try {*/
        //1.创建连接client
        webClient = new WebClient(BrowserVersion.CHROME);
        //2.设置连接的相关选项
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js
        webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常
        webClient.getOptions().setTimeout(20000);  //超时时间  ms
        for (int i = 0; i < p; i++) {
            String realTail = "#ipg" + (i + 1);
            sourceDownLoad(realTail, i);
        }

        File[] files = new File(realAddress).listFiles();
        if (files.length == p) System.out.println("======文件完整, 线程结束!=======");
        else {
            System.err.println("======文件不完整, 重新检查!=======");
            while (true) {
                files =  new File(realAddress).listFiles();
                if (files.length == p) break;
                int[] map = new int[p];
                for (File file : files) {
                    map[getIdx(file.getName())]++;
                }
                for (int i = 0; i < p; i++) {
                    if (map[i] == 0) {
                        System.err.println("======" + (i + 1) + "P不完整, 重新检查!=======");
                        String realTail = "#ipg" + (i + 1);
                        sourceDownLoad(realTail, i);
                    }
                }
            }
        }
    } /*catch (InterruptedException e) {
            System.err.println("线程异常");
        }*/


    public void sourceDownLoad(String realTail, int i) {
        try {
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg");
            HtmlPage page;
            if (i + 1 == 1) page = webClient.getPage(linkURL);
            else page = webClient.getPage(linkURL + realTail);
            //4.将页面转成指定格式
            webClient.waitForBackgroundJavaScript(1500);   //等侍js脚本执行完成

            String div = page.getElementById("cp_image").toString();

            String pictureURI = Spider.Identify(div, "img src=\"", '?');
            //下载
            Connection.Response resultImageResponse = Jsoup.connect(pictureURI).ignoreContentType(true).execute();
            FileOutputStream out = new FileOutputStream(realAddress + "/" + (i + 1) + ".jpg");
            out.write(resultImageResponse.bodyAsBytes());
            out.close();
            webClient.closeAllWindows();
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg  --->  Succeeded!!O(∩_∩)O");
        } catch (IOException e) {
            System.err.println(realAddress + " " + (i + 1) + " P下载出现IO问题, 重新下载");
            sourceDownLoad(realTail, i);
        }
    }

    /* 通过 XXP.jpg 获取第几p */
    public int getIdx(String name) {
        int idx = 0;
        while (name.charAt(idx) != 'P') idx++;
        return Integer.parseInt(name.substring(0, idx));
    }
}
