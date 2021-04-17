package com.nsa.CodingAid.ExtraClasses;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jitsi.meet.sdk.BroadcastIntentHelper;

public class clearALlCall {
    public clearALlCall(Context context) {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(context).sendBroadcast(hangupBroadcastIntent);

    }
}
