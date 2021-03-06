package com.jtchen.download;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jtchen.tool.UrlTool;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/************************************************
 * @author jtchen
 * @date 2020/12/26 0:54
 * @version 1.0
 ************************************************/
@SuppressWarnings("ConditionalBreakInInfiniteLoop")
public class Download implements Runnable {

    private final JTextArea area;
    private final String linkURL;
    private final String realAddress;
    private final int p;
    private WebClient webClient;

    public Download(String realAddress, String linkURL, int p, JTextArea area) {
        this.area = area;
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
        webClient.getOptions().setTimeout(22000);  //超时时间  ms
        for (int i = 0; i < p; i++) {
            String realTail = "#ipg" + (i + 1);
            sourceDownLoad(realTail, i);
        }

        File[] files = new File(realAddress).listFiles();
        assert files != null;
        if (files.length == p) {
            System.out.println("======文件完整, 线程结束!=======");
            area.append(UrlTool.cutAddress(realAddress) + " 下载成功 ヾ(≧▽≦*)o\n");
            area.setCaretPosition(area.getText().length());
        } else {
            System.err.println("======文件不完整, 重新检查!=======");
            while (true) {
                files = new File(realAddress).listFiles();
                assert files != null;
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
            while (true) {
                if (i + 1 == 1) page = webClient.getPage(linkURL);
                else page = webClient.getPage(linkURL + realTail);
                if (!Objects.equals(page, null)) break;
            }
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg " + page.toString());
            //4.将页面转成指定格式
            webClient.waitForBackgroundJavaScript(16432);   //等侍js脚本执行完成
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg,网页js执行完毕,正在获取资源...");

            String div = page.getElementById("cp_image").toString();

            String pictureURI = UrlTool.Identify(div, "img src=\"", '"');
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg,图片url为:" + pictureURI);
            //下载
            Connection.Response resultImageResponse = Jsoup.connect(pictureURI).ignoreContentType(true).execute();
            FileOutputStream out = new FileOutputStream(realAddress + "/" + (i + 1) + ".jpg");
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg,文件流成功打开,开始获取图片二进制流");
            byte[] data = resultImageResponse.bodyAsBytes();
            System.out.println("downloading.. " + realAddress + "/" + (i + 1) + ".jpg,资源已获取到内存,正在写入文件中....");
            out.write(data);
            out.flush();
            out.close();
            webClient.closeAllWindows();
            System.out.println("downloading.. " + realAddress
                    + "/" + (i + 1) + ".jpg  --->  Succeeded!!O(∩_∩)O");
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
