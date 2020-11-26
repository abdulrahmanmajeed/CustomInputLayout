package com.abe.custominputlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class DHBInputLayout extends LinearLayout {

    // Widgets
    private EditText mEditText;
    private TextView floatingTextView;
    private TextView errorTextView;
    // Display data
    private CharSequence hint;
    private CharSequence customErrorMessage = "";
    // Attributes
    private boolean isMandatory;
//    private boolean isReadOnly, isPassword, nonEditAble;

    // Characteristics
    int textSizeOfFloatingField = 34;
    int textSizeOfErrorField = 34;
    int textSizeOfInputField = 50;
    int textColorErrorField, textColorFloatingField, textColorInputField, textColorHint;
    int defaultHintColor;
    private Context mContext;

    // Limitations
    int maxLength;

    public DHBInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs) {

        setOrientation(LinearLayout.VERTICAL);
        mContext = context;
        initViews(context, attrs);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DHBInputLayout, 0, 0);

        if (ta.hasValue(R.styleable.DHBInputLayout_hint))
            hint = ta.getString(R.styleable.DHBInputLayout_hint);

        if (ta.hasValue(R.styleable.DHBInputLayout_customError))
            customErrorMessage = ta.getString(R.styleable.DHBInputLayout_customError);

        if (ta.hasValue(R.styleable.DHBInputLayout_isMandatory))
            isMandatory = ta.getBoolean(R.styleable.DHBInputLayout_isMandatory, false);

        if (ta.hasValue(R.styleable.DHBInputLayout_maxLength))
            maxLength = ta.getInteger(R.styleable.DHBInputLayout_maxLength, 0);

        if (ta.hasValue(R.styleable.DHBInputLayout_textColorHint)) {
            textColorHint = ta.getColor(R.styleable.DHBInputLayout_textColorHint, defaultHintColor);
            textColorFloatingField = textColorInputField;
        }

        if (ta.hasValue(R.styleable.DHBInputLayout_textColorFloatingField))
            textColorFloatingField = ta.getColor(R.styleable.DHBInputLayout_textColorFloatingField, defaultHintColor);

        if (ta.hasValue(R.styleable.DHBInputLayout_textColorErrorField))
            textColorErrorField = ta.getColor(R.styleable.DHBInputLayout_textColorErrorField, Color.RED);

        if (ta.hasValue(R.styleable.DHBInputLayout_textSizeFloatingField))
            textSizeOfFloatingField = ta.getDimensionPixelSize(R.styleable.DHBInputLayout_textSizeFloatingField, textSizeOfFloatingField);

//            if (a.hasValue(R.styleable.MultiOperationInputLayout_operationTextSize)) {
//                mOperationTextViewSize = a.getInteger(R.styleable.MultiOperationInputLayout_operationTextSize, mOperationTextViewSize);
//            }

        if (ta.hasValue(R.styleable.DHBInputLayout_textSizeInputField))
            textSizeOfInputField = ta.getDimensionPixelSize(R.styleable.DHBInputLayout_textSizeInputField, textSizeOfInputField);

        if (ta.hasValue(R.styleable.DHBInputLayout_textSizeErrorField))
            textSizeOfErrorField = ta.getDimensionPixelSize(R.styleable.DHBInputLayout_textSizeErrorField, textSizeOfErrorField);

        addFloatingView();
        addInputView();
        addErrorView();
        ta.recycle();
    }

    public void setHint(SpannableString hint) {
        getEditText().setHint(hint);
    }

    private void initViews(Context context, AttributeSet attrs) {
        floatingTextView = new TextView(context, attrs);
        mEditText = new EditText(context, attrs);
        errorTextView = new TextView(context, attrs);

        textColorErrorField = ContextCompat.getColor(context, R.color.defaultErrorColor);
        defaultHintColor = ContextCompat.getColor(context, R.color.defaultHintColor);
        textColorFloatingField = ContextCompat.getColor(context, R.color.defaultHintColor);
        textColorHint = defaultHintColor;
    }

    public EditText getEditText() {
        if (mEditText == null)
            mEditText = new EditText(mContext);
        return mEditText;
    }

    public TextView getFloatingTextView() {
        return floatingTextView;
    }

    public TextView getErrorTextView() {
        return errorTextView;
    }

    public void setText(String strValue) {
        if (!TextUtils.isEmpty(strValue)) {
            mEditText.setText(strValue);
        }
    }

    public void setTextToView(String strValue) {
        if (!TextUtils.isEmpty(strValue)) {
            mEditText.setText(strValue);
        } else {
            mEditText.setText("");
            hideFloatingView();
        }
    }

    public String getText() {
        return String.valueOf(mEditText.getText()).trim();
    }

    public void setError(String strValue) {
        if (TextUtils.isEmpty(strValue))
            strValue = hint + " " + getResources().getString(R.string.msg_field_required);
        errorTextView.setText(strValue);
    }

    public void updateErrorMessage(@StringRes int strValue) {
        if (null != errorTextView) {
            errorTextView.setVisibility(VISIBLE);
            errorTextView.setText(strValue);
        }
    }

    public void disableView() {
        mEditText.setAlpha(0.5f);
        floatingTextView.setEnabled(false);
        floatingTextView.setClickable(false);
        errorTextView.setEnabled(false);
        errorTextView.setClickable(false);
        setClickable(false);
        setEnabled(false);
    }

    public void updateErrorMessage(String strValue) {
        if (null != errorTextView) {
            errorTextView.setVisibility(VISIBLE);
            errorTextView.setText(strValue);
        }
    }

