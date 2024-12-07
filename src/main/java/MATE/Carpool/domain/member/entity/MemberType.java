package MATE.Carpool.domain.member.entity;

public enum MemberType {
    STANDARD("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String role;

    MemberType(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}