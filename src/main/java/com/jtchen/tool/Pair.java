package com.jtchen.tool;

/************************************************
 * 搜索后呈现的bz号, 漫画名形成的对
 * @author jtchen
 * @date 2020/12/27 16:14
 * @version 1.0
 ************************************************/
public class Pair {
    private final String bz; // eg. /551bz/
    private final String name; //eg.  干物妹小埋

    public Pair(String bz, String name) {
        this.bz = bz;
        this.name = name;
    }

    public String getBz() {
        return bz;
    }

    public String getName() {
        return name;
    }
}
