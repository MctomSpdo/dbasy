package com.example.dbasy.ui.dialogs;

import com.example.dbasy.Main;

import java.io.IOException;

public class DialogUtil {

    /**
     * Shows a renameDialog
     * @param labelText labelText on the Dialog
     * @param oldName current name of the given Item to rename
     * @return null if user cancels or on Error, name otherwise
     */
    public static String renameDialog(String labelText, String oldName) {
        var dialog = new RenameDialog();
        try {
            return dialog.showDialog(labelText, oldName);
        } catch (IOException e) {
            Main.RESOURCES.log.fatal("Could not load internal Files", e);
        }
        return null;
    }
}
