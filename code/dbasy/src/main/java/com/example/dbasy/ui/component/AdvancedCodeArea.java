package com.example.dbasy.ui.component;

import com.example.dbasy.Main;
import com.example.dbasy.Resource;
import javafx.concurrent.Task;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class AdvancedCodeArea extends CodeArea implements Resource {
    private ExecutorService executor;
    private Pattern pattern;

    /**
     * Stops the highlighter from working
     */
    @Override
    public void stop() {
        if(this.executor != null) {
            Main.RESOURCES.log.debug("Stopping highlighter for SQL");
            Main.RESOURCES.resources.remove(this);
            executor.shutdown();
        }
    }

    public AdvancedCodeArea() {
        super();
        init();
    }

    public AdvancedCodeArea(Pattern pattern) {
        this();
        this.pattern = pattern;
        init();
        initHightLighter();
    }

    public void init() {
        //some settings:
        setWrapText(true);
        setAutoHeight(true);
        setAutoScrollOnDragDesired(true);
        //Number markings:
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        //ContextMenu:
        setContextMenu(new CodeContextMenu());
    }

    //<editor-fold desc="Highlighter">
    public void initHightLighter() {
        this.executor = Executors.newSingleThreadExecutor();

        var cleanupWhenDone = multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .retainLatestUntilLater(executor)
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        Main.RESOURCES.resources.add(this);
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        var text = getText();
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
        setStyleSpans(0, highlighting);
    }


    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        text = text.toUpperCase(Locale.ROOT);
        var matcher = this.pattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("FUNCTION") != null ? "function" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    //</editor-fold>

    //<editor-fold desc="Context Menu">
    private static class CodeContextMenu extends ContextMenu
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
}
