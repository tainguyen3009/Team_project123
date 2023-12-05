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

public class FragmentDefinition extends Fragment {
    public FragmentDefinition() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition,container, false);//Inflate Layout


        Context context=getActivity();
        TextView text = (TextView) view.findViewById(R.id.textViewD);

        String en_definition= ((WordMeaningActivity)context).enDefinition;

        text.setText(en_definition);
        if(en_definition==null)
        {
            text.setText("No definition found");
        }

        return view;
    }
}
