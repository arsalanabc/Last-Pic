package com.skeedo.lastpic.Managers;


import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionManager  {

    private ConnectivityManager cm;

    public ConnectionManager(Context context){
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnected(){
        return this.cm.getActiveNetworkInfo() != null &&
                this.cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}