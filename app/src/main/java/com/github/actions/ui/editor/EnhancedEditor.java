package com.github.actions.ui.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;
import com.github.actions.ui.theme.DesignSystem;

/**
 * Enhanced code editor with line highlighting and better UX
 */
public class EnhancedEditor extends AppCompatEditText {
    
    private Paint linePaint;
    private int currentLine = 0;
    private DesignSystem ds;
    
    public EnhancedEditor(Context context) {
        super(context);
        init(context);
    }
    
    public EnhancedEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    private void init(Context context) {
        ds = DesignSystem.getInstance(context);
        
        linePaint = new Paint();
        linePaint.setColor(ds.colors.surfaceVariant);
        linePaint.setAlpha(40);
        
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCurrentLine();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        setOnClickListener(v -> {
            updateCurrentLine();
            invalidate();
        });
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if (getLayout() != null && currentLine >= 0 && currentLine < getLineCount()) {
            int lineTop = getLayout().getLineTop(currentLine);
            int lineBottom = getLayout().getLineBottom(currentLine);
            canvas.drawRect(0, lineTop, getWidth(), lineBottom, linePaint);
        }
        super.onDraw(canvas);
    }
    
    private void updateCurrentLine() {
        int selectionStart = getSelectionStart();
        if (selectionStart >= 0 && getLayout() != null) {
            int newLine = getLayout().getLineForOffset(selectionStart);
            if (newLine != currentLine) {
                currentLine = newLine;
                invalidate();
            }
        }
    }
}
