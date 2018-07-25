package ait.android.shoppinglist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import ait.android.shoppinglist.data.ShoppingItem;

public class ShoppingDialog extends DialogFragment implements  AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface ItemHandler {
        public void onNewItemCreated(final String name, final String category, final double price, final String description, final boolean purchased);
        public void onItemUpdated(ShoppingItem item);
    }

    private ItemHandler itemHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ItemHandler) {
            itemHandler = (ItemHandler) context;
        } else {
            throw new RuntimeException(getString(R.string.no_implement));
        }
    }

    private EditText etName;
    private EditText etPrice;
    private EditText etDescription;
    private CheckBox cbAlreadyPurchased;
    private Spinner spinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String choice;
        switch(MainActivity.buttonPressed) {
            case "New":
                choice = getString(R.string.keyword_new);
                break;
            case "Edit":
                choice = getString(R.string.edit);
                break;
            default:
                choice = "";
                break;
        }
        builder.setTitle(choice);

        View newItem = getActivity().getLayoutInflater().inflate(R.layout.shopping_dialog, null, false);

        etName = newItem.findViewById(R.id.etName);
        etPrice = newItem.findViewById(R.id.etPrice);
        etDescription = newItem.findViewById(R.id.etDescription);
        cbAlreadyPurchased = newItem.findViewById(R.id.cbAlreadyPurchased);
        spinner = newItem.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if (getArguments() != null) {
            ShoppingItem itemToEditOrView;
            if (getArguments().containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                itemToEditOrView = (ShoppingItem) getArguments().getSerializable(MainActivity.KEY_ITEM_TO_EDIT);
            } else {
                itemToEditOrView = (ShoppingItem) getArguments().getSerializable(MainActivity.KEY_ITEM_TO_VIEW);
            }

            etName.setText(itemToEditOrView.getItemName());
            etPrice.setText(Double.toString(itemToEditOrView.getItemPrice()));
            etDescription.setText(itemToEditOrView.getItemDescription());
            cbAlreadyPurchased.setChecked(itemToEditOrView.isPurchased());
            spinner.setSelection(adapter.getPosition(itemToEditOrView.getItemCategory()));

        }

        builder.setView(newItem);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        final AlertDialog alertDialog = (AlertDialog)getDialog();

        if (alertDialog != null) {
            Button positiveButton = (Button)alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        // in edit or new mode
                        // new/to be updated variables
                        String newName = "";
                        String newDescription = "";
                        String newCategory = "";
                        double newPrice = 0;
                        boolean newIsPurchased = false;

                        if (!TextUtils.isEmpty(etName.getText())) {
                            newName = etName.getText().toString();

                            if (!TextUtils.isEmpty(etPrice.getText())) {
                                newPrice = Double.parseDouble(etPrice.getText().toString());

                                if (!TextUtils.isEmpty(etName.getText())) {

                                    if (!TextUtils.isEmpty(etDescription.getText())) {
                                        newDescription = etDescription.getText().toString();
                                    } else {
                                        newDescription = getString(R.string.no_description);
                                    }

                                    if (cbAlreadyPurchased.isChecked()) {
                                        newIsPurchased = true;
                                    } else {
                                        newIsPurchased = false;
                                    }

                                    switch (spinner.getSelectedItem().toString()) {
                                        case "Food":
                                            newCategory = getString(R.string.food);
                                            break;
                                        case "Electronics":
                                            newCategory = getString(R.string.electronics);
                                            break;
                                        case "Household":
                                            newCategory = getString(R.string.household);
                                            break;
                                        case "Clothing":
                                            newCategory = getString(R.string.clothing);
                                            break;
                                        case "Book":
                                            newCategory = getString(R.string.book);
                                            break;
                                        case "Other":
                                            newCategory = getString(R.string.other);
                                            break;
                                        default:
                                            newCategory = getString(R.string.other);
                                            break;
                                    }

                                    if (getArguments() != null && getArguments().containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {

                                        ShoppingItem itemToEdit = (ShoppingItem) getArguments().getSerializable(MainActivity.KEY_ITEM_TO_EDIT);
                                        itemToEdit.setItemName(newName);
                                        itemToEdit.setItemPrice(newPrice);
                                        itemToEdit.setItemDescription(newDescription);
                                        itemToEdit.setPurchased(cbAlreadyPurchased.isChecked());
                                        itemHandler.onItemUpdated(itemToEdit);
                                    } else {
                                        itemHandler.onNewItemCreated(newName, newCategory, newPrice, newDescription, newIsPurchased);
                                    }

                                    alertDialog.dismiss();

                                } else {
                                    etName.setError(getString(R.string.empty_field));
                                }
                            } else {
                                etPrice.setError(getString(R.string.empty_field));
                            }
                        } else {
                            etName.setError(getString(R.string.empty_field));
                        }
                }
            });
        }
    }


}
