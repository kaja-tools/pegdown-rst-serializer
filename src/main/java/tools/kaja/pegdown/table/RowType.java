package tools.kaja.pegdown.table;

/**
 * Table row type
 *
 * Row type affects the used reStructuredText underline character. Character "=" is used for HEADER
 * and "-" for BODY.
 */
public enum RowType
{
    /**
     * Similar to HTML <th>. Underlined with "=".
     */
    HEADER,

    /**
     * Similar to HTML <tr>. Underlined with "-".
     */
    BODY
}