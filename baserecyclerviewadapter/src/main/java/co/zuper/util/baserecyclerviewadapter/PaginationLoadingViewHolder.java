package co.zuper.util.baserecyclerviewadapter;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import static co.zuper.util.baserecyclerviewadapter.PaginationConstants.LOAD_MORE_STATUS_OFFLINE_ERROR;

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

    void showLoadingView() {
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    void showErrorView(int errorType) {
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
