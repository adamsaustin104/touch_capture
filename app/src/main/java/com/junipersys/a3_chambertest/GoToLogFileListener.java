package com.junipersys.a3_chambertest;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;

class GoToLogFileListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        File file = new File(view.getContext().getFilesDir(), InterceptTouchFrameLayout.FILE_NAME);
        Uri uri = Uri.parse("file://" + file.getAbsolutePath());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        view.getContext().startActivity(intent);
    }
}
