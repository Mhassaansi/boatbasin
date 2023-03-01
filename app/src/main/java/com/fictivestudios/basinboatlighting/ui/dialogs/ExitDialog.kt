package com.fictivestudios.basinboatlighting.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.databinding.ExitDialogFragmentBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
class ExitDialog : DialogFragment() {
    var binding: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate( R.layout.exit_dialog, container)

        getDialog()?.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?. findViewById<Button>(R.id.btnExit) ?.setOnClickListener(View.OnClickListener {
            activity?.finish()
        })

        binding?.findViewById<Button>(R.id.btnClose)?.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
        })
        return binding
    }
}