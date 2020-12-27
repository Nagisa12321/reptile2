package com.jtchen.spider;

import com.jtchen.download.Download;
import com.jtchen.tool.UrlTool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/************************************************
 *
 * @author jtchen
 * @date 2020/12/25 21:34
 * @version 1.0
 ************************************************/
@SuppressWarnings("ForLoopReplaceableByForEach")
public class Spider implements Runnable {

    private final ExecutorService pool = Executors.newFixedThreadPool(20);

    private final String name;
    private final String url;
    private final String basicAddress;

    private JButton button;

    private final JTextArea area;


    public Spider(String basicAddress, String name, String url, JTextArea area) {
        this.basicAddress = basicAddress;
        this.name = name;
        this.area = area;
        this.url = url;
    }


    public void setButton(JButton button) {
        this.button = button;
    }

    @Override
    public void run() {
        spider();
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void spider() {
        try {

            Document doc = Jsoup.connect(url)
                    /*.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 1080)))*/
                    .get();
            Elements container = doc.getElementsByClass("detail-list-form-con");
            Document containerDoc = Jsoup.parse(container.toString());
            Elements links = containerDoc.select("a[href]");

            //遍历首页章节链接, 进行处理
            for (Element link : links) {
                String downLoadAddress = basicAddress + "/" + name + "/";

                String tailURL = link.attr("href");

                //各章链接URL
                String basicURL = "http://www.mangabz.com";
                String linkURL = basicURL + tailURL;
                //每章标题
                String rawTitle = UrlTool.Identify(link.toString(), "target=\"_blank\">", '<');
                String title = UrlTool.RemoveSpaces(rawTitle);

                //创建文件夹
                int Ps = Integer.parseInt(UrlTool.Identify(link.toString(), "<span>（", 'P'));

                String realAddress = downLoadAddress + title + "(" + Ps + ")";
                var fi = new File(realAddress);
                fi.mkdirs();


                //进入链接, 下载图片

                pool.submit(new Download(realAddress, linkURL, Ps, area));
                /*new Thread(new Download(realAddress, linkURL, realTail, i + 1)).start();*/
            }
            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

            area.append("下载任务已完成 ~\n");
            List<String> errorMessage = UrlTool.checkFile(basicAddress + "\\" + name);

            for (int i = 0; i < errorMessage.size(); ++i)
                area.append(errorMessage.get(i) + "\n");

            button.setEnabled(true);
        } catch (IOException e) {
            System.err.println(e.toString() + " 链接服务器失败! ");
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
    }

}
