package com.flatsoft.base.effects;

import android.text.style.BulletSpan;

import lombok.RequiredArgsConstructor;

/**
 * Created by adelnizamutdinov on 09/04/2014
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class Bullet implements Effect<BulletSpan> {
    final int gapWidth;

    @Override public Class<BulletSpan> clazz() {
        return BulletSpan.class;
    }

    @Override public BulletSpan newInstance() {
        return new BulletSpan(gapWidth);
    }

    @Override public boolean appliesTo(BulletSpan instance) {
        return true;
    }
}
