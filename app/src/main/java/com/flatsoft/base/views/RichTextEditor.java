package com.flatsoft.base.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.flatsoft.base.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.subjects.Subject;

/**
 * Created by adel on 07/04/14
 */
public class RichTextEditor extends FrameLayout {
    @InjectView(R.id.rich_edit_text)     SelectableEditText editText;
    @InjectView(R.id.btn_bold)           Button             boldButton;
    @InjectView(R.id.btn_italic)         Button             italicButton;
    @InjectView(R.id.btn_underline)      Button             underlineButton;
    @InjectView(R.id.btn_strike_through) Button             strikeThroughButton;
    @InjectView(R.id.btn_list)           Button             listButton;
    @InjectView(R.id.btn_bullet)         Button             bulletButton;

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        if (editText.getBoldSubject() != null) {
            editText.getBoldSubject().subscribe(bool -> toggle(boldButton, bool));
        }
    }

    @OnClick(R.id.btn_bold) void onBoldClick(Button button) {
        editText.toggleBold();
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
        button.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }
}
