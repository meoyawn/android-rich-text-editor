package com.flatsoft.base.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.flatsoft.base.R;
import com.flatsoft.base.effects.Bold;
import com.flatsoft.base.effects.Effect;
import com.flatsoft.base.effects.Italic;
import com.flatsoft.base.effects.OrderedList;
import com.flatsoft.base.effects.StrikeThrough;
import com.flatsoft.base.effects.Underline;
import com.flatsoft.base.effects.UnorderedList;
import com.flatsoft.base.text.SpannedHtml;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

        if (editText.getEffectsSubject() != null) {
            editText.getEffectsSubject().subscribe(pair -> {
                Effect effect = pair.first();
                boolean active = pair.second();

                if (effect instanceof Bold) {
                    toggle(boldButton, active);
                } else if (effect instanceof Italic) {
                    toggle(italicButton, active);
                } else if (effect instanceof Underline) {
                    toggle(underlineButton, active);
                } else if (effect instanceof StrikeThrough) {
                    toggle(strikeThroughButton, active);
                } else if (effect instanceof UnorderedList) {
                    toggle(bulletButton, active);
                } else if (effect instanceof OrderedList) {
                    toggle(listButton, active);
                }
            });
        }

        editText.setText(SpannedHtml.fromHtml("<ul>\n" +
                "<li>first</li>\n" +
                "<li>second</li>\n" +
                "</ul>\n" +
                "<ol>\n" +
                "<li>first</li>\n" +
                "<li>second</li>\n" +
                "</ol>"));
    }

    @OnClick(R.id.btn_bold) void onBoldClick(Button button) {
        editText.toggleOnCurrentSelection(Effect.BOLD);
    }

    @OnClick(R.id.btn_italic) void onItalicClick(Button button) {
        editText.toggleOnCurrentSelection(Effect.ITALIC);
    }

    @OnClick(R.id.btn_underline) void onUnderlineClick(Button button) {
        editText.toggleOnCurrentSelection(Effect.UNDERLINE);
    }

    @OnClick(R.id.btn_strike_through) void onStrikeThroughClick(Button button) {
        editText.toggleOnCurrentSelection(Effect.STRIKE_THROUGH);
    }

    @OnClick(R.id.btn_list) void onListClick(Button button) {
        editText.toggleOnCurrentSelection(Effect.ORDERED_LIST);
    }

    @OnClick(R.id.btn_bullet) void onBulletClick(Button button) {
        editText.toggleOnCurrentSelection(Effect.BULLET_EFFECT);
    }

    static void toggle(Button button, boolean active) {
        button.setTextColor(active ? Color.GREEN : Color.BLACK);
//        button.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }
}
