package ae.gov.dha.allinputlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AllInputLayout extends LinearLayout {

    private Context mContext;

    public AllInputLayout(Context context) {
        this(context, null);
    }

    public AllInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AllInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        // Can't call through to super(Context, AttributeSet, int) since it doesn't exist on API 10
        super(context, attrs);

        mContext = context;

        setOrientation(VERTICAL);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        updateOperationToggleView();
//        updateOperationTextView();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
