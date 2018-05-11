package com.zuby.user.zubbyrider.view.navigationdrawer.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.CarServiceModel;

import java.util.List;

public class CarAggregationAdapter extends RecyclerView.Adapter<CarAggregationAdapter.MyViewHolder> {

    private List<CarServiceModel.DataBean.DetailsBean> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public RecyclerView cardata;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            cardata = (RecyclerView) view.findViewById(R.id.cartype);
        }
    }

    Context mContext;

    public CarAggregationAdapter(List<CarServiceModel.DataBean.DetailsBean> moviesList, Context context) {
        this.moviesList = moviesList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_service_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CarServiceModel.DataBean.DetailsBean movie = moviesList.get(position);
        holder.title.setText(movie.getAggregationName());
        holder.cardata.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        holder.cardata.setAdapter(new CarTypeAdapter(movie.getArray()));
        holder.cardata.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
