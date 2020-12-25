package com.jtchen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
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
    private static final ExecutorService pool = Executors.newFixedThreadPool(50);

    public static void spider(String url) {
        try {
            /*//1.创建连接client
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //2.设置连接的相关选项
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js
            webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常
            webClient.getOptions().setTimeout(10000);  //超时时间  ms*/

            Document doc = Jsoup.connect(url)
                    /*.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 1080)))*/
                    .get();
            Elements container = doc.getElementsByClass("detail-list-form-con");
            Document containerDoc = Jsoup.parse(container.toString());
            Elements links = containerDoc.select("a[href]");

            //遍历首页章节链接, 进行处理
            for (Element link : links) {
                String downLoadAddress = "./src/main/resources/一拳超人/";

                String tailURL = link.attr("href");

                //各章链接URL
                String linkURL = basicURL + tailURL;
                //每章标题
                String rawTitle = Identify(link.toString(), "target=\"_blank\">", '<');
                String title = RemoveSpaces(rawTitle);

                //创建文件夹
                int Ps = Integer.parseInt(Identify(link.toString(), "<span>（", 'P'));

                String realAddress = downLoadAddress + title + "(" + Ps + ")";
                var fi = new File(realAddress);
                fi.mkdirs();


                //进入链接, 下载图片

                for (int i = 0; i < Ps; i++) {

                    String realTail = "#ipg" + (i + 1);
                    /* htmlunit */
                    /*//3.抓取页面
                    HtmlPage page;
                    if (i == 0) page = webClient.getPage(linkURL);
                    else page = webClient.getPage(linkURL + realTail);
                    //4.将页面转成指定格式
                    webClient.waitForBackgroundJavaScript(1000);   //等侍js脚本执行完成

                    String div = page.getElementById("cp_image").toString();

                    String pictureURI = Identify(div, "img src=\"", '?');*/
                    //下载

                    pool.submit(new Download(realAddress, linkURL, realTail, i + 1));
                    /*new Thread(new Download(realAddress, linkURL, realTail, i + 1)).start();*/
                }
            }
        } catch (IOException e) {
            System.err.println(e.toString() + " 链接服务器失败! ");
        }
    }

    //解析一个标题
    public static String Identify(String src, String startWith, char end) {
        int idx = 0;
        boolean isWriting = false;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) == end && isWriting) {
                isWriting = false;

                //处理builder
                return builder.toString();
            }

            if (isWriting) {
                builder.append(src.charAt(i));
            }
            if (src.charAt(i) == startWith.charAt(idx)) idx++;
            else idx = 0;

            if (idx == startWith.length()) {
                //设置为可写
                isWriting = true;
                idx = 0;
            }
        }
        return "";
    }

    /* 去掉文件名开头、结尾的空格、特殊符号 */
    public static String RemoveSpaces(String s) {
        int idx = 0;
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == ' ') idx++;
            else break;
        s = idx == 0 ? s : s.substring(idx);
        idx = s.length() - 1;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == ' ') idx--;
            else break;
        }
        s = idx == s.length() - 1 ? s : s.substring(0, idx + 1);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '"'
                    && s.charAt(i) != '?'
                    && s.charAt(i) != '\\'
                    && s.charAt(i) != '/'
                    && s.charAt(i) != ':')
                builder.append(s.charAt(i));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        spider(basicURL + "/38bz/");
        pool.shutdown();
    }
}
