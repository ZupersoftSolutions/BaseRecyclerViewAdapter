package co.zuper.util.baserecyclerviewadapter.pagination;

public interface PaginationListener {

    int getTotalPages();

    int getPreLoadNumber();

    int getPageLimit();
}
