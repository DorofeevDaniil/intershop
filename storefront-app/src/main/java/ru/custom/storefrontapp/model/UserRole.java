package ru.custom.storefrontapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "user_role")
public class UserRole {
    @Column("user_id")
    private Long userId;
    @Column("role_id")
    private Long roleId;
}
