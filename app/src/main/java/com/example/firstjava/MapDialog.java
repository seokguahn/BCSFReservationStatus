package com.example.firstjava;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MapDialog extends DialogFragment {
    String title;
    String address;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_map, null);

        String arg = this.getTag();
        String[] array = arg.split("#");
        title = array[0];
        address = array[1];

        TextView twTitle = dialog.findViewById(R.id.digmap_title);
        twTitle.setText(title);

        TextView twAddress = dialog.findViewById(R.id.digmap_address);
        twAddress.setText(address);
        twAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mapURL = "http://map.naver.com/?query=" + address;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mapURL));
                startActivity(i);

                MapDialog.this.getDialog().cancel();
            }
        });

        builder.setView(dialog);
        return builder.create();
    }
}
