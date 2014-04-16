package com.flatsoft.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by adelnizamutdinov on 16/04/2014
 */
public class RichEditorView extends WebView {
    @NotNull final StringBuilder pendingJs = new StringBuilder();
    @NotNull  RichEditorEventBus eventBus;
    @Nullable Action1<String>    htmlCallback;

    public RichEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        setWebViewClient(new WebViewClient());
        setWebChromeClient(new WebChromeClient());
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new JSInterface(), "android");
        loadUrl("file:///android_asset/editor.html");
    }

    class JSInterface {
        @JavascriptInterface public void giveHtml(final String html) {
            post(new Runnable() {
                @Override public void run() {
                    if (htmlCallback != null) {
                        htmlCallback.call(html);
                        htmlCallback = null;
                    }
                }
            });
        }

        @JavascriptInterface
        public void onSelectionChanged(final String what, final boolean active) {
            post(new Runnable() {
                @Override public void run() {
                    eventBus.highLightSubject.onNext(Pair.create(what, active));
                }
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

        @JavascriptInterface public void onFocusChanged(final boolean focused) {
            post(new Runnable() {
                @Override public void run() {
                    eventBus.editorFocusSubject.onNext(focused);
                }
            });
        }
    }

    @NotNull public Observable<String> getHtml() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(final Subscriber<? super String> subscriber) {
                getHtml(new Action1<String>() {
                    @Override public void call(String s) {
                        subscriber.onNext(s);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    private void getHtml(@NotNull Action1<String> htmlCallback) {
        this.htmlCallback = htmlCallback;
        loadUrl(js("getHtml()"));
    }

    public void setHtml(@NotNull String html) {
        loadUrl(js("setHtml('" + html + "')"));
    }

    static String js(@NotNull String script) {
        return "javascript:" + script + ";";
    }

    void addPendingJavaScript(@NotNull String script) {
        synchronized (pendingJs) {
            pendingJs.append(script).append(';');
        }
    }

    public void setEventBus(@NotNull RichEditorEventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.toggleSubject.subscribe(new Action1<String>() {
            @Override public void call(String style) {
                addPendingJavaScript("toggle('" + style + "')");
            }
        });
    }
}
