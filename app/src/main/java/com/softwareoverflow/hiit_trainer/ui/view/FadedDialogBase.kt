package com.softwareoverflow.hiit_trainer.ui.view

import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.ui.utils.dpToPx

open class FadedDialogBase : DialogFragment() {

    override fun onResume() {
        super.onResume()

        val displaySize = Point()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
             requireContext().display?.getRealSize(displaySize)
         else
            requireActivity().windowManager.defaultDisplay.getRealSize(displaySize)

        val window = requireDialog().window
        window?.setLayout(displaySize.x - 32.dpToPx, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        parentFragment?.view?.alpha = 0.25f

        val fadeColor = requireContext().resources.getColor(R.color.colorPrimaryFaded, requireActivity().theme)
        requireView().background.colorFilter =
            PorterDuffColorFilter(fadeColor, PorterDuff.Mode.SRC_IN)
        requireView().background.alpha = 230
    }

    override fun onStop() {
        super.onStop()

        parentFragment?.view?.alpha = 1f
    }
}