package com.jtchen.tool;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UrlToolTest {

    @Test
    public void checkFile() {
        List<String> err =  UrlTool.checkFile("C:\\Users\\woxi5\\Desktop\\明日醬的水手服");
        for(int i = 0;i<err.size();++i)
            System.err.println(err.get(i));
    }
}