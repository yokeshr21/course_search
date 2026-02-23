# Course Search Application

A Spring Boot + Elasticsearch application that provides production-grade course search functionality.

## Features

- Fuzzy full-text search (title + description)
- Category and type filtering
- Price range filtering
- Age range filtering
- Date filtering
- Sorting (price/date)
- Pagination
- Autocomplete (bonus)
<br/>

---

## 1. Launch Elasticsearch

Make sure Docker is installed and running.

Start Elasticsearch using Docker Compose:

```bash
docker compose up -d
```

Verify Elasticsearch is running in terminal:

```bash
curl.exe http://localhost:9200
```

You should see a JSON response similar to:

```json
{
  "name": "elasticsearch",
  "cluster_name": "docker-cluster",
  "version": {
    "number": "7.17.x"
  }
}
```

To stop Elasticsearch:

```bash
docker compose down
```
<br/>

---

## 2. Build and Run the Application

Make sure you are inside the project root directory.

### Build the project

```bash
mvn clean install
```

### Run the Spring Boot application

```bash
mvn spring-boot:run
```

Or run the generated JAR:

```bash
java -jar target/course-search-0.0.1-SNAPSHOT.jar
```

The application will start on:

```
http://localhost:8080
```

You should see logs confirming that the Spring Boot application started successfully.

<br/>

---

## 3. Populate Elasticsearch with Sample Data

The application automatically loads sample course data into Elasticsearch at startup using a DataLoader component.

To populate the index:

1. Ensure Elasticsearch is running:
   ```bash
   docker compose up -d
   ```

2. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

When the application starts:
- The index will be created (if not exists)
- Sample data will be inserted

You can verify the indexed data:

```bash
curl http://localhost:9200/courses/_search?pretty
```

You should see multiple course documents returned in the response.

<br/>

---

## 4. Search API Endpoints

Base URL:

```
http://localhost:8080/api/search
```

---

### 1️⃣ Basic Search

Search by keyword (title + description):

```bash
curl "http://localhost:8080/api/search?query=math"
```

---

### 2️⃣ Filter by Category

```bash
curl "http://localhost:8080/api/search?category=Science"
```

---

### 3️⃣ Filter by Type

```bash
curl "http://localhost:8080/api/search?type=COURSE"
```

---

### 4️⃣ Price Range Filter

```bash
curl "http://localhost:8080/api/search?minPrice=40&maxPrice=80"
```

---

### 5️⃣ Age Range Filter

```bash
curl "http://localhost:8080/api/search?minAge=8&maxAge=12"
```

---

### 6️⃣ Date Range Filter

```bash
curl "http://localhost:8080/api/search?startDate=2025-06-01&endDate=2025-07-30"
```

---

### 7️⃣ Sorting

Sort by price (ascending):

```bash
curl "http://localhost:8080/api/search?sortBy=price&direction=asc"
```

Sort by start date (descending):

```bash
curl "http://localhost:8080/api/search?sortBy=startDate&direction=desc"
```

---

### 8️⃣ Pagination

```bash
curl "http://localhost:8080/api/search?page=0&size=5"
```

<br/>

---

## 5. Bonus Features

### 🔎 Fuzzy Search

Supports typo-tolerant search using Elasticsearch fuzziness.

Example:

```bash
curl "http://localhost:8080/api/search?query=scince"
```

Even though "science" is misspelled, relevant results will still be returned.

---

### 🔤 Autocomplete

Autocomplete suggestions based on course title.

Example:

```bash
curl "http://localhost:8080/api/search/autocomplete?prefix=mat"
```

Sample response:

```json
[
  "Math Basics",
  "Mathematics for Kids",
  "Advanced Mathematics"
]
```

---

### 🧪 Using Postman

You can also test all endpoints using Postman:

1. Open Postman
2. Create a new GET request
3. Enter the URL:
   ```
   http://localhost:8080/api/search?query=math
   ```
4. Click **Send**

You will receive JSON results from Elasticsearch.
