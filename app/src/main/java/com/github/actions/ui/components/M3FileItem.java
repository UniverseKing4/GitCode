package com.github.actions.ui.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.actions.ui.theme.DesignSystem;

/**
 * Material 3 file/folder list item
 */
public class M3FileItem extends LinearLayout {
    
    private final DesignSystem ds;
    private final TextView iconView;
    private final TextView nameView;
    
    public M3FileItem(Context context, String name, boolean isFolder) {
        super(context);
        ds = DesignSystem.getInstance(context);
        
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(ds.spacing.lg, ds.spacing.md, ds.spacing.lg, ds.spacing.md);
        setMinimumHeight(ds.spacing.minTouchTarget);
        
        // Ripple effect
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ds.colors.surface);
        RippleDrawable ripple = new RippleDrawable(
            android.content.res.ColorStateList.valueOf(ds.colors.primary & 0x40FFFFFF),
            bg, null);
        setBackground(ripple);
        
        // Icon
        iconView = new TextView(context);
        iconView.setText(isFolder ? "📁" : "📄");
        iconView.setTextSize(20);
        iconView.setPadding(0, 0, ds.spacing.md, 0);
        addView(iconView);
        
        // Name
        nameView = new TextView(context);
        nameView.setText(name);
        nameView.setTextSize(16);
        nameView.setTextColor(ds.colors.onSurface);
        nameView.setSingleLine(true);
        nameView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        addView(nameView, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
    }
}
