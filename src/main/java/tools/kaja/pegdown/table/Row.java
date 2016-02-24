package tools.kaja.pegdown.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Table row
 */
public class Row
{

    /**
     * Type of this row (header or body)
     */
    protected RowType type;

    /**
     * List of cells
     */
    protected List<Cell> cells = new ArrayList<Cell>();

    /**
     * Active cell
     */
    protected Cell activeCell;

    /**
     * Constructor
     *
     * @param type Type of this row
     */
    public Row( RowType type )
    {
        this.type = type;
    }

    /**
     * Get type
     *
     * Header or body. Similar to HTML <th> and <tr>.
     * @return Type of this row
     */
    public RowType getType()
    {
        return type;
    }

    /**
     * Add a new cell
     *
     * @return Newly created cell
     */
    public Cell addCell()
    {
        activeCell = new Cell();
        cells.add( activeCell );

        return activeCell;
    }

    /**
     * Get cell at specified index (position)
     *
     * Cells are indexed from 0.
     * @param index Index (position)
     * @return Cell at given index or null if it does not exist
     */
    public Cell getCell( int index )
    {
        if ( index >= cells.size() )
        {
            return null;
        }

        return cells.get( index );
    }
}
