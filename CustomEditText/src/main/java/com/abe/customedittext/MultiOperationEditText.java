package com.abe.customedittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.internal.CheckableImageButton;

/**
 * copy from android.support.design
 * add the textView and CheckableImageButton to support additional operation
 */
public class MultiOperationEditText extends LinearLayout implements WidgetConstants {

    private Context mContext;
    private OnClickListener mOperationTextViewOnclickListener;
    private OnClickListener mMultiOperationToggleOnclickListener;

    // Other Features with input
    private TextView mOperationTextView;
    private final CharSequence mOperationText;
    private final ColorStateList mOperationTextViewColor;
    private int mOperationTextViewSize = 15;
    // Drawable
    private AppCompatCheckBox mOperationToggleView;
    private final CharSequence mOperationToggleContentDesc;
    private final ColorStateList mOperationToggleTint;
    private Drawable mOperationToggleDrawable;
    private final boolean mOperationToggleChecked;

    // Input View
    private FrameLayout mInputFrame;
    private EditText mEditText;

    // Floating View
    private TextView floatingTextView;
    private boolean isMandatory;
    private int asteriskColor;

    // Error View
    private TextView errorTextView;
    private CharSequence customErrorMessage = "";
    int textSizeOfErrorField = 34;
    private int textColorErrorField;

    // Attributes
    private boolean readOnlyView;

    // Functions
    private final int mOperationType;
    private final int mOperationToggleType;

    public MultiOperationEditText(Context context) {
        this(context, null);
    }

    public MultiOperationEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public MultiOperationEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        // Can't call through to super(Context, AttributeSet, int) since it doesn't exist on API 10
        super(context, attrs);

        mContext = context;

