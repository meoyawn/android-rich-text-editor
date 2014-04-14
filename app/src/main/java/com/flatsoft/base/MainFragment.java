package com.flatsoft.base;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by adelnizamutdinov on 03/03/2014
 */
public class MainFragment extends Fragment {
    static final String JAVASCRIPT = "javascript:";
    static final String EDITOR_URL = "file:///android_asset/editor.html";

    @InjectView(R.id.rich_edit_text) WebView webView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editor, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new Android(), "android");

        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                switch (url) {
                    case EDITOR_URL:
                        String html = "<ul>\n" +
                                "<li>first</li>\n" +
                                "<li>second</li>\n" +
                                "</ul>";
                        webView.loadUrl(js("setHtml(" + html + ")"));
                        break;
                }
            }
        });

        webView.loadUrl(EDITOR_URL);
    }

    class Android {
        @JavascriptInterface public void giveHtml(String html) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(html)
                            .show();
                });
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("HTML")
                .setOnMenuItemClickListener(item -> {
                    webView.loadUrl(js("getHtml()"));
                    return true;
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    static String js(String code) {
        return "javascript:" + code + ";";
    }
}
