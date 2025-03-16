# AggieNet

AggieNet is a **Java-based social media platform** designed for students and faculty at **Texas A&M University**, facilitating **collaboration through posts, comments, likes, and real-time chats**. The platform is built using **microservices architecture** for scalability and **Firebase authentication** for secure user login.

## Features
- **Secure Authentication** – Firebase authentication for user login and registration.
- **Post & Comment System** – Users can create, like, and comment on posts.
- **Real-time Chat** – Redis-backed message storage for fast and scalable communication.
- **Microservices Architecture** – Decoupled services for improved scalability and maintainability.
- **High Performance** – Optimized Redis caching for reducing database load.
- **Scalable Backend** – Uses Java and Spring Boot with RESTful APIs for seamless communication.

## Tech Stack
- **Backend**: Java, Spring Boot, REST APIs, Microservices
- **Database**: Firebase (Authentication & Firestore), Redis (for chat storage)
- **DevOps**: Docker, Kubernetes (if applicable)
- **Version Control**: Git & GitHub

## Setup Instructions

### Prerequisites
Ensure you have the following installed on your system:
- **Java 17+**
- **Spring Boot**
- **Redis**
- **Firebase SDK**
- **Git**

### Installation & Running Locally
1. **Clone the Repository**
   ```sh
   git clone https://github.com/SrishtiKalra/AggieNet.git
   cd AggieNet
