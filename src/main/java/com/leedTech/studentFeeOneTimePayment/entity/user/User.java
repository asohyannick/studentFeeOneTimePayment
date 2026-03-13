package com.leedTech.studentFeeOneTimePayment.entity.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (
		name = "users",
		uniqueConstraints = {
				@UniqueConstraint ( columnNames = "email", name = "uk_users_email" )
		},
		indexes = {
				@Index ( columnList = "email", name = "idx_users_email" ) ,
				@Index ( columnList = "role", name = "idx_users_role" )
		}
)
public class User  {

@Id
@GeneratedValue ( strategy = GenerationType.UUID )
@Column ( updatable = false, nullable = false )
private UUID id;

@Column ( nullable = false )
private String firstName;

@Column ( nullable = false )
private String lastName;

@Column ( unique = true, nullable = false )
private String email;

@JsonIgnore
@Column ( nullable = false )
private String password;

@Enumerated ( EnumType.STRING )
@Column ( nullable = false )
private UserRole role;

@Column ( nullable = false )
private boolean accountVerified = false;

@Column ( nullable = false )
private boolean accountActive = false;

@Column ( nullable = false )
private boolean accountBlocked = false;

@JsonIgnore
private String magicLinkToken;

@JsonIgnore
private Instant magicLinkExpirationDate;

@JsonIgnore
@Column ( nullable = false )
private int failedLoginAttempts = 0;

@JsonIgnore
private Instant lastLoginAt;

@JsonIgnore
private String otpCode;

@JsonIgnore
private Instant otpExpirationDate;

@JsonIgnore
private Instant lockedUntil;

@Column ( updatable = false, nullable = false )
private Instant createdAt;

@Column ( nullable = false )
private Instant updatedAt;

// ─── Lifecycle Hooks ────────────────────────────────────────────

@PrePersist
protected void onCreate ( ) {
	Instant now = Instant.now ( );
	this.createdAt = now;
	this.updatedAt = now;
	if ( this.role == null ) this.role = UserRole.STUDENT;
}

@PreUpdate
protected void onUpdate ( ) {
	this.updatedAt = Instant.now ( );
}
}
