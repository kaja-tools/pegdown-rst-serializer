package tools.kaja.pegdown.table;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import tools.kaja.pegdown.RstWriter;

public class TableRstWriter
{

    protected static final int PADDING = 1;

    protected RstWriter rst;

    public TableRstWriter( RstWriter rst )
    {
        this.rst = rst;
    }

    public void write( Table table )
    {
        List<Row> rows = table.getRows();

        if ( rows.size() < 1 )
        {
            return;
        }

        List<Column> columns = table.getColumns();

        writeRowSeparator( columns, RowType.BODY );
        rst.newLine();

        for ( Row row : table.getRows() )
        {
            rst.markup( "|" + StringUtils.repeat( " ", PADDING ) );
            for ( int index = 0; index < columns.size(); ++index )
            {
                Cell cell = row.getCell( index );
                int cellWidth = 0;

                if ( cell != null )
                {
                    cellWidth = cell.getWidth();
                    rst.markup( cell.getRst() );
                }

                rst.markup( StringUtils.repeat( " ", columns.get( index ).getWidth() - cellWidth ) );
                rst.markup( StringUtils.repeat( " ", PADDING ) + "|" );
                if ( index < columns.size() - 1)
                {
                    rst.markup( StringUtils.repeat( " ", PADDING ) );
                }
            }
            rst.newLine();
            writeRowSeparator( columns, row.getType() );
            rst.newLine();
        }

        rst.newLine();
    }

    protected void writeRowSeparator( List<Column> columns, RowType rowType )
    {
        String underline = rowType == RowType.HEADER ? "=" : "-";

        rst.markup( "+" );
        for ( Column column : columns )
        {
            rst.markup( StringUtils.repeat( underline, column.getWidth() + PADDING * 2 ) );
            rst.markup( "+" );
        }
    }
}
