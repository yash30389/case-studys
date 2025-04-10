# **Case Study: Ensuring Reliable Inter-Container Communication with Static IPs in Docker**

<table align="center">
  <tr>
    <td align="center">
      <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Docker_%28container_engine%29_logo.svg/610px-Docker_%28container_engine%29_logo.svg.png" alt="DOCKER" width="200"/>
    </td>
  </tr>
</table>

### **Background**

In a microservices-based application architecture, it is common to have multiple containers where each service depends on one or more other services. In this project, we had the following containerized services:

1. **API Service (Container A)**
2. **Database Service (PostgreSQL) (Container B)**
3. **Cache Service (Redis) (Container C)**

Each service needs to communicate with the others:
- **API Service** connects to **PostgreSQL** and **Redis**.
- **PostgreSQL** also benefits from **Redis** for query caching.

---

### **Challenge**

During development and deployment using Docker’s default bridge network, we encountered a critical issue:
- Docker dynamically assigns IP addresses to containers.
- On **container restarts** or **Docker daemon restart**, container IPs change.
- Hardcoding dynamic IPs in configurations (e.g., `API -> DB`) caused communication failure.
- Relying only on Docker DNS (container names) was not enough in this scenario due to specific IP whitelisting requirements (e.g., security groups/firewalls, ACLs).

---

### **Objectives**
- Ensure **static IP assignment** for each container.
- Maintain **seamless communication** between services even after restarts.
- Simplify **network configuration** and increase reliability.

---

### **Solution**

We decided to **create a custom Docker bridge network** with a defined subnet and assign **static IPs** to each container.

---

### **Implementation**

#### **Step 1: Create a custom Docker network**

```bash
docker network create \
  --subnet=172.28.0.0/16 \
  --gateway=172.28.0.1 \
  my_custom_network
```

#### **Step 2: Deploy containers with static IPs**

```bash
docker run -d --name api_service \
  --net my_custom_network \
  --ip 172.28.0.10 \
  my_image_api

docker run -d --name postgres_db \
  --net my_custom_network \
  --ip 172.28.0.20 \
  postgres:latest

docker run -d --name redis_cache \
  --net my_custom_network \
  --ip 172.28.0.30 \
  redis:latest
```

---

### **Step 3: docker-compose for better orchestration**

We created a `docker-compose.yml` to simplify and version-control the setup:

```yaml
version: '3.8'
services:
  api_service:
    image: my_image_api
    networks:
      my_network:
        ipv4_address: 172.28.0.10

  postgres_db:
    image: postgres:latest
    environment:
      POSTGRES_PASSWORD: example
    networks:
      my_network:
        ipv4_address: 172.28.0.20

  redis_cache:
    image: redis:latest
    networks:
      my_network:
        ipv4_address: 172.28.0.30

networks:
  my_network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.28.0.0/16
```

### **Results**

- **Static IP assignment succeeded.**
- Even after restarting containers or Docker service, all IPs remained consistent.
- **API Service** connected reliably to `172.28.0.20:5432` for PostgreSQL and `172.28.0.30:6379` for Redis.
- Reduced network misconfiguration and **no more broken service links** due to IP changes.
- Improved **security posture** by allowing IP-based firewall and ACL rules between containers or with external systems.



### **Additional Improvements**

1. **Health Checks**  
   We also implemented health checks to ensure services only start when their dependencies are available.
   
2. **Environment Variables**  
   Moved IPs and ports to `.env` file for flexibility and ease of change.

3. **Service Discovery Alternative**  
   Although we used static IPs, Docker’s internal DNS still worked, allowing `postgres_db:5432` as an alternative to `172.28.0.20:5432`.

---

### **Lessons Learned**

- **Static IPs** are crucial for systems requiring strict IP whitelisting.
- **Custom Docker networks** give more control than the default bridge network.
- Using **docker-compose** brings structure, reusability, and scalability.

---

### **Future Recommendations**

- Consider **Docker Swarm** or **Kubernetes** for more advanced service discovery and dynamic scaling.
- Use **Consul**, **etc.d**, or **Traefik** for more robust service registration and discovery when the architecture grows.
