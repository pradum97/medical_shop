package com.techwhizer.medicalshop.method;

import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class AutoCompleteTextField {

    private SortedSet<String> entries;
    public TextField textField;
    private ContextMenu entriesPopup;
    private ObservableList<String> list;

    public AutoCompleteTextField(TextField textField, ObservableList<String> list) {
        this.textField = textField;
        this.list = list;
    }

    public void init() {
        autoCompleteTextField();
        getEntries().addAll(list);
    }

    private void autoCompleteTextField() {
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();
        textField.textProperty().addListener((observableValue, s, s2) -> {
            if (textField.getText().length() == 0) {
                entriesPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>(entries.subSet(textField.getText().toUpperCase(),
                        textField.getText().toUpperCase() + Character.MAX_VALUE));
                if (entries.size() > 0) {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(textField, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });
        textField.focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> entriesPopup.hide());
    }

    public SortedSet<String> getEntries() {
        return entries;
    }

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            entryLabel.setStyle("-fx-text-fill: white ; -fx-font-weight: bold;");
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);

            item.setOnAction(event -> {
                textField.setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}