package com.lewei.multiple.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.lewei.multiple.fydrone.R;

public class HelpActivity extends Activity {
    private int cur_page;
    private ImageView iv_Help_Back;
    private ImageView iv_Help_Page;
    private ImageView iv_Help_Page_Next;
    private ImageView iv_Help_Page_Prev;

    private class ClickListener implements OnClickListener {
        private ClickListener() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_help_back:
                    HelpActivity.this.finish();
                    HelpActivity.this.startActivity(new Intent(HelpActivity.this, HomeActivity.class));
                case R.id.iv_help_page_next:
                    if (HelpActivity.this.cur_page == 0) {
                        HelpActivity.this.iv_Help_Page.setImageResource(R.drawable.help_page1);
                        HelpActivity.this.iv_Help_Page_Prev.setVisibility(0);
                        HelpActivity.this.cur_page = 1;
                    } else if (HelpActivity.this.cur_page == 1) {
                        HelpActivity.this.iv_Help_Page.setImageResource(R.drawable.help_page2);
                        HelpActivity.this.iv_Help_Page_Next.setVisibility(4);
                        HelpActivity.this.cur_page = 2;
                    }
                case R.id.iv_help_page_prev:
                    if (HelpActivity.this.cur_page == 1) {
                        HelpActivity.this.iv_Help_Page.setImageResource(R.drawable.help_page0);
                        HelpActivity.this.iv_Help_Page_Prev.setVisibility(4);
                        HelpActivity.this.cur_page = 0;
                    } else if (HelpActivity.this.cur_page == 2) {
                        HelpActivity.this.iv_Help_Page.setImageResource(R.drawable.help_page1);
                        HelpActivity.this.cur_page = 1;
                        HelpActivity.this.iv_Help_Page_Next.setVisibility(0);
                    }
                default:
            }
        }
    }

    public HelpActivity() {
        this.cur_page = 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        setContentView(R.layout.activity_help);
        init();
    }

    private void init() {
        this.iv_Help_Page = (ImageView) findViewById(R.id.iv_help_page);
        this.iv_Help_Back = (ImageView) findViewById(R.id.iv_help_back);
        this.iv_Help_Page_Next = (ImageView) findViewById(R.id.iv_help_page_next);
        this.iv_Help_Page_Prev = (ImageView) findViewById(R.id.iv_help_page_prev);
        this.iv_Help_Page.setOnClickListener(new ClickListener());
        this.iv_Help_Back.setOnClickListener(new ClickListener());
        this.iv_Help_Page_Next.setOnClickListener(new ClickListener());
        this.iv_Help_Page_Prev.setOnClickListener(new ClickListener());
    }
}
