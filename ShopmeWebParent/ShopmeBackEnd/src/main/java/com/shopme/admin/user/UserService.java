package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
	
	public static final int USERS_PER_PAGE = 5;
	

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public User getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }
    
    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
    	Sort sort = Sort.by(sortField);
    	
    	sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
    	Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        if (keyword != null ){
            return userRepository.findAll(keyword, pageable);
        }

    	return userRepository.findAll(pageable);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User save(User user) {
    	boolean isUpdatingUser = (user.getId() != null);
    	
    	if (isUpdatingUser) {
    		User existingUser = userRepository.findById(user.getId()).get();
    		
    		if(user.getPassword().isEmpty()) {
    			user.setPassword(existingUser.getPassword());
    		}else {
    			encodePassword(user);
    		}
    	}else {
    		encodePassword(user);
    	}

        return userRepository.save(user);
    }

    public User updateAccount(User userInform) {
        User userInDb = userRepository.findById(userInform.getId()).get();

        if (!userInform.getPassword().isEmpty()){
            userInDb.setPassword(userInform.getPassword());
            encodePassword(userInDb);
        }

        if (userInform.getPhotos() != null) {
            userInDb.setPhotos(userInform.getPhotos());
        }

        userInDb.setFirstName(userInform.getFirstName());
        userInDb.setLastName(userInform.getLastName());

        return userRepository.save(userInDb);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);

        if(userByEmail == null) return true;

        boolean isCreatingNew = (id == null);

        // Thỏa mãn để tạo mới không ?
        if(isCreatingNew){
            if(userByEmail != null) return false;
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException ex){
            throw new UserNotFoundException("Could not find any user with Id " + id);
        }
    }
    
    public void delete(Integer id) throws UserNotFoundException {
    	Long countById = userRepository.countById(id);
    	if (countById == null || countById == 0) {
    		throw new UserNotFoundException("Could not find any user with Id " + id);
    	}
    	userRepository.deleteById(id);
    }
    
    public void updateUserEnabledStatus(Integer id, boolean enabled) {
    	userRepository.updateEnabledStatus(id, enabled);
    }
}
