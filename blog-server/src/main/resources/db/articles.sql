-- =============================================
-- 博客文章初始数据
-- 文章30篇，时间线：2025年6月 ~ 2026年3月
-- =============================================

USE blog;

-- ===================== 初始化标签 =====================
INSERT IGNORE INTO `tag` (`name`, `slug`) VALUES
('Java', 'java'),
('并发编程', 'concurrent'),
('JVM', 'jvm'),
('MySQL', 'mysql'),
('Redis', 'redis'),
('计算机网络', 'network'),
('操作系统', 'os'),
('Spring', 'spring'),
('Elasticsearch', 'elasticsearch'),
('RocketMQ', 'rocketmq'),
('设计模式', 'design-pattern'),
('DDD', 'ddd'),
('架构设计', 'architecture'),
('缓存', 'cache'),
('消息队列', 'mq'),
('线程安全', 'thread-safe'),
('索引优化', 'index-optimize'),
('面试', 'interview'),
('RabbitMQ', 'rabbitmq'),
('HashMap', 'hashmap'),
('集合框架', 'collection'),
('AQS', 'aqs'),
('事务', 'transaction'),
('分库分表', 'sharding'),
('Feed流', 'feed');

-- ===================== 文章第1篇 =====================
INSERT INTO `article` (`title`, `slug`, `summary`, `content`, `category_id`, `view_count`, `status`, `seo_keywords`, `seo_description`, `author_id`, `create_time`, `publish_time`) VALUES
('ArrayList vs LinkedList：底层数据结构对比与选择',
'arraylist-vs-linkedlist',
'从源码层面深入分析 ArrayList 和 LinkedList 的底层数据结构差异，对比两者在随机访问、头部插入、遍历等场景下的性能表现，并给出实际开发中的选型建议。',
'## 前言

在日常开发中，`ArrayList` 和 `LinkedList` 是 Java 集合框架中最常用的两个 `List` 实现类。面试中也经常被问到两者的区别。本文从底层源码出发，深入分析两者的数据结构与性能差异。

## 一、ArrayList：动态数组

### 1.1 底层结构

`ArrayList` 底层基于**动态数组**实现：

```java
public class ArrayList<E> {
    transient Object[] elementData;
    private int size;
}
```

核心就是一个 `Object[]` 数组，通过 `size` 记录实际元素个数。

### 1.2 扩容机制

当数组容量不足时，`ArrayList` 会自动扩容：

```java
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    // 新容量 = 旧容量 * 1.5
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

关键点：
- 每次扩容为原来的 **1.5 倍**
- 扩容需要调用 `Arrays.copyOf()`，涉及数组的**完整拷贝**
- 默认初始容量为 10，如果已知数据量，建议通过 `new ArrayList<>(capacity)` 指定初始容量，避免多次扩容

### 1.3 时间复杂度

| 操作 | 时间复杂度 | 说明 |
|------|-----------|------|
| 随机访问 `get(i)` | O(1) | 数组下标直接定位 |
| 尾部添加 `add(e)` | 均摊 O(1) | 偶尔触发扩容 |
| 中间插入 `add(i, e)` | O(n) | 需要移动后续元素 |
| 头部插入 | O(n) | 需要移动所有元素 |
| 删除元素 | O(n) | 需要移动后续元素 |

## 二、LinkedList：双向链表

### 2.1 底层结构

`LinkedList` 底层基于**双向链表**实现：

```java
public class LinkedList<E> {
    transient Node<E> first;
    transient Node<E> last;
    transient int size;

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;
    }
}
```

每个节点保存了前驱和后继的引用，同时维护了 `first` 和 `last` 两个指针。

### 2.2 核心操作

**头插法：**

```java
private void linkFirst(E e) {
    final Node<E> f = first;
    final Node<E> newNode = new Node<>(null, e, f);
    first = newNode;
    if (f == null)
        last = newNode;
    else
        f.prev = newNode;
    size++;
}
```

**尾插法：**

```java
void linkLast(E e) {
    final Node<E> l = last;
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    if (l == null)
        first = newNode;
    else
        l.next = newNode;
    size++;
}
```

链表的插入只需要修改指针引用，不涉及数据搬迁。

### 2.3 时间复杂度

| 操作 | 时间复杂度 | 说明 |
|------|-----------|------|
| 随机访问 `get(i)` | O(n) | 需要从头/尾遍历 |
| 头尾添加 | O(1) | 直接操作指针 |
| 中间插入 | O(n) | 遍历定位 + O(1)插入 |
| 删除已知节点 | O(1) | 修改指针即可 |

## 三、性能实测

用一个简单的基准测试来验证：

```java
// 尾部插入 100 万次
List<Integer> arrayList = new ArrayList<>();
List<Integer> linkedList = new LinkedList<>();

