package com.flatsoft.base.views;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.MyBulletSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.flatsoft.base.effects.Bullet;
import com.flatsoft.base.effects.Effect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.flatsoft.base.effects.Effect.BULLET;

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
        return string.lastIndexOf("\n", position) + 1;
    }

    public <T> void toggleOnCurrentSelection(Effect<T> effect) {
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        Editable text = getText();

        T[] spans = text.getSpans(selStart, selEnd, effect.clazz());
        if (forSome(spans, effect::appliesTo)) {
            for (T span : spans) {
                if (effect.appliesTo(span)) {
                    if (!(effect instanceof Bullet)) {
                        int start = text.getSpanStart(span);
                        if (start < selStart) {
                            text.setSpan(effect.newInstance(), start, selStart, SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        int end = text.getSpanEnd(span);
                        if (end > selEnd) {
                            text.setSpan(effect.newInstance(), selEnd, end, SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    text.removeSpan(span);
                }
            }
        } else {
            if (effect instanceof Bullet) {
                String fullText = text.toString();
                int paragraphStart = paragraphStart(fullText, selStart);
                if (selStart != selEnd) {
                    String[] paragraphs = NEW_LINE.split(text.subSequence(paragraphStart, selEnd));
                    int lastStart = paragraphStart;
                    for (String paragraph : paragraphs) {
                        int start = fullText.indexOf(paragraph, lastStart);
                        int end = start + paragraph.length();
                        if (end > selEnd) {
                            break;
                        }
                        text.setSpan(effect.newInstance(), start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
                        lastStart = end;
                    }
                } else {
                    text.setSpan(effect.newInstance(), paragraphStart, selEnd, SPAN_EXCLUSIVE_INCLUSIVE);
                }
            } else if (effect instanceof List) {
                // TODO
            } else {
                int flag = selStart != selEnd ? SPAN_EXCLUSIVE_INCLUSIVE : SPAN_INCLUSIVE_INCLUSIVE;
                text.setSpan(effect.newInstance(), selStart, selEnd, flag);
            }
        }

        onSelectionChanged(selStart, selEnd);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        int end = start + lengthAfter;
        String change = String.valueOf(text.subSequence(start, end));
        if (end - start == 1) {
            Editable editable = getText();
            MyBulletSpan[] spans = editable.getSpans(start, start, BULLET.clazz());
            if (change.equals("\n") && spans.length > 0) {
                Timber.d("has %d spans", spans.length);
                MyBulletSpan span = spans[0];
                int editableStart = editable.getSpanStart(span);
                int editableEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                editable.setSpan(BULLET.newInstance(), editableStart, editableEnd - 1, SPAN_INCLUSIVE_EXCLUSIVE);
                editable.setSpan(BULLET.newInstance(), end, end, SPAN_INCLUSIVE_EXCLUSIVE);
            }
        } else {

        }
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

    static <T> boolean forSome(T[] array, Func1<T, Boolean> func1) {
        for (T item : array) {
            if (func1.call(item)) {
                return true;
            }
        }
        return false;
    }
}
