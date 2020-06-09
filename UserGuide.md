# Mobikwik User Guide

1. [Overview](#TOC-Overview)
2. [Goals for Mobikwik](#TOC-Goals-for-Mobikwik)
3. [Mobikwik Performance and Scalability](#TOC-Mobikwik-Performance-and-Scalability)
4. [Mobikwik Users](#TOC-Mobikwik-Users)
5. [Using Mobikwik](#TOC-Using-Mobikwik)
   * [Using Mobikwik with Gradle/Android](#TOC-Mobikwik-With-Gradle)
   * [Using Mobikwik with Maven](#TOC-Mobikwik-With-Maven)
   * [Primitives Examples](#TOC-Primitives-Examples)
   * [Object Examples](#TOC-Object-Examples)
   * [Finer Points with Objects](#TOC-Finer-Points-with-Objects)
   * [Nested Classes (including Inner Classes)](#TOC-Nested-Classes-including-Inner-Classes-)
   * [Array Examples](#TOC-Array-Examples)
   * [Collections Examples](#TOC-Collections-Examples)
     * [Collections Limitations](#TOC-Collections-Limitations)
   * [Serializing and Deserializing Generic Types](#TOC-Serializing-and-Deserializing-Generic-Types)
   * [Serializing and Deserializing Collection with Objects of Arbitrary Types](#TOC-Serializing-and-Deserializing-Collection-with-Objects-of-Arbitrary-Types)
   * [Built-in Serializers and Deserializers](#TOC-Built-in-Serializers-and-Deserializers)
   * [Custom Serialization and Deserialization](#TOC-Custom-Serialization-and-Deserialization)
     * [Writing a Serializer](#TOC-Writing-a-Serializer)
     * [Writing a Deserializer](#TOC-Writing-a-Deserializer)
   * [Writing an Instance Creator](#TOC-Writing-an-Instance-Creator)
     * [InstanceCreator for a Parameterized Type](#TOC-InstanceCreator-for-a-Parameterized-Type)
   * [Compact Vs. Pretty Printing for JSON Output Format](#TOC-Compact-Vs.-Pretty-Printing-for-JSON-Output-Format)
   * [Null Object Support](#TOC-Null-Object-Support)
   * [Versioning Support](#TOC-Versioning-Support)
   * [Excluding Fields From Serialization and Deserialization](#TOC-Excluding-Fields-From-Serialization-and-Deserialization)
     * [Java Modifier Exclusion](#TOC-Java-Modifier-Exclusion)
     * [Mobikwik's `@Expose`](#TOC-Mobikwik-s-Expose)
     * [User Defined Exclusion Strategies](#TOC-User-Defined-Exclusion-Strategies)
   * [JSON Field Naming Support](#TOC-JSON-Field-Naming-Support)
   * [Sharing State Across Custom Serializers and Deserializers](#TOC-Sharing-State-Across-Custom-Serializers-and-Deserializers)
   * [Streaming](#TOC-Streaming)
6. [Issues in Designing Mobikwik](#TOC-Issues-in-Designing-Mobikwik)
7. [Future Enhancements to Mobikwik](#TOC-Future-Enhancements-to-Mobikwik)

## <a name="TOC-Overview"></a>Overview

Mobikwik is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object.

Mobikwik can work with arbitrary Java objects including pre-existing objects that you do not have source code of.

## <a name="TOC-Goals-for-Mobikwik"></a>Goals for Mobikwik

* Provide easy to use mechanisms like `toString()` and constructor (factory method) to convert Java to JSON and vice-versa
* Allow pre-existing unmodifiable objects to be converted to and from JSON
* Allow custom representations for objects
* Support arbitrarily complex objects
* Generate compact and readable JSON output

## <a name="TOC-Mobikwik-Performance-and-Scalability"></a>Mobikwik Performance and Scalability

Here are some metrics that we obtained on a desktop (dual opteron, 8GB RAM, 64-bit Ubuntu) running lots of other things along-with the tests. You can rerun these tests by using the class [`PerformanceTest`](Mobikwik/src/test/java/com/google/Mobikwik/metrics/PerformanceTest.java).

* Strings: Deserialized strings of over 25MB without any problems (see `disabled_testStringDeserializationPerformance` method in `PerformanceTest`)
* Large collections:
  * Serialized a collection of 1.4 million objects (see `disabled_testLargeCollectionSerialization` method in `PerformanceTest`)
  * Deserialized a collection of 87,000 objects (see `disabled_testLargeCollectionDeserialization` in `PerformanceTest`)
* Mobikwik 1.4 raised the deserialization limit for byte arrays and collection to over 11MB from 80KB.

Note: Delete the `disabled_` prefix to run these tests. We use this prefix to prevent running these tests every time we run JUnit tests.

## <a name="TOC-Mobikwik-Users"></a>Mobikwik Users

Mobikwik was originally created for use inside Google where it is currently used in a number of projects. It is now used by a number of public projects and companies.

## <a name="TOC-Using-Mobikwik"></a>Using Mobikwik

The primary class to use is [`Mobikwik`](Mobikwik/src/main/java/com/google/Mobikwik/Mobikwik.java) which you can just create by calling `new Mobikwik()`. There is also a class [`MobikwikBuilder`](Mobikwik/src/main/java/com/google/Mobikwik/MobikwikBuilder.java) available that can be used to create a Mobikwik instance with various settings like version control and so on.

The Mobikwik instance does not maintain any state while invoking Json operations. So, you are free to reuse the same object for multiple Json serialization and deserialization operations.

## <a name="TOC-Mobikwik-With-Gradle"></a>Using Mobikwik with Gradle/Android
```
dependencies {
    implementation 'com.google.code.Mobikwik:Mobikwik:2.8.6'
}
```
## <a name="TOC-Mobikwik-With-Maven"></a>Using Mobikwik with Maven
To use Mobikwik with Maven2/3, you can use the Mobikwik version available in Maven Central by adding the following dependency:

```xml
<dependencies>
    <!--  Mobikwik: Java to Json conversion -->
    <dependency>
      <groupId>com.google.code.Mobikwik</groupId>
      <artifactId>Mobikwik</artifactId>
      <version>2.8.6</version>
      <scope>compile</scope>
    </dependency>
</dependencies>
```

That is it, now your maven project is Mobikwik enabled.

### <a name="TOC-Primitives-Examples"></a>Primitives Examples

```java
// Serialization
Mobikwik Mobikwik = new Mobikwik();
Mobikwik.toJson(1);            // ==> 1
Mobikwik.toJson("abcd");       // ==> "abcd"
Mobikwik.toJson(new Long(10)); // ==> 10
int[] values = { 1 };
Mobikwik.toJson(values);       // ==> [1]

// Deserialization
int one = Mobikwik.fromJson("1", int.class);
Integer one = Mobikwik.fromJson("1", Integer.class);
Long one = Mobikwik.fromJson("1", Long.class);
Boolean false = Mobikwik.fromJson("false", Boolean.class);
String str = Mobikwik.fromJson("\"abc\"", String.class);
String[] anotherStr = Mobikwik.fromJson("[\"abc\"]", String[].class);
```

### <a name="TOC-Object-Examples"></a>Object Examples

```java
class BagOfPrimitives {
  private int value1 = 1;
  private String value2 = "abc";
  private transient int value3 = 3;
  BagOfPrimitives() {
    // no-args constructor
  }
}

// Serialization
BagOfPrimitives obj = new BagOfPrimitives();
Mobikwik Mobikwik = new Mobikwik();
String json = Mobikwik.toJson(obj);

// ==> json is {"value1":1,"value2":"abc"}
```

Note that you can not serialize objects with circular references since that will result in infinite recursion.

```java
// Deserialization
BagOfPrimitives obj2 = Mobikwik.fromJson(json, BagOfPrimitives.class);
// ==> obj2 is just like obj
```

#### <a name="TOC-Finer-Points-with-Objects"></a>**Finer Points with Objects**

* It is perfectly fine (and recommended) to use private fields.
* There is no need to use any annotations to indicate a field is to be included for serialization and deserialization. All fields in the current class (and from all super classes) are included by default.
* If a field is marked transient, (by default) it is ignored and not included in the JSON serialization or deserialization.
* This implementation handles nulls correctly.
  * While serializing, a null field is omitted from the output.
  * While deserializing, a missing entry in JSON results in setting the corresponding field in the object to its default value: null for object types, zero for numeric types, and false for booleans.
* If a field is _synthetic_, it is ignored and not included in JSON serialization or deserialization.
* Fields corresponding to the outer classes in inner classes, anonymous classes, and local classes are ignored and not included in serialization or deserialization.

### <a name="TOC-Nested-Classes-including-Inner-Classes-"></a>Nested Classes (including Inner Classes)

Mobikwik can serialize static nested classes quite easily.

Mobikwik can also deserialize static nested classes. However, Mobikwik can **not** automatically deserialize the **pure inner classes since their no-args constructor also need a reference to the containing Object** which is not available at the time of deserialization. You can address this problem by either making the inner class static or by providing a custom InstanceCreator for it. Here is an example:

```java
public class A { 
  public String a; 

  class B { 

    public String b; 

    public B() {
      // No args constructor for B
    }
  } 
}
```

**NOTE**: The above class B can not (by default) be serialized with Mobikwik.

Mobikwik can not deserialize `{"b":"abc"}` into an instance of B since the class B is an inner class. If it was defined as static class B then Mobikwik would have been able to deserialize the string. Another solution is to write a custom instance creator for B.

```java
public class InstanceCreatorForB implements InstanceCreator<A.B> {
  private final A a;
  public InstanceCreatorForB(A a)  {
    this.a = a;
  }
  public A.B createInstance(Type type) {
    return a.new B();
  }
}
```

The above is possible, but not recommended.

### <a name="TOC-Array-Examples"></a>Array Examples

```java
Mobikwik Mobikwik = new Mobikwik();
int[] ints = {1, 2, 3, 4, 5};
String[] strings = {"abc", "def", "ghi"};

// Serialization
Mobikwik.toJson(ints);     // ==> [1,2,3,4,5]
Mobikwik.toJson(strings);  // ==> ["abc", "def", "ghi"]

// Deserialization
int[] ints2 = Mobikwik.fromJson("[1,2,3,4,5]", int[].class);
// ==> ints2 will be same as ints
```

We also support multi-dimensional arrays, with arbitrarily complex element types.

### <a name="TOC-Collections-Examples"></a>Collections Examples

```java
Mobikwik Mobikwik = new Mobikwik();
Collection<Integer> ints = Lists.immutableList(1,2,3,4,5);

// Serialization
String json = Mobikwik.toJson(ints);  // ==> json is [1,2,3,4,5]

// Deserialization
Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
Collection<Integer> ints2 = Mobikwik.fromJson(json, collectionType);
// ==> ints2 is same as ints
```

Fairly hideous: note how we define the type of collection.
Unfortunately, there is no way to get around this in Java.

#### <a name="TOC-Collections-Limitations"></a>Collections Limitations

Mobikwik can serialize collection of arbitrary objects but can not deserialize from it, because there is no way for the user to indicate the type of the resulting object. Instead, while deserializing, the Collection must be of a specific, generic type.
This makes sense, and is rarely a problem when following good Java coding practices.

### <a name="TOC-Serializing-and-Deserializing-Generic-Types"></a>Serializing and Deserializing Generic Types

When you call `toJson(obj)`, Mobikwik calls `obj.getClass()` to get information on the fields to serialize. Similarly, you can typically pass `MyClass.class` object in the `fromJson(json, MyClass.class)` method. This works fine if the object is a non-generic type. However, if the object is of a generic type, then the Generic type information is lost because of Java Type Erasure. Here is an example illustrating the point:

```java
class Foo<T> {
  T value;
}
Mobikwik Mobikwik = new Mobikwik();
Foo<Bar> foo = new Foo<Bar>();
Mobikwik.toJson(foo); // May not serialize foo.value correctly

Mobikwik.fromJson(json, foo.getClass()); // Fails to deserialize foo.value as Bar
```

The above code fails to interpret value as type Bar because Mobikwik invokes `foo.getClass()` to get its class information, but this method returns a raw class, `Foo.class`. This means that Mobikwik has no way of knowing that this is an object of type `Foo<Bar>`, and not just plain `Foo`.

You can solve this problem by specifying the correct parameterized type for your generic type. You can do this by using the [`TypeToken`](https://static.javadoc.io/com.google.code.Mobikwik/Mobikwik/2.8.5/com/google/Mobikwik/reflect/TypeToken.html) class.

```java
Type fooType = new TypeToken<Foo<Bar>>() {}.getType();
Mobikwik.toJson(foo, fooType);

Mobikwik.fromJson(json, fooType);
```
The idiom used to get `fooType` actually defines an anonymous local inner class containing a method `getType()` that returns the fully parameterized type.

### <a name="TOC-Serializing-and-Deserializing-Collection-with-Objects-of-Arbitrary-Types"></a>Serializing and Deserializing Collection with Objects of Arbitrary Types

Sometimes you are dealing with JSON array that contains mixed types. For example:
`['hello',5,{name:'GREETINGS',source:'guest'}]`

The equivalent `Collection` containing this is:

```java
Collection collection = new ArrayList();
collection.add("hello");
collection.add(5);
collection.add(new Event("GREETINGS", "guest"));
```

where the `Event` class is defined as:

```java
class Event {
  private String name;
  private String source;
  private Event(String name, String source) {
    this.name = name;
    this.source = source;
  }
}
```

You can serialize the collection with Mobikwik without doing anything specific: `toJson(collection)` would write out the desired output.

However, deserialization with `fromJson(json, Collection.class)` will not work since Mobikwik has no way of knowing how to map the input to the types. Mobikwik requires that you provide a genericised version of collection type in `fromJson()`. So, you have three options:

1. Use Mobikwik's parser API (low-level streaming parser or the DOM parser JsonParser) to parse the array elements and then use `Mobikwik.fromJson()` on each of the array elements.This is the preferred approach. [Here is an example](extras/src/main/java/com/google/Mobikwik/extras/examples/rawcollections/RawCollectionsExample.java) that demonstrates how to do this.

2. Register a type adapter for `Collection.class` that looks at each of the array members and maps them to appropriate objects. The disadvantage of this approach is that it will screw up deserialization of other collection types in Mobikwik.

3. Register a type adapter for `MyCollectionMemberType` and use `fromJson()` with `Collection<MyCollectionMemberType>`.

This approach is practical only if the array appears as a top-level element or if you can change the field type holding the collection to be of type `Collection<MyCollectionMemberType>`.

### <a name="TOC-Built-in-Serializers-and-Deserializers"></a>Built-in Serializers and Deserializers

Mobikwik has built-in serializers and deserializers for commonly used classes whose default representation may be inappropriate, for instance

* `java.net.URL` to match it with strings like `"https://github.com/google/Mobikwik/"`
* `java.net.URI` to match it with strings like `"/google/Mobikwik/"`

For many more, see the internal class [`TypeAdapters`](https://github.com/google/Mobikwik/blob/master/Mobikwik/src/main/java/com/google/Mobikwik/internal/bind/TypeAdapters.java).

You can also find source code for some commonly used classes such as JodaTime at [this page](https://sites.google.com/site/Mobikwik/Mobikwik-type-adapters-for-common-classes-1).

### <a name="TOC-Custom-Serialization-and-Deserialization"></a>Custom Serialization and Deserialization

Sometimes default representation is not what you want. This is often the case when dealing with library classes (DateTime, etc).
Mobikwik allows you to register your own custom serializers and deserializers. This is done by defining two parts:

* Json Serializers: Need to define custom serialization for an object
* Json Deserializers: Needed to define custom deserialization for a type

* Instance Creators: Not needed if no-args constructor is available or a deserializer is registered

```java
MobikwikBuilder Mobikwik = new MobikwikBuilder();
Mobikwik.registerTypeAdapter(MyType2.class, new MyTypeAdapter());
Mobikwik.registerTypeAdapter(MyType.class, new MySerializer());
Mobikwik.registerTypeAdapter(MyType.class, new MyDeserializer());
Mobikwik.registerTypeAdapter(MyType.class, new MyInstanceCreator());
```

`registerTypeAdapter` call checks if the type adapter implements more than one of these interfaces and register it for all of them.

#### <a name="TOC-Writing-a-Serializer"></a>Writing a Serializer

Here is an example of how to write a custom serializer for JodaTime `DateTime` class.

```java
private class DateTimeSerializer implements JsonSerializer<DateTime> {
  public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }
}
```

Mobikwik calls `serialize()` when it runs into a `DateTime` object during serialization.

#### <a name="TOC-Writing-a-Deserializer"></a>Writing a Deserializer

Here is an example of how to write a custom deserializer for JodaTime DateTime class.

```java
private class DateTimeDeserializer implements JsonDeserializer<DateTime> {
  public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return new DateTime(json.getAsJsonPrimitive().getAsString());
  }
}
```

Mobikwik calls `deserialize` when it needs to deserialize a JSON string fragment into a DateTime object

**Finer points with Serializers and Deserializers**

Often you want to register a single handler for all generic types corresponding to a raw type

* For example, suppose you have an `Id` class for id representation/translation (i.e. an internal vs. external representation).
* `Id<T>` type that has same serialization for all generic types
  * Essentially write out the id value
* Deserialization is very similar but not exactly the same
  * Need to call `new Id(Class<T>, String)` which returns an instance of `Id<T>`

Mobikwik supports registering a single handler for this. You can also register a specific handler for a specific generic type (say `Id<RequiresSpecialHandling>` needed special handling).
The `Type` parameter for the `toJson()` and `fromJson()` contains the generic type information to help you write a single handler for all generic types corresponding to the same raw type.

### <a name="TOC-Writing-an-Instance-Creator"></a>Writing an Instance Creator

While deserializing an Object, Mobikwik needs to create a default instance of the class.
Well-behaved classes that are meant for serialization and deserialization should have a no-argument constructor.

* Doesn't matter whether public or private

Typically, Instance Creators are needed when you are dealing with a library class that does NOT define a no-argument constructor

**Instance Creator Example**

```java
private class MoneyInstanceCreator implements InstanceCreator<Money> {
  public Money createInstance(Type type) {
    return new Money("1000000", CurrencyCode.USD);
  }
}
```

Type could be of a corresponding generic type

* Very useful to invoke constructors which need specific generic type information
* For example, if the `Id` class stores the class for which the Id is being created

#### <a name="TOC-InstanceCreator-for-a-Parameterized-Type"></a>InstanceCreator for a Parameterized Type

Sometimes the type that you are trying to instantiate is a parameterized type. Generally, this is not a problem since the actual instance is of raw type. Here is an example:

```java
class MyList<T> extends ArrayList<T> {
}

class MyListInstanceCreator implements InstanceCreator<MyList<?>> {
    @SuppressWarnings("unchecked")
  public MyList<?> createInstance(Type type) {
    // No need to use a parameterized list since the actual instance will have the raw type anyway.
    return new MyList();
  }
}
```

However, sometimes you do need to create instance based on the actual parameterized type. In this case, you can use the type parameter being passed to the `createInstance` method. Here is an example:

```java
public class Id<T> {
  private final Class<T> classOfId;
  private final long value;
  public Id(Class<T> classOfId, long value) {
    this.classOfId = classOfId;
    this.value = value;
  }
}

class IdInstanceCreator implements InstanceCreator<Id<?>> {
  public Id<?> createInstance(Type type) {
    Type[] typeParameters = ((ParameterizedType)type).getActualTypeArguments();
    Type idType = typeParameters[0]; // Id has only one parameterized type T
    return Id.get((Class)idType, 0L);
  }
}
```

In the above example, an instance of the Id class can not be created without actually passing in the actual type for the parameterized type. We solve this problem by using the passed method parameter, `type`. The `type` object in this case is the Java parameterized type representation of `Id<Foo>` where the actual instance should be bound to `Id<Foo>`. Since `Id` class has just one parameterized type parameter, `T`, we use the zeroth element of the type array returned by `getActualTypeArgument()` which will hold `Foo.class` in this case.

### <a name="TOC-Compact-Vs.-Pretty-Printing-for-JSON-Output-Format"></a>Compact Vs. Pretty Printing for JSON Output Format

The default JSON output that is provided by Mobikwik is a compact JSON format. This means that there will not be any whitespace in the output JSON structure. Therefore, there will be no whitespace between field names and its value, object fields, and objects within arrays in the JSON output. As well, "null" fields will be ignored in the output (NOTE: null values will still be included in collections/arrays of objects). See the [Null Object Support](#TOC-Null-Object-Support) section for information on configure Mobikwik to output all null values.

If you would like to use the Pretty Print feature, you must configure your `Mobikwik` instance using the `MobikwikBuilder`. The `JsonFormatter` is not exposed through our public API, so the client is unable to configure the default print settings/margins for the JSON output. For now, we only provide a default `JsonPrintFormatter` that has default line length of 80 character, 2 character indentation, and 4 character right margin.

The following is an example shows how to configure a `Mobikwik` instance to use the default `JsonPrintFormatter` instead of the `JsonCompactFormatter`:
```
Mobikwik Mobikwik = new MobikwikBuilder().setPrettyPrinting().create();
String jsonOutput = Mobikwik.toJson(someObject);
```
### <a name="TOC-Null-Object-Support"></a>Null Object Support

The default behaviour that is implemented in Mobikwik is that `null` object fields are ignored. This allows for a more compact output format; however, the client must define a default value for these fields as the JSON format is converted back into its Java form.

Here's how you would configure a `Mobikwik` instance to output null:

```java
Mobikwik Mobikwik = new MobikwikBuilder().serializeNulls().create();
```

NOTE: when serializing `null`s with Mobikwik, it will add a `JsonNull` element to the `JsonElement` structure. Therefore, this object can be used in custom serialization/deserialization.

Here's an example:

```java
public class Foo {
  private final String s;
  private final int i;

  public Foo() {
    this(null, 5);
  }

  public Foo(String s, int i) {
    this.s = s;
    this.i = i;
  }
}

Mobikwik Mobikwik = new MobikwikBuilder().serializeNulls().create();
Foo foo = new Foo();
String json = Mobikwik.toJson(foo);
System.out.println(json);

json = Mobikwik.toJson(null);
System.out.println(json);
```

The output is:

```
{"s":null,"i":5}
null
```

### <a name="TOC-Versioning-Support"></a>Versioning Support

Multiple versions of the same object can be maintained by using [@Since](Mobikwik/src/main/java/com/google/Mobikwik/annotations/Since.java) annotation. This annotation can be used on Classes, Fields and, in a future release, Methods. In order to leverage this feature, you must configure your `Mobikwik` instance to ignore any field/object that is greater than some version number. If no version is set on the `Mobikwik` instance then it will serialize and deserialize all fields and classes regardless of the version.

```java
public class VersionedClass {
  @Since(1.1) private final String newerField;
  @Since(1.0) private final String newField;
  private final String field;

  public VersionedClass() {
    this.newerField = "newer";
    this.newField = "new";
    this.field = "old";
  }
}

VersionedClass versionedObject = new VersionedClass();
Mobikwik Mobikwik = new MobikwikBuilder().setVersion(1.0).create();
String jsonOutput = Mobikwik.toJson(versionedObject);
System.out.println(jsonOutput);
System.out.println();

Mobikwik = new Mobikwik();
jsonOutput = Mobikwik.toJson(versionedObject);
System.out.println(jsonOutput);
```

The output is:

```
{"newField":"new","field":"old"}

{"newerField":"newer","newField":"new","field":"old"}
```

### <a name="TOC-Excluding-Fields-From-Serialization-and-Deserialization"></a>Excluding Fields From Serialization and Deserialization

Mobikwik supports numerous mechanisms for excluding top-level classes, fields and field types. Below are pluggable mechanisms that allow field and class exclusion. If none of the below mechanisms satisfy your needs then you can always use [custom serializers and deserializers](#TOC-Custom-Serialization-and-Deserialization).

#### <a name="TOC-Java-Modifier-Exclusion"></a>Java Modifier Exclusion

By default, if you mark a field as `transient`, it will be excluded. As well, if a field is marked as `static` then by default it will be excluded. If you want to include some transient fields then you can do the following:

```java
import java.lang.reflect.Modifier;
Mobikwik Mobikwik = new MobikwikBuilder()
    .excludeFieldsWithModifiers(Modifier.STATIC)
    .create();
```

NOTE: you can give any number of the `Modifier` constants to the `excludeFieldsWithModifiers` method. For example:

```java
Mobikwik Mobikwik = new MobikwikBuilder()
    .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
    .create();
```

#### <a name="TOC-Mobikwik-s-Expose"></a>Mobikwik's `@Expose`

This feature provides a way where you can mark certain fields of your objects to be excluded for consideration for serialization and deserialization to JSON. To use this annotation, you must create Mobikwik by using `new MobikwikBuilder().excludeFieldsWithoutExposeAnnotation().create()`. The Mobikwik instance created will exclude all fields in a class that are not marked with `@Expose` annotation.

#### <a name="TOC-User-Defined-Exclusion-Strategies"></a>User Defined Exclusion Strategies

If the above mechanisms for excluding fields and class type do not work for you then you can always write your own exclusion strategy and plug it into Mobikwik. See the [`ExclusionStrategy`](https://static.javadoc.io/com.google.code.Mobikwik/Mobikwik/2.8.5/com/google/Mobikwik/ExclusionStrategy.html) JavaDoc for more information.

The following example shows how to exclude fields marked with a specific `@Foo` annotation and excludes top-level types (or declared field type) of class `String`.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Foo {
  // Field tag only annotation
}

public class SampleObjectForTest {
  @Foo private final int annotatedField;
  private final String stringField;
  private final long longField;
  private final Class<?> clazzField;

  public SampleObjectForTest() {
    annotatedField = 5;
    stringField = "someDefaultValue";
    longField = 1234;
  }
}

public class MyExclusionStrategy implements ExclusionStrategy {
  private final Class<?> typeToSkip;

  private MyExclusionStrategy(Class<?> typeToSkip) {
    this.typeToSkip = typeToSkip;
  }

  public boolean shouldSkipClass(Class<?> clazz) {
    return (clazz == typeToSkip);
  }

  public boolean shouldSkipField(FieldAttributes f) {
    return f.getAnnotation(Foo.class) != null;
  }
}

public static void main(String[] args) {
  Mobikwik Mobikwik = new MobikwikBuilder()
      .setExclusionStrategies(new MyExclusionStrategy(String.class))
      .serializeNulls()
      .create();
  SampleObjectForTest src = new SampleObjectForTest();
  String json = Mobikwik.toJson(src);
  System.out.println(json);
}
```

The output is:

```
{"longField":1234}
```

### <a name="TOC-JSON-Field-Naming-Support"></a>JSON Field Naming Support

Mobikwik supports some pre-defined field naming policies to convert the standard Java field names (i.e., camel cased names starting with lower case --- `sampleFieldNameInJava`) to a Json field name (i.e., `sample_field_name_in_java` or `SampleFieldNameInJava`). See the [FieldNamingPolicy](https://static.javadoc.io/com.google.code.Mobikwik/Mobikwik/2.8.5/com/google/Mobikwik/FieldNamingPolicy.html) class for information on the pre-defined naming policies.

It also has an annotation based strategy to allows clients to define custom names on a per field basis. Note, that the annotation based strategy has field name validation which will raise "Runtime" exceptions if an invalid field name is provided as the annotation value.

The following is an example of how to use both Mobikwik naming policy features:

```java
private class SomeObject {
  @SerializedName("custom_naming") private final String someField;
  private final String someOtherField;

  public SomeObject(String a, String b) {
    this.someField = a;
    this.someOtherField = b;
  }
}

SomeObject someObject = new SomeObject("first", "second");
Mobikwik Mobikwik = new MobikwikBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
String jsonRepresentation = Mobikwik.toJson(someObject);
System.out.println(jsonRepresentation);
```

The output is:

```
{"custom_naming":"first","SomeOtherField":"second"}
```

If you have a need for custom naming policy ([see this discussion](https://groups.google.com/group/google-Mobikwik/browse_thread/thread/cb441a2d717f6892)), you can use the [@SerializedName](https://static.javadoc.io/com.google.code.Mobikwik/Mobikwik/2.8.5/com/google/Mobikwik/annotations/SerializedName.html) annotation.

### <a name="TOC-Sharing-State-Across-Custom-Serializers-and-Deserializers"></a>Sharing State Across Custom Serializers and Deserializers

Sometimes you need to share state across custom serializers/deserializers ([see this discussion](https://groups.google.com/group/google-Mobikwik/browse_thread/thread/2850010691ea09fb)). You can use the following three strategies to accomplish this:

1. Store shared state in static fields
2. Declare the serializer/deserializer as inner classes of a parent type, and use the instance fields of parent type to store shared state
3. Use Java `ThreadLocal`

1 and 2 are not thread-safe options, but 3 is.

### <a name="TOC-Streaming"></a>Streaming

In addition Mobikwik's object model and data binding, you can use Mobikwik to read from and write to a [stream](https://sites.google.com/site/Mobikwik/streaming). You can also combine streaming and object model access to get the best of both approaches.

## <a name="TOC-Issues-in-Designing-Mobikwik"></a>Issues in Designing Mobikwik

See the [Mobikwik design document](https://github.com/google/Mobikwik/blob/master/MobikwikDesignDocument.md "Mobikwik design document") for a discussion of issues we faced while designing Mobikwik. It also include a comparison of Mobikwik with other Java libraries that can be used for Json conversion.

## <a name="TOC-Future-Enhancements-to-Mobikwik"></a>Future Enhancements to Mobikwik

For the latest list of proposed enhancements or if you'd like to suggest new ones, see the [Issues section](https://github.com/google/Mobikwik/issues) under the project website.
