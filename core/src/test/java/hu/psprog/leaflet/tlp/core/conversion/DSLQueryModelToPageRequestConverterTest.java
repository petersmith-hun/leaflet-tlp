package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOrderDirection;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link DSLQueryModelToPageRequestConverter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class DSLQueryModelToPageRequestConverterTest {

    @InjectMocks
    private DSLQueryModelToPageRequestConverter converter;

    @Test
    public void shouldConvertCreateDefaultPageableObjectForEmptyDSLQueryModel() {

        // given
        DSLQueryModel dslQueryModel = new DSLQueryModel();

        // when
        Pageable result = converter.convert(dslQueryModel);

        // then
        assertThat(result.getPageNumber(), equalTo(0));
        assertThat(result.getPageSize(), equalTo(50));
        assertThat(result.getOffset(), equalTo(0L));
        assertThat(result.getSort(), equalTo(Sort.by(Sort.Direction.DESC, "timeStamp")));
    }

    @Test
    public void shouldConvertCreatePageableObjectWithDefaultPaginationBasedOnDSLQueryModel() {

        // given
        DSLQueryModel dslQueryModel = new DSLQueryModel();
        dslQueryModel.setLimit(20);
        dslQueryModel.setOffset(140);

        // when
        Pageable result = converter.convert(dslQueryModel);

        // then
        assertThat(result.getPageNumber(), equalTo(7));
        assertThat(result.getPageSize(), equalTo(20));
        assertThat(result.getOffset(), equalTo(140L));
        assertThat(result.getSort(), equalTo(Sort.by(Sort.Direction.DESC, "timeStamp")));
    }

    @Test
    public void shouldConvertCreatePageableObjectWithSingleFieldPaginationBasedOnDSLQueryModel() {

        // given
        DSLQueryModel dslQueryModel = new DSLQueryModel();
        dslQueryModel.setLimit(10);
        dslQueryModel.setOffset(50);
        dslQueryModel.getOrdering().put(DSLObject.MESSAGE, DSLOrderDirection.ASC);

        // when
        Pageable result = converter.convert(dslQueryModel);

        // then
        assertThat(result.getPageNumber(), equalTo(5));
        assertThat(result.getPageSize(), equalTo(10));
        assertThat(result.getOffset(), equalTo(50L));
        assertThat(result.getSort(), equalTo(Sort.by(Sort.Direction.ASC, "content")));
    }

    @Test
    public void shouldConvertCreatePageableObjectWithMultiFieldPaginationBasedOnDSLQueryModel() {

        // given
        DSLQueryModel dslQueryModel = new DSLQueryModel();
        dslQueryModel.setLimit(30);
        dslQueryModel.setOffset(90);
        dslQueryModel.getOrdering().put(DSLObject.MESSAGE, DSLOrderDirection.DESC);
        dslQueryModel.getOrdering().put(DSLObject.TIMESTAMP, DSLOrderDirection.ASC);

        // when
        Pageable result = converter.convert(dslQueryModel);

        // then
        assertThat(result.getPageNumber(), equalTo(3));
        assertThat(result.getPageSize(), equalTo(30));
        assertThat(result.getOffset(), equalTo(90L));
        assertThat(result.getSort(), equalTo(Sort.by(Sort.Direction.DESC, "content")
                .and(Sort.by(Sort.Direction.ASC, "timeStamp"))));
    }
}
