package com.flatsoft.base.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import lombok.RequiredArgsConstructor;

/**
 * Created by adel on 12/04/14
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class ListSpan implements LeadingMarginSpan {
    final int gapWidth;
    float textWidth;
    int   count;

    @Override public int getLeadingMargin(boolean first) {
        return (int) (gapWidth + textWidth);
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
                                  int bottom, CharSequence text, int start, int end, boolean first,
                                  Layout layout) {
        if (top == 0) {
            count = 0;
        }

        c.save();
        try {
            c.drawText(++count + ".", x, baseline, p);
            c.translate(gapWidth / 2f, 0);
        } finally {
            c.restore();
        }

        if (textWidth == 0f) {
            textWidth = p.measureText("55.");
        }
    }
}
