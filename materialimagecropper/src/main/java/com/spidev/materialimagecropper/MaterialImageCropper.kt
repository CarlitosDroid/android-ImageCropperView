package com.spidev.materialimagecropper

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by carlos on 8/13/17.
 */
class MaterialImageCropper : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initialize(attrs, 0,0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        initialize(attrs, defStyleAttr, 0)
    }

    fun initialize(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int){

    }
}


