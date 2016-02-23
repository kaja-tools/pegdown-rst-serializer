package tools.kaja.pegdown.table;

import java.util.ArrayList;
import java.util.List;

public class Row
{

    protected RowType type;
    protected List<Cell> cells = new ArrayList<Cell>();
    protected Cell activeCell;

    public Row( RowType type )
    {
        this.type = type;
    }

    public RowType getType()
    {
        return type;
    }

    public Cell addCell()
    {
        activeCell = new Cell();
        cells.add( activeCell );

        return activeCell;
    }

    public Cell getCell( int index )
    {
        try
        {
            return cells.get( index );
        }
        catch ( IndexOutOfBoundsException e )
        {

        }

        return null;
    }
}
