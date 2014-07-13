package com.misabelleeli.pacers_bikeshare;

/**
 * Created by M.Isabel on 7/13/2014.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;

public class AlertClass extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Defining an event listener for the Yes button click */
        OnClickListener positiveClick = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getBaseContext(), "Application is quitting ...", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        };

        /** Defining an event listener for the No button click */
        OnClickListener negativeClick = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getBaseContext(), "Returning to the main activity", Toast.LENGTH_SHORT).show();

            }
        };

        /** Creating a builder object for the AlertDialog*/
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        /**  Setting the message */
        b.setMessage("Do you want to quit this application ?");

        /** Setting the Negative button */
        b.setNegativeButton("No", negativeClick);

        /** Setting the Positive button */
        b.setPositiveButton("Yes", positiveClick);

        /** Setting a title for the dialog */
        b.setTitle("Confirmation");

        /** Creating the AlertDialog , from the builder object */
        Dialog d = b.create();

        /** Returning the alert window */
        return d;
    }

}