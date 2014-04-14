package com.flatsoft.base.views;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;

import com.flatsoft.base.effects.Effect;

import org.jetbrains.annotations.Nullable;
import org.xml.sax.XMLReader;

public class MyTagHandler implements Html.TagHandler {
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("ul")) {
            if (opening) {
                start(output, new Ul());
            } else {
                end(output, Ul.class, Effect.BULLET_EFFECT.newInstance());
            }
        } else if (tag.equalsIgnoreCase("ol")) {
            if (opening) {
                start(output, new Ol());
            } else {
                end(output, Ol.class, Effect.ORDERED_LIST.newInstance());
            }
        }
    }

    private static void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
    }

    private static void end(Editable text, Class<?> kind, Object... replaces) {
        int len = text.length();
        @Nullable Object obj = getLast(text, kind);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        if (where != len) {
            for (Object replace : replaces) {
                text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    @Nullable private static Object getLast(Spanned text, Class<?> kind) {
        /*
         * This knows that the last returned object from getSpans()
		 * will be the most recently added.
		 */
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        }
        return objs[objs.length - 1];
    }

    private static class Ul {}

    private static class Ol {}
}