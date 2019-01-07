package com.example.shinaegi.mcat;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Item holder class for showing description of message logs.
 * It extends ViewHolder which describes an item view and metadata about its place within the RecyclerView
 * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder
 */

public class ItemHolder extends ViewHolder<String>{

    private final TextView itemTextView = (TextView)getView().findViewById(R.id.item);

    private ItemHolder(View itemView)
    {
        super(itemView);
    }



    public static ItemHolder make(ViewGroup parent){

        LayoutInflater viewInflater = LayoutInflater.from(parent.getContext());
        View itemListItemView = viewInflater.inflate(R.layout.item_layout,parent,false);
        return new ItemHolder(itemListItemView);
    }

    @Override
    protected void onSetModel(String newModel)
    {
        itemTextView.setText(newModel);
        itemTextView.setTextColor(0xFFFFFF - newModel.hashCode());
        itemTextView.setBackgroundColor(newModel.hashCode());
    }


}
