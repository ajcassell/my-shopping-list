package ait.android.shoppinglist.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

import ait.android.shoppinglist.MainActivity;
import ait.android.shoppinglist.R;
import ait.android.shoppinglist.data.AppDatabase;
import ait.android.shoppinglist.data.ShoppingItem;
import ait.android.shoppinglist.touch.ShoppingListTouchHelperAdapter;

public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ViewHolder> implements ShoppingListTouchHelperAdapter {
    private List<ShoppingItem> shoppingItemsList;
    private Context context;

    public ShoppingListRecyclerAdapter(List<ShoppingItem> list, Context context) {
        shoppingItemsList = list;
        this.context = context;
    }

    public void clearList() {
        shoppingItemsList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item_row, parent, false);
        return new ViewHolder(viewRow);
    }

    public int setCategoryImage(String category) {
        int drawable_resource = R.drawable.other;
        switch (category) {
            case "Food":
                drawable_resource = R.drawable.groceries;
                break;
            case "Electronics":
                drawable_resource = R.drawable.electronics;
                break;
            case "Household":
                drawable_resource = R.drawable.household;
                break;
            case "Clothing":
                drawable_resource = R.drawable.clothing;
                break;
            case "Book":
                drawable_resource = R.drawable.books;
                break;
            case "Other":
                drawable_resource = R.drawable.other;
                break;
        }
        return drawable_resource;
    }

    public void setBtnDeleteAction(final ViewHolder holder) {
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDismiss(holder.getAdapterPosition());
            }
        });
    }

    public void setBtnEditAction(final ViewHolder holder) {
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).editItem(shoppingItemsList.get(holder.getAdapterPosition()));
            }
        });
    }

    public void setBtnDetailsAction(final ViewHolder holder) {
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).showMessage(shoppingItemsList.get(holder.getAdapterPosition()).getItemDescription());
            }
        });
    }

    public void setCheckBoxAction(final ViewHolder holder) {
        holder.cbPurchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ShoppingItem item = shoppingItemsList.get(holder.getAdapterPosition());
                item.setPurchased(holder.cbPurchased.isChecked());

                new Thread() {
                    @Override
                    public void run() {
                        AppDatabase.getAppDatabase(context).shoppingItemDao().update(item);
                    }
                }.start();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvName.setText(shoppingItemsList.get(holder.getAdapterPosition()).getItemName());
        holder.tvPrice.setText(Double.toString((shoppingItemsList.get(holder.getAdapterPosition()).getItemPrice())));
        holder.cbPurchased.setChecked(shoppingItemsList.get(holder.getAdapterPosition()).isPurchased());
        holder.category = shoppingItemsList.get(holder.getAdapterPosition()).getItemCategory();
        holder.categoryImage.setImageResource(setCategoryImage(holder.category));

        setBtnDeleteAction(holder);
        setBtnEditAction(holder);
        setBtnDetailsAction(holder);
        setCheckBoxAction(holder);
    }

    @Override
    public int getItemCount() {
        return shoppingItemsList.size();
    }

    public void addItem(ShoppingItem item) {
        shoppingItemsList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(final int position) {
        final ShoppingItem itemToDelete = shoppingItemsList.get(position);

        shoppingItemsList.remove(itemToDelete);
        notifyItemRemoved(position);

        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).shoppingItemDao().delete(itemToDelete);
            }
        }.start();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(shoppingItemsList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(shoppingItemsList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateItem(ShoppingItem item) {
        int editPos = findItemIndexByItemId(item.getItemId());
        shoppingItemsList.set(editPos, item);
        notifyItemChanged(editPos);
    }

    private int findItemIndexByItemId(long itemId) {
        for (int i = 0; i < shoppingItemsList.size(); i++) {
            if (shoppingItemsList.get(i).getItemId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPrice;
        private final CheckBox cbPurchased;
        private final Button btnDelete;
        private final Button btnEdit;
        private final Button btnDetails;
        private final ImageView categoryImage;
        private String category;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName =  itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            cbPurchased = itemView.findViewById(R.id.cbPurchased);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            categoryImage = itemView.findViewById(R.id.icon);
        }
    }

    public List<ShoppingItem> getShoppingItemsList() {
        return shoppingItemsList;
    }
}
