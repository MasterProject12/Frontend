package com.app.travel.flare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IncidentTypeAdapter extends ArrayAdapter {

    List<String> list;
    public IncidentTypeAdapter(@NonNull Context context, int resource, List<String> list) {
        super(context, resource);
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_adapter_view, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.incidentTypeTV);
        String type = (String) getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (type != null) {
            textViewName.setText(type);
        }
        return convertView;
    }
}