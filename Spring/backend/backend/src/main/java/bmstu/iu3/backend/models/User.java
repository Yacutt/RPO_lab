package bmstu.iu3.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User {

    public User() { }
    public User(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @JsonView(Views.Public.class)
    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @JsonView(Views.Public.class)
    @Column(name = "login")
    public  String login;

    @JsonIgnore
    @Column(name = "password")
    public String password;

    @JsonIgnore
    @Column(name = "salt")
    public String salt;

    @JsonView(Views.Private.class)
    @Column(name = "token")
    public String token;

    @JsonView(Views.Public.class)
    @Column(name = "activity")
    public LocalDateTime activity;

    @JsonView(Views.Public.class)
    @ManyToMany(mappedBy = "users")
    public Set<Museum> museums = new HashSet<>();


    public void addMuseum(Museum m) {
        this.museums.add(m);
        m.users.add(this);
    }

    public void removeMuseum(Museum m) {
        this.museums.remove(m);
        m.users.remove(this);
    }

}

