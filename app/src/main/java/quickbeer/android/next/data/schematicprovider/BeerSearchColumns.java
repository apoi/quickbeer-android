package quickbeer.android.next.data.schematicprovider;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface BeerSearchColumns {
    @DataType(DataType.Type.TEXT) @PrimaryKey String SEARCH = "id";
    @DataType(DataType.Type.TEXT) String JSON = "json";
}
