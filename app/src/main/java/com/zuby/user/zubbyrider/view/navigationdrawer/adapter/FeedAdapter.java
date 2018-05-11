package com.zuby.user.zubbyrider.view.navigationdrawer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.FeedModel;

import java.util.List;
 
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {
 
    private List<FeedModel.DataBean> moviesList;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
 
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.content);
            year = (TextView) view.findViewById(R.id.link);
        }
    }
 
 
    public FeedAdapter(List<FeedModel.DataBean> moviesList) {
        this.moviesList = moviesList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_bottomsheet, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        FeedModel.DataBean movie = moviesList.get(position);
//        holder.title.setText(movie.getFeed_title());
//        holder.genre.setText(movie.getFeed_content());
//        holder.year.setText(movie.getFeed_html_links());

    }
 
    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}