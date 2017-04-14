package com.hololo.app.dnschanger.dnschanger;

import com.hololo.app.dnschanger.model.DNSModel;

public interface IDNSView {
    void changeStatus(int serviceStatus);

    void setServiceInfo(DNSModel model);
}
