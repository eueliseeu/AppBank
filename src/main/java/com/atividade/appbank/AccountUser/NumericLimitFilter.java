package com.atividade.appbank.AccountUser;

import javax.swing.text.*;

public class NumericLimitFilter extends DocumentFilter {
    private final int maxLength;

    public NumericLimitFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    private boolean isValid(String text, int currentLength) {
        return text.matches("\\d+") && currentLength + text.length() <= maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (isValid(string, fb.getDocument().getLength())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs)
            throws BadLocationException {
        if (isValid(string, fb.getDocument().getLength())) {
            super.replace(fb, offset, length, string, attrs);
        }
    }
}
