# ContactSphere

ContactSphere is a full-stack contact management application that helps users securely store, organize, search, and manage their personal and professional contacts. The application provides authentication, profile management, contact management, image uploads, and cloud deployment support.

## Features

* User registration and login
* Email verification
* OAuth2 login with Google and GitHub
* Spring Security based authentication and authorization
* Role-based access control
* Contact CRUD operations (Create, Read, Update, Delete)
* Contact search and filtering
* Pagination for contact listings
* User profile management
* Contact image upload using Cloudinary
* Responsive UI with Tailwind CSS and Flowbite
* Dark mode support
* Cloud deployment using AWS Elastic Beanstalk and Amazon RDS

## Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* OAuth2

### Frontend

* Thymeleaf
* Tailwind CSS
* Flowbite
* JavaScript

### Database

* MySQL

### Cloud & Storage

* AWS Elastic Beanstalk
* Amazon RDS
* Cloudinary

## Database Design

The application follows a one-to-many relationship between Users and Contacts.

* One User can manage multiple Contacts.
* Each Contact belongs to a single User.
* Hibernate and JPA are used for object-relational mapping.
* Cascade operations are configured to maintain data consistency.

## Security

Security is implemented using Spring Security.

* BCrypt password encryption
* Session-based authentication
* OAuth2 login with Google and GitHub
* Protected routes based on user authentication
* Email verification during account registration

## Key Learning Outcomes

Through this project, I gained practical experience with:

* Spring Boot application development
* Authentication and authorization using Spring Security
* OAuth2 integration
* Database design using MySQL and JPA
* Cloud deployment on AWS
* Image storage using Cloudinary
* Building responsive user interfaces with Tailwind CSS and Thymeleaf

## Deployment

The application is deployed on AWS Elastic Beanstalk and uses Amazon RDS as the managed MySQL database.

## Screenshots

<img width="1910" height="874" alt="image" src="https://github.com/user-attachments/assets/f0e08f4e-a782-4691-9090-b6550fb5059f" />

                                                        Login Page 

<img width="1886" height="870" alt="image" src="https://github.com/user-attachments/assets/745c72f9-59bb-4733-b4c4-f27104eb2a66" />
                                                        
                                                        Signup Page
                                                        
<img width="1889" height="872" alt="image" src="https://github.com/user-attachments/assets/6d551a4c-d76f-4594-8251-502e00f4fe50" />

                                                  Fully Dark Mode Supported

<img width="1897" height="878" alt="Screenshot 2026-06-01 150110" src="https://github.com/user-attachments/assets/493f2e64-28df-4a1d-ab4b-84b96430ac33" />
                                                       
                                                       Profile Page
                                                       
<img width="1886" height="870" alt="Screenshot 2026-06-01 150125" src="https://github.com/user-attachments/assets/9509a4f3-bd08-4172-b6a3-a4f503b7278d" />
                                                       
                                                       Dashboard Page
                                                       
<img width="1889" height="868" alt="image" src="https://github.com/user-attachments/assets/31e2828e-28de-4892-a372-056557d8b18c" />
                                              
                                              Contact Page with PDF/Excel Export
                                              
<img width="1885" height="872" alt="image" src="https://github.com/user-attachments/assets/10c6b972-87aa-4c2b-9ae8-4a472708c24c" />
                                                      
                                                      Feedback Page
                                                      
<img width="427" height="761" alt="image" src="https://github.com/user-attachments/assets/96ca8310-e1a8-40a0-924d-076c04471fdd" />
                                                     
                                                     Mobile Responsive
                                                     

## Author

Arun Sharma
