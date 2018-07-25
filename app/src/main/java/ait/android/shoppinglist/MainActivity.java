package ait.android.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ait.android.shoppinglist.adapter.ShoppingListRecyclerAdapter;
import ait.android.shoppinglist.data.AppDatabase;
import ait.android.shoppinglist.data.ShoppingItem;
import ait.android.shoppinglist.touch.ShoppingListItemTouchHelperCallback;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class MainActivity extends AppCompatActivity implements ShoppingDialog.ItemHandler {

    private static final String KEY_LAST_START_TIME = "KEY_LAST_START_TIME";
    public final static String KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT";
    public final static String KEY_ITEM_TO_VIEW = "KEY_ITEM_TO_VIEW";
    public static String buttonPressed; // can be "new", "edit", or "view"

    private ShoppingListRecyclerAdapter shoppingListRecyclerAdapter;

    public Context getContext() {
        return this.getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerShoppingList);

        if (isFirstRun()) {
            new MaterialTapTargetPrompt.Builder(MainActivity.this).setTarget(findViewById(R.id.toolbar)).setPrimaryText("Options").setSecondaryText("Tap here to add a new item to your shopping list or delete all of them").show();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initItems(recyclerView);
        saveThatItWasStarted();
    }

    private void showLastStartTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String lastStartTime = sp.getString(KEY_LAST_START_TIME, getString(R.string.this_is_the_first_time));
    }

    public boolean isFirstRun() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_first),true);
    }

    public void saveThatItWasStarted() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(getString(R.string.key_first),false);
        editor.commit();
    }

    public void initItems(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                final List<ShoppingItem> items = AppDatabase.getAppDatabase(MainActivity.this).shoppingItemDao().getAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter = new ShoppingListRecyclerAdapter(items, MainActivity.this);
                        recyclerView.setAdapter(shoppingListRecyclerAdapter);

                        ItemTouchHelper.Callback callback = new ShoppingListItemTouchHelperCallback(shoppingListRecyclerAdapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(recyclerView);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onNewItemCreated(final String name, final String category, final double price, final String description, final boolean purchased) {

        new Thread() {
            @Override
            public void run() {
                final ShoppingItem newItem = new ShoppingItem(name, category, price, description, false);

                long id = AppDatabase.getAppDatabase(MainActivity.this).shoppingItemDao().insertItem(newItem);
                newItem.setItemId(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter.addItem(newItem);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onItemUpdated(final ShoppingItem item) {
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(MainActivity.this).shoppingItemDao().update(item);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter.updateItem(item);
                    }
                });
            }
        }.start();
    }

    public void editItem(ShoppingItem item) {
        ShoppingDialog dialog = new ShoppingDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ITEM_TO_EDIT, item);
        dialog.setArguments(bundle);
        buttonPressed = getString(R.string.edit);
        dialog.show(getSupportFragmentManager(), getString(R.string.shopping_dialog));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteAll() {

        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(MainActivity.this).shoppingItemDao().deleteAll(
                            shoppingListRecyclerAdapter.getShoppingItemsList());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter.clearList();
                    }
                });
            }
        }.start();
    }

    private void showShoppingDialog() {
        new ShoppingDialog().show(getSupportFragmentManager(), getString(R.string.shopping_dialog));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_addNew:
                buttonPressed = getString(R.string.keyword_new);
                showShoppingDialog();
                break;
            case R.id.action_deleteAll:
                deleteAll();
                break;
        }

        return true;

    }

    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
