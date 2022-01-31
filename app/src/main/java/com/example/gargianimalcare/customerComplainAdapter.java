package com.example.gargianimalcare;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


public class customerComplainAdapter extends RecyclerView.Adapter<customerComplainAdapter.ViewHolder>  implements Filterable {
    Context c;
    ArrayList<complaintsHelperClass> localDataSet, orig, defaultData;
    int fromWhich;

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<complaintsHelperClass> results = new ArrayList<complaintsHelperClass>();
                if (orig == null)
                    orig = localDataSet;
                if (constraint == null || constraint.length() == 0) {
                    oReturn.values = orig;
                }
                else if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final complaintsHelperClass g : orig) {
                            if (g.getComplainID().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }


                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                localDataSet = (ArrayList<complaintsHelperClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subject;
        private final TextView id;
        private final TextView time;
        private final TextView status;
        private final ImageView statusIcon;
        private final RelativeLayout complainItem;




        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            subject = (TextView) view.findViewById(R.id.subject);
            id = (TextView) view.findViewById(R.id.complainid);
            time = (TextView) view.findViewById(R.id.time);
            status=(TextView)view.findViewById(R.id.status);
            complainItem=(RelativeLayout)view.findViewById(R.id.complainitem);
            statusIcon=view.findViewById(R.id.icon_status_recycler);
        }


    }


    public customerComplainAdapter(ArrayList<complaintsHelperClass> data, Context context, int fr) {
        defaultData = data;
        localDataSet=data;
        c=context;
        fromWhich=fr;
        Log.i(TAG, "customerComplainAdapter: ..."+data.size());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.complain_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.subject.setText("Subject: "+localDataSet.get(position).getSubject());
        Log.i(TAG, "onBindViewHolder: ...."+localDataSet.get(position).getSubject());
        viewHolder.id.setText("Complain ID: "+localDataSet.get(position).getComplainID());
            Date date = new Date ();
            date.setTime((long)localDataSet.get(position).getTimeOfComplain()*1000);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        viewHolder.time.setText("Time: "+dateFormat.format(date));
        viewHolder.status.setText("Status: "+localDataSet.get(position).getStatus());
        viewHolder.complainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(c,ComplaintDetails.class);
                i.putExtra("complainID",localDataSet.get(position).getComplainID());
                i.putExtra("from", fromWhich);
                c.startActivity(i);
            }
        });

        if(localDataSet.get(position).getStatus().equals("pending"))
        {
            viewHolder.statusIcon.setImageResource(R.drawable.ic_baseline_pending1_outline_24);
        }
        else if(localDataSet.get(position).getStatus().equals("in process"))
        {
            viewHolder.statusIcon.setImageResource(R.drawable.ic_baseline_process_24);
        }
        else
        {
            viewHolder.statusIcon.setImageResource(R.drawable.ic_baseline_done_24);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


}

