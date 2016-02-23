package tools.kaja.pegdown;

import org.apache.commons.lang3.StringUtils;

/**
 * reStructuredText markup writer
 */
public class RstWriter
{

    /**
     * Newline character
     */
    protected static final String NEW_LINE = "\n";

    /**
     * Number of indent spaces
     *
     * Used to indent nested reStructuredText blocks.
     */
    protected static final int INDENT_SPACES = 4;

    /**
     * reStructuredText markup being written
     */
    protected String rst;

    /**
     * Current nesting level
     */
    protected int indent;

    /**
     * Length of last written line
     */
    protected int lastLineLength;

    /**
     * See enableRawInlineHtml()
     */
    protected boolean rawInlineHtmlEnabled;

    /**
     * Constructor
     */
    public RstWriter()
    {
        clear();
    }

    /**
     * Clear
     *
     * Clears all reStructuredText markup written so far and resets all the settings.
     */
    public void clear()
    {
        rst = "";
        indent = 0;
        lastLineLength = 0;
        rawInlineHtmlEnabled = false;
    }

    /**
     * Write inline text
     *
     * Used to render the content of inline elements.
     * Characters `, \, |, *, _ are escaped.
     *
     * @param text Inline text
     */
    public void text( String text )
    {
        markup( escape( text ) );
    }

    /**
     * Write raw reStructuredText markup
     *
     * Use methods indent() and outdent() to modify the identation.
     *
     * @param text Raw reStructuredText markup
     */
    public void markup( String text )
    {
        // Indent only of this is the first text on the line
        markup( text, rst.isEmpty() || rst.endsWith( NEW_LINE ) );
    }

    /**
     * Write a new line
     */
    public void newLine()
    {
        rst += NEW_LINE;
    }

    /**
     * Increase the indent
     *
     * Affects all future calls to markup().
     */
    public void indent()
    {
        indent += 1;
    }

    /**
     * Decrease the indent
     *
     * Affects all future calls to markup().
     */
    public void outdent()
    {
        if ( indent > 0 )
        {
            indent -= 1;
        }
    }

    /**
     * Get length of the last line written
     *
     * Returns 0 if no line has been written yet.
     * @return Length of the last line written.
     */
    public int getLastLineLength()
    {
        return lastLineLength;
    }

    /**
     * Enable raw inline HTML
     *
     * Output document will contain a role "raw-html" that is used
     * to output raw HTML. See http://docutils.sourceforge.net/docs/ref/rst/roles.html#raw
     * for more details.
     */
    public void enableRawInlineHtml()
    {
        rawInlineHtmlEnabled = true;
    }

    /**
     * Get the reST markup of this writer
     *
     * @return reStructuredText markup
     */
    public String toString()
    {
        String output = "";

        // http://docutils.sourceforge.net/docs/ref/rst/roles.html#raw
        if ( rawInlineHtmlEnabled )
        {
            output += ".. role:: raw-html(raw)\n";
            output += "    :format: html\n";
            output += "\n";
        }

        // Remove whitespace at the end of the document
        output += this.rst.replaceAll( "\\s+$", "" );

        return output;
    }

    /**
     * Write raw reStructuredText markup
     *
     * @param text Raw reStructuredText markup
     * @param useIdent Apply current ident (see properties INDENT_SPACES and indent)
     */
    protected void markup( String text, boolean useIdent )
    {
        if ( useIdent )
        {
            // Very dirty hack to avoid opening space if this line start with inline raw HTML
            // (needs to be separated from surrounding text by one space)
            text = text.replaceAll( "^\\s", "" );
        }

        int spaces = useIdent ? indent * INDENT_SPACES : 0;
        String[] lines = text.split( NEW_LINE );

        for ( int i = 0; i < lines.length; ++i )
        {
            rst += StringUtils.repeat( " ", spaces ) + lines[i];

            // Do not write a new line character if this is the last line of the markup
            if ( i < ( lines.length - 1 ) )
            {
                newLine();
            }
        }

        lastLineLength = lines[lines.length - 1].length();
    }

    /**
     * Escape reStructuredText markup
     *
     * See http://docutils.sourceforge.net/docs/user/rst/quickref.html#inline-markup.
     * @param text Raw reStructuredText markup
     * @return Escaped reStructuredText markup
     */
    protected String escape( String text )
    {
        return text.replaceAll( "([`\\|*_])", "\\\\$1" );
    }
}
