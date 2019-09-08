# redis-cache-flathash
Utilities for using Spring Cache and Spring Data Redis with the feature to store object into Redis with a flat hash form.
This is a derivative work of [Spring Data Reids](https://github.com/spring-projects/spring-data-redis)

Usage: See the [example](https://github.com/ethancommitpush/redis-cache-flathash/tree/master/example) directory for configure

```java
@Cacheable(value="springCache", key="'customer:' + #id", unless = "#result == null")/* result in redis key: springCache::customer:1 */
@CacheExpire(expire = 60)
public Customer findById(Integer id) {
	Customer customer = mockRepostory.get(id);
	return customer;
}
```

and the data would be stored in Redis with key "springCache::customer:1" like:

|                |                                                     |
|----------------|-----------------------------------------------------|
| _class         | idv.ethancommitpush.flathash.example.model.Customer |
| id             | 1                                                   |
| firstName      | FirstName1                                          |
| lastName       | LastName1                                           |
| privilege.id   | 1                                                   |
| privilege.name | normal                                              |
