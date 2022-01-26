package comp5216.sydney.edu.au.timefiles.ranking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.R;

public class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.MyViewHolder> {

    Context context;
    ArrayList<RankingModel> list;

    public RankingListAdapter(Context context, ArrayList<RankingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankingListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_ranking, parent, false);
        RankingListAdapter.MyViewHolder holder = new RankingListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingListAdapter.MyViewHolder holder, int position) {
        RankingModel rank = list.get(position);


        DecimalFormat df = new DecimalFormat("0.0");
        String rate_s = "Remains: " + Float.toString(Float.parseFloat(df.format(rank.getRatio() * 100))) + "%" ;

        String ratio = rank.getActual_time() / 60000 + "/" + rank.getPlanned_time() / 60000 + " mins";

        holder.nameView.setText(rank.getUserName());
        holder.ratioView.setText(ratio);
        holder.completionView.setText(rate_s);
        holder.rankView.setText(String.valueOf(rank.getRank()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView ratioView;
        TextView completionView;
        TextView rankView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.user_name);
            ratioView = itemView.findViewById(R.id.time_ratio);
            completionView = itemView.findViewById(R.id.completion_rate);
            rankView = itemView.findViewById(R.id.rank);
        }
    }
}