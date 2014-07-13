package com.misabelleeli.pacers_bikeshare;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AboutFragment extends Fragment {



    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView checkout_content = (TextView)getView().findViewById(R.id.checkout_content);
        TextView checkout_content2 = (TextView)getView().findViewById(R.id.checkout_content2);
        TextView checkout_content3 = (TextView)getView().findViewById(R.id.checkout_content3);
        TextView return_content = (TextView)getView().findViewById(R.id.return_content);
        TextView return_content2 = (TextView)getView().findViewById(R.id.return_content2);

        checkout_content.setText(Html.fromHtml(getResources().getString(R.string.checkout_content)));
        checkout_content2.setText(Html.fromHtml(getResources().getString(R.string.checkout_content2)));
        checkout_content3.setText(Html.fromHtml(getResources().getString(R.string.checkout_content3)));
        return_content.setText(Html.fromHtml(getResources().getString(R.string.return_content)));
        return_content2.setText(Html.fromHtml(getResources().getString(R.string.return_content2)));



    }

}
