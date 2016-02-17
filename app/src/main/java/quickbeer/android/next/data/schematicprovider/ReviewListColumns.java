package quickbeer.android.next.data.schematicprovider;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface ReviewListColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey String BEER_ID = "id";
    @DataType(DataType.Type.TEXT) String JSON = "json";
}
