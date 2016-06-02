package tools.kaja.pegdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

public class ToRstSerializerTest
{

    @Test
    public void testMd2RstHeader()
    {
        testMd2Rst( "header" );
    }

    @Test
    public void testMd2RstInline()
    {
        testMd2Rst( "inline" );
    }

    @Test
    public void testMd2RstCodeBlock()
    {
        testMd2Rst( "codeBlock" );
    }

    @Test
    public void testMd2RstEntity()
    {
        testMd2Rst( "entity" );
    }

    @Test
    public void testMd2RstList()
    {
        testMd2Rst( "list" );
    }

    @Test
    public void testMd2RstImage()
    {
        testMd2Rst( "image" );
    }

    @Test
    public void testMd2RstLink()
    {
        testMd2Rst( "link" );
    }

    @Test
    public void testMd2RstBlockquote()
    {
        testMd2Rst( "blockquote" );
    }

    @Test
    public void testMd2RstHtml()
    {
        testMd2Rst( "html" );
    }
    
    @Test
    public void testMd2RstReserved()
    {
        testMd2Rst( "reserved" );
    }

    @Test
    public void testMd2RstTable()
    {
        testMd2Rst( "table" );
    }

    private void testMd2Rst( String testName )
    {
        String md = readResource( testName + ".md" );
        String rstExpected = readResource( testName + ".rst" );

        PegDownProcessor pegDownProcessor = new PegDownProcessor(
            Extensions.FENCED_CODE_BLOCKS | Extensions.TABLES );

        char[] mdChars = md.toCharArray();
        RootNode astRoot = pegDownProcessor.parseMarkdown( mdChars );
        String rstActual = new ToRstSerializer().toRst( astRoot, mdChars );
        assertEquals( rstExpected, rstActual );
    }

    private String readResource( String resource )
    {
        try
        {
            return IOUtils.toString( this.getClass().getResourceAsStream( resource ), "UTF-8" );
        }
        catch (Exception e)
        {
            fail("Failed to read test resource " + resource + ". Exception: " + e);
        }

        return null;
    }
}
