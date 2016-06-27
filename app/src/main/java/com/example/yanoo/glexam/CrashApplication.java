package com.example.yanoo.glexam;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.ReportField;

import android.app.Application;
@ReportsCrashes(
        formKey = "",
        mailTo = "moohoorama@gmail.com",
        customReportContent = {
                ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE, ReportField.LOGCAT },
        resToastText = R.string.error_toast,
        //mode = ReportingInteractionMode.DIALOG,
        mode = ReportingInteractionMode.TOAST,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.error_title,
        resDialogCommentPrompt = R.string.error_comment,
        resDialogText = R.string.error_text
)

/** ACRA
 * Created by Yanoo on 2016. 6. 20..
 */
public class CrashApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        ACRA.init(this);
    }
}
