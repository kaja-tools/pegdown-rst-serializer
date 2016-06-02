package tools.kaja.pegdown;

import static org.parboiled.common.Preconditions.checkArgNotNull;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.pegdown.ast.AbbreviationNode;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.AnchorLinkNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.BlockQuoteNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.DefinitionListNode;
import org.pegdown.ast.DefinitionNode;
import org.pegdown.ast.DefinitionTermNode;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.InlineHtmlNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.OrderedListNode;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.QuotedNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SimpleNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.StrikeNode;
import org.pegdown.ast.StrongEmphSuperNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableColumnNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.ast.WikiLinkNode;

import tools.kaja.pegdown.table.Cell;
import tools.kaja.pegdown.table.RowType;
import tools.kaja.pegdown.table.Table;
import tools.kaja.pegdown.table.TableRstWriter;

/**
 * Serilize PegDown AST tree to reST (reStructuredText)
 */
public class ToRstSerializer
    implements Visitor
{
    /**
     * Default header underline characters
     *
     * As recommended by Sphinx/Python http://www.sphinx-doc.org/en/stable/rest.html#sections.
     */
    protected static final String[] DEFAULT_HEADERS = { "#", "*", "=", "-", "^", "\"" };

    protected String[] headers;
    protected RstWriter rstWriter;
    protected Map<String, RstWriter> rstWriters;
    protected Table table;
    protected TableRstWriter tableRstWriter;
    protected Map<String, ExpImageNode> images;
    protected List<String> unsupported;
    protected String listItemMarker;

    /**
     * Constructor
     *
     * @param headers Custom header underline characters
     */
    public ToRstSerializer( String[] headers )
    {
        this.headers = headers;
        rstWriters = new HashMap<String, RstWriter>();
        rstWriters.put( "main", new RstWriter() );
        rstWriters.put( "tableCell", new RstWriter() );
        setRstWriter( "main" );
        tableRstWriter = new TableRstWriter( rstWriter );
    }

    /**
     * Constructor
     */
    public ToRstSerializer()
    {
        this( DEFAULT_HEADERS );
    }

    /**
     * Serialize Markdown to reStructuredText
     *
     * @param astRoot PegDown AST root
     * @param md Markdown source document
     * @return reStructuredText document
     */
    public String toRst( RootNode astRoot, char[] md )
    {
        checkArgNotNull( astRoot, "astRoot" );
        // TreeMap has sorted keys. Stable tests.
        images = new TreeMap<String, ExpImageNode>();
        unsupported = new ArrayList<String>();
        astRoot.accept( this );
        writeImages();
        writeUnsupported();

        String rst = rstWriter.output( true );

        rstWriter.clear();
        unsupported = null;
        images = null;

        return rst;
    }

    public void visit( AbbreviationNode node )
    {
        unsupported( node );
    }

    public void visit( AnchorLinkNode node )
    {
        unsupported( node );
    }

    public void visit( AutoLinkNode node )
    {
        unsupported( node );
    }

    /**
     * Block quotation
     *
     * @param node Block quotation node
     */
    public void visit( BlockQuoteNode node )
    {
        rstWriter.indent();
        visitChildren( node );
        rstWriter.outdent();
        // http://docutils.sourceforge.net/docs/user/rstWriter/quickref.html#block-quotes
        // "Use empty comments to separate indentation contexts, such as block quotes and directive contents."
        rstWriter.markup( ".." );
        rstWriter.newLine();
        rstWriter.newLine();
    }

    /**
     * Bulleted list
     *
     * @param node Bulleted list node
     */
    public void visit( BulletListNode node )
    {
        listItemMarker = "*";
        visitChildren( node );
        rstWriter.newLine();
    }

    /**
     * Code (inline)
     *
     * @param node Code node
     */
    public void visit( CodeNode node )
    {
        rstWriter.markup( "``" );
        rstWriter.markup( node.getText() );
        rstWriter.markup( "``" );
    }

    public void visit( DefinitionListNode node )
    {
        unsupported( node );
    }

    public void visit( DefinitionNode node )
    {
        unsupported( node );
    }

    public void visit( DefinitionTermNode node )
    {
        unsupported( node );
    }

    /**
     * Image
     *
     * @param node Image node
     */
    public void visit( ExpImageNode node )
    {
        // This does not work if the image is inline
        // rstWriter.markup( ".. image:: " + node.url );
        //
        // Substitution definition must be used instead:
        // http://docutils.sourceforge.net/docs/ref/rstWriter/restructuredtext.html#substitution-definitions

        String id = image( node );

        rstWriter.markup( "|" + id + "|" );
    }

    /**
     * Link
     *
     * @param node Link node
     */
    public void visit( ExpLinkNode node )
    {
        rstWriter.markup( "`" );
        visitChildren( node );
        rstWriter.markup( " <" + node.url + ">`_" );
        // Link titles not supported
        unsupported( node );
    }

    /**
     * Header
     *
     * @param node Header node
     */
    public void visit( HeaderNode node )
    {
        visitChildren( node );

        int level = node.getLevel();
        String delimiter = headers.length >= level ? headers[level - 1] : headers[0];

        rstWriter.newLine();
        rstWriter.markup( StringUtils.repeat( delimiter, rstWriter.getLastLineLength() ) );
        rstWriter.newLine();
        rstWriter.newLine();
    }

    /**
     * HTML (block)
     *
     * @param node HTML block node
     */
    public void visit( HtmlBlockNode node )
    {
        rstWriter.markup( ".. raw:: html" );
        rstWriter.newLine();
        rstWriter.newLine();
        rstWriter.indent();
        rstWriter.markup( node.getText() );
        rstWriter.outdent();
        // http://docutils.sourceforge.net/docs/user/rstWriter/quickref.html#block-quotes
        // "Use empty comments to separate indentation contexts, such as block quotes and directive contents."
        rstWriter.newLine();
        rstWriter.markup( ".." );
        rstWriter.newLine();
        rstWriter.newLine();
        rstWriter.newLine();
    }

    /**
     * Inline HTML
     *
     * @param node Inline HTML node
     */
    public void visit( InlineHtmlNode node )
    {
        rstWriter.enableRawInlineHtml();
        rstWriter.markup( " :raw-html:`" + node.getText() + "` " );
    }

    /**
     * List item
     *
     * @param node List item node
     */
    public void visit( ListItemNode node )
    {
        rstWriter.markup( listItemMarker + " " );
        // This does not handle multiline items
        visitChildren( node );
        rstWriter.newLine();
    }

    public void visit( MailLinkNode node )
    {
        unsupported( node );
    }

    /**
     * Ordered list
     *
     * @param node Ordered list node
     */
    public void visit( OrderedListNode node )
    {
        listItemMarker = "#.";
        visitChildren( node );
        rstWriter.newLine();
    }

    /**
     * Paragraph of text
     *
     * @param node Paragraph node
     */
    public void visit( ParaNode node )
    {
        visitChildren( node );
        rstWriter.newLine();
        rstWriter.newLine();
    }

    public void visit( QuotedNode node )
    {
        unsupported( node );
    }

    public void visit( ReferenceNode node )
    {
        unsupported( node );
        visitChildren( node );
    }

    public void visit( RefImageNode node )
    {
        unsupported( node );
    }

    public void visit( RefLinkNode node )
    {
        unsupported( node );
        visitChildren( node );
    }

    public void visit( RootNode node )
    {
        visitChildren( node );
    }

    public void visit( SimpleNode node )
    {
        unsupported( node );
    }

    /**
     * Special text
     *
     * @param node Special text node
     */
    public void visit( SpecialTextNode node )
    {
        rstWriter.text( node.getText() );
    }

    public void visit( StrikeNode node )
    {
        unsupported( node );
    }

    /**
     * Emphasis (italics) or strong emphasis (bold)
     *
     * @param node Emphasis node
     */
    public void visit( StrongEmphSuperNode node )
    {
        String delimiter = node.isStrong() ? "**" : "*";

        rstWriter.markup( delimiter );
        visitChildren( node );
        rstWriter.markup( delimiter );
    }

    /**
     * Table body
     *
     * @param node Table body node
     */
    public void visit( TableBodyNode node )
    {
        table.setNextRowType( RowType.BODY );
        visitChildren( node );
    }

    public void visit( TableCaptionNode node )
    {
        unsupported( node );
    }

    /**
     * Table cell
     *
     * @param node Table cell node
     */
    public void visit( TableCellNode node )
    {
        Cell cell = table.addCell();
        setRstWriter( "tableCell" );
        visitChildren( node );
        boolean isRawInlineHtmlEnabled = rstWriter.isRawInlineHtmlEnabled();
        cell.setRst( rstWriter.output( false ) );
        rstWriter.clear();
        setRstWriter( "main" );
        if ( isRawInlineHtmlEnabled )
        {
        	rstWriter.enableRawInlineHtml();
        }
    }

    public void visit( TableColumnNode node )
    {
        unsupported( node );
    }

    /**
     * Table header
     *
     * @param node Table header node
     */
    public void visit( TableHeaderNode node )
    {
        table.setNextRowType( RowType.HEADER );
        visitChildren( node );
    }

    /**
     * Table
     *
     * Enabled via PegDownProcessor option Extensions.TABLES.
     * @param node Table node
     */
    public void visit( TableNode node )
    {
        table = new Table();
        visitChildren( node );
        tableRstWriter.write( table );
        table = null;
    }

    /**
     * Table row
     *
     * @param node Table row node
     */
    public void visit( TableRowNode node )
    {
        table.addRow();
        visitChildren( node );
    }

    /**
     * Code (block)
     *
     * Enabled via PegDownProcessor option Extensions.FENCED_CODE_BLOCKS.
     * @param node Code block node
     */
    public void visit( VerbatimNode node )
    {
        if ( node.getType() != null && node.getType().length() > 0 )
        {
            rstWriter.markup( ".. code-block:: " + node.getType() );
        }
        else
        {
            rstWriter.markup( "::" );
        }

        rstWriter.newLine();
        rstWriter.newLine();
        rstWriter.indent();
        rstWriter.markup( node.getText() );
        rstWriter.outdent();
        // http://docutils.sourceforge.net/docs/user/rstWriter/quickref.html#block-quotes
        // "Use empty comments to separate indentation contexts, such as block quotes and directive contents."
        rstWriter.newLine();
        rstWriter.markup( ".." );
        rstWriter.newLine();
        rstWriter.newLine();
        rstWriter.newLine();
    }

    public void visit( WikiLinkNode node )
    {
        unsupported( node );
    }

    public void visit( TextNode node )
    {
        // Inline and block HTML is already processed by PegDown and does not reach this code.
        // Method unescapeHtml4() is here only to transform HTML entities into the real characters.
        // HTML entities are not trapped by PegDown's HTML processors.
        rstWriter.text( StringEscapeUtils.unescapeHtml4 ( node.getText() ) );
    }

    public void visit( SuperNode node )
    {
        visitChildren( node );
    }

    public void visit( Node node )
    {
    }

    protected void visitChildren( SuperNode node )
    {
        for ( Node child : node.getChildren() )
        {
            child.accept( this );
        }
    }

    protected void setRstWriter( String name )
    {
        rstWriter = rstWriters.get( name );
    }

    protected String image ( ExpImageNode node )
    {
        // We could also use a random string. But then the tests could
        // not assert stable value. Hash supports this.
        String id = hash( node.url + ":" + node.title );

        images.put( id, node );

        return id;
    }

    protected void writeImages()
    {
        for ( Map.Entry<String, ExpImageNode> entry : images.entrySet() )
        {
            ExpImageNode image = entry.getValue();

            rstWriter.markup( String.format( ".. |%s| image:: %s", entry.getKey(), image.url ) );

            rstWriter.newLine();
        }
    }

    protected void unsupported( AbstractNode node )
    {
        unsupported.add( node.toString() );
    }

    protected void writeUnsupported()
    {
        if ( unsupported.size() < 1 )
        {
            return;
        }

        rstWriter.newLine();
        rstWriter.markup( ".." );
        rstWriter.newLine();
        rstWriter.indent();
        rstWriter.markup(
            "Following nodes are not supported yet and were processed only partially "
            +
            "during the Markdown to reStructuredText conversion" );
        rstWriter.newLine();
        for ( String node : unsupported )
        {
            rstWriter.markup( node );
            rstWriter.newLine();
        }
        rstWriter.outdent();
    }

    protected String hash( String text )
    {
        MessageDigest md;

        try
        {
            // This is not used for any security related stuff.
            // We just need 32 chars hash of a string. MD5 is fine for this.
            md = MessageDigest.getInstance( "MD5" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new RuntimeException( e );
        }

        try
        {
            md.update( text.getBytes( "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }

        byte[] digest = md.digest();

        return String.format( "%032x", new java.math.BigInteger( 1, digest ) );
    }
}
