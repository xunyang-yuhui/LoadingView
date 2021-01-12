package com.yu.loadingdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_threesquare;
    private Button btn_threeball;
    private Button btn_jumpgraph;
    private LoadingDialogFragment dialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_threesquare = findViewById(R.id.btn_threesquare);
        btn_threeball = findViewById(R.id.btn_threeball);
        btn_jumpgraph = findViewById(R.id.btn_jumpgraph);
        initData();
    }

    private void initData() {
        btn_threesquare.setOnClickListener(this);
        btn_threeball.setOnClickListener(this);
        btn_jumpgraph.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_threesquare) {
            if (dialogFragment == null) {
                dialogFragment = new LoadingDialogFragment();
            } else {
                dialogFragment.dismiss();
            }
            dialogFragment.setType(LoadingDialogFragment.Type.THREESQUARE);
            dialogFragment.show(getSupportFragmentManager(),"dialog");
        } else if (view.getId() == R.id.btn_threeball) {
            if (dialogFragment == null) {
                dialogFragment = new LoadingDialogFragment();
            } else {
                dialogFragment.dismiss();
            }
            dialogFragment.setType(LoadingDialogFragment.Type.THREEBALL);
            dialogFragment.show(getSupportFragmentManager(),"dialog");
        } else if (view.getId() == R.id.btn_jumpgraph) {
            if (dialogFragment == null) {
                dialogFragment = new LoadingDialogFragment();
            } else {
                dialogFragment.dismiss();
            }
            dialogFragment.setType(LoadingDialogFragment.Type.JUMPGRAPH);
            dialogFragment.show(getSupportFragmentManager(),"dialog");
        }


    }


}
