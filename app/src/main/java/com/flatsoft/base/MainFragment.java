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

import com.flatsoft.base.views.RichTextEditor;

import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by adelnizamutdinov on 03/03/2014
 */
public class MainFragment extends Fragment {
    @Nullable @InjectView(R.id.rich_editor) RichTextEditor richTextEditor;

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
    }

    @Override public void onDestroyView() {
        if (richTextEditor != null) {
            richTextEditor.getHtml(html -> {
                this.html = html;
                if (richTextEditor != null) {
                    richTextEditor.setHtml(html);
                }
            });
        }
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("HTML")
                .setOnMenuItemClickListener(item -> {
                    if (richTextEditor != null) {
                        richTextEditor.getHtml(html -> {
                            if (getActivity() != null) {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(html)
                                        .show();
                            }
                        });
                    }
                    return true;
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
