package com.verbosetech.cookfu.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by a_man on 21-03-2018.
 */

public class BaseFragment extends Fragment {
    public void toast(String message, boolean isShort) {
        if (getActivity() != null && getActivity().getApplicationContext() != null)
            Toast.makeText(getActivity().getApplicationContext(), message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }
}
