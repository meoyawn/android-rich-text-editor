package com.flatsoft.base.views;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.EditText;

import com.flatsoft.base.effects.Effect;
import com.flatsoft.base.effects.ParagraphEffect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * Created by adel on 08/04/14
 */
public class SelectableEditText extends EditText {
    static final Pattern                      NEW_LINE     = Pattern.compile("\n");
    static final MutablePair<Effect, Boolean> MUTABLE_PAIR = new MutablePair<>(null, null);

    @Nullable @Getter
    Subject<MutablePair<Effect, Boolean>, MutablePair<Effect, Boolean>> effectsSubject = PublishSubject.create();

    public SelectableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        Timber.d("");
    }

    @Override protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        for (Effect effect : Effect.EFFECTS) {
            Object[] spans = getText().getSpans(selStart, selEnd, effect.clazz());
            boolean found = false;
            for (Object span : spans) {
                if (effect.appliesTo(span)) {
                    int start = getText().getSpanStart(span);
                    int end = getText().getSpanEnd(span);
                    if (start <= selStart && end >= selEnd) {
                        found = true;
                    }
                }
            }
            if (effectsSubject != null) {
                effectsSubject.onNext(MUTABLE_PAIR.first(effect).second(found));
            }
        }
    }

    static int paragraphStart(String string, int position) {
        int lastIndex = string.lastIndexOf("\n", string.length() > position && string.charAt(position) != '\n' ?
                position :
                position - 1);
        return lastIndex < position ?
                lastIndex + 1 :
                position;
    }

    static int paragraphEnd(String string, int position) {
        int indexOf = string.indexOf("\n", position);
        return indexOf != -1 ? indexOf : string.length();
    }

    public <T> void toggleOnCurrentSelection(Effect<T> effect) {
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        Editable text = getText();

        if (effect instanceof ParagraphEffect) {
            String fullText = text.toString();
            selStart = paragraphStart(fullText, selStart);
            selEnd = paragraphEnd(fullText, selEnd);

            int min = Math.min(selStart, selEnd);
            int max = Math.max(selStart, selEnd);
            selStart = min;
            selEnd = max;
        }

        T[] spans = text.getSpans(selStart, selEnd, effect.clazz());
        if (forSome(spans, effect::appliesTo)) {
            for (T span : spans) {
                if (effect.appliesTo(span)) {
                    int start = text.getSpanStart(span);
                    if (start < selStart) {
                        text.setSpan(effect.newInstance(), start, selStart, SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    int end = text.getSpanEnd(span);
                    if (end > selEnd) {
                        if (text.charAt(selEnd) == '\n') {
                            text.setSpan(effect.newInstance(), selEnd + 1, end, SPAN_INCLUSIVE_INCLUSIVE);
                        } else {
                            text.setSpan(effect.newInstance(), selEnd, end, SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                    }

                    text.removeSpan(span);
                }
            }
        } else {
            text.setSpan(effect.newInstance(), selStart, selEnd, SPAN_INCLUSIVE_INCLUSIVE);
        }

        onSelectionChanged(selStart, selEnd);
    }

    @Override @NotNull public Editable getText() {
        Editable text = super.getText();
        return text != null ? text : new SpannableStringBuilder();
    }

    @Data
    @Accessors(fluent = true)
    @AllArgsConstructor(suppressConstructorProperties = true)
    public static class MutablePair<F, S> {
        F first;
        S second;
    }

    static <T> boolean forAll(T[] array, Func1<T, Boolean> func1) {
        for (T item : array) {
            if (!func1.call(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean forSome(T[] array, Func1<T, Boolean> func1) {
        for (T item : array) {
            if (func1.call(item)) {
                return true;
            }
        }
        return false;
    }
}
