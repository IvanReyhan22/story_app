package com.example.storyapp.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class PrimaryButton : AppCompatButton {

    private var progressBar: ProgressBar? = null

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColor: Int = 0
    private var btnText: String = "Click Me"

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PrimaryButton)
            btnText = typedArray.getString(R.styleable.PrimaryButton_text) ?: btnText
            typedArray.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(txtColor)
        text = btnText
        textSize = 14f
        isAllCaps = true
        gravity = Gravity.CENTER
    }

    private fun init() {
        txtColor = resources.getColor(R.color.white)
        enabledBackground =
            ContextCompat.getDrawable(context, R.drawable.primary_button) as Drawable
        disabledBackground =
            ContextCompat.getDrawable(context, R.drawable.primary_button_disabled) as Drawable
    }
}