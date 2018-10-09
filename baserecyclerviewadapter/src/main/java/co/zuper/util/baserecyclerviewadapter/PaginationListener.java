package co.zuper.util.baserecyclerviewadapter;

public interface PaginationListener {

    int getTotalPages();

    int getTotalRecords();

    int getPreLoadNumber();

    int getPageLimit();
}
