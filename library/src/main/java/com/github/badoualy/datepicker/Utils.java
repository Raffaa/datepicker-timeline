package com.github.badoualy.datepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

final class Utils {

    static int getPrimaryColor(final Context context) {
        int color = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        if (color != 0) {
            // If using support library v7 primaryColor
            TypedValue t = new TypedValue();
            context.getTheme().resolveAttribute(color, t, true);
            color = t.data;
        } else {
            // If using native primaryColor (SDK >21)
            TypedArray t = context.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
            color = t.getColor(0, ContextCompat.getColor(context, R.color.mti_default_primary));
            t.recycle();
        }

        return color;
    }

    static int getPrimaryDarkColor(final Context context) {
        int color = context.getResources().getIdentifier("colorPrimaryDark", "attr", context.getPackageName());
        if (color != 0) {
            // If using support library v7 primaryColor
            TypedValue t = new TypedValue();
            context.getTheme().resolveAttribute(color, t, true);
            color = t.data;
        } else {
            // If using native primaryColor (SDK >21)
            TypedArray t = context.obtainStyledAttributes(new int[]{android.R.attr.colorPrimaryDark});
            color = t.getColor(0, ContextCompat.getColor(context, R.color.mti_default_primary_dark));
            t.recycle();
        }

        return color;
    }
}
