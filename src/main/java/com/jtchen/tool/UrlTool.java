package com.jtchen.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/************************************************
 *
 * @author jtchen
 * @date 2020/12/27 13:05
 * @version 1.0
 ************************************************/
public class UrlTool {
    //检查所有文件完整性
    public static List<String> checkFile(String Dir) {
        List<String> error = new ArrayList<>();
        File[] subDir = new File(Dir).listFiles();
        assert subDir != null;
        for (File dir : subDir) {
            String dirName = dir.toString();
            String CountStr = dirName.substring(dirName.lastIndexOf('(') + 1, dirName.lastIndexOf(')'));
            int count = Integer.parseInt(CountStr);
            if (count != Objects.requireNonNull(dir.listFiles()).length)
                error.add(dirName + " ->不完整，请好好检查O(∩_∩)O");

        }
        return error;
    }

    //解析一个标题
    public static String Identify(String src, String startWith, char end) {
        int idx = 0;
        boolean isWriting = false;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) == end && isWriting) {
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

    public static String cutAddress(String add) {
        return add.substring(add.lastIndexOf('/'));
    }
}
