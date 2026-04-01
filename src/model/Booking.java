package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

// Valid status values defined in BookingStatus enum.
@DatabaseTable(tableName = "booking")
public class Booking {

    @DatabaseField(id = true, columnName = "booking_id", canBeNull = false)
    private String bookingId;

    @DatabaseField(foreign = true, columnName = "guest_id", canBeNull = false, indexName = "idx_booking_guest")
    private Guest guest;

    @DatabaseField(foreign = true, columnName = "org_id", canBeNull = false)
    private Organization org;

    @DatabaseField
    private String attraction;

    @DatabaseField(columnName = "booking_date")
    private String bookingDate;

    @DatabaseField(columnName = "party_size")
    private int partySize = 1;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private BookingStatus status = BookingStatus.confirmed;

    @DatabaseField(columnName = "total_cost")
    private double totalCost;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    public Booking() {}
}
