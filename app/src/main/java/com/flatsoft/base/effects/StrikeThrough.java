package com.flatsoft.base.effects;

import android.text.style.StrikethroughSpan;

/**
 * Created by adelnizamutdinov on 09/04/2014
 */
public class StrikeThrough implements Effect<StrikethroughSpan> {
    @Override public Class<StrikethroughSpan> clazz() {
        return StrikethroughSpan.class;
    }

    @Override public StrikethroughSpan newInstance() {
        return new StrikethroughSpan();
    }

    @Override public boolean appliesTo(StrikethroughSpan instance) {
        return true;
    }
}
