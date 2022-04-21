package edu.zjut.contactsMaterial3Experimental.Helpers


import android.content.Context
import android.graphics.Typeface

class FontHelper(context: Context) {
    private val mContext=context
    fun getAliFontLight(): Typeface {
        return Typeface.createFromAsset(mContext.assets,"alibaba_font_light.ttf")
    }
}