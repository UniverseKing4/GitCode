package com.github.actions.ui.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.actions.ui.theme.DesignSystem;

/**
 * Material 3 tab item for tab bar
 */
public class M3TabItem extends LinearLayout {
    
    private final DesignSystem ds;
    private final TextView nameView;
    private final TextView closeButton;
    private boolean selected = false;
    
    public M3TabItem(Context context, String name) {
        super(context);
        ds = DesignSystem.getInstance(context);
        
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(ds.spacing.lg, ds.spacing.sm, ds.spacing.md, ds.spacing.sm);
        
        // Name
        nameView = new TextView(context);
        nameView.setText(name);
        nameView.setTextSize(14);
        nameView.setSingleLine(true);
        nameView.setMaxWidth(ds.spacing.dp(150));
        nameView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        addView(nameView);
        
        // Close button
        closeButton = new TextView(context);
        closeButton.setText("×");
        closeButton.setTextSize(18);
        closeButton.setPadding(ds.spacing.sm, 0, 0, 0);
        closeButton.setMinWidth(ds.spacing.dp(24));
        closeButton.setGravity(Gravity.CENTER);
        addView(closeButton);
        
        updateStyle();
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateStyle();
    }
    
    public void setOnCloseClickListener(OnClickListener listener) {
        closeButton.setOnClickListener(listener);
    }
    
    private void updateStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(ds.spacing.dp(8));
        
        if (selected) {
            bg.setColor(ds.colors.surfaceContainer);
            nameView.setTextColor(ds.colors.onSurface);
            closeButton.setTextColor(ds.colors.onSurface);
        } else {
            bg.setColor(ds.colors.surfaceVariant);
            nameView.setTextColor(ds.colors.onSurfaceVariant);
            closeButton.setTextColor(ds.colors.onSurfaceVariant);
        }
        
        setBackground(bg);
    }
}
