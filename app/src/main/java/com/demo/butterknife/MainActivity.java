package com.demo.butterknife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.demo.buterknife.ButterKnife;
import com.demo.butterknife_annotations.BindView;
import com.demo.butterknife_compiler.FieldViewBinding;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.textview)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通过工具类实现依赖注入
        ButterKnife.bind(this);
        textView.setText("hahhah");
        button.setText("this is a button");
        System.out.println(FieldViewBinding.class.getName());
        System.out.println(FieldViewBinding.class.getCanonicalName());
        System.out.println(FieldViewBinding.class.getSimpleName());
        System.out.println(FieldViewBinding.class.getTypeName());
    }

}