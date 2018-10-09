package co.zuper.util.baserecyclerviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private BaseRecyclerViewAdapter baseAdapter;

    private OnItemClickListener onItemClickListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
        if (onItemClickListener != null) {
            if (!itemView.isClickable())
                itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }
    }

    void setBaseAdapter(BaseRecyclerViewAdapter baseAdapter) {
        this.baseAdapter = baseAdapter;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(baseAdapter, getAdapterPosition());
        }
    }
}
