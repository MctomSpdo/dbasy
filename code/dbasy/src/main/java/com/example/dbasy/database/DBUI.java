package com.example.dbasy.database;

import javafx.scene.image.Image;

import java.util.regex.Pattern;

public abstract class DBUI {
    /**
     * Gets the Icon of the Database that is in use.
     * @return Image DatabaseIcon
     */
    public abstract Image getIcon();

    /**
     * Get Pattern for highlighting sql code
     *
     * There are different sections in the pattern:
     *
     * <b>KEYWORD:</b> These are all the SQL Keywords used for the database
     * <b>FUNCTION:</b> All the functions used in this database
     * <b>BRACE: </b> Typical { and }
     * <b>BRACKET: </b> The Brackets used in the Language (Typical ( and ))
     * <b>SEMICOLON: </b> Marks the Semicolon
     * <b>STRING: </b> Marks a String in the SQL Syntax
     * <b>COMMENT: </b> Marks a Comment in the SQL Syntax
     * @return Pattern
     */
    public abstract Pattern getPattern();
}
