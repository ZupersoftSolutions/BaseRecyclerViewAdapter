package co.zuper.util.baserecyclerviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import co.zuper.util.baserecyclerviewadapter.listener.OnItemClickListener;
import co.zuper.util.baserecyclerviewadapter.listener.OnItemLongClickListener;

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    private View itemView;
    private BaseRecyclerViewAdapter baseAdapter;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    void setBaseAdapter(BaseRecyclerViewAdapter baseAdapter) {
        this.baseAdapter = baseAdapter;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        if (!itemView.isClickable()) {
            itemView.setClickable(true);
        }
        itemView.setOnClickListener(this);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        if (!itemView.isClickable()) {
            itemView.setClickable(true);
        }
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(baseAdapter, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(baseAdapter, getAdapterPosition());
            return true;
        }
        return false;
    }
}
