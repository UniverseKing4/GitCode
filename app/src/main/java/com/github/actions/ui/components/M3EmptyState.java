package com.github.actions.ui.components;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.actions.ui.theme.DesignSystem;

/**
 * Material 3 empty state view
 */
public class M3EmptyState extends LinearLayout {
    
    private final DesignSystem ds;
    private final TextView iconView;
    private final TextView messageView;
    
    public M3EmptyState(Context context, String icon, String message) {
        super(context);
        ds = DesignSystem.getInstance(context);
        
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setPadding(ds.spacing.xxxl, ds.spacing.xxxl, ds.spacing.xxxl, ds.spacing.xxxl);
        
        // Icon
        iconView = new TextView(context);
        iconView.setText(icon);
        iconView.setTextSize(48);
        iconView.setGravity(Gravity.CENTER);
        addView(iconView);
        
        // Message
        messageView = new TextView(context);
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(ds.colors.onSurfaceVariant);
        messageView.setGravity(Gravity.CENTER);
        messageView.setPadding(0, ds.spacing.lg, 0, 0);
        addView(messageView);
    }
    
    public void setIcon(String icon) {
        iconView.setText(icon);
    }
    
    public void setMessage(String message) {
        messageView.setText(message);
    }
}
