package com.gipsyz.safe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.gipsyz.safe.dto.SimpleBeacon;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.List;

/**
 * Created by batman on 06/12/2014.
 */
public class DeviceAdapter extends ArrayAdapter<SimpleBeacon> {

    public DeviceAdapter(Context context, int resource, List<SimpleBeacon> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SimpleBeacon beacon = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.device, parent, false);
        Switch textView = (Switch) rowView.findViewById(R.id.switch2);
        textView.setText(beacon.getUuid());
        rowView.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getContext()).load("DELETE", AppUtils.BEACON_URL + "/" + beacon.getId())
                        .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if(result != null){
                            if((result.getHeaders().code() & 200) == 200) {
                                remove(beacon);
                                notifyDataSetChanged();
                            } else Toast.makeText(getContext(), "Something bad happened", Toast.LENGTH_SHORT).show();
                        } if(e != null) e.printStackTrace();
                    }
                });
            }
        });
        return rowView;
    }
}
