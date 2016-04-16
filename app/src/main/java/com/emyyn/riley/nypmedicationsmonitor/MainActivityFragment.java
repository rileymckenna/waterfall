package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_start, container, false);

        Button login = (Button) rootview.findViewById(R.id.b_Login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });

        try {
            Thread.sleep(4000);
            Intent intent = new Intent(getActivity(), NavigationActivity.class);
            getActivity().startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rootview;
    }
}
