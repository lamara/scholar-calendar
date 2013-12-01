package com.scholarscraper.separators;


/**
 *  Used for inserting separators with arbitrary text into the listview. These
 *  objects are dateless so expect a null return value from getDueDate()
 *
 *  @author Alex
 */
public class GenericSeparator
    extends DateSeparator
{
    private String text;

    public GenericSeparator(String text) {
        this.text = text;
    }

    @Override
    protected String getSeparatorString()
    {
        return text;
    }

}
