package com.verbosetech.cookfu;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class OpenSansTextView extends android.support.v7.widget.AppCompatTextView {
    public OpenSansTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "opensans_regular.ttf"));
    }

    public OpenSansTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "opensans_regular.ttf"));
    }

    public OpenSansTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "opensans_regular.ttf"));
    }

}
