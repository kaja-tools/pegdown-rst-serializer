package tools.kaja.pegdown.table;

import java.util.ArrayList;
import java.util.List;

public class Table
{

    protected List<Row> rows = new ArrayList<Row>();
    protected List<Column> columns;
    protected Row activeRow;
    protected RowType nextRowType = RowType.BODY;

    public void setNextRowType( RowType type )
    {
        nextRowType = type;
    }

    public void addRow()
    {
        activeRow = new Row( nextRowType );
        rows.add( activeRow );
    }

    public List<Row> getRows()
    {
        return rows;
    }

    public Cell addCell()
    {
        columns = null;

        if ( activeRow == null )
        {
            addRow();
        }

        return activeRow.addCell();
    }

    public List<Column> getColumns()
    {
        if ( columns == null )
        {
            loadColumns();
        }

        return columns;
    }

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
