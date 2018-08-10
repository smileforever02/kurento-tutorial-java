package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {
	public List<UserFriend> findByUserUserIdAndStatus(String userId, int status);
}
