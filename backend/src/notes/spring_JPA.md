@Query("SELECT p FROM Post p JOIN FETCH p.author")
       ↑       ↑    ↑    ↑              ↑
    SELECT  entity alias relationship  fetch eagerly
```

**Key differences:**

|                 SQL           |              JPQL            |
|-------------------------------|------------------------------|
| Table names: `posts`, `users` | Entity names: `Post`, `User` |
| Column names: `user_id`       | Field names: `author`        |
| Returns rows                  | Returns objects              |
| Database-specific             | Database-independent         |

**Why JPQL?**
- ✅ Works with any database (PostgreSQL, MySQL, etc.)
- ✅ Type-safe (references Java classes)
- ✅ Refactoring-friendly (IDE can find usages)
- ✅ Returns Java objects automatically

---

## 📊 Performance Comparison

Let's see the actual difference:

### Scenario: Get 100 posts with authors

```
**Without JOIN FETCH:**
```
Query 1: SELECT * FROM posts
    ↓ 100 posts returned
Query 2: SELECT * FROM users WHERE id = 'user-1'
Query 3: SELECT * FROM users WHERE id = 'user-2'
Query 4: SELECT * FROM users WHERE id = 'user-3'
...
Query 101: SELECT * FROM users WHERE id = 'user-100'

Total: 101 queries
Time: ~500ms (if each query takes 5ms)
```

**With JOIN FETCH:**
```
Query 1: SELECT p.*, u.* FROM posts p JOIN users u ON p.user_id = u.id
    ↓ Returns everything in one go

Total: 1 query
Time: ~20ms
```