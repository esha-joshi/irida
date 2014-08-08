package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.Timestamped;

/**
 * A password reset object.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "password_reset")
public class PasswordReset implements Comparable<PasswordReset>, Timestamped {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@OneToOne
	@NotNull
	private User user;

	@Id
	@NotNull
	private String id;

	protected PasswordReset() {
		this.createdDate = new Date();
	}

	public PasswordReset(User user) {
		this();
		this.user = user;
		this.id = UUID.randomUUID().toString();
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public User getUser() {
		return user;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public int compareTo(PasswordReset passwordReset) {
		return createdDate.compareTo(passwordReset.createdDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, user, id);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PasswordReset) {
			PasswordReset p = (PasswordReset) other;
			return Objects.equals(createdDate, p.createdDate) && Objects.equals(user, p.user)
					&& Objects.equals(id, p.id);
		}

		return false;
	}

	@Override
	public Date getTimestamp() {
		return createdDate;
	}
}
