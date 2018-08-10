package org.kurento.tutorial.one2onecallrec.behappy.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
	public User findByUserIdAndPassword(String userId, String password);
}
