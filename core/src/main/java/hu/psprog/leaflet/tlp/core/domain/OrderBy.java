package hu.psprog.leaflet.tlp.core.domain;

/**
 * Possible order by values with its assigned field names.
 *
 * @author Peter Smith
 */
public enum OrderBy {

    /**
     * Order by level field.
     */
    LEVEL("level"),

    /**
     * Order by timestamp field.
     */
    TIMESTAMP("timeStamp"),

    /**
     * Order by content (log message) field.
     */
    CONTENT("content");

    private String field;

    OrderBy(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
