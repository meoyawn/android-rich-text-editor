package com.flatsoft.base.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flatsoft.base.App;
import com.flatsoft.base.R;

import org.jetbrains.annotations.Nullable;

/**
 * Created by adelnizamutdinov on 03/03/2014
 */
public class MainFragment extends Fragment {
    @Override public void onStart() {
        super.onStart();
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setHomeButtonEnabled(false);
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
            getActivity().getActionBar().setTitle(R.string.app_name);
        }
        App.gaSendScreen(getActivity(), "Main Fragment");
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editor, container, false);
    }
}
