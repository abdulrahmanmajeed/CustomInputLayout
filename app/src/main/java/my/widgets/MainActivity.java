package my.widgets;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.design.widget.NXBTextInputLayout;
import android.text.TextUtils;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NXBTextInputLayout emailInputLayout = findViewById(R.id.email_input_layout);
        NXBTextInputLayout inputSearch = findViewById(R.id.field_search);
        NXBTextInputLayout spinnerView = findViewById(R.id.spinner_layout);
        NXBTextInputLayout mandatoryField = findViewById(R.id.mandatory_field);

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
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}