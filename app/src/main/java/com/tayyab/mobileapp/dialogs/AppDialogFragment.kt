package com.tayyab.mobileapp.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tayyab.mobileapp.activities.AuthActivity
import com.tayyab.mobileapp.utils.AppSettings

class AppDialogFragment : DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            // Add customization options here
            .setTitle("Logout")
// Supporting text
            .setMessage("Do you want to Logout?")
            // Confirming action
            .setPositiveButton("Yes") { dialog, which ->
                var appSettings: AppSettings = AppSettings(requireContext())
                appSettings.saveLoggedIn(false)
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                dismiss()
                // Do something for button click
            }
// Dismissive action
            .setNegativeButton("No") { dialog, which ->
                // Do something for button click
                dismiss()
            }
// Neutral action
//            .setNeutralButton("Neutral") { dialog, which ->
//                // Do something for button click
//            }
            .create()
    }
}