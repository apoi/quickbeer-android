package quickbeer.android.next.data.schematicprovider;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by antti on 17.10.2015.
 */
public interface BeerSearchColumns {
    @DataType(DataType.Type.TEXT) @PrimaryKey String SEARCH = "id";
    @DataType(DataType.Type.TEXT) String JSON = "json";
}
