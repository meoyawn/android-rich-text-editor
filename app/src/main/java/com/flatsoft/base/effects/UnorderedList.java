package com.flatsoft.base.effects;

import com.flatsoft.base.text.MyBulletSpan;

import lombok.RequiredArgsConstructor;

/**
 * Created by adelnizamutdinov on 09/04/2014
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class UnorderedList implements Effect<MyBulletSpan>, ParagraphEffect {
    final int gapWidth;

    @Override public Class<MyBulletSpan> clazz() {
        return MyBulletSpan.class;
    }

    @Override public MyBulletSpan newInstance() {
        return new MyBulletSpan(gapWidth);
    }

    @Override public boolean appliesTo(MyBulletSpan instance) {
        return true;
    }
}
