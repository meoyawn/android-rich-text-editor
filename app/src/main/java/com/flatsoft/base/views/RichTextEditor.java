package com.flatsoft.base.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import com.flatsoft.base.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by adel on 07/04/14
 */
public class RichTextEditor extends FrameLayout {
    static final String URL = "file:///android_asset/editor.html";

    static final String BOLD                  = "bold";
    static final String ITALIC                = "italic";
    static final String UNDERLINE             = "underline";
    static final String STRIKE_THROUGH        = "strikeThrough";
    static final String INSERT_ORDERED_LIST   = "insertOrderedList";
    static final String INSERT_UNORDERED_LIST = "insertUnorderedList";

    @InjectView(R.id.rich_edit_text)     WebView webView;
    @InjectView(R.id.btn_bold)           Button  boldButton;
    @InjectView(R.id.btn_italic)         Button  italicButton;
    @InjectView(R.id.btn_underline)      Button  underlineButton;
    @InjectView(R.id.btn_strike_through) Button  strikeThroughButton;
    @InjectView(R.id.btn_list)           Button  listButton;
    @InjectView(R.id.btn_bullet)         Button  bulletButton;

    @Nullable Action1<String> htmlCallback;

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new Android(), "android");
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                switch (url) {
                    case URL:
                        String html = "<ol>" +
                                "<li>first</li>" +
                                "<li>second</li>" +
                                "</ol>";
                        String code = "setHtml('" + html + "')";
                        webView.loadUrl(js(code));
                        break;
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onConsoleMessage(@NotNull ConsoleMessage consoleMessage) {
                log(consoleMessage);
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webView.loadUrl(URL);
    }

    public void getHtml(Action1<String> htmlCallback) {
        this.htmlCallback = htmlCallback;
        webView.loadUrl(js("getHtml()"));
    }

    class Android {
        @JavascriptInterface public void giveHtml(String html) {
            post(() -> {
                if (htmlCallback != null) {
                    htmlCallback.call(html);
                }
            });
        }

        @JavascriptInterface public void onSelectionChanged(boolean bold,
                                                            boolean italic,
                                                            boolean underline,
                                                            boolean strikeThrough,
                                                            boolean ordered,
                                                            boolean unOrdered) {
            toggle(boldButton, bold);
            toggle(italicButton, italic);
            toggle(underlineButton, underline);
            toggle(strikeThroughButton, strikeThrough);
            toggle(listButton, ordered);
            toggle(bulletButton, unOrdered);
        }
    }

    @OnClick(R.id.btn_bold) void onBoldClick() {
        toggle(BOLD);
    }

    @OnClick(R.id.btn_italic) void onItalicClick() {
        toggle(ITALIC);
    }

    @OnClick(R.id.btn_underline) void onUnderlineClick() {
        toggle(UNDERLINE);
    }

    @OnClick(R.id.btn_strike_through) void onStrikeThroughClick() {
        toggle(STRIKE_THROUGH);
    }

    @OnClick(R.id.btn_list) void onListClick() {
        toggle(INSERT_ORDERED_LIST);
    }

    @OnClick(R.id.btn_bullet) void onBulletClick() {
        toggle(INSERT_UNORDERED_LIST);
    }

    static void toggle(Button button, boolean active) {
        button.setTextColor(active ? Color.GREEN : Color.BLACK);
//        button.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }

    static String js(String code) {
        return "javascript:" + code + ";";
    }

    void toggle(String what) {
        webView.loadUrl(js("toggle('" + what + "')"));
    }

    static void log(ConsoleMessage consoleMessage) {
        String message = consoleMessage.message();
        switch (consoleMessage.messageLevel()) {
            case DEBUG:
            case LOG:
            case TIP:
                Timber.d(message);
                break;
            case ERROR:
                Timber.e(message);
                break;
            case WARNING:
                Timber.w(message);
                break;
        }
    }
}
