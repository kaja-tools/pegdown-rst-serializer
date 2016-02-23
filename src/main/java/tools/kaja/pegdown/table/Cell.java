package tools.kaja.pegdown.table;

public class Cell
{

    protected String rst = "";

    public String getRst()
    {
        return rst;
    }

    public void setRst( String rst )
    {
        this.rst = rst;
    }

    public int getWidth()
    {
        return rst.length();
    }
}
