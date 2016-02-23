# Introduction

Project **pegdown-rst-serializer** enables you to convert [Markdown](https://daringfireball.net/projects/markdown/) to
[reStructuredText](http://docutils.sourceforge.net/rst.html) using Java.
It is built on top of [pegdown](https://github.com/sirthias/pegdown) Java Markdown parser.

# Installation

> **TODO:** Upload to central Maven repo.

# Usage

```java
char[] md = FileUtils.readFileToString( "README.md" ).toCharArray();

PegDownProcessor pegDownProcessor = new PegDownProcessor();
RootNode astRoot = pegDownProcessor.parseMarkdown( md );
String rst = new ToRstSerializer().toRst( astRoot, md );
```