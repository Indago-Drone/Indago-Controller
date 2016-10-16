package com.lewei.multiple.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.lewei.lib.LeweiLib;
import com.lewei.multiple.app.Applications;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.utils.ParamsConfig;

public class SetActivity extends Activity {
    private ImageView iv_Set_Back;
    private ImageView iv_Set_Hardware;
    private ImageView iv_Set_Params_AutoSave;
    private ImageView iv_Set_Preview_Select;
    private ImageView iv_Set_RightHandMode;
    private RelativeLayout layout_Set_Hardware;
    private RelativeLayout layout_Set_Reset_Params;

    private class ClickListener implements OnClickListener {
        private ClickListener() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_set_back:
                    SetActivity.this.finish();
                    Intent intent = new Intent();
                    intent.setClass(SetActivity.this, HomeActivity.class);
                    SetActivity.this.startActivity(intent);
                case R.id.iv_set_params_autosave:
                    if (Applications.isParamsAutoSave) {
                        Applications.isParamsAutoSave = false;
                        ParamsConfig.writeParamsAutoSave(SetActivity.this.getApplicationContext(), Boolean.valueOf(false));
                        SetActivity.this.iv_Set_Params_AutoSave.setImageResource(R.drawable.close_icon);
                        return;
                    }
                    Applications.isParamsAutoSave = true;
                    ParamsConfig.writeParamsAutoSave(SetActivity.this.getApplicationContext(), Boolean.valueOf(true));
                    SetActivity.this.iv_Set_Params_AutoSave.setImageResource(R.drawable.open_icon);
                case R.id.layout_set_reset_params:
                    ParamsConfig.writeTrimOffset(SetActivity.this.getApplicationContext(), 1, 0);
                    ParamsConfig.writeTrimOffset(SetActivity.this.getApplicationContext(), 2, 0);
                    ParamsConfig.writeTrimOffset(SetActivity.this.getApplicationContext(), 3, 0);
                    Toast.makeText(SetActivity.this, "Reset parameters success.", 1).show();
                case R.id.iv_set_hardware:
                    if (LeweiLib.Hardware_flag == 0) {
                        LeweiLib.Hardware_flag = 1;
                        ParamsConfig.writeHardwareDecode(SetActivity.this.getApplicationContext(), 1);
                        SetActivity.this.iv_Set_Hardware.setImageResource(R.drawable.open_icon);
                        return;
                    }
                    LeweiLib.Hardware_flag = 0;
                    ParamsConfig.writeHardwareDecode(SetActivity.this.getApplicationContext(), 0);
                    SetActivity.this.iv_Set_Hardware.setImageResource(R.drawable.close_icon);
                case R.id.iv_set_righthandmode:
                    if (Applications.isRightHandMode) {
                        Applications.isRightHandMode = false;
                        ParamsConfig.writeRightHandMode(SetActivity.this.getApplicationContext(), Boolean.valueOf(false));
                        SetActivity.this.iv_Set_RightHandMode.setImageResource(R.drawable.close_icon);
                        return;
                    }
                    Applications.isRightHandMode = true;
                    ParamsConfig.writeRightHandMode(SetActivity.this.getApplicationContext(), Boolean.valueOf(true));
                    SetActivity.this.iv_Set_RightHandMode.setImageResource(R.drawable.open_icon);
                case R.id.iv_set_preview_select:
                    if (LeweiLib.HD_flag == 0) {
                        LeweiLib.HD_flag = 1;
                        ParamsConfig.writeHDflag(SetActivity.this.getApplicationContext(), 1);
                        SetActivity.this.iv_Set_Preview_Select.setImageResource(R.drawable.open_icon);
                        return;
                    }
                    LeweiLib.HD_flag = 0;
                    ParamsConfig.writeHDflag(SetActivity.this.getApplicationContext(), 0);
                    SetActivity.this.iv_Set_Preview_Select.setImageResource(R.drawable.close_icon);
                default:
            }
        }
    }

    public SetActivity() {
        this.iv_Set_Back = null;
        this.iv_Set_Params_AutoSave = null;
        this.iv_Set_Hardware = null;
        this.iv_Set_RightHandMode = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        set_init();
    }

    private void set_init() {
        this.iv_Set_Back = (ImageView) findViewById(R.id.iv_set_back);
        this.iv_Set_Params_AutoSave = (ImageView) findViewById(R.id.iv_set_params_autosave);
        this.iv_Set_Hardware = (ImageView) findViewById(R.id.iv_set_hardware);
        this.iv_Set_RightHandMode = (ImageView) findViewById(R.id.iv_set_righthandmode);
        this.iv_Set_Preview_Select = (ImageView) findViewById(R.id.iv_set_preview_select);
        this.layout_Set_Reset_Params = (RelativeLayout) findViewById(R.id.layout_set_reset_params);
        this.layout_Set_Reset_Params.setOnClickListener(new ClickListener());
        this.layout_Set_Hardware = (RelativeLayout) findViewById(R.id.layout_set_hardware);
        if (ParamsConfig.readRightHandMode(getApplicationContext())) {
            this.iv_Set_RightHandMode.setImageResource(R.drawable.open_icon);
        } else {
            this.iv_Set_RightHandMode.setImageResource(R.drawable.close_icon);
        }
        if (ParamsConfig.readParamsAutoSave(getApplicationContext())) {
            this.iv_Set_Params_AutoSave.setImageResource(R.drawable.open_icon);
        } else {
            this.iv_Set_Params_AutoSave.setImageResource(R.drawable.close_icon);
        }
        if (LeweiLib.HD_flag == 0) {
            this.iv_Set_Preview_Select.setImageResource(R.drawable.close_icon);
        } else {
            this.iv_Set_Preview_Select.setImageResource(R.drawable.open_icon);
        }
        if (VERSION.SDK_INT < 16) {
            this.layout_Set_Hardware.setVisibility(8);
            LeweiLib.Hardware_flag = 0;
            ParamsConfig.writeHardwareDecode(getApplicationContext(), 0);
        } else if (LeweiLib.Hardware_flag == 0) {
            this.iv_Set_Hardware.setImageResource(R.drawable.close_icon);
        } else {
            this.iv_Set_Hardware.setImageResource(R.drawable.open_icon);
        }
        this.iv_Set_Back.setOnClickListener(new ClickListener());
        this.iv_Set_Params_AutoSave.setOnClickListener(new ClickListener());
        this.iv_Set_Hardware.setOnClickListener(new ClickListener());
        this.iv_Set_RightHandMode.setOnClickListener(new ClickListener());
        this.iv_Set_Preview_Select.setOnClickListener(new ClickListener());
    }
}
