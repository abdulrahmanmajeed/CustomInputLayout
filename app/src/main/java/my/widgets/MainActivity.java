package my.widgets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.MultiOperationEditText;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

        emailInputLayout.setOperationTextViewOnclickListener(view -> {
//                TODO some functionality
            Toast.makeText(MainActivity.this, "you clicked on forgot", Toast.LENGTH_SHORT).show();
        });

        String text;
        int color = ContextCompat.getColor(this, android.R.color.holo_red_dark);
//        text = getString(R.string.app_name) + " *";
//        inputLayout.setPlaceHolder(spanText(text, color, (text.length() - 1), text.length()));


        findViewById(R.id.btn_login).setOnClickListener(v -> {
//            inputLayout.showErrorView();
            startActivity(new Intent(this, SecondActivity.class));

        });
    }

    public SpannableString spanText(String text, int color, int from, int to) {
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}