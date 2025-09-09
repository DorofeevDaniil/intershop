package ru.custom.storefrontapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private Long id;
    @Column("username")
    private String username;
    private String password;
    private Boolean enabled;
}
