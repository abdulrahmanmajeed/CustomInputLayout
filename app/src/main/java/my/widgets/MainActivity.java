package my.widgets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DHBInputLayout inputLayout = findViewById(R.id.input_name);

        String text;
        int color = ContextCompat.getColor(this, android.R.color.holo_red_dark);
//        text = getString(R.string.app_name) + " *";
//        inputLayout.setPlaceHolder(spanText(text, color, (text.length() - 1), text.length()));


        findViewById(R.id.btn_login).setOnClickListener(v -> {
            inputLayout.showErrorView();

        });
    }

    public SpannableString spanText(String text, int color, int from, int to) {
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}