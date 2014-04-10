package com.flatsoft.base.effects;

import android.text.style.UnderlineSpan;

/**
* Created by adelnizamutdinov on 09/04/2014
*/
public class Underline implements Effect<UnderlineSpan> {
    @Override public Class<UnderlineSpan> clazz() {
        return UnderlineSpan.class;
    }

    @Override public UnderlineSpan newInstance() {
        return new UnderlineSpan();
    }

    @Override public boolean appliesTo(UnderlineSpan instance) {
        return true;
    }
}
