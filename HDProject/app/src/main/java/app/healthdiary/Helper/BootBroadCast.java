package app.healthdiary.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.healthdiary.Activities.LoginScreen;

public class BootBroadCast extends BroadcastReceiver {
    public BootBroadCast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        /* service*  */
        /*Intent service=new Intent(context, MyService.class);
          context.startService(service);
        */

        /** Activity*/

        Intent newIntent=new Intent(context,LoginScreen.class);
        /* MyActivity action defined in AndroidManifest.xml */
        newIntent.setAction("android.intent.action.MAIN");

        /* MyActivity category defined in AndroidManifest.xml */
        newIntent.addCategory("android.intent.category.LAUNCHER");

            /*
             * If activity is not launched in Activity environment, this flag is
             * mandatory to set
             */
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(newIntent);

        /* application */
        /*Intent app = context.getPackageManager().getLaunchIntentForPackage("course.examples.HealthDiary");
        context.startActivity(app);*/
    }
}
