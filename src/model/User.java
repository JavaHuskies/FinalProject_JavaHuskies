package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(id = true, columnName = "user_id", canBeNull = false)
    private String userId;

    @DatabaseField(foreign = true, columnName = "org_id", canBeNull = false, indexName = "idx_user_org")
    private Organization org;

    @DatabaseField(foreign = true, columnName = "enterprise_id", canBeNull = false, indexName = "idx_user_enterprise")
    private Enterprise enterprise;

    @DatabaseField(columnName = "first_name")
    private String firstName;

    @DatabaseField(columnName = "last_name")
    private String lastName;

    @DatabaseField(unique = true)
    private String email;

    @DatabaseField(columnName = "password_hash", canBeNull = false)
    private String passwordHash;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private UserRole role;

    @DatabaseField(columnName = "jwt_token")
    private String jwtToken;

    @DatabaseField(columnName = "is_active", canBeNull = false)
    private boolean isActive = true;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    public User() {}

    public User(String userId, Organization org, Enterprise enterprise,
                String firstName, String lastName, String email,
                String passwordHash, UserRole role) {
        this.userId = userId;
        this.org = org;
        this.enterprise = enterprise;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
