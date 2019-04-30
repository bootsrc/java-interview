# 使用HashMap的时候如何避免内存泄漏

当Key为复杂类型（自定义对象)的时候，如果map.put(obj, value)后
如果修改了obj中的某些字段值，而且这这个字段会导致obj.hashCode()发送变化的。就必定导致内存泄漏。

结果是后面如论是进行map.get(obj), remove(obj), containsKey(obj)都没有意义。
map.get(obj)==null， remove(obj)操作无效， containsKey(obj)==false
这条数据无法被GC处理。 称之为内存泄漏。  大量的内存泄漏，最终导致内存溢出异常。

代码见[HashMapMemoryLeak.java](/code/HashMapMemoryLeak.java)

贴出来如下
```java
import java.util.HashMap;
import java.util.Map;

/**
 * 演示HashMap的内存泄漏产生过程, 参考test()方法
 */
public class HashMapMemoryLeak {
    public static void main(String[] args) {
//        test1();
        test2();
    }

    private static void test1() {
        Map<Person, Integer> map = new HashMap<Person, Integer>();
        Person p = new Person("zhangsan", 12);

        System.out.println("step1 p=" + p);
        System.out.println("System.identityHashCode(p)=" + System.identityHashCode(p));
        map.put(p, 1);

        p.setName("lisi"); // 因为p.name参与了hash值的计算，修改了之后hash值发生了变化，所以下面删除不掉

        System.out.println("System.identityHashCode(p)=" + System.identityHashCode(p));

        map.remove(p);

        System.out.println("step2 p=" + p);
        System.out.println("hashCOde" + p.hashCode());

        System.out.println("---------After remove()-------");
        System.out.println("map.size()    " +  map.size());
        System.out.println("map.containsKey(p) " + map.containsKey(p));
        System.out.println("map.get(p) " + map.get(p));
    }

    private static void test2() {
        Map<Person, Integer> map = new HashMap<Person, Integer>();
        Person p = new Person("zhangsan", 12);

        map.put(p, 1);

        System.out.println("map.get(p)="+ map.get(p));
        Person p2 = new Person("zhangsan", 13);

        System.out.println("p.hashCode() == p2.hashCode() ?    "  + (p.hashCode() == p2.hashCode()));

        System.out.println("p.equals(p2)    "  + p.equals(p2));
        System.out.println("map.size()    " +  map.size());
        System.out.println("map.get(p)="+ map.get(p));
        System.out.println("map.get(p2)="+ map.get(p2));

        // 在setName之前，假设p对应的KV对存放的位置是X

        p.setName("lisi"); // 这里造成p2这个key的数据内存泄漏。导致后面无法被删除掉
        // p.setName("lisi")后， 这个Node存放的Entry的未知不变，但是，里面Node里面的Key的name变里，导致key的hashCode()变了。
        // 后面通过map.get(p)去检索数据的时候，p.hashCode()变了，经过hash计算后得到位置是Y(Y此时为空)
        // 所以这时候map.get(p)返回null, map.containsKey(p)返回false
        // 所以位置X处的Node数据已经无法进行get() ,put(),containsKey()， remove()的操作了
        // 这个内存因为被HashMap引用，也无法GC，
        // 这快内存不能被删也不能鄂博查询，也不能被GC回收，称之为内存泄漏(memory leak)
        // 这才几个字节的内存泄漏，还不会出事故， 大量的内存泄漏，会浪费很多内存，降低系统性能。最终浪费大量的堆内存，
        // 最终导致内存溢出（Out of memory, OutOfMemoryException, 或者报错Heap Space...）
        // 结论： 大量的内存泄漏会最终导致内存溢出。   如果出现了内存溢出的报错，我们可以去查看代码，是否有内存泄漏，
        //       或者到map里查看是否有一些无意义的垃圾数据（极有可能是内存溢出的数据）

        map.remove(p);
//        map.remove(p2);

        System.out.println("---------After map.remove(p)-------------");
        System.out.println("map.size()    " +  map.size());
        System.out.println("map.get(p)="+ map.get(p));
        System.out.println("map.get(p2)="+ map.get(p2));

    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

//    @Override
//    public boolean equals(Object obj) {
//        return super.equals(obj);
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Person) {
            Person personValue =  (Person)obj;

            if (personValue.getName() == null && name ==null) {
                return true;
            }

            if (personValue.getName() != null && personValue.getName().equals(name)){
                return true;
            }
        }

        return false;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//
//        if (obj instanceof Person) {
//            Person personValue =  (Person)obj;
//
//            if (personValue.getName() == null && name ==null && personValue.getAge() ==age) {
//                return true;
//            }
//
//            if (personValue.getName() != null && personValue.getName().equals(name) && personValue.getAge() ==age){
//                return true;
//            }
//        }
//
//        return false;
//    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//
//        return hashCode() == obj.hashCode();
//    }

    @Override
    public int hashCode() {
        return name.hashCode() * 123;
    }
}

```