package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	public User registerUser(User user) {
		if (getUser(user.getUserId()) == null) {
			user.setCreatedDate(new Date());
			return userRepository.save(user);
		} else {
			return null;
		}
	}

	public User updateUser(User user) {
		return userRepository.save(user);
	}

	public User getUser(String userId) {
		return userRepository.findOne(userId);

	}

	public boolean verifyPassword(String userId, String password) {
		return userRepository.findByUserIdAndPassword(userId, password) != null;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}