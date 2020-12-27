package com.jtchen.app;

import com.formdev.flatlaf.FlatLightLaf;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/************************************************
 *
 * @author jtchen
 * @date 2020/12/27 17:38
 * @version 1.0
 ************************************************/
@SuppressWarnings("unused")
public class select {
    private JButton button1;
    private JButton allButton;
    private JPanel root;

    public select() {
        button1.addActionListener(e -> new Thread(new searchGUI()).start());
    }

    public static void main(String[] args) {
        FlatLightLaf.install();
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        JFrame frame = new JFrame("select");
        frame.setContentPane(new select().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(400, 500);
        frame.setVisible(true);
    }
}
