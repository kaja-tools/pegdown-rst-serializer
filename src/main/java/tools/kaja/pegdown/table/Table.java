package tools.kaja.pegdown.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Table
 *
 * reStructuredText is using space-sensitive table painting syntax. It cannot be written at the same time
 * as the Markdown parser is reading the source. This class represents an intermediate structure that is
 * built by the parser and then written by TableRstWritter.
 */
public class Table
{

    /**
     * List of rows
     */
    protected List<Row> rows = new ArrayList<Row>();

    /**
     * List of columns
     */
    protected List<Column> columns;

    /**
     * Active (last created) row
     */
    protected Row activeRow;

    /**
     * Type of the next created row
     */
    protected RowType nextRowType = RowType.BODY;

    /**
     * Set type of the next created row(s)
     *
     * @param type Type of the row
     */
    public void setNextRowType( RowType type )
    {
        nextRowType = type;
    }

    /**
     * Add a new row
     */
    public void addRow()
    {
        activeRow = new Row( nextRowType );
        rows.add( activeRow );
    }

    /**
     * Get list of rows
     *
     * @return List of rows
     */
    public List<Row> getRows()
    {
        return rows;
    }

    /**
     * Add a new cell
     *
     * @return Newly created cell
     */
    public Cell addCell()
    {
        columns = null;

        if ( activeRow == null )
        {
            addRow();
        }

        return activeRow.addCell();
    }

    /**
     * Get list of columns
     *
     * @return List of columns
     */
    public List<Column> getColumns()
    {
        if ( columns == null )
        {
            loadColumns();
        }

        return columns;
    }

    /**
     * Load columns
     *
     * Columns are lazy loaded. Every call to addCell() cleans the cache.
     */
    protected void loadColumns()
    {
        columns = new ArrayList<Column>();
        int index = 0;

        while ( true )
        {
            Column column = getColumn( index );

            if ( column == null )
            {
                break;
            }

            columns.add( column );
            ++index;
        }
    }

    /**
     * Get column at specified index
     *
     * Columns are indexed from 0.
     * @param index Column index
     * @return Column or null if it does not exist
     */
    protected Column getColumn( int index )
    {
        Column column = new Column();

        for ( Row row : rows )
        {
            Cell cell = row.getCell( index );
            if ( cell != null )
            {
                column.addCell( cell );
            }
        }

        if ( column.size() > 0 )
        {
            return column;
        }

        return null;
    }
}
