package hu.psprog.leaflet.tlp.web.exception.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Standard error message response model.
 *
 * @author Peter Smith
 */
@Builder(builderMethodName = "getBuilder", setterPrefix = "with")
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorMessageResponse(
        String message
) { }
