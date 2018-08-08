package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	public void registerUser(User user) {
		if (userRepository.findOne(user.getUserId()) == null) {
			user.setCreatedDate(new Date());
			userRepository.save(user);	
		} else {
			
		}
	}
	
	public void updateUser(User user) {
		userRepository.save(user);
	}
	
	public User getUser(String userId) {
		return userRepository.findOne(userId);
		
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
