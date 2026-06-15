package ma.gov.justice.gestion_dossiers.config;

import ma.gov.justice.gestion_dossiers.entity.User;
import ma.gov.justice.gestion_dossiers.entity.UserRole;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("مدير");
                admin.setLastName("النظام");
                admin.setEmail("admin@justice.gov.ma");
                admin.setRole(UserRole.MANAGER);
                userRepository.save(admin);
                System.out.println("✅ Default admin user created: admin / admin123");
            }
            
            if (userRepository.findByUsername("ahmed").isEmpty()) {
                User emp = new User();
                emp.setUsername("ahmed");
                emp.setPassword(passwordEncoder.encode("ahmed123"));
                emp.setFirstName("أحمد");
                emp.setLastName("محمدي");
                emp.setEmail("ahmed@justice.gov.ma");
                emp.setRole(UserRole.SESSION_CLERK);
                userRepository.save(emp);
                System.out.println("✅ Default employee user created: ahmed / ahmed123");
            }

            if (userRepository.findByUsername("fatima").isEmpty()) {
                User clerk = new User();
                clerk.setUsername("fatima");
                clerk.setPassword(passwordEncoder.encode("fatima123"));
                clerk.setFirstName("فاطمة");
                clerk.setLastName("الزهراء");
                clerk.setEmail("fatima@justice.gov.ma");
                clerk.setRole(UserRole.CLERK);
                userRepository.save(clerk);
                System.out.println("✅ Default clerk user created: fatima / fatima123");
            }

            if (userRepository.findByUsername("karim").isEmpty()) {
                User archive = new User();
                archive.setUsername("karim");
                archive.setPassword(passwordEncoder.encode("karim123"));
                archive.setFirstName("كريم");
                archive.setLastName("بنيس");
                archive.setEmail("karim@justice.gov.ma");
                archive.setRole(UserRole.ARCHIVE_OFFICER);
                userRepository.save(archive);
                System.out.println("✅ Default archive officer created: karim / karim123");
            }
        };
    }
}
