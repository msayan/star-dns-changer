package com.hololo.app.dnschanger.utils.event;

import com.hololo.app.dnschanger.model.DNSModel;

public class ServiceInfo {
    private DNSModel model;

    public ServiceInfo(DNSModel model) {
        this.model = model;
    }

    public DNSModel getModel() {
        return model;
    }

    public void setModel(DNSModel model) {
        this.model = model;
    }
}