long start = System.nanoTime();
for (int i = 0; i < 1_000_000; i++) {
    arrayList.add(i);
}
long end = System.nanoTime();
System.out.println("ArrayList 尾插耗时: " + (end - start) / 1_000_000 + "ms");

start = System.nanoTime();
for (int i = 0; i < 1_000_000; i++) {
    linkedList.add(i);
}
end = System.nanoTime();
System.out.println("LinkedList 尾插耗时: " + (end - start) / 1_000_000 + "ms");
```

**典型结果：**

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| 尾部插入 100万次 | ~50ms | ~120ms |
| 头部插入 10万次 | ~2500ms | ~15ms |
| 随机访问 10万次 | ~8ms | ~8000ms |
| 遍历 100万次 | ~15ms | ~40ms |

### 为什么尾插 ArrayList 更快？

虽然 LinkedList 尾插是 O(1)，但每次 `new Node()` 需要创建对象，涉及 JVM 堆内存分配。而 ArrayList 使用预分配的数组，大部分情况下只是赋值操作，且 CPU 缓存友好（连续内存）。

### 为什么遍历 ArrayList 也更快？

**CPU 缓存行（Cache Line）效应。** 数组的内存是连续的，CPU 预取机制可以高效加载；链表节点在内存中分散分布，每次访问都可能发生 Cache Miss。

## 四、内存占用对比

以存储 Integer 对象为例：

- **ArrayList**：底层数组引用 + 对象本身。1000个元素约 4KB（引用）+ 未使用容量
- **LinkedList**：每个 Node 包含 prev + item + next 三个引用。1000个元素约 24KB

LinkedList 的内存开销约为 ArrayList 的 **5~6 倍**。

## 五、选型建议

| 场景 | 推荐 | 原因 |
|------|------|------|
| 大多数场景 | **ArrayList** | 随机访问快、缓存友好、内存紧凑 |
| 频繁头部插入 | **LinkedList** | ArrayList 头插 O(n) 太慢 |
| 频繁头尾操作（队列/双端队列） | **LinkedList** 或 ArrayDeque | ArrayDeque 通常更优 |
| 已知数据量 | **ArrayList** | 预分配容量避免扩容 |
| 大量随机访问 | **ArrayList** | O(1) vs O(n) |

**经验法则：** 除非有明确的头部频繁插入需求，否则默认选择 `ArrayList`。

## 六、面试常考问题

### Q1: ArrayList 线程安全吗？

不安全。多线程下可使用 `Collections.synchronizedList()` 或 `CopyOnWriteArrayList`。

### Q2: ArrayList 的 modCount 有什么用？

用于**快速失败（fail-fast）**机制。迭代过程中如果列表被结构性修改（增/删），迭代器会比较 modCount 与 expectedModCount，不一致则抛出 `ConcurrentModificationException`。

### Q3: 子数组 sublist 有什么坑？

`subList()` 返回的是原列表的**视图**，不是独立副本。对子列表的修改会影响原列表，反之亦然。

## 总结

- `ArrayList` 基于动态数组，随机访问 O(1)，尾部插入均摊 O(1)，适合绝大多数场景
- `LinkedList` 基于双向链表，头尾操作 O(1)，但随机访问和遍历性能差
- 实际开发中 `ArrayList` 是首选，CPU 缓存友好性和内存紧凑性带来显著优势
- 两者都不是线程安全的，多线程场景需要额外的同步措施',
3, 0, 1, 'ArrayList,LinkedList,Java集合,数据结构,面试',
'深入对比Java中ArrayList和LinkedList的底层实现、时间复杂度、内存占用与性能表现，给出实际开发选型建议',
1, '2025-06-08 10:30:00', '2025-06-08 10:30:00');

-- 关联标签
INSERT INTO `article_tag` (`article_id`, `tag_id`) SELECT a.id, t.id FROM `article` a, `tag` t WHERE a.slug = 'arraylist-vs-linkedlist' AND t.slug IN ('java', 'collection', 'interview');
