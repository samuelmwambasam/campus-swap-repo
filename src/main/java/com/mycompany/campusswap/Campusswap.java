package com.mycompany.campusswap;

import campusswap.gui.LoginForm;

import javax.swing.SwingUtilities;

public class Campusswap {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}