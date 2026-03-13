package com.example.cybercert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;

@SpringBootApplication
public class CyberCertApplication {

        public static void main(String[] args) {
                SpringApplication.run(CyberCertApplication.class, args);
        }

        @Autowired
        PasswordEncoder passwordEncoder;

        @Bean
        CommandLineRunner init(UserRepository userRepository, CertificationRepository certificationRepository) {
                return args -> {

                        // ADMIN
                        if (userRepository.findByUsername("admin").isEmpty()) {

                                User admin = new User();
                                admin.setUsername("admin");
                                admin.setPassword(passwordEncoder.encode("admin123"));
                                admin.setEmail("admin@cybercert.com");
                                admin.setRole(Role.ADMIN);

                                userRepository.save(admin);
                                System.out.println("ADMIN CREADO");
                        }

                        // CERTIFICACIONES
                        if (certificationRepository.count() == 0) {

                                Certification pwn = new Certification(
                                                "Pwn",
                                                "Advanced",
                                                70,
                                                "Online / Labs",
                                                "English",
                                                "This course dives deep into binary exploitation, memory corruption, and reverse engineering. You will learn practical techniques to analyze, exploit, and secure vulnerable programs in real-world environments.",
                                                List.of(
                                                                "Solid Linux knowledge",
                                                                "Intermediate C programming",
                                                                "Understanding of memory layout and pointers",
                                                                "Basic assembly language understanding",
                                                                "Debugging experience with gdb or pwndbg"),
                                                List.of(
                                                                "Stack and heap exploitation",
                                                                "Buffer overflows and format string attacks",
                                                                "ROP chains and bypassing protections",
                                                                "Reverse engineering simple binaries",
                                                                "Practical exploitation labs"),
                                                "assets/img/pwn.jpg");

                                Certification osint = new Certification(
                                                "OSINT",
                                                "Beginner",
                                                40,
                                                "Online",
                                                "English",
                                                "Learn how to gather actionable intelligence from publicly available sources. This course covers tools and methodologies used in investigative work, corporate security, and ethical hacking.",
                                                List.of(
                                                                "Basic internet research skills",
                                                                "Understanding of social media platforms",
                                                                "Familiarity with Google search operators",
                                                                "Basic cybersecurity knowledge",
                                                                "Attention to detail and analytical thinking"),
                                                List.of(
                                                                "Searching and scraping public data",
                                                                "Analyzing social media profiles",
                                                                "Domain and IP reconnaissance",
                                                                "Understanding online footprints",
                                                                "Reporting and documenting findings"),
                                                "assets/img/OSINT.jpg");

                                Certification web = new Certification(
                                                "Web",
                                                "Intermediate",
                                                60,
                                                "Online / Labs",
                                                "English",
                                                "Focused on web application security, this course teaches how to discover, exploit, and mitigate common web vulnerabilities like XSS, SQLi, CSRF, and more, using hands-on labs.",
                                                List.of(
                                                                "Basic web technologies (HTML, CSS, JavaScript)",
                                                                "Understanding of HTTP and HTTPS",
                                                                "Knowledge of web servers and databases",
                                                                "Familiarity with Linux and basic scripting",
                                                                "Security awareness concepts"),
                                                List.of(
                                                                "SQL Injection attacks",
                                                                "Cross-Site Scripting (XSS)",
                                                                "Cross-Site Request Forgery (CSRF)",
                                                                "Authentication and session attacks",
                                                                "Practical lab exercises on web targets"),
                                                "assets/img/web.jpg");

                                Certification pentester = new Certification(
                                                "Pentester",
                                                "Intermediate",
                                                65,
                                                "Online / Labs",
                                                "English",
                                                "A full professional penetration testing workflow course covering reconnaissance, scanning, exploitation, post-exploitation, and reporting. Designed for aspiring ethical hackers.",
                                                List.of(
                                                                "Networking fundamentals",
                                                                "Linux basics",
                                                                "Security fundamentals",
                                                                "Familiarity with common penetration testing tools",
                                                                "Analytical and problem-solving mindset"),
                                                List.of(
                                                                "Reconnaissance and information gathering",
                                                                "Vulnerability scanning and enumeration",
                                                                "Exploitation techniques",
                                                                "Post-exploitation and persistence",
                                                                "Professional reporting and documentation"),
                                                "assets/img/Pentest.png");
                                Certification redTeam = new Certification(
                                                "Red Team",
                                                "Advanced",
                                                80,
                                                "Online / Labs",
                                                "English",
                                                "This course focuses on full-scope Red Team operations, simulating real-world adversaries. You will learn offensive strategies, covert exploitation, lateral movement, and evasion techniques to assess organizational security in depth.",
                                                List.of(
                                                                "Advanced networking knowledge",
                                                                "Windows and Linux administration",
                                                                "Understanding of Active Directory environments",
                                                                "Familiarity with penetration testing tools and frameworks",
                                                                "Analytical thinking and problem-solving mindset"),
                                                List.of(
                                                                "Covert reconnaissance and social engineering",
                                                                "Lateral movement and privilege escalation",
                                                                "Persistence and evasion techniques",
                                                                "Simulated attack campaigns on complex environments",
                                                                "Reporting and recommendations for defensive measures"),
                                                "assets/img/redteam.png");
                                List<Certification> certs = certificationRepository.saveAll(
                                                List.of(pwn, osint, web, pentester, redTeam));

                                System.out.println("CERTIFICACIONES CREADAS:");

                                for (Certification c : certs) {
                                        System.out.println(c.getId() + " - " + c.getName());
                                }
                        }
                };
        }

}