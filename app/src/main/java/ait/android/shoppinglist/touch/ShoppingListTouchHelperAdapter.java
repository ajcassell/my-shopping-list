package ait.android.shoppinglist.touch;

public interface ShoppingListTouchHelperAdapter {
    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
