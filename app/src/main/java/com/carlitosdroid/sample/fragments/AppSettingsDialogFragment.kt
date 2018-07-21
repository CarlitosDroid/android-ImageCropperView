package com.carlitosdroid.sample.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carlitosdroid.sample.R
import kotlinx.android.synthetic.main.dialog_fragment_camera.*

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 9/26/17.
 *
 */
class AppSettingsDialogFragment : DialogFragment() {

    private var onCameraRationaleListener: OnCameraRationaleListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Activity) {
            onCameraRationaleListener = activity as? OnCameraRationaleListener
        }
    }

    override fun onDetach() {
        onCameraRationaleListener = null
        super.onDetach()
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        labelPosition = arguments.getInt("labelPosition")
        labelName = arguments.getString("labelName")
    }*/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tvTitle.text = getString(R.string.open_settings_tap_permissions, arguments!!.getString("title"))

        btnAccept.setOnClickListener { _ ->
            dialog.dismiss()
            onCameraRationaleListener?.onAccept()
        }
    }

    companion object {
        fun newInstance(title: String): AppSettingsDialogFragment {
            val cameraDialogFragment = AppSettingsDialogFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            cameraDialogFragment.arguments = bundle
            return cameraDialogFragment
        }
    }

    interface OnCameraRationaleListener {
        fun onAccept()
    }
}