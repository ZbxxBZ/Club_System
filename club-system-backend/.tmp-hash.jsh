import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
var e = new BCryptPasswordEncoder();
System.out.println("admin123=" + e.encode("admin123"));
System.out.println("cadmin123=" + e.encode("cadmin123"));
System.out.println("test123=" + e.encode("test123"));
/exit
