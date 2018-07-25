package ait.android.shoppinglist.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ShoppingItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long itemId;

    @ColumnInfo(name = "item_name")
    private String itemName;
    @ColumnInfo(name = "item_price")
    private double itemPrice;
    @ColumnInfo(name = "is_purchased")
    private boolean isPurchased;
    @ColumnInfo(name="item_category")
    private String itemCategory;
    @ColumnInfo(name="item_description")
    private String itemDescription;

    public ShoppingItem(String itemName, String itemCategory, double itemPrice, String itemDescription, boolean isPurchased) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.isPurchased = isPurchased;
        this.itemCategory = itemCategory;
        this.itemDescription = itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public String getItemCategory() { return itemCategory; }

    public String getItemDescription() { return itemDescription; }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setPurchased(boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public void setItemCategory(String cat) {
        itemCategory = cat;
    }

    public void setItemDescription(String description) {
        itemDescription = description;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
}
