package phattrienungdungvoij2ee.bai5_qlsp_jpa.model;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity//
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String login_name;

    @Column
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "AccountRole",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Lien ket tai khoan cu dan voi chung cu dang o
    @ManyToOne
    @JoinColumn(name = "chungcu_id", nullable = true)
    private ChungCu chungCu;
}
