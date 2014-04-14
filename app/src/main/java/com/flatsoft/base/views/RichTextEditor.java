package com.flatsoft.base.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.flatsoft.base.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by adel on 07/04/14
 */
public class RichTextEditor extends FrameLayout {
    @InjectView(R.id.rich_edit_text)     WebView editText;
    @InjectView(R.id.btn_bold)           Button  boldButton;
    @InjectView(R.id.btn_italic)         Button  italicButton;
    @InjectView(R.id.btn_underline)      Button  underlineButton;
    @InjectView(R.id.btn_strike_through) Button  strikeThroughButton;
    @InjectView(R.id.btn_list)           Button  listButton;
    @InjectView(R.id.btn_bullet)         Button  bulletButton;

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_bold) void onBoldClick(Button button) {
    }

    @OnClick(R.id.btn_italic) void onItalicClick(Button button) {
    }

    @OnClick(R.id.btn_underline) void onUnderlineClick(Button button) {
    }

    @OnClick(R.id.btn_strike_through) void onStrikeThroughClick(Button button) {
    }

    @OnClick(R.id.btn_list) void onListClick(Button button) {
    }

    @OnClick(R.id.btn_bullet) void onBulletClick(Button button) {
    }

    static void toggle(Button button, boolean active) {
        button.setTextColor(active ? Color.GREEN : Color.BLACK);
//        button.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }
}
