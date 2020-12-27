package com.jtchen.spider;

import com.jtchen.download.Download;
import com.jtchen.tool.Pair;
import com.jtchen.tool.UrlTool;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/************************************************
 *
 * @author jtchen
 * @date 2020/12/25 21:34
 * @version 1.0
 ************************************************/
public class Spider {

    private static final String basicURL = "http://www.mangabz.com";
    private static final ExecutorService pool = Executors.newFixedThreadPool(24);

    private static String name;

    private static String basicAddress = "./src/main/resources";

    private static JTextArea area;

    public static void setBasicAddress(String basicAddress) {
        Spider.basicAddress = basicAddress;
    }


    public static void setName(String name) {
        Spider.name = name;
    }

    public static void setArea(JTextArea area) {
        Spider.area = area;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void spider(String url) {
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
        } catch (IOException e) {
            System.err.println(e.toString() + " 链接服务器失败! ");
        }
    }


    public static void main(String[] args) {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        System.out.println("===请选择你要的模式:===");
        System.out.println("1. 疯狂爬取");
        System.out.println("2. 搜索爬取");
        int in = new Scanner(System.in).nextInt();
        if (in == 1) {

        } else if (in == 2) {
            System.out.println("输入你想要的漫画");
            name = new Scanner(System.in).next();
            Pair[] pairs = Search.searchFromHomePage(name);

            System.out.println("===搜索结果如下===");
            for (int i = 0; i < pairs.length; i++) {
                System.out.println(i + ". " + pairs[i].getName());
            }
            System.out.println("请输入你要下载的序号");
            int idx = new Scanner(System.in).nextInt();
            if (idx >= pairs.length)
                System.err.println("输入序号有误");
            else spider(basicURL + pairs[idx].getBz());

        } else {
            System.err.println("输入的不是1或2, 程序退出");
        }
        pool.shutdown();
    }
}
