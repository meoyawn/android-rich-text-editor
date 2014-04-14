package com.flatsoft.base.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.style.LeadingMarginSpan;

public class MyBulletSpan implements LeadingMarginSpan, ParcelableSpan {
    private final int     mGapWidth;
    private final boolean mWantColor;
    private final int     mColor;

    private static final int  BULLET_RADIUS      = 5;
    private static       Path sBulletPath        = null;
    public static final  int  STANDARD_GAP_WIDTH = 2;

    public MyBulletSpan() {
        mGapWidth = STANDARD_GAP_WIDTH;
        mWantColor = false;
        mColor = 0;
    }

    public MyBulletSpan(int gapWidth) {
        mGapWidth = gapWidth;
        mWantColor = false;
        mColor = 0;
    }

    public MyBulletSpan(int gapWidth, int color) {
        mGapWidth = gapWidth;
        mWantColor = true;
        mColor = color;
    }

    public MyBulletSpan(Parcel src) {
        mGapWidth = src.readInt();
        mWantColor = src.readInt() != 0;
        mColor = src.readInt();
    }

    public int getSpanTypeId() {
        return 8;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mGapWidth);
        dest.writeInt(mWantColor ? 1 : 0);
        dest.writeInt(mColor);
    }

    public int getLeadingMargin(boolean first) {
        return 4 * mGapWidth + 2 * BULLET_RADIUS + mGapWidth;
    }

    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        Paint.Style style = p.getStyle();
        int oldcolor = 0;

        if (mWantColor) {
            oldcolor = p.getColor();
            p.setColor(mColor);
        }

        p.setStyle(Paint.Style.FILL);

        if (c.isHardwareAccelerated()) {
            if (sBulletPath == null) {
                sBulletPath = new Path();
                // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                sBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Direction.CW);
            }

            c.save();
            c.translate(4 * mGapWidth + x + dir * BULLET_RADIUS, (top + bottom) / 2.0f);
            c.drawPath(sBulletPath, p);
            c.restore();
        } else {
            c.drawCircle(4 * mGapWidth + x + dir * BULLET_RADIUS, (top + bottom) / 2.0f, BULLET_RADIUS, p);
        }

        if (mWantColor) {
            p.setColor(oldcolor);
        }

        p.setStyle(style);
    }
}
