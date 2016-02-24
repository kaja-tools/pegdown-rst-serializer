package tools.kaja.pegdown.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Table column
 *
 * This class represents a vertical group of cells. The primary goal
 * is to calculate it's width. Column width is calculated as a max width among all the cells.
 */
public class Column
{

    /**
     * List of cells
     */
    protected List<Cell> cells = new ArrayList<Cell>();

    /**
     * Column width
     */
    protected Integer width;

    /**
     * Add a cell
     *
     * @param cell Cell
     */
    public void addCell( Cell cell )
    {
        cells.add( cell );
        width = null;
    }

    /**
     * Number of cells in this column
     *
     * @return Number of cells in this column
     */
    public int size()
    {
        return cells.size();
    }

    /**
     * Get width
     *
     * Width is calculated as a max width among all the cells.
     * @return Width of this column
     */
    public int getWidth()
    {
        if ( width == null )
        {
            loadWidth();
        }

        return width;
    }

    /**
     * Load width
     */
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
