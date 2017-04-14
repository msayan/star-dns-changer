package com.hololo.app.dnschanger.dnschanger;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.VpnService;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hololo.app.dnschanger.DNSChangerApp;
import com.hololo.app.dnschanger.R;
import com.hololo.app.dnschanger.about.AboutActivity;
import com.hololo.app.dnschanger.model.DNSModel;
import com.hololo.app.dnschanger.model.DNSModelJSON;
import com.hololo.app.dnschanger.settings.SettingsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hololo.app.dnschanger.dnschanger.DNSPresenter.SERVICE_OPEN;

public class MainActivity extends AppCompatActivity implements IDNSView, DialogInterface.OnClickListener {

    private static final int REQUEST_CONNECT = 21;
    private static final Pattern IP_PATTERN = Patterns.IP_ADDRESS;

    @BindView(R.id.firstDnsEdit)
    EditText firstDnsEdit;
    @BindView(R.id.secondDnsEdit)
    EditText secondDnsEdit;
    @BindView(R.id.startButton)
    Button startButton;

    @Inject
    DNSPresenter presenter;
    @Inject
    Gson gson;
    @BindView(R.id.app_bar_image)
    ImageView appBarImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.activity_main)
    CoordinatorLayout activityMain;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.chooseButton)
    Button chooseButton;
    @BindView(R.id.logo)
    ImageView logo;

    private List<DNSModel> dnsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerDNSComponent.builder().applicationComponent(DNSChangerApp.getApplicationComponent()).dNSModule(new DNSModule(this)).build().inject(this);
        ButterKnife.bind(this);
        initViews();
        getServiceStatus();
        parseIntent();
    }

    private void parseIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String dnsModelJSON = getIntent().getExtras().getString("dnsModel", "");
            if (!dnsModelJSON.isEmpty()) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1903);
                if (dnsList == null)
                    getDNSItems();
                DNSModel model = gson.fromJson(dnsModelJSON, DNSModel.class);
                if (model.getName().equals(getString(R.string.custom_dns))) {
                    firstDnsEdit.setText(model.getFirstDns());
                    secondDnsEdit.setText(model.getSecondDns());
                } else {
                    for (int i = 0; i < dnsList.size(); i++) {
                        DNSModel dnsModel = dnsList.get(i);
                        if (dnsModel.getName().equals(model.getName())) {
                            onClick(null, i);
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeSnackbar(getString(R.string.dns_starting));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startButton.performClick();
                    }
                });
            }
        }
    }

    private void getServiceStatus() {
        if (presenter.isWorking()) {
            serviceStarted();
            presenter.getServiceInfo();
        } else {
            serviceStopped();
        }
    }

    @Override
    public void changeStatus(int serviceStatus) {
        if (serviceStatus == SERVICE_OPEN) {
            serviceStarted();
            makeSnackbar(getString(R.string.service_started));
        } else {
            serviceStopped();
            makeSnackbar(getString(R.string.service_stoppped));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                openAboutActivity();
                break;
            case R.id.settings:
                openSettingsActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void setServiceInfo(DNSModel model) {
        chooseButton.setText(model.getName());
        firstDnsEdit.setText(model.getFirstDns());
        secondDnsEdit.setText(model.getSecondDns());
    }

    private void serviceStopped() {
        startButton.setText(R.string.start);
        startButton.setBackgroundResource(R.drawable.button);
        firstDnsEdit.setEnabled(true);
        secondDnsEdit.setEnabled(true);
        firstDnsEdit.setText("");
        secondDnsEdit.setText("");
        chooseButton.setEnabled(true);
        chooseButton.setText(R.string.choose_dns_server);
        chooseButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private void serviceStarted() {
        startButton.setText(R.string.stop);
        startButton.setBackgroundResource(R.drawable.button_red);
        firstDnsEdit.setEnabled(false);
        secondDnsEdit.setEnabled(false);
        chooseButton.setEnabled(false);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_vpn_key_black_24dp);
        drawable.setBounds(40, 0, drawable.getIntrinsicHeight() + 40, drawable.getIntrinsicWidth());
        chooseButton.setCompoundDrawables(drawable, null, null, null);
    }

    private void makeSnackbar(String message) {
        Snackbar.make(activityMain, message, Snackbar.LENGTH_LONG).show();
    }

    private void initViews() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        logo.bringToFront();
        logo.requestLayout();
        logo.invalidate();

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        firstDnsEdit.setFilters(filters);
        secondDnsEdit.setFilters(filters);
        ViewCompat.setTranslationZ(logo, 8);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            presenter.startService(getDnsModel());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private DNSModel getDnsModel() {
        DNSModel dnsModel = new DNSModel();
        String first = firstDnsEdit.getText().toString();
        String second = secondDnsEdit.getText().toString();

        dnsModel.setName(getString(R.string.custom_dns));

        if (dnsList != null)
            for (DNSModel model : dnsList) {
                if (model.getFirstDns().equals(first) && model.getSecondDns().equals(second)) {
                    dnsModel.setName(model.getName());
                }
            }

        dnsModel.setFirstDns(first);
        dnsModel.setSecondDns(second);

        return dnsModel;
    }

    private boolean isValid() {
        boolean result = true;
        firstDnsEdit.setError(null);
        secondDnsEdit.setError(null);

        if (!IP_PATTERN.matcher(firstDnsEdit.getText()).matches()) {
            firstDnsEdit.setError(getString(R.string.enter_valid_dns));
            result = false;
        }

        if (!IP_PATTERN.matcher(secondDnsEdit.getText()).matches()) {
            secondDnsEdit.setError(getString(R.string.enter_valid_dns));
            result = false;
        }

        return result;
    }

    @OnClick({R.id.chooseButton, R.id.startButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chooseButton:
                openChooser();
                break;
            case R.id.startButton:
                startDNS();
                break;
        }
    }

    private void openChooser() {
        CharSequence[] items = getDNSItems();
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(items, this).setTitle(R.string.choose_dns_server).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        ListView listView = dialog.getListView();
        listView.setDivider(ContextCompat.getDrawable(this, R.drawable.divider)); // set color
        listView.setDividerHeight(1);
        listView.setPadding(16, 16, 16, 16);
        dialog.show();
    }

    private CharSequence[] getDNSItems() {
        CharSequence[] result = new CharSequence[17];

        try {
            InputStream is = getAssets().open("dns_servers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            DNSModelJSON dnsModels = gson.fromJson(json, DNSModelJSON.class);
            dnsList = dnsModels.getModelList();
            int counter = 0;
            for (DNSModel dnsModel : dnsList) {
                result[counter] = (dnsModel.getName());
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void startDNS() {
        if (presenter.isWorking()) {
            presenter.stopService();
        } else if (isValid()) {
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CONNECT);
            } else {
                onActivityResult(REQUEST_CONNECT, RESULT_OK, null);
            }
        } else {
            makeSnackbar(getString(R.string.enter_valid_dns));
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        DNSModel model = dnsList.get(which);
        firstDnsEdit.setText(model.getFirstDns());
        secondDnsEdit.setText(model.getSecondDns());
        chooseButton.setText(model.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
