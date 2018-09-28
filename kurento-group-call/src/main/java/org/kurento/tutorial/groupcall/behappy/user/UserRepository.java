package org.kurento.tutorial.groupcall.behappy.user;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
	public User findByUserIdAndPassword(String userId, String password);
	public List<User> findByUserIdOrNickName(String userId, String nickName);
	
	@Transactional
    @Query(value="update users set photo=:photo where user_id=:userId", nativeQuery=true)  
    @Modifying 
    public void updatePhoto(@Param("userId") String userId, @Param("photo") String photo);
}