//    public void setDrawableEnd(@DrawableRes int drawableEnd, boolean isArabic) {
//        if (drawableEnd != 0) {
//            if (isArabic) {
//                mEditText.setCompoundDrawablesWithIntrinsicBounds(drawableEnd, 0, 0, 0);
//            } else {
//                mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableEnd, 0);
//            }
//        }
//    }

//    public void setDrawableStart(@DrawableRes int drawableStart, boolean isArabic) {
//        if (drawableStart != 0) {
//            if (isArabic) {
//                mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableStart, 0);
//            } else {
//                mEditText.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, 0, 0);
//            }
//            mEditText.setCompoundDrawablePadding(10);
//        }
//    }

    private void addFloatingView() {

        CharSequence strFloatingLabel;
        if (isMandatory && !TextUtils.isEmpty(hint)) {
            strFloatingLabel = hint + " " + getResources().getString(R.string.symbol_asterisk);
            Spannable placeHolderSpan = new SpannableString(strFloatingLabel);
            placeHolderSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#A30401")), strFloatingLabel.length() - 1,
                    strFloatingLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            floatingTextView.setText(placeHolderSpan);
        } else {
            strFloatingLabel = hint;
            floatingTextView.setText(strFloatingLabel);
        }
        floatingViewSetUp();

        addView(floatingTextView);
        hideFloatingView();
    }

    private void hideFloatingView() {
        if (null != floatingTextView)
            floatingTextView.setVisibility(GONE);
    }

    public void showFloatingView() {
        if (null != floatingTextView)
            floatingTextView.setVisibility(VISIBLE);
    }

    private void floatingViewSetUp() {

        floatingTextView.setBackground(null);
        //TODO This was not working need to be check later
//        mTextViewFloating.setDuplicateParentStateEnabled(true);
//        mTextViewFloating.setClickable(false);
//        mTextViewFloating.setFocusable(false);
        floatingTextView.setOnClickListener(view -> performClick());
        floatingTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeOfFloatingField);
        floatingTextView.setTextColor(textColorFloatingField);
        floatingTextView.setSingleLine();
        floatingTextView.setMaxLines(1);
    }

    private void addInputView() {

//        if (isPassword)
//            mEditTextInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEditText.setHint(hint);
        mEditText.setHintTextColor(textColorHint);
        mEditText.setBackground(null);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeOfInputField);
        if (maxLength != 0) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            mEditText.setFilters(fArray);
        }

        focusController();
        textWatcher();
        addView(mEditText);
    }

    private void textWatcher() {

        inputViewTextValidation();

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputViewTextValidation();
            }
        });
    }

    private void inputViewTextValidation() {
        if (TextUtils.isEmpty(mEditText.getText().toString()))
            hideFloatingView();
        else {
            showFloatingView();
            hideErrorView();
        }
    }

    private void focusController() {

        mEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(mEditText.getText()) && isMandatory)
                    showErrorView();
            } else {
                if (null != mEditText.getText())
                    mEditText.setSelection(mEditText.getText().length());
            }
        });
    }

    private void addErrorView() {

        CharSequence defaultErrorMessage;
        if (isMandatory)
            defaultErrorMessage = hint + " " + getResources().getString(R.string.msg_field_required);
        else
            defaultErrorMessage = customErrorMessage;

        errorTextView.setText(defaultErrorMessage);
        errorViewSetUp();
        addView(errorTextView);
        hideErrorView();
    }

    public void hideErrorView() {
        if (null != errorTextView)
            errorTextView.setVisibility(GONE);
    }

    public void showErrorView() {
        if (null != errorTextView)
            errorTextView.setVisibility(VISIBLE);
    }

    private void errorViewSetUp() {

        errorTextView.setBackground(null);
        //TODO This was not working need to be check later
//        mTextViewError.setDuplicateParentStateEnabled(true);
//        mTextViewError.setClickable(false);
//        mTextViewError.setFocusable(false);
        errorTextView.setOnClickListener(view -> performClick());
        errorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeOfErrorField);
        errorTextView.setTextColor(textColorErrorField);
    }
}
