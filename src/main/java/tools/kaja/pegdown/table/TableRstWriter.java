package tools.kaja.pegdown.table;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import tools.kaja.pegdown.RstWriter;

/**
 * reStructuredText table markup writer
 */
public class TableRstWriter
{

    /**
     * Min number of spaces between column pipe "|" and content
     */
    protected static final int PADDING = 1;

    /**
     * Main document reStructuredText markup writer
     */
    protected RstWriter rstWriter;

    /**
     * Constructor
     *
     * @param rstWriter Main document reStructuredText markup writer
     */
    public TableRstWriter( RstWriter rstWriter )
    {
        this.rstWriter = rstWriter;
    }

    /**
     * Write table markup
     *
     * Markup is written to the main document writer specified
     * in the constructor.
     * @param table Parsed table structure
     */
    public void write( Table table )
    {
        List<Row> rows = table.getRows();

        if ( rows.size() < 1 )
        {
            return;
        }

        List<Column> columns = table.getColumns();

        writeRowSeparator( columns, RowType.BODY );
        rstWriter.newLine();

        for ( Row row : table.getRows() )
        {
            rstWriter.markup( "|" + StringUtils.repeat( " ", PADDING ) );
            for ( int index = 0; index < columns.size(); ++index )
            {
                Cell cell = row.getCell( index );
                int cellWidth = 0;

                if ( cell != null )
                {
                    cellWidth = cell.getWidth();
                    rstWriter.markup( cell.getRst() );
                }

                rstWriter.markup( StringUtils.repeat( " ", columns.get( index ).getWidth() - cellWidth ) );
                rstWriter.markup( StringUtils.repeat( " ", PADDING ) + "|" );
                if ( index < columns.size() - 1 )
                {
                    rstWriter.markup( StringUtils.repeat( " ", PADDING ) );
                }
            }
            rstWriter.newLine();
            writeRowSeparator( columns, row.getType() );
            rstWriter.newLine();
        }

        rstWriter.newLine();
    }

    /**
     * Write horizontal row separator
     *
     * Example (rowType = HEADER):
     *
     * +======+=================+
     *
     * Example (rowType = BODY):
     *
     * +------+-----------------+
     *
     * @param columns List of columns
     * @param rowType Type of the first row
     */
    protected void writeRowSeparator( List<Column> columns, RowType rowType )
    {
        String underline = rowType == RowType.HEADER ? "=" : "-";

        rstWriter.markup( "+" );
        for ( Column column : columns )
        {
            rstWriter.markup( StringUtils.repeat( underline, column.getWidth() + PADDING * 2 ) );
            rstWriter.markup( "+" );
        }
    }
}
