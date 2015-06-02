# EmailValidator4J
[![Build Status](https://travis-ci.org/egulias/EmailValidator4J.svg?branch=master)](https://travis-ci.org/egulias/EmailValidator4J)

RFC strict EmailValidator for Java

**Suported RFCs**

RFC 5321, 5322, 6530, 6531, 6532.

Install it!
-----------
EmailValidator4j is available on [JCenter]!!!

```groovy
dependencies {
}
```

```xml
<dependency>
  <groupId></groupId>
  <artifactId></artifactId>
  <version></version>
</dependency>
```

[JCenter]: https://bintray.com/egulias/maven/email-validator-4j


Getting Started
---------------

```java
```

Validation Strategies
---------------
The library comes with an extension point to allow for custom validations.

Validation strategies should implement `emailvalidator4j.ValidationStrategy`

## Out of the box available validators

### WarningsNotAllowed
Will make an email invalid if there's at lest one [Warning](https://github.com/egulias/EmailValidator4J/blob/master/src/main/java/emailvalidator4j/parser/Warnings.java)

### MXRecord
Will check for the existance of a Mail eXchange record on the host.
**this makes a dns request**



