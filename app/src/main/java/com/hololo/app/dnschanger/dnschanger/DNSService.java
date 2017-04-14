package com.hololo.app.dnschanger.dnschanger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hololo.app.dnschanger.DNSChangerApp;
import com.hololo.app.dnschanger.R;
import com.hololo.app.dnschanger.model.DNSModel;
import com.hololo.app.dnschanger.utils.RxBus;
import com.hololo.app.dnschanger.utils.event.GetServiceInfo;
import com.hololo.app.dnschanger.utils.event.ServiceInfo;
import com.hololo.app.dnschanger.utils.event.StartEvent;
import com.hololo.app.dnschanger.utils.event.StopEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class DNSService extends VpnService {
    public final static String DNS_MODEL = "DNSModelIntent";

    @Inject
    RxBus rxBus;
    @Inject
    Context context;
    @Inject
    Gson gson;

    private VpnService.Builder builder = new VpnService.Builder();
    private ParcelFileDescriptor fileDescriptor;
    private Thread mThread;
    private boolean shouldRun = true;
    private DatagramChannel tunnel;
    private DNSModel dnsModel;
    private SharedPreferences preferences;

    private Disposable subscriber;

    private void stopThisService() {
        this.shouldRun = false;
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.edit().putBoolean("isStarted", false).apply();
        preferences.edit().remove("dnsModel").apply();
        Timber.e("Servis kapandı.");
        subscriber.dispose();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        DNSChangerApp.getApplicationComponent().inject(this);
        subscribe();
    }

    private void subscribe() {
        subscriber = rxBus.getEvents().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof StopEvent) {
                    stopThisService();
                } else if (o instanceof GetServiceInfo) {
                    rxBus.sendEvent(new ServiceInfo(dnsModel));
                }
            }
        });
    }

    private void setTunnel(DatagramChannel tunnel) {
        this.tunnel = tunnel;
    }

    private void setFileDescriptor(ParcelFileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    @Override
    public int onStartCommand(final Intent paramIntent, int p1, int p2) {
        mThread = new Thread(new Runnable() {
            public void run() {
                try {
                    dnsModel = paramIntent.getParcelableExtra(DNS_MODEL);

                    String modelJSON = gson.toJson(dnsModel);
                    preferences.edit().putString("dnsModel", modelJSON).apply();

                    setFileDescriptor(builder.setSession(DNSService.this.getText(R.string.app_name).toString()).
                            addAddress("192.168.0.1", 24).addDnsServer(dnsModel.getFirstDns()).addDnsServer(dnsModel.getSecondDns()).establish());
                    setTunnel(DatagramChannel.open());
                    tunnel.connect(new InetSocketAddress("127.0.0.1", 8087));
                    protect(tunnel.socket());
                    while (shouldRun)
                        Thread.sleep(100L);
                } catch (Exception exception) {
                    Timber.e(exception);
                } finally {
                    if (fileDescriptor != null) {
                        try {
                            fileDescriptor.close();
                            setFileDescriptor(null);
                        } catch (IOException e) {
                            Timber.d(e);
                        }
                    }
                }
            }
        }
                , "DNS Changer");
        mThread.start();
        rxBus.sendEvent(new StartEvent());
        preferences.edit().putBoolean("isStarted", true).apply();
        return Service.START_STICKY;
    }
}
