package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendsListRepository extends JpaRepository<FriendsList, Long> {

	public List<FriendsList> findByRequesterUserId(String userId);
	
	public List<FriendsList> findByAccepterUserId(String userId);
}
