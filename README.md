# Introduction

Project **pegdown-rst-serializer** enables you to convert [Markdown](https://daringfireball.net/projects/markdown/) to
[reStructuredText](http://docutils.sourceforge.net/rst.html) using Java.
It is built on top of [pegdown](https://github.com/sirthias/pegdown) Java Markdown parser.

# Installation

```
<dependency>
    <groupId>tools.kaja</groupId>
    <artifactId>pregdown-rst-serializer<artifactId>
    <version>1.0.0</version>
</dependency>
```

JAR can be downloaded directly from [Maven central repository](http://repo1.maven.org/maven2/tools/kaja/pegdown-rst-serializer/).

# Usage

```java
char[] md = FileUtils.readFileToString( "README.md" ).toCharArray();

PegDownProcessor pegDownProcessor = new PegDownProcessor();
RootNode astRoot = pegDownProcessor.parseMarkdown( md );
String rst = new ToRstSerializer().toRst( astRoot, md );
```

Example input file README.md:

```
Hello [GitHub](https://github.com/)
```

Resulting reStructuredText string:

```
Hello `GitHub <https://github.com/>`_
```