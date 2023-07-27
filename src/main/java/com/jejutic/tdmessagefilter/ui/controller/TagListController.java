package com.jejutic.tdmessagefilter.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.Set;

public class TagListController {
    public ListView<String> tags;
    public Button removeTagButton;
    public TextField newTagField;
    public Button addNewTagButton;
    private Set<String> tagListSource;

    @FXML
    private void onAddNewTagClick(ActionEvent actionEvent) {
        String text = newTagField.getText();
        if (text != null && !text.equals("") && !tagListSource.contains(text)) {
            tagListSource.add(text);
            tags.getItems().add(text);
            newTagField.setText("");
        }
    }

    @FXML
    private void onRemoveTagClick(ActionEvent actionEvent) {
        int selectedInd = tags.getSelectionModel().getSelectedIndex();

        if (selectedInd != -1) {
            String tagForRemove = tags.getItems().get(selectedInd);
            tags.getItems().remove(selectedInd);
            tagListSource.remove(tagForRemove);
        }
    }

    public void setTagListSource(Set<String> tagListSource) {
        tags.getItems().addAll(tagListSource);

        this.tagListSource = tagListSource;
    }
}
