package com.example.shinaegi.mcat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by shinaegi on 2017-06-17.
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
        itemTextView.setBackgroundColor(newModel.hashCode());
    }


}
