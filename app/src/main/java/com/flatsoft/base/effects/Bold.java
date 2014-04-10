package com.flatsoft.base.effects;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

/**
 * Created by adelnizamutdinov on 09/04/2014
 */
public class Bold implements Effect<StyleSpan> {
    @Override public Class<StyleSpan> clazz() {
        return StyleSpan.class;
    }

    @Override public StyleSpan newInstance() {
        return new StyleSpan(Typeface.BOLD);
    }

    @Override public boolean appliesTo(StyleSpan instance) {
        int style = instance.getStyle();
        return style == Typeface.BOLD || style == Typeface.BOLD_ITALIC;
    }
}
