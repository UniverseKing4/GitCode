package com.github.actions.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import java.util.regex.*;

public class SyntaxHighlighter {
    
    private Context context;
    
    public SyntaxHighlighter(Context context) {
        this.context = context;
    }
    
    public Spannable highlight(String code, String language) {
        SpannableString spannable = new SpannableString(code);
        
        if (language.equals("java") || language.equals("kt")) {
            highlightJava(spannable, code);
        } else if (language.equals("js") || language.equals("ts")) {
            highlightJavaScript(spannable, code);
        } else if (language.equals("py")) {
            highlightPython(spannable, code);
        }
        
        return spannable;
    }
    
    private void highlightJava(SpannableString spannable, String code) {
        String[] keywords = {"public", "private", "class", "void", "int", "String", "if", "else", "for", "while", "return", "new", "this", "static", "final"};
        
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(0xFFFF79C6), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        
        Pattern stringPattern = Pattern.compile("\"[^\"]*\"");
        Matcher stringMatcher = stringPattern.matcher(code);
        while (stringMatcher.find()) {
            spannable.setSpan(new ForegroundColorSpan(0xFF50FA7B), stringMatcher.start(), stringMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        Pattern commentPattern = Pattern.compile("//.*");
        Matcher commentMatcher = commentPattern.matcher(code);
        while (commentMatcher.find()) {
            spannable.setSpan(new ForegroundColorSpan(0xFF6272A4), commentMatcher.start(), commentMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    
    private void highlightJavaScript(SpannableString spannable, String code) {
        String[] keywords = {"function", "const", "let", "var", "if", "else", "for", "while", "return", "class", "async", "await"};
        
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(0xFFFF79C6), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    
    private void highlightPython(SpannableString spannable, String code) {
        String[] keywords = {"def", "class", "if", "elif", "else", "for", "while", "return", "import", "from", "as", "try", "except"};
        
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(0xFFFF79C6), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
