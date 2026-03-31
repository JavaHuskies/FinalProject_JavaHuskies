/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_records")
public class UserRecord {
    
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "user_name", canBeNull = false)
    private String userName;

    @DatabaseField(canBeNull = false)
    private String password;

    @DatabaseField(columnName = "first_name", canBeNull = false)
    private String firstName;

    @DatabaseField(columnName = "last_name", canBeNull = false)
    private String lastName;

    @DatabaseField(canBeNull = false)
    private String email;

    // TODO: Add organization FK once Organization entity is defined.
    // TODO: Add enterprise FK once Enterprise entity is defined.
}
