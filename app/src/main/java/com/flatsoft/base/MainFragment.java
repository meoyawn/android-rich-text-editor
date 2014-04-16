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

import com.flatsoft.base.views.RichEditorButtons;
import com.flatsoft.base.views.RichEditorEventBus;
import com.flatsoft.base.views.RichEditorView;

import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

/**
 * Created by adelnizamutdinov on 03/03/2014
 */
public class MainFragment extends Fragment {
    @InjectView(R.id.rich_edit_text) RichEditorView    richEditorView;
    @InjectView(R.id.button_bar)     RichEditorButtons richEditorButtons;

    @Nullable String html;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editor, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        RichEditorEventBus eventBus = new RichEditorEventBus();
        richEditorView.setEventBus(eventBus);
        richEditorButtons.setEventBus(eventBus);

        richEditorView.setHtml("<ul>\n" +
                "<li>first</li>\n" +
                "<li>second</li>\n" +
                "<li>third</li>\n" +
                "</ul>");
    }

    @Override public void onDestroyView() {
        richEditorView.getHtml().subscribe(new Action1<String>() {
            @Override public void call(String html) {
                MainFragment.this.html = html;
                if (richEditorView != null) {
                    richEditorView.setHtml(html);
                }
            }
        });
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("HTML")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        if (richEditorView != null) {
                            richEditorView.getHtml().subscribe(new Action1<String>() {
                                @Override public void call(String html) {
                                    if (getActivity() != null) {
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage(html)
                                                .show();
                                    }
                                }
                            });
                        }
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
