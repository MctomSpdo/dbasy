package com.example.dbasy.ui.tab.code;

import com.example.dbasy.Main;
import com.example.dbasy.Resource;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.invalid.InvalidDatabase;
import com.example.dbasy.ui.UiUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeTab extends Tab implements Resource {
    @FXML
    private VBox root;

    @FXML
    private Button btRun;

    @FXML
    private ChoiceBox cbDatabase;

    private CodeArea codeArea;

    private Database source;

    private ExecutorService executor;

    @FXML
    public void initialize() {
        loadCodeArea();
    }

    @FXML
    void onBtRun(ActionEvent event) {
        query();
    }

    public CodeTab(Database db, Scene scene) {
        super("console");
        setSource(db);
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
        //add highlight style css to scene:
        //TODO: don't add it every time when constructed
        scene.getStylesheets().add(CodeTab.class.getResource("sql-keywords.css").toExternalForm());
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
        codeArea.setWrapText(true);
        codeArea.setAutoHeight(true);
        codeArea.setAutoScrollOnDragDesired(true);

        //code highlighting:
        Main.RESOURCES.log.debug("Starting highlighter for SQL");
        this.executor = Executors.newSingleThreadExecutor();

        Subscription cleanupWhenDone = codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .retainLatestUntilLater(executor)
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        //add to Resources for closing:
        Main.RESOURCES.resources.add(this);

        //stop highlighting when closing the tab
        setOnClosed((event) -> {
            Main.RESOURCES.resources.remove(this);
            this.stop();
        });

        //onKey listener:
        codeArea.setOnKeyPressed(this::handleKeyInputs);

        this.codeArea = codeArea;
        this.root.getChildren().add(codeArea);
    }

    //<editor-fold desc="Context Menu">
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
    //</editor-fold>

    //<editor-fold desc="Highlighting">

    /**
     * Stops the highlighter from working
     */
    @Override
    public void stop() {
        Main.RESOURCES.log.debug("Stopping highlighter for SQL");
        executor.shutdown();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        var text = this.codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        this.codeArea.setStyleSpans(0, highlighting);
    }


    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        text = text.toUpperCase(Locale.ROOT);
        var matcher = source.getUI().getPattern().matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("FUNCTION") != null ? "function" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    //</editor-fold>

    public void handleKeyInputs(KeyEvent event) {
        if(event.isControlDown()) {
            if(event.getCode() == KeyCode.ENTER) {
                query();
            }
        }
    }

    public void query() {
        var sql = getSelectedStatement();
        sql.forEach((value) -> {
            //clean up String:
            value = value.replaceAll("\n", "").replaceAll("\t", "").trim();
            //create requests for every String (in new Thread for efficiency, but wait till every Thread is done
            if(!value.isEmpty()) {
                try {
                    String finalValue = value;
                    var thread = new Thread(() -> {
                        //make database request and show result
                        try {
                            var result = this.source.request(finalValue);
                            Platform.runLater(() -> {
                                Main.getController().addResult(result);
                            });
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    Main.RESOURCES.log.info("Canceled Statements: ", e);
                }
            }
        });
    }

    /**
     * Gets the sql for the statements that are selected
     * @return selected SQL, or sql where the cursor is at
     */
    public List<String> getSelectedStatement() {
        var text = this.codeArea.getSelectedText();
        if(text.isEmpty()) {
            var sql = UiUtil.getSqlStatementFromPosition(this.codeArea.getText(), this.codeArea.getCaretPosition()).replaceAll(";", "");
            return List.of(sql);
        }
        return List.of(text.split(";"));
    }
}
