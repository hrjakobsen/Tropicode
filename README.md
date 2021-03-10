# Protocol analysis of JVM bytecode

The goal of this project is to provide a tool for analyzing object protocol (typestates) for languages targeting the 
Java Virtual Machine (JVM). 

# The tool
The project is very early in its development, and as of now don't support nearly enough of the JVM instructions to be 
used for practical programs. With that said however, there exists enough functionality to showcase the usefulness of 
the tool.

Consider the following class definition:

```java
package simplecall;

import Annotations.Protocol;

@Protocol("{setFirstName; {setLastName; {greet; end}}  setLastName; {setFirstName; {greet; end}}}")
public class Person {
    private String firstName;
    private String lastName;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void greet() {
        System.out.println("Hello " + firstName + " " + lastName);
    }
}
```

The `@Protocol` annotation specifies that the protocol of the `Person` class is to either call `setFirstName` or `setLastName` followed 
by the other operation (`setFirstName` if you started with `setLastName` etc.). After setting both names, the `greet` 
method can be called, and the protocol is finished.

Now let's look at how the class may be used in a program:

```java
package simplecall;

public class Main {
    public static void main(String[] args) {
        Person p = new Person();
        p.setFirstName("John");
        p.greet();
    }
}
```

In the code above, we have forgot to call the `setLastName` method before greeting the person. The tool catches the error, 
and prints the following error:

```
================================================================================
              Checker.Exceptions.InvalidProtocolOperationException              
Invalid operation 'greet' for protocol {setLastName; {greet; end}}. The availabl
e operations are: {setLastName}
================================================================================
```

All analysis is performed statically (without executing the program), and hence will not incur a runtime overhead, and 
will catch protocol errors before the program is deployed.

# Is this a Java tool?

Yes and no. The checker itself performs all analysis on the Java bytecode (the compiled code for the JVM). This means 
that the tool will work for Java code, but also for other languages that compiles to Java bytecode such as Scala, Kotlin
or Clojure. Be aware though, that the protocol specifications _are_ language specific and must relate to the compiled 
`.class` files, and not necessarily the classes defined in the program text. 

For Java this is pretty straightforward, as classes generated classes matches closely the ones defined in the program 
text, for other languages this may not be the case. The `@Protocol` annotation is currently provided by the tool for use
in Java programs. In the future this will expanded to other languages as well.


# Todo
* Add choice typestates
    * Requires parsing of enum classes along with analysis of the static jump-arrays in other classes
* Implement rest of bytecode operations
* Track objects in:
    * ~~Static members~~
    * ~~Instance members~~
    * Parameters
* Check method bodies upon reaching an invocation (to check aliasing)