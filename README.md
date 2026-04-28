# Smart-Contact-Manager
SCM 2.0 is a comprehensive, full-stack web application designed to revolutionize how individuals manage their professional and personal connections. In an era where networking is key, this project provides a secure, scalable, and user-friendly platform to store, organize, and access contact information from anywhere. The application is built using the Spring Boot framework, leveraging the power of Java for robust backend logic and Tailwind CSS for a modern, responsive frontend experience.

# Core Functionality & Technical Architecture
The heart of SCM 2.0 lies in its sophisticated data management. I implemented a Relational Database Schema using MySQL, where a strict One-to-Many relationship is maintained between Users and their Contacts. To ensure data integrity, I utilized Spring Data JPA with advanced mapping techniques like mappedBy and CascadeType.ALL, ensuring that contact data is always synchronized with the parent user.

For handling large text data such as "User Bios" or "Contact Descriptions," I utilized the @Lob annotation and defined columns as TEXT types. This architectural choice prevents common database limitations and ensures the application can handle extensive user input without performance degradation.

# Security & Authentication
Security is a top priority for SCM 2.0. The system integrates Spring Security to handle authentication and authorization. Beyond standard form-based login, the project supports OAuth2 integration, allowing users to sign in seamlessly via Google and GitHub. This not only enhances security but also improves user onboarding.

# UI/UX Strategy
The frontend is crafted using Thymeleaf and Tailwind CSS, supplemented by the Flowbite component library. By choosing a utility-first CSS approach, I ensured that the application is:

Fully Responsive: Optimized for desktops, tablets, and mobile devices.

Accessible: Adhering to WAI-ARIA standards to ensure usability for everyone.

Performance-Oriented: Using Tailwind's JIT (Just-In-Time) engine to keep the CSS bundle size minimal.

Theme-Ready: Featuring a native Dark Mode toggle that adapts to user preferences.

# Why I Built This
This project was developed with a "Product-First" mindset. Instead of reinventing standard UI components, I leveraged professional libraries like Flowbite to focus my energy on solving complex backend challenges, such as database normalization, secure API design, and OAuth2 workflows. It stands as a testament to my ability to build production-ready applications that balance aesthetic design with technical excellence.
