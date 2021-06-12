package com.example.zingmp3.controller;

import com.example.zingmp3.dto.JwtResponse;
import com.example.zingmp3.model.Playlist;
import com.example.zingmp3.model.Role;
import com.example.zingmp3.model.User;
import com.example.zingmp3.service.JwtService;
import com.example.zingmp3.service.playlistService.IPlaylistService;
import com.example.zingmp3.service.role.IRoleService;
import com.example.zingmp3.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IPlaylistService playlistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateTokenLogin(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(user.getUsername());

        return ResponseEntity.ok(new JwtResponse(currentUser.getId(), userDetails.getUsername(), jwt, userDetails.getAuthorities()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Iterable<User> users = userService.findAll();
        for (User currentUser : users) {
            if(currentUser.getUsername().equals(user.getUsername())){
                return new ResponseEntity<>("dbusername",HttpStatus.BAD_REQUEST);
            }else if(currentUser.getPhone().equals(user.getPhone())){
                return new ResponseEntity<>("dbphone",HttpStatus.BAD_REQUEST);
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        Optional<Role> role = roleService.findById(1L);

        Playlist playlist = new Playlist();
        playlist.setNamePlaylist("Play_List_Root");
        playlistService.save(playlist);
        roles.add(role.get());
        user.setPlaylistRoot(playlist);
        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
