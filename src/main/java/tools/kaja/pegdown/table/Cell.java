package tools.kaja.pegdown.table;

/**
 * Table cell
 */
public class Cell
{

    /**
     * Inner reStructuredText markup
     */
    protected String rst = "";

    /**
     * Get inner reStructuredText markup
     *
     * @return reStructuredText markup
     */
    public String getRst()
    {
        return rst;
    }

    /**
     * Set inner reStructuredText markup
     *
     * @param rst reStructuredText markup
     */
    public void setRst( String rst )
    {
        this.rst = rst;
    }

    /**
     * Get width (length)
     *
     * @return String length of the inner reStructuredText markup
     */
    public int getWidth()
    {
        return rst.length();
    }
}
