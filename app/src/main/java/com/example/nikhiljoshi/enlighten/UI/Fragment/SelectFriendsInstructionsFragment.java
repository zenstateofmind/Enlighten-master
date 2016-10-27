package com.example.nikhiljoshi.enlighten.ui.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.ui.Activity.SelectFriendsActivity;

/**
 * This is the fragment that represents the screen that
 * instructs the users to select their friends whose tweet articles
 * they are interested in
 */
public class SelectFriendsInstructionsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_friends_instructions, container, false);

        Button instructionButton = (Button) rootView.findViewById(R.id.instructions_ok_button);
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectFriendsActivity) getActivity()).onSelected();
            }
        });

        return rootView;
    }

    public interface Callback {
        void onSelected();
    }
}