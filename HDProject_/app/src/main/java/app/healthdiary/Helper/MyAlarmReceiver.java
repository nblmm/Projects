package app.healthdiary.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.healthdiary.Activities.WeeklyQ1Direct;

public class MyAlarmReceiver extends BroadcastReceiver {
    public MyAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(context, RandomMomentContacts.class);
        intent.setClass(context, WeeklyQ1Direct.class);
        intent.putExtra("notify", 1);
        context.startActivity(intent);
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
