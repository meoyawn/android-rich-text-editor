package com.flatsoft.base.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
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
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by adel on 07/04/14
 */
public class RichTextEditor extends FrameLayout {
    final StringBuilder pendingJs = new StringBuilder();

    @InjectView(R.id.rich_edit_text)     WebView webView;
    @InjectView(R.id.button_bar)         View    buttonBar;
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
        if (isInEditMode()) {
            return;
        }

        ButterKnife.inject(this);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "android");
        webView.loadUrl("file:///android_asset/editor.html");
    }

    public void getHtml(@NotNull Action1<String> htmlCallback) {
        this.htmlCallback = htmlCallback;
        webView.loadUrl(js("getHtml()"));
    }

    @NotNull public Observable<String> getHtml() {
        return Observable.create((Subscriber<? super String> subscriber) ->
                getHtml(str -> {
                    subscriber.onNext(str);
                    subscriber.onCompleted();
                }));
    }

    public void setHtml(@NotNull String html) {
        webView.loadUrl(js("setHtml('" + html + "')"));
    }

    @OnClick(R.id.btn_bold) void onBoldClick() { toggle("bold"); }

    @OnClick(R.id.btn_italic) void onItalicClick() { toggle("italic"); }

    @OnClick(R.id.btn_underline) void onUnderlineClick() { toggle("underline"); }

    @OnClick(R.id.btn_strike_through) void onStrikeThroughClick() { toggle("strikeThrough"); }

    @OnClick(R.id.btn_list) void onListClick() { toggle("insertOrderedList"); }

    @OnClick(R.id.btn_bullet) void onBulletClick() { toggle("insertUnorderedList"); }

    void addPendingJavaScript(@NotNull String script) {
        synchronized (pendingJs) {
            pendingJs.append(script).append(';');
        }
    }

    class JSInterface {
        @JavascriptInterface public void giveHtml(String html) {
            post(() -> {
                if (htmlCallback != null) {
                    htmlCallback.call(html);
                    htmlCallback = null;
                }
            });
        }

        @JavascriptInterface
        public void onSelectionChanged(boolean bold, boolean italic, boolean underline,
                                       boolean strikeThrough, boolean ordered, boolean unOrdered) {
            post(() -> {
                toggle(boldButton, bold);
                toggle(italicButton, italic);
                toggle(underlineButton, underline);
                toggle(strikeThroughButton, strikeThrough);
                toggle(listButton, ordered);
                toggle(bulletButton, unOrdered);
            });
        }

        @JavascriptInterface public String getPendingJavaScript() {
            synchronized (pendingJs) {
                if (pendingJs.length() > 0) {
                    String pendingCommands = pendingJs.toString();
                    pendingJs.setLength(0);
                    return pendingCommands;
                }
                return null;
            }
        }

        @JavascriptInterface public void onFocusChanged(boolean focused) {
            post(() -> buttonBar.setVisibility(focused ? VISIBLE : GONE));
        }
    }

    static void toggle(@NotNull Button button, boolean active) {
        button.setTextColor(active ? Color.GREEN : Color.BLACK);
        button.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }

    void toggle(@NotNull String what) {
        addPendingJavaScript("toggle('" + what + "')");
    }

    static String js(@NotNull String script) {
        return "javascript:" + script + ";";
    }
}
