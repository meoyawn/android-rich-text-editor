package com.flatsoft.base.views;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * Created by adel on 08/04/14
 */
public class SelectableEditText extends EditText {
    @Nullable @Getter Subject<Boolean, Boolean> boldSubject = PublishSubject.create();

    public SelectableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        StyleSpan[] spans = getText().getSpans(selStart, selEnd, StyleSpan.class);
        boolean boldFound = false;
        for (StyleSpan styleSpan : spans) {
            if (styleSpan.getStyle() == BOLD) {
                int start = getText().getSpanStart(styleSpan);
                int end = getText().getSpanEnd(styleSpan);
                if (start <= selStart && end >= selEnd) {
                    boldFound = true;
                }
            }
        }
        if (boldSubject != null) {
            boldSubject.onNext(boldFound);
        }
    }

    public void toggleBold() {
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        Editable text = getText();

        StyleSpan[] spans = text.getSpans(selStart, selEnd, StyleSpan.class);
        if (spans.length > 0) {
            for (StyleSpan styleSpan : spans) {
                if (styleSpan.getStyle() == BOLD) {
                    int start = text.getSpanStart(styleSpan);
                    if (start < selStart) {
                        text.setSpan(new StyleSpan(BOLD), start, selStart, SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    int end = text.getSpanEnd(styleSpan);
                    if (end > selEnd) {
                        text.setSpan(new StyleSpan(BOLD), selEnd, end, SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    text.removeSpan(styleSpan);
                }
            }
        } else {
            int flag = selStart != selEnd ?
                    SPAN_EXCLUSIVE_INCLUSIVE :
                    SPAN_INCLUSIVE_INCLUSIVE;
            text.setSpan(new StyleSpan(BOLD), selStart, selEnd, flag);
        }
    }

    @Override @NotNull public Editable getText() {
        Editable text = super.getText();
        return text != null ? text : new SpannableStringBuilder();
    }
}
