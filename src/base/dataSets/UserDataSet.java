package base.dataSets;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserDataSet implements Serializable { // Serializable Important to Hibernate!
	private static final long serialVersionUID = -8706689714326132798L;

	 @Id
	 @Column(name = "id")
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private long id;

	 @Column(name = "username")
	 private String username;

	 @Column(name = "password")
	 private String password;

	 @Column(name = "email")
	 private String email;
	 
	 //Important to Hibernate!
	 public UserDataSet() {
	 }	
	
	 public UserDataSet(long id, String username, String password, String email) {
	        this.setId(id);
	        this.setName(username);
	        this.setPassword(password);
	        this.setEmail(email);
	        }
	 
	 public UserDataSet(String username) {
	        this.setId(-1);
	        this.setName(username);
	        this.setPassword("");
	        this.setEmail("");
	 }
	 
	 public String getName() {
		 return username;
	 }	 

	 public String getPassword() {
		 return password;
	 }	
	 
	 public String getEmail() {
		 return email;
	 }		 
	 
	 public void setName(String username) {
	     this.username = username;
	 }

	 public void setPassword(String password) {
	     this.password = password;
	 }

	 public void setEmail(String email) {
	     this.email = email;
	 }
	 
	 
	 public long getId() {
	     return id;
	 }
	 
	 public void setId(long id) {
	     this.id = id;
	 }
	 
	 @Override
	 public String toString() {
	     return "UserDataSet{" +
	             "id=" + id +
	             ", username='" + username + '\'' +
	             '}';
	    }	 
	 
}
