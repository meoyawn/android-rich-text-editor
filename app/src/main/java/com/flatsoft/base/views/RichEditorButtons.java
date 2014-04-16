package com.flatsoft.base.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flatsoft.base.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by adelnizamutdinov on 16/04/2014
 */
public class RichEditorButtons extends LinearLayout {
    static final String BOLD                  = "bold";
    static final String ITALIC                = "italic";
    static final String UNDERLINE             = "underline";
    static final String STRIKE_THROUGH        = "strikeThrough";
    static final String INSERT_ORDERED_LIST   = "insertOrderedList";
    static final String INSERT_UNORDERED_LIST = "insertUnorderedList";

    @InjectView(R.id.btn_bold)           Button boldButton;
    @InjectView(R.id.btn_italic)         Button italicButton;
    @InjectView(R.id.btn_underline)      Button underlineButton;
    @InjectView(R.id.btn_strike_through) Button strikeThroughButton;
    @InjectView(R.id.btn_list)           Button listButton;
    @InjectView(R.id.btn_bullet)         Button bulletButton;

    @NotNull RichEditorEventBus  eventBus;
    @NotNull Map<String, Button> buttonMap;

    public RichEditorButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        buttonMap = new HashMap<>(6);
        buttonMap.put(BOLD, boldButton);
        buttonMap.put(ITALIC, italicButton);
        buttonMap.put(UNDERLINE, underlineButton);
        buttonMap.put(STRIKE_THROUGH, strikeThroughButton);
        buttonMap.put(INSERT_ORDERED_LIST, listButton);
        buttonMap.put(INSERT_UNORDERED_LIST, bulletButton);
    }

    public void setEventBus(@NotNull RichEditorEventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.editorFocusSubject.subscribe(new Action1<Boolean>() {
            @Override public void call(Boolean aBoolean) {
                setVisibility(aBoolean ? VISIBLE : GONE);
            }
        });
        eventBus.highLightSubject.subscribe(new Action1<Pair<String, Boolean>>() {
            @Override public void call(Pair<String, Boolean> stringBooleanPair) {
                boolean active = stringBooleanPair.second;
                Button button = buttonMap.get(stringBooleanPair.first);
                highlight(button, active);
            }
        });
    }

    static void highlight(@NotNull Button button, boolean active) {
        button.setTextColor(active ? Color.GREEN : Color.BLACK);
        button.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }

    void toggle(String what) {
        eventBus.toggleSubject.onNext(what);
    }

    @OnClick(R.id.btn_bold) void onBoldClick() { toggle(BOLD); }

    @OnClick(R.id.btn_italic) void onItalicClick() { toggle(ITALIC); }

    @OnClick(R.id.btn_underline) void onUnderlineClick() { toggle(UNDERLINE); }

    @OnClick(R.id.btn_strike_through) void onStrikeThroughClick() { toggle(STRIKE_THROUGH); }

    @OnClick(R.id.btn_list) void onListClick() { toggle(INSERT_ORDERED_LIST); }

    @OnClick(R.id.btn_bullet) void onBulletClick() { toggle(INSERT_UNORDERED_LIST); }
}
