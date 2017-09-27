package com.spidev.mandarinfish.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spidev.mandarinfish.R
import kotlinx.android.synthetic.main.dialog_fragment_camera.*

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 9/26/17.
 *
 */
class CameraDialogFragment : DialogFragment() {

    var onCameraRationaleListener : OnCameraRationaleListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is Activity){
            onCameraRationaleListener = activity as? OnCameraRationaleListener
        }
    }

    override fun onDetach() {
        onCameraRationaleListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        btnAccept.setOnClickListener{ _ ->

            onCameraRationaleListener?.onAccept()
        }

        btnCancel.setOnClickListener { _ ->
            dialog.dismiss()
        }
    }

    companion object {
        fun newInstance(title: String): CameraDialogFragment {
            val cameraDialogFragment = CameraDialogFragment()
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