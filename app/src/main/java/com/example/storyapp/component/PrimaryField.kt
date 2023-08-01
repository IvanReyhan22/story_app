package com.example.storyapp.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class PrimaryField : AppCompatEditText, View.OnTouchListener {
    private lateinit var suffixButtonImage: Drawable
    private var hintText: String = context.getString(R.string.fill_the_field)

    private var validateForm: Boolean = false
    private var isInputPassword: Boolean = false
    private var isPasswordShown: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PrimaryField)
            validateForm = typedArray.getBoolean(R.styleable.PrimaryField_validate, false)
            hintText = typedArray.getString(R.styleable.PrimaryField_hintText) ?: hintText
            typedArray.recycle()
        }
        init()
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
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        hint = hintText
    }

    private fun init() {
        isInputPassword = inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD + 1

        suffixButtonImage = if (isInputPassword) {
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24) as Drawable
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24) as Drawable
        }

        if (isInputPassword) showSuffixButton()

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (validateForm) validateInput(s.toString())
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isSuffixButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (suffixButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isSuffixButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - suffixButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isSuffixButtonClicked = true
                }
            }

            return controlSuffixClick(event, isSuffixButtonClicked)
        }
        return false
    }

    private fun controlSuffixClick(event: MotionEvent, suffixButtonClicked: Boolean): Boolean {
        if (suffixButtonClicked) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (isInputPassword) {
                        togglePassword()
                        updateSuffixButton()
                    } else {
                        when {
                            text != null -> text?.clear()
                        }
                    }
                    return true
                }
                else -> return false
            }
        } else return false
    }


    private fun showSuffixButton() {
        setButtonDrawables(endOfTheText = suffixButtonImage)
    }

    private fun hideSuffixButton() {
        setButtonDrawables()
    }

    private fun togglePassword() {
        isPasswordShown = !isPasswordShown
        Log.e("PASSWORD TOGGLE", isPasswordShown.toString())
        if (isPasswordShown) {
            super.setInputType(InputType.TYPE_CLASS_TEXT)
        } else {
            super.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD + 1)
        }
    }

    private fun updateSuffixButton() {
        if (isPasswordShown) {
            setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_visibility_off_24
                ) as Drawable,
                null
            )
        } else {
            setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_visibility_24
                ) as Drawable,
                null
            )
        }
    }

    private fun validateInput(input: String) {
        if (isInputPassword) {
            when {
                input.length < 8 -> super.setError(context.getString(R.string.minimum_char_8))
                !input.contains(Regex("\\d")) -> super.setError(context.getString(R.string.password_contain_number))
                else -> error = null
            }
        }

        if (inputType == InputType.TYPE_CLASS_TEXT) {
            when {
                input.length < 5 -> super.setError(context.getString(R.string.minimum_char_5))
                input.isEmpty() -> super.setError(context.getString(R.string.field_required))
            }
        }

        if (inputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + 1) {
            when {
                !android.util.Patterns.EMAIL_ADDRESS.matcher(
                    input
                ).matches() -> super.setError(context.getString(R.string.email_not_valid))
                else -> error = null
            }
        }


    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

}