        setOrientation(VERTICAL);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);

        @SuppressLint("RestrictedApi")
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.MultiOperationInputLayout, defStyleAttr, R.style.MultiOperationInputLayout);

        readOnlyView = a.getBoolean(R.styleable.MultiOperationInputLayout_readOnlyView, false);
        isMandatory = a.getBoolean(R.styleable.MultiOperationInputLayout_isMandatory, false);

        if (a.hasValue(R.styleable.MultiOperationInputLayout_operationToggleDrawable)) {
            mOperationToggleDrawable = a.getDrawable(R.styleable.MultiOperationInputLayout_operationToggleDrawable);
        } else {
            mOperationToggleDrawable = getResources().getDrawable(R.drawable.design_password_eye_icon);
        }
        mOperationToggleDrawable = a.getDrawable(R.styleable.MultiOperationInputLayout_operationToggleDrawable);
        mOperationToggleContentDesc = a.getText(
                R.styleable.MultiOperationInputLayout_operationToggleContentDescription);
        mOperationType = a.getInt(R.styleable.MultiOperationInputLayout_operationType, 1);
        mOperationToggleType = a.getInt(R.styleable.MultiOperationInputLayout_operationToggleType, 1);

        if (a.hasValue(R.styleable.MultiOperationInputLayout_operationTextSize)) {
            mOperationTextViewSize = a.getInteger(R.styleable.MultiOperationInputLayout_operationTextSize, mOperationTextViewSize);
        }
        if (a.hasValue(R.styleable.MultiOperationInputLayout_operationTextString)) {
            mOperationText = a.getText(R.styleable.MultiOperationInputLayout_operationTextString);
        } else {
            mOperationText = "";
        }
        if (a.hasValue(R.styleable.MultiOperationInputLayout_operationTextColor)) {
            mOperationTextViewColor = a.getColorStateList(R.styleable.MultiOperationInputLayout_operationTextColor);
        } else {
            mOperationTextViewColor = ColorStateList.valueOf(getResources().getColor(R.color.text_operation_color));
        }
        asteriskColor = a.getColor(R.styleable.MultiOperationInputLayout_asteriskColor, ContextCompat.getColor(context, R.color.text_input_error_color_light));
        if (a.hasValue(R.styleable.MultiOperationInputLayout_operationToggleTint)) {
            mOperationToggleTint = a.getColorStateList(R.styleable.MultiOperationInputLayout_operationToggleTint);
        } else {
            mOperationToggleTint = ColorStateList.valueOf(getResources().getColor(R.color.text_operation_color));
        }

        mOperationToggleChecked = a.getBoolean(R.styleable.MultiOperationInputLayout_operationToggleIconChecked, false);

        setUpView(context, attrs);
        addFloatingView();
        addView(mInputFrame);

        a.recycle();
    }

    private void setUpView(Context context, AttributeSet attrs) {

        mInputFrame = new FrameLayout(context);
        mInputFrame.setAddStatesFromChildren(true);

        floatingTextView = new TextView(context, attrs);
        floatingTextView.setFocusable(false);
        floatingTextView.setClickable(false);

        errorTextView = new TextView(context, attrs);
        errorTextView.setFocusable(false);
        errorTextView.setClickable(false);
    }

    @Override
    public void addView(View child, int index, final ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            // Make sure that the EditText is vertically at the bottom, so that it sits on the
            // EditText's underline
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(params);
            flp.gravity = Gravity.CENTER_VERTICAL | (flp.gravity & ~Gravity.VERTICAL_GRAVITY_MASK);
            mInputFrame.addView(child, flp);

            // Now use the EditText's LayoutParams as our own and update them to make enough space
            // for the label
            mInputFrame.setLayoutParams(params);
            updateInputLayoutMargins();
            setEditText((EditText) child);
        } else {
            // Carry on adding the View...
            super.addView(child, index, params);
        }
    }

    private void addFloatingView() {

        CharSequence strFloatingLabel = "I am your hint";
        if (isMandatory) {
//            strFloatingLabel = mEditText.getHint() + " " + getResources().getString(R.string.symbol_asterisk);
            Spannable placeHolderSpan = new SpannableString(strFloatingLabel);
            placeHolderSpan.setSpan(new ForegroundColorSpan(asteriskColor), strFloatingLabel.length() - 1,
                    strFloatingLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            floatingTextView.setText(placeHolderSpan);
        } else {
//            strFloatingLabel = hint;
//            floatingTextView.setText(strFloatingLabel);
        }
//        floatingViewSetUp();
        addView(floatingTextView);
    }

    private void addErrorView() {
        errorTextView.setText("I am your error");
        addView(errorTextView);
    }

    private void textWatcher() {

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    floatingTextView.setVisibility(GONE);
                    errorTextView.setVisibility(GONE);
                } else {
                    floatingTextView.setVisibility(VISIBLE);
                    errorTextView.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void setEditText(EditText editText) {
        // If we already have an EditText, throw an exception
        if (mEditText != null) {
            throw new IllegalArgumentException("We already have an EditText, can only have one");
        }

        mEditText = editText;
//        addFloatingView();
        addErrorView();
        textWatcher();

        if (isOperationToggleVisible()) {
            updateOperationToggleView();
        } else if (isOperationSpinner()) {
            updateOperationSpinner();
        } else {
            updateOperationTextView();
        }
    }

    private void updateInputLayoutMargins() {
        // Create/update the LayoutParams so that we can add enough top margin
        // to the EditText so make room for the label
        final LayoutParams lp = (LayoutParams) mInputFrame.getLayoutParams();
        final int newTopMargin;

        newTopMargin = 0;

        if (newTopMargin != lp.topMargin) {
            lp.topMargin = newTopMargin;
            mInputFrame.requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updateOperationToggleView();
        updateOperationTextView();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateOperationSpinner() {
        if (mEditText == null) {
            // If there is no EditText, there is nothing to update
            return;
        }
        readOnlyView = true;
        readOnlyEditText();
        mEditText.setText(R.string.spinner_default_value);
        updateOperationToggleView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void readOnlyEditText() {
        mEditText.setEnabled(false);
        mEditText.setClickable(true);
        mEditText.setFocusableInTouchMode(false);
        mEditText.setFocusable(false);

//        setEnabled(true);
//        setFocusable(true);
//        setFocusableInTouchMode(true);

//        mEditText.setCursorVisible(false);
//        mEditText.setInputType(InputType.TYPE_NULL);
        mEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performClick();
            }
        });
//        mEditText.setOnClickListener(mMultiOperationToggleOnclickListener);
//        mEditText.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "Oh you want spinner?", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void updateOperationTextView() {
        if (mEditText == null) {
            // If there is no EditText, there is nothing to update
            return;
        }

        if (readOnlyView) {
            readOnlyEditText();
        }

        if (shouldShowMultiOperationTextView()) {
            if (mOperationTextView == null) {
                mOperationTextView = (TextView) LayoutInflater.from(getContext())
                        .inflate(R.layout.view_input_operation_text, mInputFrame, false);
                mOperationTextView.setTextColor(mOperationTextViewColor);
                mOperationTextView.setTextSize(mOperationTextViewSize);
                mOperationTextView.setText(mOperationText);
                mOperationTextView.setOnClickListener(mOperationTextViewOnclickListener);
                mInputFrame.addView(mOperationTextView);
            }

            if (mEditText != null && ViewCompat.getMinimumHeight(mEditText) <= 0) {
                mEditText.setMinimumHeight(ViewCompat.getMinimumHeight(mOperationTextView));
            }

            // Copy over the EditText's padding so that we match
            mOperationTextView.setPadding(mEditText.getPaddingLeft(),
                    mEditText.getPaddingTop(), mEditText.getPaddingRight(),
                    mEditText.getPaddingBottom());
            mOperationTextView.setVisibility(View.VISIBLE);
        } else if (null != mOperationTextView) {
            mOperationTextView.setVisibility(View.GONE);
        }
    }

    private void updateOperationToggleView() {
        if (mEditText == null) {
            // If there is no EditText, there is nothing to update
            return;
        }

        if (isOperationToggleVisible() || isOperationSpinner()) {
            if (mOperationToggleView == null) {
                mOperationToggleView = (AppCompatCheckBox) LayoutInflater.from(getContext())
                        .inflate(R.layout.view_input_operation_image, mInputFrame, false);
                mOperationToggleView.setButtonDrawable(mOperationToggleDrawable);
                mOperationToggleView.setButtonTintList(mOperationToggleTint);
                mOperationToggleView.setContentDescription(mOperationToggleContentDesc);
//                if (isOperationSpinner()) {
//                    mInputFrame.setEnabled(false);
//                    mInputFrame.setClickable(false);
//                    mInputFrame.setFocusable(false);
//                    mInputFrame.setFocusableInTouchMode(false);
//                }
                mInputFrame.addView(mOperationToggleView);

                if (passwordToggleEnable()) {
                    mOperationToggleView.setOnClickListener(view -> passwordVisibilityToggleRequested());
                } else {
                    if (isOperationSpinner()) {
                        mOperationToggleView.setEnabled(false);
                        mOperationToggleView.setClickable(true);
                        mOperationToggleView.setFocusableInTouchMode(false);
                        mOperationToggleView.setFocusable(false);

//                        setClickable(true);
//                        setFocusable(true);
//                        setFocusableInTouchMode(true);

                        mOperationToggleView.setOnClickListener(v -> performClick());
                    } else {
                        mOperationToggleView.setOnClickListener(mMultiOperationToggleOnclickListener);
                    }
                }
                setOperationToggleChecked(mOperationToggleChecked);
            }

//            if (mEditText != null && ViewCompat.getMinimumHeight(mEditText) <= 0) {
//                mEditText.setMinimumHeight(ViewCompat.getMinimumHeight(mOperationToggleView));
//            }

            mOperationToggleView.setVisibility(VISIBLE);

            // Copy over the EditText's padding so that we match
            mOperationToggleView.setPadding(mEditText.getPaddingLeft(),
                    mEditText.getPaddingTop(), mEditText.getPaddingRight(),
                    mEditText.getPaddingBottom());
        } else {
            if (mOperationToggleView != null && mOperationToggleView.getVisibility() == VISIBLE) {
                mOperationToggleView.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    void passwordVisibilityToggleRequested() {
        // Store the current cursor position
        final int selection = mEditText.getSelectionEnd();

        if (hasPasswordTransformation()) {
            mEditText.setTransformationMethod(null);
            mOperationToggleView.setChecked(true);
        } else {
            mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mOperationToggleView.setChecked(false);
        }
        // And restore the cursor position
        mEditText.setSelection(selection);
    }

    @SuppressLint("RestrictedApi")
    public void setOperationToggleChecked(boolean checked) {
        if (mOperationToggleView != null) {
            mOperationToggleView.setChecked(checked);

            if (passwordToggleEnable()) {
                if (checked /*&& hasPasswordTransformation()*/) {
                    mEditText.setTransformationMethod(null);
                } else {
                    mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            } else {
                mEditText.setTransformationMethod(null);
            }
        }
    }

    private boolean hasPasswordTransformation() {
        return mEditText != null
                && mEditText.getTransformationMethod() instanceof PasswordTransformationMethod;
    }

//    private boolean shouldShowOperationIcon() {
//        return isOperationToggleVisible();
//    }

    private boolean passwordToggleEnable() {
        return mOperationToggleType == OPERATION_TOGGLE_TYPE_PASSWORD;
    }

    private boolean shouldShowMultiOperationTextView() {
        return mOperationType == OPERATION_TYPE_EDIT_TEXT_WITH_TEXT;
    }

    private boolean isOperationToggleVisible() {
        return mOperationType == OPERATION_TYPE_EDIT_TEXT_WITH_TOGGLE;
    }

    private boolean isOperationSpinner() {
        return mOperationType == OPERATION_TYPE_SPINNER;
    }

    /**
     * add the OnclickListener to the right toggle
     *
     * @param onclickListener the listener
     */
    public void setOperationTextViewOnclickListener(OnClickListener onclickListener) {
        this.mOperationTextViewOnclickListener = onclickListener;
        if (mOperationTextView != null) {
            mOperationTextView.setOnClickListener(mOperationTextViewOnclickListener);
        }
    }

    /**
     * add the OnclickListener to the right textView
     *
     * @param onclickListener the listener
     */
    public void setOperationToggleOnclickListener(OnClickListener onclickListener) {
        if (!passwordToggleEnable()) {
            this.mMultiOperationToggleOnclickListener = onclickListener;
            if (mOperationToggleView != null) {
                mOperationToggleView.setOnClickListener(mMultiOperationToggleOnclickListener);
            }
        }
    }
}
