package com.hololo.app.dnschanger.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hololo.app.dnschanger.BuildConfig;
import com.hololo.app.dnschanger.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.versionText)
    TextView versionText;
    @BindView(R.id.sendFeedback)
    TextView sendFeedback;
    @BindView(R.id.activity_about)
    LinearLayout activityAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        getSupportActionBar().setTitle(R.string.about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        versionText.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.sendFeedback)
    public void onClick() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"hololo.dev@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.about_app));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.tell_us_what_you_think));

        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
    }
}
