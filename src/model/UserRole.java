package model;

// Enum constants use camelCase to match the string values stored in the DB
// and the role constants in Claims.java. DataType.ENUM_NAME stores name() as-is.
public enum UserRole {
    networkAdmin,
    systemAdmin,
    enterpriseAdmin,
    organizationAdmin,
    groupCeo,
    groupCfo,
    enterprisePresident,
    enterpriseCoo,
    orgDirector,
    creativeLead,
    technologyLead,
    marketingLead,
    complianceOfficer,
    dataAnalyst
}
