package com.jtchen.app;

import com.jtchen.spider.Search;
import com.jtchen.spider.Spider;
import com.jtchen.tool.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("unchecked")
public class searchGUI extends JDialog implements Runnable {
    private static final String basicURL = "http://www.mangabz.com";
    private Pair[] pairs;
    private JPanel contentPane;
    private JList list1;
    private JButton downloadButton;
    private JButton searchButton;
    private JTextArea Console;
    private JTextField textField1;
    private JTextArea textArea2;
    private JButton browseButton;
    private JButton buttonOK;

    public searchGUI() {
        downloadButton.setEnabled(false);
        textArea2.setText("C:\\Users\\12164\\Desktop");
        Console.setLineWrap(true);
        Console.setEnabled(false);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        searchButton.addActionListener(e -> {
            String name = textField1.getText();
            Console.append("正在打开『浏览器』...并且开始搜索\n");
            pairs = Search.searchFromHomePage(name);
            Console.append("搜完完成, 结果如右边\n");

            DefaultListModel<String> defaultListModel = new DefaultListModel<>();
            for (int i = 0; i < pairs.length; i++) {
                defaultListModel.add(i, pairs[i].getName());
            }
            list1.setModel(defaultListModel);
            list1.setSelectedIndex(0);
            downloadButton.setEnabled(true);
        });
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("选择『下载』文件夹");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filepath = chooser.getSelectedFile().getAbsolutePath();
                textArea2.setText(filepath);
            }
        });
        downloadButton.addActionListener(e -> {
            searchButton.setEnabled(false);
            browseButton.setEnabled(false);
            int idx = list1.getSelectedIndex();
            Console.append("开始下载...\n");
            Spider.setBasicAddress(textArea2.getText());
            Spider.setName(pairs[idx].getName());
            Spider.setArea(Console);
            Spider.spider(basicURL + pairs[idx].getBz());
        });
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("searchGUI");
        frame.setContentPane(new searchGUI().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(400, 200);
        frame.pack();
        frame.setVisible(true);
    }
}
