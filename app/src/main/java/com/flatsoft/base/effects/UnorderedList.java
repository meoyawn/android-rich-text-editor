package com.flatsoft.base.effects;

import com.flatsoft.base.text.ParaBulletSpan;

import lombok.RequiredArgsConstructor;

/**
 * Created by adelnizamutdinov on 09/04/2014
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class UnorderedList implements Effect<ParaBulletSpan>, ParagraphEffect {
    final int gapWidth;

    @Override public Class<ParaBulletSpan> clazz() {
        return ParaBulletSpan.class;
    }

    @Override public ParaBulletSpan newInstance() {
        return new ParaBulletSpan(gapWidth);
    }

    @Override public boolean appliesTo(ParaBulletSpan instance) {
        return true;
    }
}
