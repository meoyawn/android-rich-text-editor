package com.flatsoft.base.views;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.util.AttributeSet;
import android.widget.EditText;

import com.flatsoft.base.effects.Effect;
import com.flatsoft.base.effects.ParagraphEffect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    protected void onTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
        Editable text = getText();
        int end = start + lengthAfter;

        // deleting last char of paragraphstyle should clear it
        if (lengthBefore > lengthAfter) {
            for (ParagraphStyle span : text.getSpans(start, end, ParagraphStyle.class)) {
                int spanStart = text.getSpanStart(span);
                int spanEnd = text.getSpanEnd(span);
                if (spanStart == spanEnd) {
                    text.removeSpan(span);
                }
            }
        }

        // newline should show the paragraphstyle
        if (text.subSequence(start, end).toString().equals("\n")) {
            if (forSome(text.getSpans(start, end, ParagraphStyle.class),
                    span -> text.getSpanEnd(span) == end)) {
                text.replace(end, end, " ", 0, 1);
                setSelection(end);
            }
        }
    }

    @Override protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        for (Effect effect : Effect.EFFECTS) {
            Class<?> clazz = effect.clazz();
            Object[] spans = getText().getSpans(selStart, selEnd, clazz);
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
        }

        T[] spans = text.getSpans(selStart, selEnd, effect.clazz());
        if (forSome(spans, effect::appliesTo)) {
            for (T span : spans) {
                if (effect.appliesTo(span)) {
                    int start = text.getSpanStart(span);
                    int end = text.getSpanEnd(span);
                    text.removeSpan(span);

                    if (start < selStart) {
                        if (false) {
                            if (!TextUtils.isEmpty(text.subSequence(start, selStart - 1))) {
                                text.setSpan(effect.newInstance(), start, selStart - 1, SPAN_INCLUSIVE_INCLUSIVE);
                            }
                        } else {
                            text.setSpan(effect.newInstance(), start, selStart, SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                    }

                    if (end > selEnd) {
                        // WHY? dont remember
                        if (false) {
                            if (!TextUtils.isEmpty(text.subSequence(selEnd + 1, end))) {
                                text.setSpan(effect.newInstance(), selEnd + 1, end, SPAN_INCLUSIVE_INCLUSIVE);
                            }
                        } else {
                            text.setSpan(effect.newInstance(), selEnd, end, SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                    }
                }
            }
        } else {
            text.setSpan(effect.newInstance(), selStart, selEnd, SPAN_INCLUSIVE_INCLUSIVE);

            // analyze and merge
            mergeSeparateSpans(text);

            if (effect instanceof ParagraphEffect) {
                if (selStart == selEnd) {
                    text.replace(selEnd, selEnd, " ", 0, 1);
                }
            }
        }

        onSelectionChanged(selStart, selEnd);
    }


    void mergeSeparateSpans(Editable text) {
        Map<Effect, List<Range>> rangeMap = new HashMap<>();
        rangeMap.put(Effect.BULLET_EFFECT, new ArrayList<>());
        rangeMap.put(Effect.ORDERED_LIST, new ArrayList<>());

        ParagraphStyle[] spans = text.getSpans(0, length(), ParagraphStyle.class);
        sort(text, spans);

        for (ParagraphStyle span : spans) {
            int spanStart = text.getSpanStart(span);
            int spanEnd = text.getSpanEnd(span);

            boolean added = false;
            List<Range> ranges = rangeMap.get(effect(rangeMap.keySet(), span.getClass()));
            for (Range range : ranges) {
                if (range.from == spanEnd) {
                    range.from = spanStart;
                    range.add(span);
                    added = true;
                }
                if (range.to == spanStart) {
                    range.to = spanEnd;
                    range.add(span);
                    added = true;
                }
            }
            if (!added) {
                ranges.add(new Range(spanStart, spanEnd, span));
            }
        }
        for (Map.Entry<Effect, List<Range>> entry : rangeMap.entrySet()) {
            for (Range range : entry.getValue()) {
                if (range.spans.size() > 1) {
                    for (ParagraphStyle span : range.spans) {
                        text.removeSpan(span);
                    }
                    text.setSpan(entry.getKey().newInstance(), range.from, range.to, SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }

    void sort(Editable text, ParagraphStyle[] spans) {
        Arrays.sort(spans, (span1, span2) -> {
            int lhs = text.getSpanStart(span1);
            int rhs = text.getSpanStart(span2);
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        });
    }

    @Nullable static Effect effect(Collection<Effect> effects, Class<?> clazz) {
        for (Effect effect : effects) {
            if (effect.clazz().equals(clazz)) {
                return effect;
            }
        }
        return null;
    }

    static class Range {
        int from;
        int to;
        List<ParagraphStyle> spans = new ArrayList<>();

        Range(int from, int to, ParagraphStyle paragraphStyle) {
            this.from = from;
            this.to = to;
            add(paragraphStyle);
        }

        void add(ParagraphStyle span) {
            spans.add(span);
        }
    }

    @NotNull @Override public Editable getText() {
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

    public static <T> boolean forSome(Iterable<T> array, Func1<T, Boolean> func1) {
        for (T item : array) {
            if (func1.call(item)) {
                return true;
            }
        }
        return false;
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
