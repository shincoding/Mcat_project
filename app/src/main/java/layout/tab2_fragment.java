package layout;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.example.shinaegi.mcat.ItemHolder;
import com.example.shinaegi.mcat.MainActivity;
import com.example.shinaegi.mcat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class tab2_fragment extends Fragment {

    private Button btnTEST;
    private CustomAdapter recyclerViewAdapter;
    public static ArrayList<String> list_added_messages;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2_fragment, container, false);

        list_added_messages = new ArrayList<String>();

        recyclerViewAdapter = new CustomAdapter();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        final Handler handler = new Handler();
        final Runnable worker = new Runnable() {
            @Override
            public void run() {

                for (String item : MainActivity.list_times){
                    Log.d("this time:", item);
                    if (!list_added_messages.contains(item))
                    {
                        recyclerViewAdapter.addMessage(item);

                        list_added_messages.add(item);
                    }

                }


                handler.postDelayed(this, 2000);

            }
        };
        handler.post(worker);


        return view;
    }


    private class CustomAdapter extends RecyclerView.Adapter<ItemHolder>
    {

        private List<String> items = new ArrayList<>();

        public CustomAdapter()
        {
            super();
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position)
        {
            return items.get(position).hashCode();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return ItemHolder.make(parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder,int position)
        {
            holder.setModel(items.get(position));
        }

        @Override
        public int getItemCount()
        {
            return items.size();
        }

        public void addMessage(String message)
        {
            int listIndex = (int) Math.floor((1-Math.random())*items.size());
            items.add(listIndex, message);
            notifyDataSetChanged();
        }

        public void shuffleExisting()
        {
            Collections.shuffle(items);
            notifyDataSetChanged();
        }

        public void removeRandom()
        {
            if (!items.isEmpty())
            {
                int listIndex = (int) Math.floor((1-Math.random())*items.size());
                items.remove(listIndex);
                notifyDataSetChanged();
            }
        }
    }

}
