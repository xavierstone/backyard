package com.xavierstone.backyard.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.xavierstone.backyard.R;

public class SignInDialogFragment extends DialogFragment {

    public interface SignInDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String email, String password);
        public void onDialogNeutralClick(DialogFragment dialog, String email, String password);
    }

    // Use this instance of the interface to deliver action events
    SignInDialogListener listener;
    View dialogView = null;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (SignInDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SignInDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_sign_in, null);
        final EditText emailField = dialogView.findViewById(R.id.username);
        final EditText passwordField = dialogView.findViewById(R.id.password);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.signButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(SignInDialogFragment.this, emailField.getText().toString(), passwordField.getText().toString());
                    }
                })
                .setNeutralButton(R.string.createAccount, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNeutralClick(SignInDialogFragment.this, emailField.getText().toString(), passwordField.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SignInDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void switchDialog(View view){
        EditText nameField = dialogView.findViewById(R.id.name);
        nameField.setVisibility(View.VISIBLE);
    }

}
