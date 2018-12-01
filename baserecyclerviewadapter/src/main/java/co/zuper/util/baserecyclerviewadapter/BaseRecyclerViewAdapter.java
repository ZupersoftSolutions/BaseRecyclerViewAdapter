package co.zuper.util.baserecyclerviewadapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.zuper.util.baserecyclerviewadapter.listener.OnItemClickListener;
import co.zuper.util.baserecyclerviewadapter.listener.OnItemLongClickListener;
import co.zuper.util.baserecyclerviewadapter.pagination.PaginationListener;
import co.zuper.util.baserecyclerviewadapter.pagination.PaginationLoadMoreCallback;
import co.zuper.util.baserecyclerviewadapter.pagination.PaginationLoadingViewHolder;
import co.zuper.util.baserecyclerviewadapter.pagination.PaginationRetryCallback;

import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .DEFAULT_FIRST_PAGE_NO;
import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .LOAD_MORE_STATUS_ERROR;
import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .LOAD_MORE_STATUS_LOADING;
import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .LOAD_MORE_STATUS_OFFLINE_ERROR;
import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .LOAD_MORE_STATUS_SUCCESS;
import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .PAGINATION_LOADING_TYPE;
import static co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants
        .PAGINATION_USER_DEFINED_TYPE;

public abstract class BaseRecyclerViewAdapter<D> extends RecyclerView.Adapter<RecyclerView
        .ViewHolder> implements PaginationRetryCallback {

    private final String TAG = this.getClass().getSimpleName();

    private List<D> data;
    private int firstPageNo = DEFAULT_FIRST_PAGE_NO;
    private int currentPageNo = firstPageNo;
    private boolean isPaginationEnabled = false;
    private boolean isLoadingViewAdded = false;
    private boolean shouldShowLoadingView = true;
    private boolean shouldShowErrorView = true;
    private int loadMoreStatus = LOAD_MORE_STATUS_SUCCESS;
    private int paginationLoadingLayoutId = R.layout.item_pagination_loading;

    private PaginationListener paginationListener;
    private PaginationLoadMoreCallback loadMoreCallback;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private Handler handler;

    public BaseRecyclerViewAdapter() {
        data = new ArrayList<>();
        handler = new Handler();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
                return createItemViewHolder(parent);

            case PAGINATION_LOADING_TYPE:
                return new PaginationLoadingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(paginationLoadingLayoutId, parent, false), this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).setBaseAdapter(this);
        switch (getItemViewType(position)) {
            default:
                if (onItemClickListener != null) {
                    ((BaseViewHolder) holder).setOnItemClickListener(onItemClickListener);
                }
                if (onItemLongClickListener != null) {
                    ((BaseViewHolder) holder).setOnItemLongClickListener(onItemLongClickListener);
                }
                bindHolder(holder, position);
                if (isPaginationEnabled) {
                    loadMore(position);
                }
                break;

            case PAGINATION_LOADING_TYPE:
                PaginationLoadingViewHolder viewHolder = (PaginationLoadingViewHolder) holder;
                switch (loadMoreStatus) {
                    case LOAD_MORE_STATUS_LOADING:
                        viewHolder.showLoadingView();
                        break;

                    case LOAD_MORE_STATUS_ERROR:
                        viewHolder.showErrorView(LOAD_MORE_STATUS_ERROR);
                        break;

                    case LOAD_MORE_STATUS_OFFLINE_ERROR:
                        viewHolder.showErrorView(LOAD_MORE_STATUS_OFFLINE_ERROR);
                        break;
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getItemCount() - 1 && isLoadingViewAdded) ? PAGINATION_LOADING_TYPE :
                PAGINATION_USER_DEFINED_TYPE;
    }

    @Override
    public int getItemCount() {
        return hasLoadingViewAdded() ? data.size() + 1: data.size();
    }

    @Override
    public void onRetryClicked() {
        loadMoreCallback.loadMore(currentPageNo);
    }

    private boolean hasLoadingViewAdded(){
        return loadMoreStatus != LOAD_MORE_STATUS_SUCCESS;
    }

    private int getPageNoForPosition(int position) {
        return ((getItemCount() - 1) - position) / paginationListener.getPageLimit();
    }

    private void loadMore(int position) {
        if (getItemCount() == 0) {
            return;
        }

        if (loadMoreStatus != LOAD_MORE_STATUS_SUCCESS) {
            return;
        }

        if (currentPageNo > paginationListener.getTotalPages()) {
            return;
        }

        if (paginationListener.getPreLoadNumber() <= getItemCount() && (getItemCount() - position
                == paginationListener.getPreLoadNumber())) {
            loadMoreCallback.loadMore(currentPageNo);
            if(shouldShowLoadingView) {
                showLoadingView();
            }
        }
    }

    private void addEmptyData() {
//        Log.d(TAG, "AddEmptyData called");
//        data.add(null);
//        Log.d(TAG, "DATA SIZE after adding null - " + data.size());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                notifyItemInserted(data.size());
//            }
//        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(data.size());
            }
        });
    }

    private void removeEmptyData() {
//        Log.d(TAG, "RemoveEmptyData called");
//        Log.d(TAG, "DATA SIZE before remove - " + data.size());
//        if (data.size() > 0) {
//            data.remove(data.size() - 1);
//            Log.d(TAG, "DATA SIZE after removed - " + data.size());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    notifyItemInserted(data.size());
//                }
//            });
//        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(data.size());
            }
        });
    }

    private void showLoadingView() {
        loadMoreStatus = LOAD_MORE_STATUS_LOADING;
        isLoadingViewAdded = true;
        addEmptyData();
    }

    private void showLoadMoreErrorView(int errorType) {
        if (errorType == LOAD_MORE_STATUS_OFFLINE_ERROR) {
            loadMoreStatus = LOAD_MORE_STATUS_OFFLINE_ERROR;
        } else {
            loadMoreStatus = LOAD_MORE_STATUS_ERROR;
        }
        isLoadingViewAdded = true;
        addEmptyData();
    }

    private void removeLoadingViewHolder() {
        if(isLoadingViewAdded) {
            isLoadingViewAdded = false;
            removeEmptyData();
        }
    }

    // #######################################
    // Pagination methods
    // #######################################

    /**
     * Method must be called once you get the success response from data source
     */
    public void onLoadMoreCompleted() {
        currentPageNo++;
        loadMoreStatus = LOAD_MORE_STATUS_SUCCESS;
        removeLoadingViewHolder();
    }

    /**
     * Method must be called when there is an error in fetching data from data source
     *
     * @param errorType
     */
    public void onLoadMoreFailed(int errorType) {
        removeLoadingViewHolder();
        if(shouldShowErrorView) {
            showLoadMoreErrorView(errorType);
        }
    }

    public boolean isFirstPage() {
        return currentPageNo == 1;
    }

    public void setPaginationLoadingLayout(int id) {
        this.paginationLoadingLayoutId = id;
    }

    public void setLoadMoreLoadingViewVisibility(boolean isVisible) {
        this.shouldShowLoadingView = isVisible;
    }

    public void setLoadMoreErrorViewVisibility(boolean isVisible) {
        this.shouldShowErrorView = isVisible;
    }

    /**
     * Set <code>true</code> to enable pagination or <code>false</code> to disable pagination
     *
     * @param paginationEnabled - flag
     */
    public void setPaginationEnabled(boolean paginationEnabled) {
        isPaginationEnabled = paginationEnabled;
    }

    // #######################################
    // Set listeners
    // #######################################

    /**
     * Set pagination listener
     *
     * @param paginationListener - listener
     */
    public void setPaginationListener(PaginationListener paginationListener) {
        this.paginationListener = paginationListener;
    }

    /**
     * Set load more callback
     *
     * @param loadMoreCallback - callback
     */
    public void setPaginationLoadMoreCallback(PaginationLoadMoreCallback loadMoreCallback) {
        this.loadMoreCallback = loadMoreCallback;
    }

    /**
     * Sets item click listener
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Sets item long click listener
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    // #######################################
    // Data related methods
    // #######################################

    public List<D> getData() {
        return data;
    }

    public void add(D d) {
        data.add(d);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<D> moveResults, boolean shouldCallNotifyDataSetChanged) {
        if (!shouldCallNotifyDataSetChanged) {
            for (D result : moveResults) {
                data.add(result);
            }
            notifyItemRangeInserted(getItemCount(), moveResults.size());
        } else {
            addSet(moveResults);
        }
    }

    private void addSet(List<D> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }

//    public void addWithRange(int fromPosition, int toPosition, List<D> d) {
//        for (int i = fromPosition, j = 0; i <= toPosition; i++, j++) {
//            data.add(i, d.get(j));
//        }
//        notifyItemRangeChanged(fromPosition, toPosition - fromPosition);
//    }

    public void updateItem(int positionToUpdate, D d) {
        data.set(positionToUpdate, d);
        notifyItemChanged(positionToUpdate);
    }

    private void remove(D r) {
        int position = data.indexOf(r);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public D getItem(int position) {
        return data.get(position);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void setFirstPageNo(int pageNo) {
        firstPageNo = pageNo;
    }

    public void resetAdapter(){
        if(!isEmpty()){
            clear();
        }
        currentPageNo = firstPageNo;
    }

    // #######################################
    // Abstract methods
    // #######################################

    /**
     * Method should return User Defined ViewHolder which should extend {@link BaseViewHolder}
     *
     * @param parent
     * @return
     */
    public abstract BaseViewHolder createItemViewHolder(ViewGroup parent);

    /**
     * Method called from BaseRecyclerViewAdapter to bind User Defined ViewHolder
     *
     * @param holder
     * @param position
     */
    public abstract void bindHolder(RecyclerView.ViewHolder holder, int position);
}
