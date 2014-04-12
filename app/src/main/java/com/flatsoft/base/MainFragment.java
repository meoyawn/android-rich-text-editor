package com.flatsoft.base;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ParagraphStyle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.flatsoft.base.text.SpannedHtml;
import com.flatsoft.base.views.SelectableEditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.flatsoft.base.views.SelectableEditText.forSome;

/**
 * Created by adelnizamutdinov on 03/03/2014
 */
public class MainFragment extends Fragment {
    @NotNull @InjectView(R.id.rich_edit_text) SelectableEditText richEditText;

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
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("HTML")
                .setOnMenuItemClickListener(item -> {
                    if (getActivity() != null) {
                        WebView webView = new WebView(getActivity());
                        webView.loadData(SpannedHtml.toHtml(richEditText.getText()), "text/html", "utf-8");

                        new AlertDialog.Builder(getActivity())
                                .setView(webView)
                                .show();
                    }
                    return true;
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("DEBUG")
                .setOnMenuItemClickListener(item -> {
                    Editable text = richEditText.getText();
                    Object[] spans = text.getSpans(0, richEditText.length(), Object.class);
                    if (forSome(spans, span -> span instanceof BackgroundColorSpan)) {
                        for (BackgroundColorSpan span : text.getSpans(0, richEditText.length(), BackgroundColorSpan.class)) {
                            text.removeSpan(span);
                        }
                    } else {
                        for (Object o : spans) {
                            if (o instanceof ParagraphStyle) {
                                int start = text.getSpanStart(o);
                                int end = text.getSpanEnd(o);
                                int flags = text.getSpanFlags(o);
                                text.setSpan(new BackgroundColorSpan(System.identityHashCode(o)), start, end, flags);
                            }
                        }
                    }
                    return true;
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
