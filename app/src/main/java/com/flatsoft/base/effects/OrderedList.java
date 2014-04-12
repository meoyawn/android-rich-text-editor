package com.flatsoft.base.effects;

import com.flatsoft.base.text.ListSpan;

import lombok.RequiredArgsConstructor;

/**
 * Created by adel on 12/04/14
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class OrderedList implements Effect<ListSpan>, ParagraphEffect {
    final int gapWidth;

    @Override public Class<ListSpan> clazz() {
        return ListSpan.class;
    }

    @Override public ListSpan newInstance() {
        return new ListSpan(gapWidth);
    }

    @Override public boolean appliesTo(ListSpan instance) {
        return true;
    }
}
