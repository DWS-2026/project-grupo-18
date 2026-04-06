package com.example.cybercert.Services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.cybercert.Role;
import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.Image;
import com.example.cybercert.Models.User;
import com.example.cybercert.Repositories.UserRepository;

@Service
public class DBInitializerService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CertificationService certificationService;

    @Autowired
    ImageService imageService;

    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@cybercert.com");
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("ADMIN CREADO");
        }

        
        if (userRepository.findByUsername("user").isEmpty()) {

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@cybercert.com");
            user.setRole(Role.USER);

            userRepository.save(user);
            System.out.println("USER CREADO");
        }

        // CERTIFICACIONES
        if (certificationService.count() == 0) {

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
                    null);

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
                    null);

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
                    null);

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
                    null);
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
                    null);

            setCertImage(redTeam, "static/assets/img/redteam.png");
            setCertImage(pwn, "static/assets/img/pwn.jpg");
            setCertImage(osint, "static/assets/img/OSINT.jpg");
            setCertImage(web, "static/assets/img/web.jpg");
            setCertImage(pentester, "static/assets/img/Pentest.png");

            certificationService.save(pwn);
            certificationService.save(osint);
            certificationService.save(web);
            certificationService.save(pentester);
            certificationService.save(redTeam);

            System.out.println("CERTIFICACIONES CREADAS:");
        }
    }

    public void setCertImage(Certification cert, String classpathResource) throws IOException {
        Resource image = new ClassPathResource(classpathResource);

        Image createdImage = imageService.createImage(image.getInputStream());
        cert.setImage(createdImage);
    }
}
