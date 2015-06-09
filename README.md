# EmailValidator4J
[![Build Status](https://travis-ci.org/egulias/EmailValidator4J.svg?branch=master)](https://travis-ci.org/egulias/EmailValidator4J)
[![Download](https://api.bintray.com/packages/egulias/maven/EmailValidator4J/images/download.svg) ](https://bintray.com/egulias/maven/EmailValidator4J/_latestVersion)

Simple RFC compliant EmailValidator for Java.

**Suported RFCs**

RFC 5321, 5322, 6530, 6531, 6532.

Install it!
-----------
EmailValidator4j is available on [JCenter]!!!

```groovy
dependencies {
    compile 'egulias:EmailValidator4J:1.0.0'
}
```

```xml
<dependency>
    <groupId>egulias</groupId>
    <artifactId>EmailValidator4J</artifactId>
    <version>1.0.0</version>
    <type>jar</type>
    <classifier>sources</classifier>
</dependency>
```

[JCenter]: https://bintray.com/egulias/maven/EmailValidator4J

## Why not using ANTLR
Because you don't want to download internet to validate an email.

Getting Started
---------------

```java

import emailvalidator4j.EmailValidator

public class ValidateEmail {
    public static void main (String[] args) {
        EmailValidator validator = new EmailValidator();
        if (validator->isValid(args[0]) {
            System.out.println("Valid email");
        } else {
            System.out.println("Invalid email");
        }
    }
}
```

Validation Strategies
---------------
The library comes with an extension point to allow for custom validations.

Validation strategies should implement `emailvalidator4j.ValidationStrategy`

## Out of the box available validators

### WarningsNotAllowed
Will make an email invalid if there's at least one [Warning](https://github.com/egulias/EmailValidator4J/blob/master/src/main/java/emailvalidator4j/parser/Warnings.java)

### MXRecord
Will check for the existence of a Mail eXchange record on the host.
**this makes a dns request**


Licence
-----------
Released under the MIT License attached with this code.

