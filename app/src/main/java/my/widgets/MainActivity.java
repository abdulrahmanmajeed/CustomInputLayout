package my.widgets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.MultiOperationEditText;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

//import com.abe.custominputlayout.DHBInputLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DHBInputLayout inputLayout = findViewById(R.id.input_name);

        MultiOperationEditText emailInputLayout = findViewById(R.id.email_input_layout);
        MultiOperationEditText inputSearch = findViewById(R.id.field_search);
        MultiOperationEditText spinnerView = findViewById(R.id.spinner_layout);
        MultiOperationEditText mandatoryField = findViewById(R.id.mandatory_field);

        emailInputLayout.setOperationTextViewOnclickListener(view -> {
//                TODO some functionality
            showToast("you clicked on forgot");
        });

        spinnerView.setOnClickListener(v ->
                showToast("You can open popup"));

        inputSearch.setOperationToggleOnclickListener(view -> {

            inputSearch.clear();
        });


        findViewById(R.id.btn_login).setOnClickListener(v -> {
//            startActivity(new Intent(this, SecondActivity.class));
            if (TextUtils.isEmpty(mandatoryField.getText()))
                mandatoryField.showError();
            else {
                showToast("you may proceed");
            }
        });
    }

    private void showToast(String str) {
        Toast.makeText(MainActivity.this, "you clicked on forgot", Toast.LENGTH_SHORT).show();
    }
}