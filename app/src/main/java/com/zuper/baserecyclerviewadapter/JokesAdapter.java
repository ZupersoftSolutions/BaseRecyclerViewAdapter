package com.zuper.baserecyclerviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuper.baserecyclerviewadapter.baserecyclerviewadapter.R;
import com.zuper.baserecyclerviewadapter.model.Joke;

import co.zuper.util.baserecyclerviewadapter.BaseRecyclerViewAdapter;
import co.zuper.util.baserecyclerviewadapter.BaseViewHolder;

public class JokesAdapter extends BaseRecyclerViewAdapter<Joke> {

    @Override
    public BaseViewHolder createItemViewHolder(ViewGroup parent) {
        return new JokeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joke, parent, false));
    }

    @Override
    public void bindHolder(RecyclerView.ViewHolder holder, int position) {
        ((JokeViewHolder) holder).bind(getData().get(position));
    }

    public static class JokeViewHolder extends BaseViewHolder {

        private TextView punchLineView, setupView;

        public JokeViewHolder(View itemView) {
            super(itemView);
            punchLineView = itemView.findViewById(R.id.text_punchline);
            setupView = itemView.findViewById(R.id.text_setup);
        }

        public void bind(Joke joke) {
            setupView.setText(joke.getSetup());
            punchLineView.setText(joke.getPunchline());
        }
    }
}
