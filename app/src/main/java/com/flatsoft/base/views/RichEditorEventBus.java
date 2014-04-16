package com.flatsoft.base.views;

import android.util.Pair;

import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by adelnizamutdinov on 16/04/2014
 */
public class RichEditorEventBus {
    Subject<Pair<String, Boolean>,
            Pair<String, Boolean>> highLightSubject   = PublishSubject.create();
    Subject<Boolean, Boolean>      editorFocusSubject = PublishSubject.create();
    Subject<String, String>        toggleSubject      = PublishSubject.create();
}
