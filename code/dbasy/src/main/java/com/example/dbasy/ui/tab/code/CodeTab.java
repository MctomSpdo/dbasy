package com.example.dbasy.ui.tab.code;

import com.example.dbasy.Main;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.invalid.InvalidDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.IOException;

public class CodeTab extends Tab {
    @FXML
    private VBox root;

    @FXML
    private Button btRun;

    @FXML
    private ChoiceBox cbDatabase;

    private CodeArea codeArea;

    private Database source;

    @FXML
    public void initialize() {
        System.out.println("init");
        loadCodeArea();
    }

    public CodeTab(Database db) {
        super("console");
        setSource(db);
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
    }

    public Database getSource() {
        return source;
    }

    public void setSource(Database source) {
        if(source instanceof InvalidDatabase) {
            throw new IllegalArgumentException("Database has to be Valid!");
        }
        this.source = source;
    }

    private void init() throws IOException {
        var fxmlLoader = new FXMLLoader(CodeTab.class.getResource("codetab.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        setContent(root);
    }

    private void loadCodeArea() {
        var codeArea = new CodeArea();
        //Number markings:
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        //ContextMenu:
        codeArea.setContextMenu( new CodeContextMenu() );

        this.codeArea = codeArea;
        this.root.getChildren().add(codeArea);
    }

    private class CodeContextMenu extends ContextMenu
    {
        private MenuItem fold, unfold, print;

        public CodeContextMenu()
        {
            fold = new MenuItem( "Fold selected text" );
            fold.setOnAction( AE -> { hide(); fold(); } );

            unfold = new MenuItem( "Unfold from cursor" );
            unfold.setOnAction( AE -> { hide(); unfold(); } );

            print = new MenuItem( "Print" );
            print.setOnAction( AE -> { hide(); print(); } );

            getItems().addAll( fold, unfold, print );
        }

        /**
         * Folds multiple lines of selected text, only showing the first line and hiding the rest.
         */
        private void fold() {
            ((CodeArea) getOwnerNode()).foldSelectedParagraphs();
        }

        /**
         * Unfold the CURRENT line/paragraph if it has a fold.
         */
        private void unfold() {
            CodeArea area = (CodeArea) getOwnerNode();
            area.unfoldParagraphs( area.getCurrentParagraph() );
        }

        private void print() {
            System.out.println( ((CodeArea) getOwnerNode()).getText() );
        }
    }
}
