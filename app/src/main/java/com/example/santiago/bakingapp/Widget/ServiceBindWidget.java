package com.example.santiago.bakingapp.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ServiceBindWidget extends RemoteViewsService {
    private static final String TAG = "ServiceBindWidget";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListAdapter(this);
    }
}
