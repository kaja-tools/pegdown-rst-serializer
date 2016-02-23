package tools.kaja.pegdown.table;

import java.util.ArrayList;
import java.util.List;

public class Column
{

    protected List<Cell> cells = new ArrayList<Cell>();
    protected Integer width;

    public void addCell( Cell cell )
    {
        cells.add( cell );
        width = null;
    }

    public int size()
    {
        return cells.size();
    }

    public int getWidth()
    {
        if ( width == null )
        {
            loadWidth();
        }

        return width;
    }

    protected void loadWidth()
    {
        width = 0;

        for ( Cell cell : cells )
        {
            int cellWidth = cell.getWidth();

            if ( cellWidth > width )
            {
                width = cellWidth;
            }
        }
    }
}
