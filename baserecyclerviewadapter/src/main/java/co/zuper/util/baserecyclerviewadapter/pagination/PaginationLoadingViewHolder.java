package co.zuper.util.baserecyclerviewadapter.pagination;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import co.zuper.util.baserecyclerviewadapter.BaseViewHolder;
import co.zuper.util.baserecyclerviewadapter.R;

import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants.LOAD_MORE_STATUS_OFFLINE_ERROR;

public class PaginationLoadingViewHolder extends BaseViewHolder implements View.OnClickListener {

    private PaginationRetryCallback callback;
    private ConstraintLayout loadingView;
    private ConstraintLayout errorView;
    private TextView errorMsgView;

    public PaginationLoadingViewHolder(View itemView, PaginationRetryCallback callback) {
        super(itemView);
        this.callback = callback;
        loadingView = itemView.findViewById(R.id.layout_loading);
        errorView = itemView.findViewById(R.id.layout_error);
        errorMsgView = itemView.findViewById(R.id.text_error_msg);
    }

    @Override
    public void onClick(View v) {
        callback.onRetryClicked();
    }

    public void showLoadingView() {
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    public void showErrorView(int errorType) {
        if (errorType == LOAD_MORE_STATUS_OFFLINE_ERROR) {
            errorMsgView.setText("No internet connection.");
        } else {
            errorMsgView.setText("Something went wrong.");
        }
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setOnClickListener(this);
    }
}
