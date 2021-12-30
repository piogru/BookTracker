package com.example.booktracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FinishConfirmationDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BookDetailsActivity activity = (BookDetailsActivity) getActivity();
        return new AlertDialog.Builder(requireContext())
            .setTitle(R.string.book_finish_title)
            .setMessage(getString(R.string.book_finish_message))
            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                Intent replyIntent = new Intent();
                activity.setResult(200, replyIntent);
                activity.finish();
            } )
            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {} )
            .create();
    }

    public static String TAG = "PurchaseConfirmationDialog";
}