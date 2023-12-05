package algonquin.cst2335.team_project.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.nguy1041.R;
import algonquin.cst2335.team_project.WordMeaningActivity;

public class FragmentExample extends Fragment {
    public FragmentExample() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition,container, false);//Inflate Layout

        Context context=getActivity();

        TextView text = (TextView) view.findViewById(R.id.textViewD);//Find textView Id

        String example= ((WordMeaningActivity)context).example;
        text.setText(example);

        if(example==null)
        {
            text.setText("No Example found");
        }


        return view;
    }
}
