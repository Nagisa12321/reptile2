package com.jtchen;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpiderTest {

    @org.junit.Test
    public void identify() {
        String src = "<a href=\"/m27862/\" class=\"detail-list-form-item  \" title=\"\" target=\"_blank\">作者推特P站圖（2017） <span>（23P）</span> </a>";
        System.out.println(Spider.Identify(src, "target=\"_blank\">", '<'));
    }

    @Test
    public void removeSpaces() {
        String src = "作者推特P站圖（2017） ";
        System.out.println("'" + Spider.RemoveSpaces(src) + "'");
    }
}