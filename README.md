# Tech Blog API - Endpoints to Implement

## 1 Authentication (3 endpoints)

| Method | Endpoint         | Auth Required | Description                    |
|--------|------------------|---------------|--------------------------------|
| `POST` | `/auth/register` | ❌             | Register new user              |
| `POST` | `/auth/login`    | ❌             | Login and receive JWT token    |
| `GET`  | `/auth/me`       | ✅             | Get current authenticated user |

---

## 2 User Profiles (2 endpoints)

| Method  | Endpoint            | Auth Required | Description             |
|---------|---------------------|---------------|-------------------------|
| `GET`   | `/users/{username}` | ❌             | Get user public profile |
| `PATCH` | `/users/me`         | ✅             | Update own profile      |

**Cache:** Redis cache for profiles (`user:profile:{username}`, TTL 1 hour)

---

## 3 Posts (5 endpoints)

| Method   | Endpoint        | Auth Required | Description             |
|----------|-----------------|---------------|-------------------------|
| `POST`   | `/posts`        | ✅             | Create new post         |
| `GET`    | `/posts/{slug}` | ❌             | Get single post by slug |
| `GET`    | `/posts`        | ❌             | List posts with filters |
| `PATCH`  | `/posts/{slug}` | ✅             | Update own post         |
| `DELETE` | `/posts/{slug}` | ✅             | Delete own post         |

**Query Parameters for `GET /posts`:**

- `?page=0` - Page number (default: 0)
- `&size=20` - Page size (default: 20, max: 100)
- `&sort=created_at` - Sort by: `created_at` | `views` | `likes`
- `&author=username` - Filter by author
- `&tag=spring-boot` - Filter by tag

**Cache:** Redis cache for individual posts (`post:{slug}`, TTL 1 hour)

---

## 4 Reactions (2 endpoints)

| Method   | Endpoint             | Auth Required | Description   |
|----------|----------------------|---------------|---------------|
| `POST`   | `/posts/{slug}/like` | ✅             | Like a post   |
| `DELETE` | `/posts/{slug}/like` | ✅             | Unlike a post |

**Implementation:**

- Use Redis Sets: `post:{slug}:likes` → Set of user IDs
- Atomic operations to prevent race conditions
- Periodic sync to PostgreSQL

---

## 5 Comments (3 endpoints)

| Method   | Endpoint                 | Auth Required | Description                   |
|----------|--------------------------|---------------|-------------------------------|
| `POST`   | `/posts/{slug}/comments` | ✅             | Add comment to post           |
| `GET`    | `/posts/{slug}/comments` | ❌             | Get post comments (paginated) |
| `DELETE` | `/comments/{id}`         | ✅             | Delete own comment            |

---

## Summary

**Total: 15 endpoints**

- Authentication: 3
- User Profiles: 2
- Posts: 5
- Reactions: 2
- Comments: 3

**Tech Requirements:**

- JWT authentication with Spring Security
- Redis caching (profiles, posts, likes)
- PostgreSQL database
- Pagination for lists
- SOLID principles + Design Patterns
- Unit + Integration tests
- Docker + CI/CD
